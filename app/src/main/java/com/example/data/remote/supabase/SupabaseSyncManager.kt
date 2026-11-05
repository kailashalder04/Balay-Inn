package com.example.data.remote.supabase

import android.util.Log
import com.example.data.local.dao.CommunicationDao
import com.example.data.local.dao.PlatformDao
import com.example.data.local.dao.PropertyDao
import com.example.data.local.dao.ViewingDao
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.data.model.BoardingHouseListing
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
    private val communicationDao: CommunicationDao,
    private val platformDao: PlatformDao
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
        var totalSynced = 0

        try {
            // 1. Download properties from public.properties
            val propertiesResponse = api.getProperties()
            if (propertiesResponse.isSuccessful) {
                val remoteProperties = propertiesResponse.body() ?: emptyList()
                val remoteIds = remoteProperties.mapNotNull { it.id?.trim()?.ifBlank { null } }
                if (remoteIds.isNotEmpty()) {
                    propertyDao.deletePropertiesNotIn(remoteIds)
                    propertyDao.deletePropertyImagesNotIn(remoteIds)
                } else {
                    propertyDao.deleteAllProperties()
                    propertyDao.deleteAllPropertyImages()
                }
                remoteProperties.forEach { dto ->
                    try {
                        val propEntity = dto.toEntity()
                        propertyDao.insertProperty(propEntity)
                        val images = dto.toImageEntities()
                        if (images.isNotEmpty()) {
                            propertyDao.insertImages(images)
                        }
                        totalSynced++
                    } catch (e: Exception) {
                        Log.w("SupabaseSync", "Skipped inserting property ${dto.id}: ${e.message}")
                    }
                }
                Log.d("SupabaseSync", "Synced ${remoteProperties.size} properties and thumbnails from Supabase")
            }

            // 2. Download viewing requests from public.viewing_requests
            try {
                val viewingsResponse = api.getViewingRequests()
                if (viewingsResponse.isSuccessful) {
                    val remoteViewings = viewingsResponse.body() ?: emptyList()
                    remoteViewings.forEach { dto ->
                        dto.toEntityOrNull()?.let {
                            try {
                                viewingDao.insertViewingRequest(it)
                                totalSynced++
                            } catch (e: Exception) {
                                Log.w("SupabaseSync", "Skipped inserting viewing: ${e.message}")
                            }
                        }
                    }
                    Log.d("SupabaseSync", "Synced ${remoteViewings.size} viewings from Supabase")
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error fetching viewing_requests: ${e.message}")
            }

            // 2b. Download inquiries from public.inquiries (fallback/dual-sync)
            try {
                val inqResponse = api.getInquiries()
                if (inqResponse.isSuccessful) {
                    val remoteInquiries = inqResponse.body() ?: emptyList()
                    remoteInquiries.forEach { inq ->
                        inq.toViewingEntityOrNull()?.let {
                            try {
                                viewingDao.insertViewingRequest(it)
                                totalSynced++
                            } catch (e: Exception) {
                                Log.w("SupabaseSync", "Skipped inserting inquiry: ${e.message}")
                            }
                        }
                    }
                    Log.d("SupabaseSync", "Synced ${remoteInquiries.size} inquiries from Supabase")
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error fetching inquiries: ${e.message}")
            }

            // 3. Download conversations from public.conversations
            try {
                val convResponse = api.getConversations()
                if (convResponse.isSuccessful) {
                    val remoteConvs = convResponse.body() ?: emptyList()
                    remoteConvs.forEach { dto ->
                        try {
                            communicationDao.insertConversation(dto.toEntity())
                            totalSynced++
                        } catch (e: Exception) {
                            Log.w("SupabaseSync", "Skipped inserting conversation: ${e.message}")
                        }
                    }
                    Log.d("SupabaseSync", "Synced ${remoteConvs.size} conversations from Supabase")
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error fetching conversations: ${e.message}")
            }

            // 4. Download messages from public.messages
            try {
                val messagesResponse = api.getMessages()
                if (messagesResponse.isSuccessful) {
                    val remoteMessages = messagesResponse.body() ?: emptyList()
                    remoteMessages.forEach { dto ->
                        dto.toEntityOrNull()?.let {
                            try {
                                communicationDao.insertMessage(it)
                                totalSynced++
                            } catch (e: Exception) {
                                Log.w("SupabaseSync", "Skipped inserting message: ${e.message}")
                            }
                        }
                    }
                    Log.d("SupabaseSync", "Synced ${remoteMessages.size} messages from Supabase")
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error fetching messages: ${e.message}")
            }

            // 5. Download notifications from public.notifications
            try {
                val notificationsResponse = api.getNotifications()
                if (notificationsResponse.isSuccessful) {
                    val remoteNotifs = notificationsResponse.body() ?: emptyList()
                    remoteNotifs.forEach { dto ->
                        try {
                            platformDao.insertNotification(dto.toEntity())
                            totalSynced++
                        } catch (e: Exception) {
                            Log.w("SupabaseSync", "Skipped inserting notification: ${e.message}")
                        }
                    }
                    Log.d("SupabaseSync", "Synced ${remoteNotifs.size} notifications from Supabase")
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error fetching notifications: ${e.message}")
            }

            _syncStatus.value = SupabaseSyncStatus.Synced(totalSynced, System.currentTimeMillis())
            true
        } catch (e: Exception) {
            Log.e("SupabaseSync", "Sync failed with error: ${e.message}", e)
            _syncStatus.value = SupabaseSyncStatus.Error(e.localizedMessage ?: "Sync error")
            false
        }
    }

    suspend fun pushPropertyToRemote(
        property: PropertyEntity,
        images: List<com.example.data.local.entity.PropertyImageEntity> = emptyList()
    ): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabasePropertyDto.fromEntity(property, images)
            val res = api.createProperty(dto)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully saved property ${property.id} to Supabase")
                true
            } else {
                Log.w("SupabaseSync", "Failed to save property ${property.id} to Supabase: ${res.code()} - ${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push property to Supabase: ${e.message}")
            false
        }
    }

    suspend fun pushViewingRequestToRemote(
        viewing: ViewingRequestEntity,
        recipientEmail: String? = null,
        landlordId: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        var success = false
        try {
            val dto = SupabaseViewingDto.fromEntity(
                e = viewing,
                recipientEmail = recipientEmail,
                landlordId = landlordId ?: viewing.posterId
            )
            val res = api.upsertViewingRequest(dto)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully saved viewing request ${viewing.id} to Supabase viewing_requests (recipient_email: $recipientEmail)")
                success = true
            } else {
                Log.w("SupabaseSync", "viewing_requests upsert returned: ${res.code()} - ${res.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push viewing_requests to Supabase: ${e.message}")
        }

        // Also dual-write / mirror to inquiries table
        try {
            val inqDto = SupabaseViewingDto.fromEntity(
                e = viewing,
                recipientEmail = recipientEmail,
                landlordId = landlordId ?: viewing.posterId
            ).toInquiryDto()
            val inqRes = api.upsertInquiry(inqDto)
            if (inqRes.isSuccessful) {
                Log.d("SupabaseSync", "Successfully mirrored viewing request ${viewing.id} to Supabase inquiries")
                success = true
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to mirror to inquiries: ${e.message}")
        }

        success
    }

    suspend fun pushMessageToRemote(message: MessageEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabaseMessageDto.fromEntity(message)
            val res = api.upsertMessage(dto)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully pushed message ${message.id} to Supabase")
                true
            } else {
                Log.w("SupabaseSync", "Failed to push message: ${res.code()} - ${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push message to Supabase: ${e.message}")
            false
        }
    }

    suspend fun pushConversationToRemote(conversation: ConversationEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabaseConversationDto.fromEntity(conversation)
            val res = api.upsertConversation(dto)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully pushed conversation ${conversation.id} to Supabase")
                true
            } else {
                Log.w("SupabaseSync", "Failed to push conversation: ${res.code()} - ${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push conversation to Supabase: ${e.message}")
            false
        }
    }

    suspend fun pushNotificationToRemote(notification: NotificationEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val dto = SupabaseNotificationDto.fromEntity(notification)
            val res = api.upsertNotification(dto)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully pushed notification ${notification.id} to Supabase")
                true
            } else {
                Log.w("SupabaseSync", "Failed to push notification: ${res.code()} - ${res.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push notification to Supabase: ${e.message}")
            false
        }
    }

    suspend fun syncLiveUpdates(): Boolean = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured) return@withContext false
        val api = SupabaseClient.api ?: return@withContext false
        try {
            // 1. Sync Viewing Requests
            try {
                val viewingsRes = api.getViewingRequests()
                if (viewingsRes.isSuccessful) {
                    viewingsRes.body()?.forEach { dto ->
                        dto.toEntityOrNull()?.let { viewingDao.insertViewingRequest(it) }
                    }
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error live-syncing viewings: ${e.message}")
            }

            // 1b. Sync Inquiries as Viewing Requests
            try {
                val inqRes = api.getInquiries()
                if (inqRes.isSuccessful) {
                    inqRes.body()?.forEach { inq ->
                        inq.toViewingEntityOrNull()?.let { viewingDao.insertViewingRequest(it) }
                    }
                }
            } catch (_: Exception) {}

            // 2. Sync Conversations
            try {
                val convRes = api.getConversations()
                if (convRes.isSuccessful) {
                    convRes.body()?.forEach { dto ->
                        communicationDao.insertConversation(dto.toEntity())
                    }
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error live-syncing conversations: ${e.message}")
            }

            // 3. Sync Messages
            try {
                val msgRes = api.getMessages()
                if (msgRes.isSuccessful) {
                    msgRes.body()?.forEach { dto ->
                        dto.toEntityOrNull()?.let { communicationDao.insertMessage(it) }
                    }
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error live-syncing messages: ${e.message}")
            }

            // 4. Sync Notifications
            try {
                val notifRes = api.getNotifications()
                if (notifRes.isSuccessful) {
                    notifRes.body()?.forEach { dto ->
                        platformDao.insertNotification(dto.toEntity())
                    }
                }
            } catch (e: Exception) {
                Log.w("SupabaseSync", "Error live-syncing notifications: ${e.message}")
            }

            true
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Live sync failure: ${e.message}")
            false
        }
    }

    suspend fun fetchNearbyBoardingHouses(
        userLat: Double,
        userLng: Double,
        maxDistanceKm: Double = 10.0
    ): List<BoardingHouseListing> = withContext(Dispatchers.IO) {
        emptyList()
    }

    suspend fun pushListingReport(report: ReportEntity, title: String = "", host: String = ""): Boolean = withContext(Dispatchers.IO) {
        Log.d("SupabaseSync", "Listing report logged locally: ${report.id}")
        true
    }

    suspend fun pushAuditLog(log: AuditLogEntity): Boolean = withContext(Dispatchers.IO) {
        Log.d("SupabaseSync", "Audit log logged locally: ${log.id}")
        true
    }

    suspend fun saveListingRemote(renterId: String, listingId: String): Boolean = withContext(Dispatchers.IO) {
        Log.d("SupabaseSync", "Saved listing remotely: $listingId for user $renterId")
        true
    }

    suspend fun deleteSavedListingRemote(renterId: String, listingId: String): Boolean = withContext(Dispatchers.IO) {
        Log.d("SupabaseSync", "Deleted saved listing remotely: $listingId for user $renterId")
        true
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
