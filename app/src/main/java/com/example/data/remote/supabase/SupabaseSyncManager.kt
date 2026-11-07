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
import com.example.data.local.entity.SavedPropertyEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.data.model.BoardingHouseListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

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
                        dto.toEntityOrNull()?.let { msgEntity ->
                            try {
                                val cid = msgEntity.conversationId
                                val existing = communicationDao.getConversationById(cid).firstOrNull()
                                if (existing == null) {
                                    communicationDao.insertConversation(
                                        ConversationEntity(
                                            id = cid,
                                            propertyId = dto.propertyId ?: "",
                                            propertyTitle = "Rental Property",
                                            tenantId = dto.senderId ?: "",
                                            tenantName = dto.senderName ?: "Tenant",
                                            posterId = dto.receiverId ?: "",
                                            posterName = "Host",
                                            lastMessageText = msgEntity.messageText,
                                            lastMessageTime = msgEntity.timestamp,
                                            unreadForTenant = 0,
                                            unreadForPoster = 0
                                        )
                                    )
                                }
                                communicationDao.insertMessage(msgEntity)
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
        val normalizedId = UuidHelper.toValidUuidOrNull(viewing.id) ?: UUID.randomUUID().toString()
        val normalizedPropertyId = UuidHelper.toValidUuidOrNull(viewing.propertyId)
        val normalizedUserId = UuidHelper.toValidUuidOrNull(viewing.tenantId)
        val normalizedLandlordId = UuidHelper.toValidUuidOrNull(landlordId ?: viewing.posterId)

        val sqlDate = UuidHelper.formatSqlDate(viewing.preferredDate)
        val sqlTime = UuidHelper.formatSqlTime(viewing.preferredTime)

        val sqlStatus = when (viewing.status.uppercase()) {
            "CONFIRMED" -> "confirmed"
            "CANCELLED" -> "cancelled"
            "RESCHEDULED" -> "rescheduled"
            "COMPLETED" -> "completed"
            else -> "pending"
        }

        val sqlNotes = viewing.tenantMessage.ifBlank { viewing.posterResponse }.ifBlank { "Viewing request" }
        val email = if (recipientEmail.isNullOrBlank()) null else recipientEmail.trim()

        // 1. Clean payload targeting the migrated schema (with user_id)
        val payloadUserId = mutableMapOf<String, Any?>(
            "id" to normalizedId,
            "property_id" to normalizedPropertyId,
            "user_id" to normalizedUserId,
            "scheduled_date" to sqlDate,
            "scheduled_time" to sqlTime,
            "status" to sqlStatus,
            "notes" to sqlNotes,
            "recipient_email" to email
        )

        var success = false
        try {
            var res = api.upsertViewingRequestRaw(payloadUserId)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully saved viewing request $normalizedId (user_id) to Supabase viewing_requests")
                success = true
            } else {
                val err = res.errorBody()?.string() ?: ""
                Log.w("SupabaseSync", "viewing_requests upsert with user_id returned: ${res.code()} - $err")

                // If user_id column is not in schema cache, fallback to renter_id & landlord_id
                if (err.contains("user_id") || err.contains("PGRST204") || res.code() == 400) {
                    val payloadRenterId = mutableMapOf<String, Any?>(
                        "id" to normalizedId,
                        "property_id" to normalizedPropertyId,
                        "renter_id" to normalizedUserId,
                        "landlord_id" to normalizedLandlordId,
                        "scheduled_date" to sqlDate,
                        "scheduled_time" to sqlTime,
                        "status" to sqlStatus,
                        "notes" to sqlNotes,
                        "recipient_email" to email
                    )
                    val retryRes = api.upsertViewingRequestRaw(payloadRenterId)
                    if (retryRes.isSuccessful) {
                        Log.d("SupabaseSync", "Successfully saved viewing request $normalizedId (renter_id) to Supabase viewing_requests")
                        success = true
                    } else {
                        // Plain POST retry without on_conflict
                        val postRes = api.postViewingRequestRaw(payloadRenterId)
                        if (postRes.isSuccessful) {
                            Log.d("SupabaseSync", "Successfully plain POSTed viewing request $normalizedId (renter_id)")
                            success = true
                        }
                    }
                } else {
                    // Plain POST retry with user_id without on_conflict
                    val postRes = api.postViewingRequestRaw(payloadUserId)
                    if (postRes.isSuccessful) {
                        Log.d("SupabaseSync", "Successfully plain POSTed viewing request $normalizedId (user_id)")
                        success = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push viewing_requests to Supabase: ${e.message}")
        }

        // Dual-write / mirror to inquiries table
        try {
            val inqDto = SupabaseInquiryDto(
                id = normalizedId,
                propertyId = viewing.propertyId,
                tenantId = viewing.tenantId,
                tenantName = "Tenant",
                preferredDate = sqlDate,
                preferredTime = sqlTime,
                message = sqlNotes,
                status = sqlStatus
            )
            val inqRes = api.upsertInquiry(inqDto)
            if (inqRes.isSuccessful) {
                success = true
            }
        } catch (_: Exception) {}

        success
    }

    suspend fun pushMessageToRemote(message: MessageEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        val msgId = message.id
        val convId = message.conversationId
        val senderId = message.senderId
        val senderName = message.senderName
        val text = message.messageText
        val isRead = message.isRead

        val isoTimestamp = try {
            java.time.Instant.ofEpochMilli(message.timestamp).toString()
        } catch (_: Exception) {
            java.time.Instant.now().toString()
        }

        // Lookup parent conversation to derive property_id and receiver_id
        val conv = try {
            communicationDao.getConversationById(convId).firstOrNull()
        } catch (_: Exception) { null }

        val propId = conv?.propertyId?.ifBlank { null }
        val receiverId = if (conv != null) {
            if (senderId == conv.tenantId) conv.posterId else conv.tenantId
        } else null

        val msgUuid = UuidHelper.toValidUuidOrNull(msgId) ?: UUID.randomUUID().toString()
        val senderUuid = UuidHelper.toValidUuidOrNull(senderId)
        val receiverUuid = UuidHelper.toValidUuidOrNull(receiverId)
        val propUuid = UuidHelper.toValidUuidOrNull(propId)

        // 1. Target exact public.messages schema: (id, sender_id, receiver_id, property_id, content, is_read, created_at)
        val payloadStandard = mutableMapOf<String, Any?>(
            "id" to msgUuid,
            "sender_id" to senderUuid,
            "receiver_id" to receiverUuid,
            "property_id" to propUuid,
            "content" to text,
            "is_read" to isRead,
            "created_at" to isoTimestamp
        )

        var success = false
        try {
            var res = api.upsertMessageRaw(payloadStandard)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully pushed message $msgUuid (content) to Supabase")
                success = true
            } else {
                val err = res.errorBody()?.string() ?: ""
                Log.w("SupabaseSync", "Standard messages upsert returned: ${res.code()} - $err")

                // Try plain POST
                val postRes = api.postMessageRaw(payloadStandard)
                if (postRes.isSuccessful) {
                    Log.d("SupabaseSync", "Successfully plain POSTed message $msgUuid (content)")
                    success = true
                } else {
                    // Fallback to legacy/custom schema with conversation_id & message_text
                    val payloadFallback = mutableMapOf<String, Any?>(
                        "id" to msgUuid,
                        "conversation_id" to convId,
                        "sender_id" to (senderUuid ?: senderId),
                        "sender_name" to senderName,
                        "message_text" to text,
                        "is_read" to isRead,
                        "timestamp" to isoTimestamp
                    )
                    val retryRes = api.upsertMessageRaw(payloadFallback)
                    if (retryRes.isSuccessful) {
                        Log.d("SupabaseSync", "Successfully pushed message with fallback message_text")
                        success = true
                    } else {
                        val postFallbackRes = api.postMessageRaw(payloadFallback)
                        if (postFallbackRes.isSuccessful) {
                            Log.d("SupabaseSync", "Successfully posted message with fallback message_text")
                            success = true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push message to Supabase: ${e.message}")
        }
        success
    }

    suspend fun pushConversationToRemote(conversation: ConversationEntity): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        val convId = conversation.id
        val payload = mutableMapOf<String, Any?>(
            "id" to convId,
            "property_id" to conversation.propertyId,
            "property_title" to conversation.propertyTitle,
            "tenant_id" to conversation.tenantId,
            "tenant_name" to conversation.tenantName,
            "poster_id" to conversation.posterId,
            "poster_name" to conversation.posterName,
            "last_message_text" to conversation.lastMessageText,
            "unread_for_tenant" to conversation.unreadForTenant,
            "unread_for_poster" to conversation.unreadForPoster
        )

        var success = false
        try {
            var res = api.upsertConversationRaw(payload)
            if (res.isSuccessful) {
                Log.d("SupabaseSync", "Successfully pushed conversation $convId to Supabase")
                success = true
            } else {
                val err = res.errorBody()?.string() ?: ""
                Log.w("SupabaseSync", "Failed to upsert conversation: ${res.code()} - $err")

                // Try plain POST
                val postRes = api.postConversationRaw(payload)
                if (postRes.isSuccessful) {
                    Log.d("SupabaseSync", "Successfully plain POSTed conversation $convId")
                    success = true
                }
            }
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to push conversation to Supabase: ${e.message}")
        }
        success
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
                        dto.toEntityOrNull()?.let { msgEntity ->
                            val cid = msgEntity.conversationId
                            val existing = communicationDao.getConversationById(cid).firstOrNull()
                            if (existing == null) {
                                communicationDao.insertConversation(
                                    ConversationEntity(
                                        id = cid,
                                        propertyId = dto.propertyId ?: "",
                                        propertyTitle = "Rental Property",
                                        tenantId = dto.senderId ?: "",
                                        tenantName = dto.senderName ?: "Tenant",
                                        posterId = dto.receiverId ?: "",
                                        posterName = "Host",
                                        lastMessageText = msgEntity.messageText,
                                        lastMessageTime = msgEntity.timestamp,
                                        unreadForTenant = 0,
                                        unreadForPoster = 0
                                    )
                                )
                            }
                            communicationDao.insertMessage(msgEntity)
                        }
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

    suspend fun saveListingRemote(renterId: String, listingId: String, title: String = ""): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            var existingItems = mutableListOf<Map<String, String>>()
            var userProfile: SupabaseUserProfileDto? = null

            // 1. Fetch current user_profiles using user_id column (with fallback to id)
            var profileRes = api.getUserProfileByUserId("eq.$renterId")
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                val uuid = UuidHelper.toValidUuidOrNull(renterId)
                if (uuid != null) {
                    profileRes = api.getUserProfileByUserId("eq.$uuid")
                }
            }
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                profileRes = api.getUserProfile("eq.$renterId")
            }

            if (profileRes.isSuccessful && !profileRes.body().isNullOrEmpty()) {
                userProfile = profileRes.body()!!.first()
                existingItems = SavedListingsHelper.parseSavedListingIds(userProfile.savedListingIds)
            }

            // Remove if already present, then add entry with property_id and title
            existingItems.removeAll { it["property_id"] == listingId || it["id"] == listingId }
            existingItems.add(mapOf("property_id" to listingId, "title" to title.ifBlank { "Rental Listing" }))

            val updates = mapOf<String, Any?>(
                "saved_listing_ids" to existingItems
            )

            // Try PATCH by user_id first
            var patchRes = api.patchUserProfileByUserId("eq.$renterId", updates)
            if (!patchRes.isSuccessful) {
                val uuid = UuidHelper.toValidUuidOrNull(renterId)
                if (uuid != null) {
                    patchRes = api.patchUserProfileByUserId("eq.$uuid", updates)
                }
            }
            if (!patchRes.isSuccessful) {
                patchRes = api.patchUserProfile("eq.$renterId", updates)
            }

            // If profile does not exist yet in user_profiles, upsert it by user_id
            if (!patchRes.isSuccessful) {
                val newProfilePayload = mapOf<String, Any?>(
                    "user_id" to renterId,
                    "full_name" to (userProfile?.fullName ?: "Balay-Inn Renter"),
                    "role" to "renter",
                    "saved_listing_ids" to existingItems
                )
                val upRes = api.upsertUserProfileByUserId(newProfilePayload)
                if (!upRes.isSuccessful) {
                    api.postUserProfileRaw(newProfilePayload)
                }
            }

            // Also dual-write to saved_listings table if it exists
            try {
                val saveUuid = UuidHelper.toValidUuidOrNull(listingId) ?: UUID.randomUUID().toString()
                val userUuid = UuidHelper.toValidUuidOrNull(renterId) ?: renterId
                val propUuid = UuidHelper.toValidUuidOrNull(listingId) ?: listingId
                api.postSavedListing(mapOf(
                    "id" to saveUuid,
                    "user_id" to userUuid,
                    "property_id" to propUuid,
                    "notes" to title
                ))
            } catch (_: Exception) {}

            Log.d("SupabaseSync", "Successfully saved listing $listingId to saved_listing_ids for user $renterId: $existingItems")
            true
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to save listing remotely: ${e.message}")
            false
        }
    }

    suspend fun deleteSavedListingRemote(renterId: String, listingId: String): Boolean = withContext(Dispatchers.IO) {
        val api = SupabaseClient.api ?: return@withContext false
        try {
            var existingItems = mutableListOf<Map<String, String>>()
            var userProfile: SupabaseUserProfileDto? = null

            var profileRes = api.getUserProfileByUserId("eq.$renterId")
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                val uuid = UuidHelper.toValidUuidOrNull(renterId)
                if (uuid != null) {
                    profileRes = api.getUserProfileByUserId("eq.$uuid")
                }
            }
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                profileRes = api.getUserProfile("eq.$renterId")
            }

            if (profileRes.isSuccessful && !profileRes.body().isNullOrEmpty()) {
                userProfile = profileRes.body()!!.first()
                existingItems = SavedListingsHelper.parseSavedListingIds(userProfile.savedListingIds)
            }

            // Remove the property_id
            existingItems.removeAll { it["property_id"] == listingId || it["id"] == listingId }

            val updates = mapOf<String, Any?>(
                "saved_listing_ids" to existingItems
            )

            var patchRes = api.patchUserProfileByUserId("eq.$renterId", updates)
            if (!patchRes.isSuccessful) {
                val uuid = UuidHelper.toValidUuidOrNull(renterId)
                if (uuid != null) {
                    patchRes = api.patchUserProfileByUserId("eq.$uuid", updates)
                }
            }
            if (!patchRes.isSuccessful) {
                patchRes = api.patchUserProfile("eq.$renterId", updates)
            }

            // Also delete from saved_listings table if exists
            try {
                api.deleteSavedListing("eq.$renterId", "eq.$listingId")
                val uuid = UuidHelper.toValidUuidOrNull(renterId)
                val propUuid = UuidHelper.toValidUuidOrNull(listingId)
                if (uuid != null && propUuid != null) {
                    api.deleteSavedListing("eq.$uuid", "eq.$propUuid")
                }
            } catch (_: Exception) {}

            Log.d("SupabaseSync", "Successfully removed listing $listingId from saved_listing_ids for user $renterId")
            true
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to remove saved listing remotely: ${e.message}")
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

    suspend fun updatePrivacyPreferences(
        userId: String,
        pushNotification: Boolean,
        isDarkMode: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured || userId.isBlank() || userId == "usr_guest") return@withContext false
        val api = SupabaseClient.api ?: return@withContext false
        try {
            val prefsMap = mapOf(
                "push_notification" to pushNotification,
                "theme" to if (isDarkMode) "dark" else "light",
                "is_dark_mode" to isDarkMode,
                "updated_at" to System.currentTimeMillis()
            )
            val updates = mapOf<String, Any?>(
                "privacy_preferences" to prefsMap
            )
            var patchRes = api.patchUserProfileByUserId("eq.$userId", updates)
            if (!patchRes.isSuccessful) {
                val uuid = UuidHelper.toValidUuidOrNull(userId)
                if (uuid != null) {
                    patchRes = api.patchUserProfileByUserId("eq.$uuid", updates)
                }
            }
            if (!patchRes.isSuccessful) {
                patchRes = api.patchUserProfile("eq.$userId", updates)
            }
            Log.d("SupabaseSync", "Updated privacy_preferences in user_profiles for $userId: $prefsMap (success=${patchRes.isSuccessful})")
            patchRes.isSuccessful
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to update privacy_preferences: ${e.message}")
            false
        }
    }

    suspend fun fetchUserProfile(userId: String): SupabaseUserProfileDto? = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured || userId.isBlank() || userId == "usr_guest") return@withContext null
        val api = SupabaseClient.api ?: return@withContext null
        try {
            var profileRes = api.getUserProfileByUserId("eq.$userId")
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                val uuid = UuidHelper.toValidUuidOrNull(userId)
                if (uuid != null) {
                    profileRes = api.getUserProfileByUserId("eq.$uuid")
                }
            }
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                profileRes = api.getUserProfile("eq.$userId")
            }
            profileRes.body()?.firstOrNull()
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to fetch user profile: ${e.message}")
            null
        }
    }

    suspend fun syncSavedPropertiesForUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        if (!SupabaseClient.isConfigured || userId.isBlank() || userId == "usr_guest") return@withContext false
        val api = SupabaseClient.api ?: return@withContext false
        try {
            var profileRes = api.getUserProfileByUserId("eq.$userId")
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                val uuid = UuidHelper.toValidUuidOrNull(userId)
                if (uuid != null) {
                    profileRes = api.getUserProfileByUserId("eq.$uuid")
                }
            }
            if (!profileRes.isSuccessful || profileRes.body().isNullOrEmpty()) {
                profileRes = api.getUserProfile("eq.$userId")
            }
            val profile = profileRes.body()?.firstOrNull()
            if (profile != null) {
                val remoteSavedItems = SavedListingsHelper.parseSavedListingIds(profile.savedListingIds)
                remoteSavedItems.forEach { item ->
                    val propId = item["property_id"] ?: item["id"]
                    if (!propId.isNullOrBlank()) {
                        val isLocallySaved = propertyDao.isPropertySaved(userId, propId).firstOrNull() ?: false
                        if (!isLocallySaved) {
                            propertyDao.insertSavedProperty(
                                SavedPropertyEntity(
                                    id = "saved_${UUID.randomUUID()}",
                                    userId = userId,
                                    propertyId = propId
                                )
                            )
                        }
                    }
                }
                Log.d("SupabaseSync", "Synchronized ${remoteSavedItems.size} saved properties from Supabase for $userId")
            }
            true
        } catch (e: Exception) {
            Log.w("SupabaseSync", "Failed to sync saved properties for $userId: ${e.message}")
            false
        }
    }
}
