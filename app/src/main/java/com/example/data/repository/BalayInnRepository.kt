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

    val supabaseSyncManager = SupabaseSyncManager(propDao, viewDao, commDao)
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
                geoDao.insertRegions(listOf(MarketplaceDataSeed.regionVII))
                geoDao.insertProvinces(MarketplaceDataSeed.provinces)
                geoDao.insertCities(MarketplaceDataSeed.boholMunicipalities + MarketplaceDataSeed.cebuCities)
                geoDao.insertBarangays(MarketplaceDataSeed.generateBarangays())

                // Seed Users
                userDao.insertUsers(MarketplaceDataSeed.seedUsers)

                // Seed Properties & Images
                propDao.insertProperties(PropertyDataSeed.sampleProperties)
                propDao.insertImages(PropertyDataSeed.sampleImages)

                // Seed Viewings
                PropertyDataSeed.sampleViewingRequests.forEach { viewDao.insertViewingRequest(it) }

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
                        notes = "Balay-Inn database seeded with full Bohol 48 LGUs, Cebu hierarchy, verified demo properties, and roles."
                    )
                )
            }
            try {
                propDao.insertProperties(PropertyDataSeed.sampleProperties)
                propDao.insertImages(PropertyDataSeed.sampleImages)
            } catch (_: Exception) {}

            // Attempt background synchronization with Supabase if configured
            try {
                supabaseSyncManager.syncAll()
            } catch (_: Exception) {}
        } catch (e: Exception) {
            android.util.Log.e("BalayInnRepo", "Database initialization encountered an error: ${e.message}", e)
        }
    }

    // Geographic Queries
    fun getActiveCountries() = geoDao.getActiveCountries()
    fun getRegions(countryId: String) = geoDao.getRegionsByCountry(countryId)
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
            supabaseSyncManager.pushPropertyToRemote(property)
        } catch (_: Exception) {}
    }

    // Favorites / Saved
    fun getSavedProperties(userId: String) = propDao.getSavedProperties(userId)
    fun isPropertySaved(userId: String, propertyId: String) = propDao.isPropertySaved(userId, propertyId)
    suspend fun toggleSaveProperty(userId: String, propertyId: String) = withContext(Dispatchers.IO) {
        val isSaved = propDao.isPropertySaved(userId, propertyId).firstOrNull() ?: false
        if (isSaved) {
            propDao.deleteSavedProperty(userId, propertyId)
        } else {
            propDao.insertSavedProperty(
                SavedPropertyEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    propertyId = propertyId
                )
            )
        }
    }

    // Viewings
    fun getViewingsForTenant(tenantId: String) = viewDao.getViewingsForTenant(tenantId)
    fun getViewingsForPoster(posterId: String) = viewDao.getViewingsForPoster(posterId)
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
        message: String
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
            supabaseSyncManager.pushViewingRequestToRemote(req)
        } catch (_: Exception) {}

        // Trigger notification to poster
        platDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = posterId,
                title = "New Viewing Request",
                message = "A tenant requested a viewing for your property on $date ($time).",
                category = "VIEWING",
                relatedPropertyId = propertyId
            )
        )
    }

    suspend fun updateViewingStatus(viewing: ViewingRequestEntity, newStatus: String, responseNote: String = "") = withContext(Dispatchers.IO) {
        val updated = viewing.copy(
            status = newStatus,
            posterResponse = responseNote.ifBlank { viewing.posterResponse },
            updatedAt = System.currentTimeMillis()
        )
        viewDao.updateViewingRequest(updated)
        try {
            supabaseSyncManager.pushViewingRequestToRemote(updated)
        } catch (_: Exception) {}

        // Notify tenant
        platDao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = viewing.tenantId,
                title = "Viewing Request Update",
                message = "Your viewing request status is now: $newStatus. Note: $responseNote",
                category = "VIEWING",
                relatedPropertyId = viewing.propertyId
            )
        )
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
        commDao.insertMessage(msg)
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
            sendMessage(convId, tenantId, tenantName, initialMessage)
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
    }

    // Audit Log
    fun getAuditLogs() = platDao.getAuditLogs()

    private suspend fun logAudit(action: String, entityType: String, entityId: String, notes: String) {
        platDao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                adminUserId = _currentUser.value.id,
                actionType = action,
                entityType = entityType,
                entityId = entityId,
                notes = notes
            )
        )
    }

    // Users
    fun getAllUsers() = userDao.getAllUsers()

    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
        _currentUser.value = user
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
        if (_currentUser.value.id == user.id) {
            _currentUser.value = user
        }
    }
}
