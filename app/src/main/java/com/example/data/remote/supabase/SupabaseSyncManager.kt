package com.example.data.remote.supabase

import android.util.Log
import com.example.data.local.dao.CommunicationDao
import com.example.data.local.dao.PropertyDao
import com.example.data.local.dao.ViewingDao
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.ViewingRequestEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

sealed class SupabaseSyncStatus {
    object Unconfigured : SupabaseSyncStatus()
    object Idle : SupabaseSyncStatus()
    object Syncing : SupabaseSyncStatus()
    data class Synced(val itemsCount: Int, val timestamp: Long) : SupabaseSyncStatus()
    data class Error(val message: String) : SupabaseSyncStatus()
}

class SupabaseSyncManager(
    private val propertyDao: PropertyDao,
    private val viewingDao: ViewingDao,
    private val communicationDao: CommunicationDao
) {
    private val _syncStatus = MutableStateFlow<SupabaseSyncStatus>(
        if (SupabaseClient.isConfigured) SupabaseSyncStatus.Idle else SupabaseSyncStatus.Unconfigured
    )
    val syncStatus: StateFlow<SupabaseSyncStatus> = _syncStatus.asStateFlow()

    suspend fun syncAll(): Boolean = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured) {
            _syncStatus.value = SupabaseSyncStatus.Unconfigured
            Log.d("SupabaseSync", "Supabase credentials not configured in .env / Secrets panel. Operating in offline-first Room mode.")
            return@withContext false
        }

        val api = SupabaseClient.api ?: run {
            _syncStatus.value = SupabaseSyncStatus.Error("Failed to initialize Supabase client")
            return@withContext false
        }

        _syncStatus.value = SupabaseSyncStatus.Syncing

        try {
            // 1. Download remote properties from Supabase
            val propertiesResponse = api.getProperties()
            var totalSynced = 0

            if (propertiesResponse.isSuccessful) {
                val remoteProperties = propertiesResponse.body() ?: emptyList()
                if (remoteProperties.isNotEmpty()) {
                    val entities = remoteProperties.mapNotNull { it.toEntityOrNull() ?: it.toEntity() }
                    var propsSaved = 0
                    entities.forEach { entity ->
                        try {
                            propertyDao.insertProperty(entity)
                            propsSaved++
                        } catch (e: Exception) {
                            Log.w("SupabaseSync", "Skipped inserting property ${entity.id}: ${e.message}")
                        }
                    }
                    totalSynced += propsSaved
                    Log.d("SupabaseSync", "Downloaded & saved $propsSaved properties from Supabase")
                }
            } else {
                Log.w("SupabaseSync", "Properties fetch failed: ${propertiesResponse.code()} ${propertiesResponse.message()}")
            }

            // 2. Download remote viewing requests
            val viewingsResponse = api.getViewingRequests()
            if (viewingsResponse.isSuccessful) {
                val remoteViewings = viewingsResponse.body() ?: emptyList()
                if (remoteViewings.isNotEmpty()) {
                    val entities = remoteViewings.mapNotNull { it.toEntityOrNull() }
                    var viewingsSaved = 0
                    entities.forEach { entity ->
                        try {
                            viewingDao.insertViewingRequest(entity)
                            viewingsSaved++
                        } catch (e: Exception) {
                            Log.w("SupabaseSync", "Skipped inserting viewing ${entity.id}: ${e.message}")
                        }
                    }
                    totalSynced += viewingsSaved
                    Log.d("SupabaseSync", "Downloaded & processed $viewingsSaved valid viewing requests from Supabase")
                }
            }

            // 3. Download remote messages
            val messagesResponse = api.getMessages()
            if (messagesResponse.isSuccessful) {
                val remoteMessages = messagesResponse.body() ?: emptyList()
                if (remoteMessages.isNotEmpty()) {
                    val entities = remoteMessages.mapNotNull { it.toEntityOrNull() }
                    var messagesSaved = 0
                    entities.forEach { entity ->
                        try {
                            communicationDao.insertMessage(entity)
                            messagesSaved++
                        } catch (e: Exception) {
                            Log.w("SupabaseSync", "Skipped inserting message ${entity.id}: ${e.message}")
                        }
                    }
                    totalSynced += messagesSaved
                    Log.d("SupabaseSync", "Downloaded & processed $messagesSaved messages from Supabase")
                }
            }

            _syncStatus.value = SupabaseSyncStatus.Synced(totalSynced, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Sync failed with error: ${e.message}", e)
            _syncStatus.value = SupabaseSyncStatus.Error(e.localizedMessage ?: "Sync error")
            false
        }
    }

    suspend fun pushPropertyToRemote(property: PropertyEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabasePropertyDto.fromEntity(property)
            val res = api.createProperty(dto)
            res.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push property to Supabase: ${e.message}")
            false
        }
    }

    suspend fun pushViewingRequestToRemote(viewing: ViewingRequestEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabaseViewingDto.fromEntity(viewing)
            val res = api.createViewingRequest(dto)
            res.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push viewing request to Supabase: ${e.message}")
            false
        }
    }

    suspend fun triggerEdgeNotification(payload: EdgeNotificationPayload): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val res = api.invokeGmailNotificationFunction(payload)
            res.isSuccessful && (res.body()?.ok == true)
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to invoke notify-gmail Edge Function: ${e.message}")
            false
        }
    }
}
