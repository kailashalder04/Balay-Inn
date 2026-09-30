package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CountryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.PropertyImageEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.RegionEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedPropertyEntity
import com.example.data.local.entity.SavedSearchEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.data.model.UserRole
import com.example.data.repository.BalayInnRepository
import com.example.data.repository.PropertyMatchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MapBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)

data class DistanceFilter(
    val centerLat: Double,
    val centerLon: Double,
    val radiusKm: Double
)

fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

data class MarketplaceFilterState(
    val keyword: String = "",
    val provinceId: String = "",
    val cityMunicipalityId: String = "",
    val barangayId: String = "",
    val minRent: Double = 0.0,
    val maxRent: Double = 100000.0,
    val propertyType: String = "All", // All, Studio, Apartment, Condominium, House, Room, Townhouse
    val bedrooms: Int? = null,
    val furnishedStatus: String = "All", // All, Furnished, Semi-Furnished, Unfurnished
    val parkingRequired: Boolean = false,
    val petFriendly: Boolean = false,
    val verifiedOnly: Boolean = false,
    val sortBy: String = "NEWEST", // NEWEST, PRICE_ASC, PRICE_DESC
    val bounds: MapBounds? = null,
    val distanceFilter: DistanceFilter? = null
)

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    val repository = BalayInnRepository(application)

    val currentUser: StateFlow<UserEntity> = repository.currentUser

    // Master Geographic Lists
    val countries: StateFlow<List<CountryEntity>> = repository.getActiveCountries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProvinces: StateFlow<List<ProvinceEntity>> = repository.getAllActiveProvinces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCities: StateFlow<List<CityMunicipalityEntity>> = repository.getAllActiveCities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBarangays: StateFlow<List<BarangayEntity>> = repository.getAllActiveBarangays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive property images mapping (propertyId -> List of imageUrls)
    val propertyImagesMap: StateFlow<Map<String, List<String>>> = repository.getAllPropertyImages()
        .map { list -> list.groupBy({ it.propertyId }, { it.imageUrl }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getImagesFor(propertyId: String): List<String> = propertyImagesMap.value[propertyId] ?: emptyList()

    // Filter State
    private val _filterState = MutableStateFlow(MarketplaceFilterState())
    val filterState: StateFlow<MarketplaceFilterState> = _filterState.asStateFlow()

    // View Mode (LIST vs MAP)
    private val _viewMode = MutableStateFlow("LIST")
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()

    fun setViewMode(mode: String) {
        _viewMode.value = mode
    }

    // Light / Dark Mode State Persistence
    private val prefs = application.getSharedPreferences("balay_inn_settings", Context.MODE_PRIVATE)
    private val _isDarkMode = MutableStateFlow(
        prefs.getBoolean("is_dark_mode", false)
    )
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }

    // Canonical Role for Current User
    val userRole: StateFlow<UserRole> = currentUser
        .map { it.canonicalRole }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), currentUser.value.canonicalRole)

    // Role-tailored managed properties:
    // Landlord -> their owned properties
    // Representative -> their assigned properties
    // Admin -> all properties
    // Tenant -> empty
    val managedProperties: StateFlow<List<PropertyEntity>> = combine(
        currentUser,
        repository.getAllProperties()
    ) { user, allProps ->
        when (user.canonicalRole) {
            UserRole.LANDLORD -> allProps.filter { it.ownerId == user.id }
            UserRole.REPRESENTATIVE -> allProps.filter { it.posterId == user.id }
            UserRole.ADMIN -> allProps
            UserRole.TENANT -> emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Role-tailored Viewings:
    // Tenant -> viewings they requested
    // Landlord -> viewings for properties they own
    // Representative -> viewings for properties they manage
    // Admin -> all viewings
    val userViewings: StateFlow<List<ViewingRequestEntity>> = combine(
        currentUser,
        repository.getAllViewings(),
        repository.getAllProperties()
    ) { user, allViewings, allProps ->
        when (user.canonicalRole) {
            UserRole.TENANT -> allViewings.filter { it.tenantId == user.id }
            UserRole.LANDLORD -> {
                val ownedPropIds = allProps.filter { it.ownerId == user.id }.map { it.id }.toSet()
                allViewings.filter { it.propertyId in ownedPropIds }
            }
            UserRole.REPRESENTATIVE -> allViewings.filter { it.posterId == user.id }
            UserRole.ADMIN -> allViewings
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tenant Saved Searches
    val savedSearches: StateFlow<List<SavedSearchEntity>> = currentUser
        .combine(repository.getAllProperties()) { user, _ -> user.id }
        .combine(repository.getSavedSearches(currentUser.value.id)) { _, searches -> searches }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tenant Recently Viewed Properties
    private val _recentlyViewedIds = MutableStateFlow<List<String>>(listOf("prop_boh_01", "prop_ceb_01"))
    val recentlyViewedProperties: StateFlow<List<PropertyEntity>> = combine(
        _recentlyViewedIds,
        repository.getAllActiveProperties()
    ) { ids, allProps ->
        ids.mapNotNull { id -> allProps.find { it.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordPropertyView(propertyId: String) {
        val list = _recentlyViewedIds.value.toMutableList()
        list.remove(propertyId)
        list.add(0, propertyId)
        _recentlyViewedIds.value = list.take(10)
    }

    // Tenant Recommended & New Matches
    val recommendedProperties: StateFlow<List<PropertyEntity>> = repository.getAllActiveProperties()
        .map { list -> list.filter { it.verificationLevel >= 3 || it.savesCount > 10 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newMatchesProperties: StateFlow<List<PropertyEntity>> = repository.getAllActiveProperties()
        .map { list -> list.take(6) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Audit Logs
    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    // Filtered Properties Stream
    val properties: StateFlow<List<PropertyEntity>> = combine(
        repository.getAllActiveProperties(),
        _filterState
    ) { allProps, filter ->
        allProps.filter { prop ->
            // Keyword
            val matchesKeyword = filter.keyword.isBlank() ||
                prop.title.contains(filter.keyword, ignoreCase = true) ||
                prop.description.contains(filter.keyword, ignoreCase = true) ||
                prop.neighborhood.contains(filter.keyword, ignoreCase = true) ||
                prop.nearbyLandmark.contains(filter.keyword, ignoreCase = true)

            // Province
            val matchesProvince = filter.provinceId.isBlank() || prop.provinceId == filter.provinceId

            // City
            val matchesCity = filter.cityMunicipalityId.isBlank() || prop.cityMunicipalityId == filter.cityMunicipalityId

            // Barangay
            val matchesBarangay = filter.barangayId.isBlank() || prop.barangayId == filter.barangayId

            // Price Range
            val matchesPrice = prop.monthlyRent in filter.minRent..filter.maxRent

            // Property Type
            val matchesType = filter.propertyType == "All" || prop.propertyType.equals(filter.propertyType, ignoreCase = true)

            // Bedrooms
            val matchesBeds = filter.bedrooms == null || prop.bedrooms == filter.bedrooms

            // Furnished
            val matchesFurnished = filter.furnishedStatus == "All" || prop.furnishedStatus.equals(filter.furnishedStatus, ignoreCase = true)

            // Parking
            val matchesParking = !filter.parkingRequired || prop.parkingAvailable

            // Pets
            val matchesPets = !filter.petFriendly || (!prop.petPolicy.contains("No Pets", ignoreCase = true))

            // Verified Only
            val matchesVerified = !filter.verifiedOnly || prop.verificationLevel >= 3

            // Map Bounds ("Search this area" - visible map coordinates)
            val matchesBounds = filter.bounds == null || (
                prop.latitude in filter.bounds.minLat..filter.bounds.maxLat &&
                prop.longitude in filter.bounds.minLon..filter.bounds.maxLon
            )

            // Distance Search ("Within 1 km / 5 km / 10 km / 25 km")
            val matchesDistance = filter.distanceFilter == null || (
                calculateDistanceKm(
                    filter.distanceFilter.centerLat,
                    filter.distanceFilter.centerLon,
                    prop.latitude,
                    prop.longitude
                ) <= filter.distanceFilter.radiusKm
            )

            matchesKeyword && matchesProvince && matchesCity && matchesBarangay && matchesPrice &&
                matchesType && matchesBeds && matchesFurnished && matchesParking && matchesPets &&
                matchesVerified && matchesBounds && matchesDistance
        }.sortedWith(
            when (filter.sortBy) {
                "PRICE_ASC" -> compareBy { it.monthlyRent }
                "PRICE_DESC" -> compareByDescending { it.monthlyRent }
                else -> compareByDescending { it.createdAt }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Properties
    val savedProperties: StateFlow<List<SavedPropertyEntity>> = currentUser
        .combine(repository.getAllActiveProperties()) { user, _ -> user.id }
        .combine(repository.getSavedProperties(currentUser.value.id)) { _, saved -> saved }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isPropertySaved(propertyId: String): Flow<Boolean> =
        repository.isPropertySaved(currentUser.value.id, propertyId)

    fun toggleSave(propertyId: String) {
        viewModelScope.launch {
            repository.toggleSaveProperty(currentUser.value.id, propertyId)
        }
    }

    // Filter Updates
    fun updateKeyword(keyword: String) {
        _filterState.value = _filterState.value.copy(keyword = keyword)
    }

    fun updateProvince(provinceId: String) {
        _filterState.value = _filterState.value.copy(
            provinceId = provinceId,
            cityMunicipalityId = "",
            barangayId = ""
        )
    }

    fun updateCity(cityId: String) {
        _filterState.value = _filterState.value.copy(
            cityMunicipalityId = cityId,
            barangayId = ""
        )
    }

    fun updateBarangay(barangayId: String) {
        _filterState.value = _filterState.value.copy(barangayId = barangayId)
    }

    fun updatePriceRange(min: Double, max: Double) {
        _filterState.value = _filterState.value.copy(minRent = min, maxRent = max)
    }

    fun updatePropertyType(type: String) {
        _filterState.value = _filterState.value.copy(propertyType = type)
    }

    fun updateBedrooms(beds: Int?) {
        _filterState.value = _filterState.value.copy(bedrooms = beds)
    }

    fun updateFurnished(status: String) {
        _filterState.value = _filterState.value.copy(furnishedStatus = status)
    }

    fun toggleParkingRequired() {
        _filterState.value = _filterState.value.copy(parkingRequired = !_filterState.value.parkingRequired)
    }

    fun togglePetFriendly() {
        _filterState.value = _filterState.value.copy(petFriendly = !_filterState.value.petFriendly)
    }

    fun toggleVerifiedOnly() {
        _filterState.value = _filterState.value.copy(verifiedOnly = !_filterState.value.verifiedOnly)
    }

    fun setSortBy(sortBy: String) {
        _filterState.value = _filterState.value.copy(sortBy = sortBy)
    }

    fun searchMapArea(minLat: Double, maxLat: Double, minLon: Double, maxLon: Double) {
        _filterState.value = _filterState.value.copy(
            bounds = MapBounds(
                minLat = minOf(minLat, maxLat),
                maxLat = maxOf(minLat, maxLat),
                minLon = minOf(minLon, maxLon),
                maxLon = maxOf(minLon, maxLon)
            )
        )
    }

    fun clearMapAreaSearch() {
        _filterState.value = _filterState.value.copy(bounds = null)
    }

    fun setDistanceFilter(centerLat: Double, centerLon: Double, radiusKm: Double?) {
        _filterState.value = _filterState.value.copy(
            distanceFilter = radiusKm?.let { DistanceFilter(centerLat, centerLon, it) }
        )
    }

    fun clearDistanceFilter() {
        _filterState.value = _filterState.value.copy(distanceFilter = null)
    }

    fun resetFilters() {
        _filterState.value = MarketplaceFilterState()
    }

    // User Switching
    fun switchUser(user: UserEntity) {
        viewModelScope.launch {
            repository.switchUser(user)
        }
    }

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Viewing Requests
    val tenantViewings: StateFlow<List<ViewingRequestEntity>> = currentUser
        .combine(repository.getAllViewings()) { user, _ -> user.id }
        .combine(repository.getViewingsForTenant(currentUser.value.id)) { _, viewings -> viewings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posterViewings: StateFlow<List<ViewingRequestEntity>> = currentUser
        .combine(repository.getAllViewings()) { user, _ -> user.id }
        .combine(repository.getViewingsForPoster(currentUser.value.id)) { _, viewings -> viewings }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun requestViewing(
        propertyId: String,
        posterId: String,
        date: String,
        time: String,
        visitors: Int,
        message: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createViewingRequest(
                propertyId = propertyId,
                tenantId = currentUser.value.id,
                posterId = posterId,
                date = date,
                time = time,
                visitors = visitors,
                message = message
            )
            onComplete()
        }
    }

    fun updateViewingStatus(viewing: ViewingRequestEntity, status: String, note: String) {
        viewModelScope.launch {
            repository.updateViewingStatus(viewing, status, note)
        }
    }

    // Conversations & Chat
    val conversations: StateFlow<List<ConversationEntity>> = currentUser
        .combine(repository.getConversations(currentUser.value.id)) { _, convs -> convs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> =
        repository.getMessages(convId)

    fun sendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, currentUser.value.id, currentUser.value.fullName, text)
        }
    }

    fun startConversation(property: PropertyEntity, message: String, onConversationStarted: (String) -> Unit) {
        viewModelScope.launch {
            val convId = repository.startOrGetConversation(
                propertyId = property.id,
                propertyTitle = property.title,
                tenantId = currentUser.value.id,
                tenantName = currentUser.value.fullName,
                posterId = property.posterId,
                posterName = property.posterName,
                initialMessage = message
            )
            onConversationStarted(convId)
        }
    }

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = currentUser
        .combine(repository.getNotifications(currentUser.value.id)) { _, notifs -> notifs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // Requirements & Matching
    val requirements: StateFlow<List<TenantRequirementEntity>> = currentUser
        .combine(repository.getRequirements(currentUser.value.id)) { _, reqs -> reqs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRequirement(req: TenantRequirementEntity) {
        viewModelScope.launch {
            repository.saveRequirement(req)
        }
    }

    fun deleteRequirement(id: String) {
        viewModelScope.launch {
            repository.deleteRequirement(id)
        }
    }

    fun calculatePropertyMatch(property: PropertyEntity, req: TenantRequirementEntity): PropertyMatchResult =
        repository.calculateMatch(property, req)

    // Property Creation
    fun postProperty(
        title: String,
        description: String,
        type: String,
        rent: Double,
        provinceId: String,
        cityId: String,
        barangayId: String,
        neighborhood: String,
        landmark: String,
        streetAddress: String,
        privacy: String,
        lat: Double,
        lng: Double,
        beds: Int,
        baths: Int,
        area: Double,
        furnished: String,
        parking: Boolean,
        pets: String,
        dues: Double,
        internetIncluded: Boolean,
        relationship: String,
        isAuthorized: Boolean,
        amenities: List<String>,
        imageUrls: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val propId = "prop_${System.currentTimeMillis()}"
            val newProperty = PropertyEntity(
                id = propId,
                title = title,
                description = description,
                propertyType = type,
                monthlyRent = rent,
                countryId = "ctry_ph",
                regionId = "reg_vii",
                provinceId = provinceId,
                cityMunicipalityId = cityId,
                barangayId = barangayId,
                neighborhood = neighborhood,
                nearbyLandmark = landmark,
                streetAddressPrivate = streetAddress,
                locationPrivacy = privacy,
                latitude = lat,
                longitude = lng,
                bedrooms = beds,
                bathrooms = baths,
                floorAreaSqm = area,
                furnishedStatus = furnished,
                parkingAvailable = parking,
                petPolicy = pets,
                availabilityDate = "Immediately",
                associationDues = dues,
                internetIncluded = internetIncluded,
                amenitiesJson = amenities.joinToString(prefix = "[\"", separator = "\",\"", postfix = "\"]"),
                status = "ACTIVE",
                ownerId = if (relationship == "Property owner") user.id else "usr_owner_external",
                posterId = user.id,
                posterName = user.fullName,
                posterRole = relationship,
                isPosterAuthorized = isAuthorized,
                verificationLevel = user.verificationLevel,
                phoneVerified = user.phoneVerified,
                emailVerified = user.emailVerified,
                identitySubmitted = user.identitySubmitted,
                professionalVerified = user.professionalVerified
            )

            val images = imageUrls.mapIndexed { index, url ->
                PropertyImageEntity(
                    id = "img_${propId}_$index",
                    propertyId = propId,
                    imageUrl = url,
                    isPrimary = index == 0,
                    orderIndex = index
                )
            }

            repository.createProperty(newProperty, images, user)
            onSuccess()
        }
    }

    // Reports
    val allReports: StateFlow<List<ReportEntity>> = repository.getAllReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitReport(targetType: String, targetId: String, reason: String, details: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.submitReport(currentUser.value.id, targetType, targetId, reason, details)
            onDone()
        }
    }

    fun updateReport(report: ReportEntity, status: String) {
        viewModelScope.launch {
            repository.updateReportStatus(report, status)
        }
    }

    // Verification Documents
    val userDocuments: StateFlow<List<VerificationDocumentEntity>> = currentUser
        .combine(repository.getDocumentsForUser(currentUser.value.id)) { _, docs -> docs }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<VerificationDocumentEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitVerificationDoc(docType: String, fileName: String, propertyId: String? = null, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.submitDocument(currentUser.value.id, propertyId, docType, fileName)
            onDone()
        }
    }

    fun reviewDocument(doc: VerificationDocumentEntity, newStatus: String, notes: String) {
        viewModelScope.launch {
            repository.reviewDocument(doc, newStatus, notes, currentUser.value.id)
        }
    }

    // Location Admin
    fun addProvinceAdmin(name: String, code: String) {
        viewModelScope.launch {
            repository.addProvince(name, code, "reg_vii")
        }
    }

    fun addCityAdmin(name: String, type: String, code: String, provinceId: String) {
        viewModelScope.launch {
            repository.addCity(name, type, code, provinceId)
        }
    }

    fun addBarangayAdmin(name: String, code: String, cityId: String) {
        viewModelScope.launch {
            repository.addBarangay(name, code, cityId)
        }
    }

    fun toggleProvinceStatus(province: ProvinceEntity) {
        viewModelScope.launch {
            repository.toggleProvinceActive(province)
        }
    }

    fun toggleCityStatus(city: CityMunicipalityEntity) {
        viewModelScope.launch {
            repository.toggleCityActive(city)
        }
    }

    // Poster Properties
    val posterProperties: StateFlow<List<PropertyEntity>> = currentUser
        .combine(repository.getPropertiesByPoster(currentUser.value.id)) { _, props -> props }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updatePropertyListingStatus(propertyId: String, status: String) {
        viewModelScope.launch {
            repository.updatePropertyStatus(propertyId, status)
        }
    }

    // Location Display Helper
    fun getLocationDisplay(property: PropertyEntity): String {
        val city = allCities.value.find { it.id == property.cityMunicipalityId }?.name ?: ""
        val prov = allProvinces.value.find { it.id == property.provinceId }?.name ?: ""
        val bgy = allBarangays.value.find { it.id == property.barangayId }?.name ?: ""

        return when {
            bgy.isNotBlank() && city.isNotBlank() -> "$bgy, $city, $prov"
            city.isNotBlank() -> "$city, $prov"
            prov.isNotBlank() -> prov
            else -> "Philippines"
        }
    }

    fun getImagesForProperty(propertyId: String): Flow<List<PropertyImageEntity>> =
        repository.getImagesForProperty(propertyId)
}
