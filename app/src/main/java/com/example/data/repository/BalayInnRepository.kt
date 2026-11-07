package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.totalAvailableBeds
import com.example.data.local.entity.PropertyImageEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedPropertyEntity
import com.example.data.local.entity.SavedSearchEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.data.remote.gmail.GmailNotificationService
import com.example.data.remote.supabase.SupabaseSyncManager
import com.example.data.remote.supabase.SupabaseUserProfileDto
import com.example.data.seed.MarketplaceDataSeed
import com.example.data.seed.PropertyDataSeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

data class PropertyMatchResult(
    val scorePercentage: Int,
    val positiveReasons: List<String>,
    val warningReasons: List<String>
)

class BalayInnRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val geoDao = db.geographicDao()
    private val propDao = db.propertyDao()
    private val userDao = db.userDao()
    private val viewDao = db.viewingDao()
    private val commDao = db.communicationDao()
    private val platDao = db.platformDao()

    val supabaseSyncManager = SupabaseSyncManager(propDao, viewDao, commDao, platDao)
    val gmailNotificationService = GmailNotificationService(context)

    // Current logged-in user state (defaults to Tenant Joshua Alcantara)
    private val _currentUser = MutableStateFlow<UserEntity>(MarketplaceDataSeed.seedUsers.first())
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    suspend fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val count = geoDao.getCountryCount()
            if (count == 0) {
                // Seed Geographic Hierarchy
                geoDao.insertCountries(listOf(MarketplaceDataSeed.country))
                geoDao.insertRegions(MarketplaceDataSeed.seedRegions)
                geoDao.insertProvinces(MarketplaceDataSeed.provinces)
                geoDao.insertCities(MarketplaceDataSeed.allCities)
                geoDao.insertBarangays(MarketplaceDataSeed.generateBarangays())

                // Seed Predefined Users (Admin, Host, Renter)
                userDao.insertUsers(MarketplaceDataSeed.seedUsers)

                // Seed Conversations & Messages
                PropertyDataSeed.sampleConversations.forEach { commDao.insertConversation(it) }
                PropertyDataSeed.sampleMessages.forEach { commDao.insertMessage(it) }

                // Seed Platform data
                PropertyDataSeed.sampleRequirements.forEach { platDao.insertRequirement(it) }
                PropertyDataSeed.sampleSavedSearches.forEach { platDao.insertSavedSearch(it) }
                PropertyDataSeed.sampleNotifications.forEach { platDao.insertNotification(it) }
                PropertyDataSeed.sampleReports.forEach { platDao.insertReport(it) }
                PropertyDataSeed.sampleDocuments.forEach { platDao.insertDocument(it) }

                // Log initialization
                platDao.insertAuditLog(
                    AuditLogEntity(
                        id = UUID.randomUUID().toString(),
                        adminUserId = "usr_admin_1",
                        actionType = "INITIAL_SEED",
                        entityType = "DATABASE",
                        entityId = "ALL",
                        notes = "Balay-Inn database initialized for live cloud-backed operations."
                    )
                )
            }
            try {
                geoDao.insertRegions(MarketplaceDataSeed.seedRegions)
                geoDao.insertProvinces(MarketplaceDataSeed.provinces)
                geoDao.insertCities(MarketplaceDataSeed.allCities)
                geoDao.insertBarangays(MarketplaceDataSeed.generateBarangays())

                // Clean up any legacy mock/demo properties so only live cloud properties exist
                val mockPropertyIds = PropertyDataSeed.sampleProperties.map { it.id }
                mockPropertyIds.forEach { mockId ->
                    propDao.deleteProperty(mockId)
                }
            } catch (_: Exception) {}

            // Attempt background synchronization with Supabase if configured
            try {
                supabaseSyncManager.syncAll()
            } catch (_: Exception) {}
        } catch (e: Exception) {
            android.util.Log.e("BalayInnRepo", "Database initialization encountered an error: ${e.message}", e)
        }
    }

    suspend fun syncWithCloud(activeUserId: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = activeUserId ?: _currentUser.value.id
            if (user.isNotBlank() && user != "usr_guest") {
                supabaseSyncManager.syncSavedPropertiesForUser(user)
            }
            supabaseSyncManager.syncAll()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun syncLiveUpdates(activeUserId: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            val user = activeUserId ?: _currentUser.value.id
            if (user.isNotBlank() && user != "usr_guest") {
                supabaseSyncManager.syncSavedPropertiesForUser(user)
            }
            supabaseSyncManager.syncLiveUpdates()
        } catch (_: Exception) {
            false
        }
    }

    suspend fun updatePrivacyPreferences(userId: String, pushNotification: Boolean, isDarkMode: Boolean): Boolean = withContext(Dispatchers.IO) {
        supabaseSyncManager.updatePrivacyPreferences(userId, pushNotification, isDarkMode)
    }

    suspend fun fetchUserProfile(userId: String): SupabaseUserProfileDto? = withContext(Dispatchers.IO) {
        supabaseSyncManager.fetchUserProfile(userId)
    }

    suspend fun syncSavedPropertiesForUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        supabaseSyncManager.syncSavedPropertiesForUser(userId)
    }

    // Geographic Queries
    fun getActiveCountries() = geoDao.getActiveCountries()
    fun getRegions(countryId: String) = geoDao.getRegionsByCountry(countryId)
    fun getAllActiveRegions() = geoDao.getAllActiveRegions()
    fun getProvinces(regionId: String) = geoDao.getProvincesByRegion(regionId)
    fun getAllActiveProvinces() = geoDao.getAllActiveProvinces()
    fun getCities(provinceId: String) = geoDao.getCitiesByProvince(provinceId)
    fun getAllActiveCities() = geoDao.getAllActiveCities()
    fun getBarangays(cityId: String) = geoDao.getBarangaysByCity(cityId)
    fun getAllActiveBarangays() = geoDao.getAllActiveBarangays()

    // Location Admin Actions
    suspend fun addProvince(name: String, code: String, regionId: String) = withContext(Dispatchers.IO) {
        val newProv = ProvinceEntity(
            id = "prov_${code.lowercase()}_${System.currentTimeMillis() % 10000}",
            regionId = regionId,
            name = name,
            code = code,
            isActive = true
        )
        geoDao.insertProvince(newProv)
        logAudit("ADD_PROVINCE", "PROVINCE", newProv.id, "Added province: $name")
    }

    suspend fun addCity(name: String, type: String, code: String, provinceId: String) = withContext(Dispatchers.IO) {
        val newCity = CityMunicipalityEntity(
            id = "city_${code.lowercase()}_${System.currentTimeMillis() % 10000}",
            provinceId = provinceId,
            name = name,
            type = type,
            code = code,
            isActive = true
        )
        geoDao.insertCity(newCity)
        logAudit("ADD_CITY", "CITY_MUNICIPALITY", newCity.id, "Added $type: $name")
    }

    suspend fun addBarangay(name: String, code: String, cityId: String) = withContext(Dispatchers.IO) {
        val newBgy = BarangayEntity(
            id = "bgy_${code.lowercase()}_${System.currentTimeMillis() % 10000}",
            cityMunicipalityId = cityId,
            name = name,
            code = code,
            isActive = true
        )
        geoDao.insertBarangay(newBgy)
        logAudit("ADD_BARANGAY", "BARANGAY", newBgy.id, "Added barangay: $name")
    }

    suspend fun toggleProvinceActive(province: ProvinceEntity) = withContext(Dispatchers.IO) {
        val updated = province.copy(isActive = !province.isActive)
        geoDao.updateProvince(updated)
        logAudit("TOGGLE_ACTIVE", "PROVINCE", province.id, "Set is_active = ${updated.isActive}")
    }

    suspend fun toggleCityActive(city: CityMunicipalityEntity) = withContext(Dispatchers.IO) {
        val updated = city.copy(isActive = !city.isActive)
        geoDao.updateCity(updated)
        logAudit("TOGGLE_ACTIVE", "CITY_MUNICIPALITY", city.id, "Set is_active = ${updated.isActive}")
    }

    // Properties
    fun getAllActiveProperties(): Flow<List<PropertyEntity>> = propDao.getAllActiveProperties()
    fun getAllProperties(): Flow<List<PropertyEntity>> = propDao.getAllProperties()
    fun getPropertyById(propertyId: String): Flow<PropertyEntity?> = propDao.getPropertyById(propertyId)
    suspend fun getPropertyByIdSync(propertyId: String): PropertyEntity? = propDao.getPropertyByIdSync(propertyId)
    fun getPropertiesByPoster(posterId: String): Flow<List<PropertyEntity>> = propDao.getPropertiesByPoster(posterId)
    fun getPropertiesByOwner(ownerId: String): Flow<List<PropertyEntity>> = propDao.getPropertiesByOwner(ownerId)
    fun getImagesForProperty(propertyId: String): Flow<List<PropertyImageEntity>> = propDao.getImagesForProperty(propertyId)
    fun getAllPropertyImages(): Flow<List<PropertyImageEntity>> = propDao.getAllPropertyImages()

    suspend fun canUserManageProperty(user: UserEntity, propertyId: String): Boolean {
        if (user.canonicalRole == com.example.data.model.UserRole.ADMIN) return true
        val prop = propDao.getPropertyByIdSync(propertyId) ?: return false
        return when (user.canonicalRole) {
            com.example.data.model.UserRole.LANDLORD -> prop.ownerId == user.id
            com.example.data.model.UserRole.REPRESENTATIVE -> prop.posterId == user.id && prop.isPosterAuthorized
            else -> false
        }
    }

    suspend fun updatePropertyStatus(propertyId: String, newStatus: String, requestedBy: UserEntity? = null) = withContext(Dispatchers.IO) {
        if (requestedBy != null && !canUserManageProperty(requestedBy, propertyId)) {
            throw SecurityException("User ${requestedBy.fullName} (${requestedBy.role}) is not authorized to modify property $propertyId")
        }
        val existing = propDao.getPropertyByIdSync(propertyId) ?: return@withContext
        val updated = existing.copy(status = newStatus, updatedAt = System.currentTimeMillis())
        propDao.updateProperty(updated)
        logAudit("UPDATE_STATUS", "PROPERTY", propertyId, "Status updated to $newStatus by ${requestedBy?.fullName ?: "System"}")

        val isFullOrUnavailable = newStatus.uppercase() in listOf("OCCUPIED", "FULL", "RENTED", "PAUSED", "UNAVAILABLE") || updated.totalAvailableBeds <= 0
        if (isFullOrUnavailable) {
            val hostId = updated.ownerId.ifBlank { updated.posterId }
            notifyPropertyFull(propertyId, hostId, updated.title)
            try {
                val savedRenters = propDao.getRenterIdsWhoSavedProperty(propertyId)
                savedRenters.forEach { renterId ->
                    notifySavedPropertyUnavailable(renterId, propertyId, updated.title)
                }
            } catch (_: Exception) {}
        }
    }

    suspend fun updateProperty(property: PropertyEntity, requestedBy: UserEntity? = null) = withContext(Dispatchers.IO) {
        if (requestedBy != null && !canUserManageProperty(requestedBy, property.id)) {
            throw SecurityException("User ${requestedBy.fullName} (${requestedBy.role}) is not authorized to modify property ${property.id}")
        }
        val updated = property.copy(updatedAt = System.currentTimeMillis())
        propDao.updateProperty(updated)
        logAudit("UPDATE_PROPERTY", "PROPERTY", property.id, "Property updated: ${property.title} by ${requestedBy?.fullName ?: "System"}")
        try {
            val images = propDao.getImagesForPropertySync(property.id)
            supabaseSyncManager.pushPropertyToRemote(updated, images)
        } catch (_: Exception) {}

        val isFullOrUnavailable = updated.status.uppercase() in listOf("OCCUPIED", "FULL", "RENTED", "PAUSED", "UNAVAILABLE") || updated.totalAvailableBeds <= 0
        if (isFullOrUnavailable) {
            val hostId = updated.ownerId.ifBlank { updated.posterId }
            notifyPropertyFull(property.id, hostId, updated.title)
            try {
                val savedRenters = propDao.getRenterIdsWhoSavedProperty(property.id)
                savedRenters.forEach { renterId ->
                    notifySavedPropertyUnavailable(renterId, property.id, updated.title)
                }
            } catch (_: Exception) {}
        }
    }

    suspend fun deleteProperty(propertyId: String) = withContext(Dispatchers.IO) {
        propDao.deleteProperty(propertyId)
        logAudit("DELETE_PROPERTY", "PROPERTY", propertyId, "Property deleted: $propertyId")
    }

    suspend fun createProperty(property: PropertyEntity, images: List<PropertyImageEntity>, creator: UserEntity? = null) = withContext(Dispatchers.IO) {
        if (creator != null && !creator.canonicalRole.canPostProperty) {
            throw SecurityException("Tenants are not authorized to post properties.")
        }
        propDao.insertProperty(property)
        propDao.insertImages(images)
        logAudit("CREATE_PROPERTY", "PROPERTY", property.id, "Property created: ${property.title} by ${property.posterName}")
        try {
            supabaseSyncManager.pushPropertyToRemote(property, images)
        } catch (_: Exception) {}
    }

    // Favorites / Saved
    fun getSavedProperties(userId: String) = propDao.getSavedProperties(userId)
    fun isPropertySaved(userId: String, propertyId: String) = propDao.isPropertySaved(userId, propertyId)
    suspend fun toggleSaveProperty(userId: String, propertyId: String) = withContext(Dispatchers.IO) {
        val isSaved = propDao.isPropertySaved(userId, propertyId).firstOrNull() ?: false
        val property = propDao.getPropertyById(propertyId).firstOrNull()
            ?: com.example.data.seed.PropertyDataSeed.sampleProperties.find { it.id == propertyId }
        val title = property?.title ?: "Rental Listing"

        if (isSaved) {
            propDao.deleteSavedProperty(userId, propertyId)
            try {
                supabaseSyncManager.deleteSavedListingRemote(userId, propertyId)
            } catch (_: Exception) {}
        } else {
            propDao.insertSavedProperty(
                SavedPropertyEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    propertyId = propertyId
                )
            )
            try {
                supabaseSyncManager.saveListingRemote(userId, propertyId, title)
            } catch (_: Exception) {}
        }
    }

    // Viewings
    fun getViewingsForTenant(tenantId: String) = viewDao.getViewingsForTenant(tenantId)
    fun getViewingsForPoster(posterId: String) = viewDao.getViewingsForPoster(posterId)
    fun getViewingsForHost(hostId: String) = viewDao.getViewingsForHost(hostId)
    fun getViewingsForOwner(ownerId: String) = viewDao.getViewingsForOwner(ownerId)
    fun getAllViewings() = viewDao.getAllViewings()

    // Saved Searches
    fun getSavedSearches(userId: String) = platDao.getSavedSearches(userId)

    suspend fun createViewingRequest(
        propertyId: String,
        tenantId: String,
        posterId: String,
        date: String,
        time: String,
        visitors: Int,
        message: String,
        recipientEmail: String? = null
    ) = withContext(Dispatchers.IO) {
        val id = "view_${System.currentTimeMillis()}"
        val req = ViewingRequestEntity(
            id = id,
            propertyId = propertyId,
            tenantId = tenantId,
            posterId = posterId,
            preferredDate = date,
            preferredTime = time,
            numberOfVisitors = visitors,
            tenantMessage = message,
            status = "REQUESTED"
        )
        viewDao.insertViewingRequest(req)
        try {
            supabaseSyncManager.pushViewingRequestToRemote(
                viewing = req,
                recipientEmail = recipientEmail,
                landlordId = posterId
            )
        } catch (_: Exception) {}

        val prop = propDao.getPropertyById(propertyId).firstOrNull()
        val propTitle = prop?.title ?: "Rental Listing"
        val noteSuffix = if (message.isNotBlank()) " Note: \"$message\"." else ""

        // 1. Live notification to Host with full viewing request details
        val posterNotif = NotificationEntity(
            id = "notif_${UUID.randomUUID()}",
            userId = posterId,
            title = "New Viewing Request: $propTitle",
            message = "Viewing request available for $propTitle on $date at $time for $visitors visitor(s).$noteSuffix",
            category = "VIEWING",
            relatedPropertyId = propertyId
        )
        platDao.insertNotification(posterNotif)
        try {
            supabaseSyncManager.pushNotificationToRemote(posterNotif)
        } catch (_: Exception) {}

        // If property owner is separate, notify them as well
        if (prop != null && prop.ownerId.isNotBlank() && prop.ownerId != posterId) {
            val ownerNotif = posterNotif.copy(id = "notif_${UUID.randomUUID()}", userId = prop.ownerId)
            platDao.insertNotification(ownerNotif)
            try {
                supabaseSyncManager.pushNotificationToRemote(ownerNotif)
            } catch (_: Exception) {}
        }

        // 2. Live confirmation notification to Renter (Tenant)
        val tenantNotif = NotificationEntity(
            id = "notif_${UUID.randomUUID()}",
            userId = tenantId,
            title = "Viewing Request Sent: $propTitle",
            message = "Your viewing request for $propTitle was submitted for $date at $time ($visitors visitor(s)). Awaiting host confirmation.",
            category = "VIEWING",
            relatedPropertyId = propertyId
        )
        platDao.insertNotification(tenantNotif)
        try {
            supabaseSyncManager.pushNotificationToRemote(tenantNotif)
        } catch (_: Exception) {}

        // 3. Create conversation & in-app message with contact details so it appears in Messages on both Renter & Host dashboards
        try {
            val tenantUser = userDao.getUserByIdSync(tenantId)
            val tenantName = tenantUser?.fullName?.ifBlank { null } ?: "Renter"
            val hostName = prop?.posterName?.ifBlank { null } ?: "Host/Caretaker"
            val contactInfo = listOfNotNull(
                tenantUser?.phone?.takeIf { it.isNotBlank() }?.let { "📱 Phone: $it" },
                tenantUser?.email?.takeIf { it.isNotBlank() }?.let { "✉️ Email: $it" }
            ).joinToString(" | ")

            val chatMessageContent = buildString {
                append("📅 [Property Viewing Request: $propTitle]\n")
                append("Scheduled: $date at $time ($visitors visitor${if (visitors > 1) "s" else ""})")
                if (message.isNotBlank()) {
                    append("\nRenter Message: \"$message\"")
                }
                if (contactInfo.isNotBlank()) {
                    append("\nContact Details: $contactInfo")
                }
            }

            val propertyOwner = prop?.ownerId?.takeIf { it.isNotBlank() } ?: posterId
            startOrGetConversation(
                propertyId = propertyId,
                propertyTitle = propTitle,
                tenantId = tenantId,
                tenantName = tenantName,
                posterId = propertyOwner,
                posterName = hostName,
                initialMessage = chatMessageContent
            )
        } catch (_: Exception) {}
    }

    suspend fun updateViewingStatus(
        viewing: ViewingRequestEntity,
        newStatus: String,
        responseNote: String = "",
        recipientEmail: String? = null
    ) = withContext(Dispatchers.IO) {
        val updated = viewing.copy(
            status = newStatus,
            posterResponse = responseNote.ifBlank { viewing.posterResponse },
            updatedAt = System.currentTimeMillis()
        )
        viewDao.updateViewingRequest(updated)
        try {
            supabaseSyncManager.pushViewingRequestToRemote(
                viewing = updated,
                recipientEmail = recipientEmail,
                landlordId = viewing.posterId
            )
        } catch (_: Exception) {}

        val prop = propDao.getPropertyById(viewing.propertyId).firstOrNull()
        val propTitle = prop?.title ?: "Rental Listing"

        if (newStatus == "CANCELLED") {
            // Host notification when renter cancels viewing request or transaction
            val hostCancelNotif = NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = viewing.posterId,
                title = "Viewing Request Cancelled",
                message = "The viewing request for '$propTitle' on ${viewing.preferredDate} (${viewing.preferredTime}) was cancelled by the renter.${if (responseNote.isNotBlank()) " Reason: $responseNote" else ""}",
                category = "VIEWING",
                relatedPropertyId = viewing.propertyId
            )
            platDao.insertNotification(hostCancelNotif)
            try {
                supabaseSyncManager.pushNotificationToRemote(hostCancelNotif)
            } catch (_: Exception) {}

            if (prop != null && prop.ownerId.isNotBlank() && prop.ownerId != viewing.posterId) {
                val ownerCancelNotif = hostCancelNotif.copy(id = "notif_${UUID.randomUUID()}", userId = prop.ownerId)
                platDao.insertNotification(ownerCancelNotif)
                try {
                    supabaseSyncManager.pushNotificationToRemote(ownerCancelNotif)
                } catch (_: Exception) {}
            }

            // Tenant confirmation
            val tenantCancelNotif = NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = viewing.tenantId,
                title = "Viewing Cancelled",
                message = "Your viewing appointment for '$propTitle' on ${viewing.preferredDate} (${viewing.preferredTime}) has been cancelled.",
                category = "VIEWING",
                relatedPropertyId = viewing.propertyId
            )
            platDao.insertNotification(tenantCancelNotif)
            try {
                supabaseSyncManager.pushNotificationToRemote(tenantCancelNotif)
            } catch (_: Exception) {}
        } else {
            // 1. Live notification to Tenant (Host replied / confirmed / rescheduled)
            val tenantNotif = NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = viewing.tenantId,
                title = "Host Response: $propTitle",
                message = "Host response for '$propTitle': \"${responseNote.ifBlank { "Appointment $newStatus" }}\" (Status: $newStatus for ${viewing.preferredDate} at ${viewing.preferredTime}).",
                category = "VIEWING",
                relatedPropertyId = viewing.propertyId
            )
            platDao.insertNotification(tenantNotif)
            try {
                supabaseSyncManager.pushNotificationToRemote(tenantNotif)
            } catch (_: Exception) {}

            // 2. Live confirmation to Host
            val hostNotif = NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = viewing.posterId,
                title = "Viewing Marked $newStatus",
                message = "Viewing for '$propTitle' on ${viewing.preferredDate} (${viewing.preferredTime}) marked as $newStatus.",
                category = "VIEWING",
                relatedPropertyId = viewing.propertyId
            )
            platDao.insertNotification(hostNotif)
            try {
                supabaseSyncManager.pushNotificationToRemote(hostNotif)
            } catch (_: Exception) {}
        }
    }

    // Conversations & Messages
    fun getConversations(userId: String) = commDao.getConversationsForUser(userId)
    fun getMessages(conversationId: String) = commDao.getMessagesForConversation(conversationId)

    suspend fun sendMessage(conversationId: String, senderId: String, senderName: String, text: String) = withContext(Dispatchers.IO) {
        val msg = MessageEntity(
            id = "msg_${System.currentTimeMillis()}",
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            messageText = text,
            timestamp = System.currentTimeMillis()
        )
        // 1. Update conversation last message & unread counters in local DB & Supabase FIRST
        val conv = commDao.getConversationById(conversationId).firstOrNull()
        if (conv != null) {
            val isSenderTenant = senderId == conv.tenantId
            val updatedConv = conv.copy(
                lastMessageText = text,
                lastMessageTime = System.currentTimeMillis(),
                unreadForTenant = if (isSenderTenant) conv.unreadForTenant else conv.unreadForTenant + 1,
                unreadForPoster = if (!isSenderTenant) conv.unreadForPoster else conv.unreadForPoster + 1
            )
            commDao.updateConversation(updatedConv)
            try {
                supabaseSyncManager.pushConversationToRemote(updatedConv)
            } catch (_: Exception) {}

            // Send live notification to recipient: if host replied, notify renter
            val recipientId = if (isSenderTenant) conv.posterId else conv.tenantId
            val notifTitle = if (isSenderTenant) "New Inquiry from $senderName" else "Host Replied: $senderName"
            val notif = NotificationEntity(
                id = "notif_${UUID.randomUUID()}",
                userId = recipientId,
                title = notifTitle,
                message = "Re: ${conv.propertyTitle} — \"$text\"",
                category = "MESSAGE",
                relatedPropertyId = conv.propertyId
            )
            platDao.insertNotification(notif)
            try {
                supabaseSyncManager.pushNotificationToRemote(notif)
            } catch (_: Exception) {}
        }

        // 2. Insert message and push to Supabase
        commDao.insertMessage(msg)
        try {
            supabaseSyncManager.pushMessageToRemote(msg)
        } catch (_: Exception) {}
    }

    suspend fun startOrGetConversation(
        propertyId: String,
        propertyTitle: String,
        tenantId: String,
        tenantName: String,
        posterId: String,
        posterName: String,
        initialMessage: String
    ): String = withContext(Dispatchers.IO) {
        val convId = "conv_${tenantId}_${propertyId}"
        val existing = commDao.getConversationById(convId).firstOrNull()
        if (existing == null) {
            val conv = ConversationEntity(
                id = convId,
                propertyId = propertyId,
                propertyTitle = propertyTitle,
                tenantId = tenantId,
                tenantName = tenantName,
                posterId = posterId,
                posterName = posterName,
                lastMessageText = initialMessage,
                lastMessageTime = System.currentTimeMillis()
            )
            commDao.insertConversation(conv)
            try {
                supabaseSyncManager.pushConversationToRemote(conv)
            } catch (_: Exception) {}
            if (initialMessage.isNotBlank()) {
                sendMessage(convId, tenantId, tenantName, initialMessage)
            }
        } else if (initialMessage.isNotBlank()) {
            sendMessage(convId, tenantId, tenantName, initialMessage)
        }
        convId
    }

    // Notifications
    fun getNotifications(userId: String) = platDao.getNotificationsForUser(userId)
    suspend fun markNotificationRead(id: String) = withContext(Dispatchers.IO) {
        platDao.markNotificationAsRead(id)
    }

    suspend fun insertNotification(notification: NotificationEntity) = withContext(Dispatchers.IO) {
        platDao.insertNotification(notification)
        try {
            supabaseSyncManager.pushNotificationToRemote(notification)
        } catch (_: Exception) {}
    }

    suspend fun notifyPropertyFull(propertyId: String, hostId: String, title: String) = withContext(Dispatchers.IO) {
        val notif = NotificationEntity(
            id = "notif_full_${propertyId}_${System.currentTimeMillis()}",
            userId = hostId,
            title = "Property Full: $title",
            message = "Your property '$title' has reached full occupancy (0 available beds). The listing is now marked as occupied.",
            category = "STATUS",
            relatedPropertyId = propertyId
        )
        platDao.insertNotification(notif)
        try {
            supabaseSyncManager.pushNotificationToRemote(notif)
        } catch (_: Exception) {}
    }

    suspend fun notifyAccountVerified(userId: String, isHost: Boolean) = withContext(Dispatchers.IO) {
        val notif = NotificationEntity(
            id = "notif_verified_${userId}_${System.currentTimeMillis()}",
            userId = userId,
            title = "Account Verified Successfully ✓",
            message = if (isHost) {
                "Congratulations! Your host identity and property documents have been verified. Your verified badge is now active on all listings."
            } else {
                "Congratulations! Your tenant profile has been verified. You now have full verified access to book viewings and message hosts."
            },
            category = "VERIFICATION",
            relatedPropertyId = null
        )
        platDao.insertNotification(notif)
        try {
            supabaseSyncManager.pushNotificationToRemote(notif)
        } catch (_: Exception) {}
    }

    suspend fun notifyAppUpdate(userId: String) = withContext(Dispatchers.IO) {
        val notif = NotificationEntity(
            id = "notif_app_update_v24_${userId}",
            userId = userId,
            title = "Balay-Inn App Update 🚀",
            message = "Balay-Inn v2.4 is active with improved real-time viewing sync, instant renter messaging, and automated availability status.",
            category = "SYSTEM",
            relatedPropertyId = null
        )
        platDao.insertNotification(notif)
        try {
            supabaseSyncManager.pushNotificationToRemote(notif)
        } catch (_: Exception) {}
    }

    suspend fun notifySavedPropertyUnavailable(userId: String, propertyId: String, title: String) = withContext(Dispatchers.IO) {
        val notif = NotificationEntity(
            id = "notif_unavail_${userId}_${propertyId}",
            userId = userId,
            title = "Hearted Place Now Unavailable",
            message = "'$title' on your saved list has been marked as fully occupied or unavailable. Explore similar rentals nearby!",
            category = "SAVED",
            relatedPropertyId = propertyId
        )
        platDao.insertNotification(notif)
        try {
            supabaseSyncManager.pushNotificationToRemote(notif)
        } catch (_: Exception) {}
    }

    // Tenant Requirements & Transparent Match Calculator
    fun getRequirements(userId: String) = platDao.getRequirementsForUser(userId)
    suspend fun saveRequirement(req: TenantRequirementEntity) = withContext(Dispatchers.IO) {
        platDao.insertRequirement(req)
    }
    suspend fun deleteRequirement(id: String) = withContext(Dispatchers.IO) {
        platDao.deleteRequirement(id)
    }

    fun calculateMatch(property: PropertyEntity, req: TenantRequirementEntity): PropertyMatchResult {
        var score = 100
        val positive = mutableListOf<String>()
        val warning = mutableListOf<String>()

        // 1. Budget check
        if (property.monthlyRent <= req.maxBudget) {
            positive.add("Within your monthly budget (₱${property.monthlyRent.toInt()} vs max ₱${req.maxBudget.toInt()})")
        } else {
            val over = (property.monthlyRent - req.maxBudget).toInt()
            score -= 25
            warning.add("Exceeds max budget by ₱$over/month")
        }

        // 2. Location check
        if (req.preferredProvinceId.isNotBlank()) {
            if (property.provinceId == req.preferredProvinceId) {
                if (req.preferredCityId.isNotBlank() && property.cityMunicipalityId == req.preferredCityId) {
                    positive.add("Exact preferred city/municipality match")
                } else {
                    positive.add("Within preferred province")
                }
            } else {
                score -= 30
                warning.add("Outside preferred province")
            }
        }

        // 3. Furnishing
        if (req.furnishedRequired) {
            if (property.furnishedStatus == "Furnished") {
                positive.add("Fully furnished as required")
            } else {
                score -= 15
                warning.add("Only ${property.furnishedStatus.lowercase()} (Furnished requested)")
            }
        }

        // 4. Pet Friendly
        if (req.petFriendlyRequired) {
            if (property.petPolicy.contains("Pets", ignoreCase = true) && !property.petPolicy.contains("No Pets", ignoreCase = true)) {
                positive.add("Pet-friendly policy (${property.petPolicy})")
            } else {
                score -= 15
                warning.add("No pets policy on listing")
            }
        }

        // 5. Parking
        if (req.parkingRequired) {
            if (property.parkingAvailable) {
                positive.add("Dedicated parking available")
            } else {
                score -= 15
                warning.add("No dedicated parking")
            }
        }

        // 6. Bedrooms
        if (property.bedrooms >= req.minBedrooms) {
            positive.add("${property.bedrooms} bedroom(s) fits bedroom requirement")
        } else {
            score -= 15
            warning.add("Fewer bedrooms than desired (${property.bedrooms} vs ${req.minBedrooms})")
        }

        val finalScore = score.coerceIn(10, 99)
        return PropertyMatchResult(finalScore, positive, warning)
    }

    // Reports & Moderation
    fun getAllReports() = platDao.getAllReports()
    suspend fun submitReport(reporterId: String, targetType: String, targetId: String, reason: String, details: String) = withContext(Dispatchers.IO) {
        val report = ReportEntity(
            id = "rep_${System.currentTimeMillis()}",
            reporterId = reporterId,
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            details = details,
            status = "PENDING"
        )
        platDao.insertReport(report)
        logAudit("SUBMIT_REPORT", targetType, targetId, "Reported for $reason: $details")
        try {
            supabaseSyncManager.pushListingReport(report, targetId, "")
        } catch (_: Exception) {}
    }

    suspend fun updateReportStatus(report: ReportEntity, newStatus: String) = withContext(Dispatchers.IO) {
        val updated = report.copy(status = newStatus)
        platDao.updateReport(updated)
        logAudit("MODERATE_REPORT", "REPORT", report.id, "Action: $newStatus")
    }

    // Verification Documents
    fun getDocumentsForUser(userId: String) = platDao.getDocumentsForUser(userId)
    fun getAllDocuments() = platDao.getAllDocuments()

    suspend fun submitDocument(userId: String, propertyId: String?, docType: String, fileName: String) = withContext(Dispatchers.IO) {
        val doc = VerificationDocumentEntity(
            id = "doc_${System.currentTimeMillis()}",
            userId = userId,
            propertyId = propertyId,
            documentType = docType,
            fileName = fileName,
            status = "SUBMITTED"
        )
        platDao.insertDocument(doc)
        logAudit("SUBMIT_DOCUMENT", "VERIFICATION_DOC", doc.id, "User submitted $docType")
    }

    suspend fun reviewDocument(doc: VerificationDocumentEntity, newStatus: String, notes: String, adminId: String) = withContext(Dispatchers.IO) {
        val updated = doc.copy(
            status = newStatus,
            reviewerNotes = notes,
            reviewedAt = System.currentTimeMillis()
        )
        platDao.updateDocument(updated)
        logAudit("REVIEW_DOCUMENT", "VERIFICATION_DOC", doc.id, "Status: $newStatus, Notes: $notes")

        if (newStatus.equals("APPROVED", ignoreCase = true) || newStatus.equals("VERIFIED", ignoreCase = true)) {
            val user = userDao.getUserById(doc.userId).firstOrNull()
            val isHost = user?.canonicalRole?.isLandlord == true || user?.role?.equals("host", ignoreCase = true) == true || user?.role == "PROPERTY_OWNER"
            notifyAccountVerified(doc.userId, isHost = isHost)
        }
    }

    // Audit Log
    fun getAuditLogs() = platDao.getAuditLogs()

    private suspend fun logAudit(action: String, entityType: String, entityId: String, notes: String) {
        val logEntity = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            adminUserId = _currentUser.value.id,
            actionType = action,
            entityType = entityType,
            entityId = entityId,
            notes = notes
        )
        platDao.insertAuditLog(logEntity)
        try {
            supabaseSyncManager.pushAuditLog(logEntity)
        } catch (_: Exception) {}
    }

    // Users
    fun getAllUsers() = userDao.getAllUsers()

    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
        _currentUser.value = user
        try {
            platDao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    userId = user.id,
                    title = "Welcome to Balay-Inn! 🎉",
                    message = "Your Gmail account (${user.email}) is registered & linked with local database & cloud server for notifications.",
                    category = "ACCOUNT",
                    relatedPropertyId = null,
                    createdAt = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            android.util.Log.e("BalayInnRepo", "Failed to insert welcome notification: ${e.message}")
        }
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
        if (_currentUser.value.id == user.id) {
            _currentUser.value = user
        }
    }
}
