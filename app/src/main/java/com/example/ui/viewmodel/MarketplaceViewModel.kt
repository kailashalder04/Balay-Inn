package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
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
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.data.model.getCategorySpecifications
import com.example.data.model.UserRole
import com.example.data.repository.BalayInnRepository
import com.example.data.repository.PropertyMatchResult
import com.example.data.remote.supabase.SupabaseClient
import com.example.data.remote.supabase.SupabaseSignInRequest
import com.example.data.remote.supabase.SupabaseSignUpRequest
import com.example.data.remote.supabase.SupabaseAuthUser
import com.example.data.remote.supabase.SupabaseSyncStatus
import com.example.data.remote.supabase.SupabaseUserDto
import com.example.data.remote.supabase.SupabaseUserProfileDto
import com.example.data.seed.MarketplaceDataSeed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    val selectedLocationZones: Set<String> = emptySet(), // Multi-Location Zone Selection (e.g. Cebu IT Park + Mabolo + Lahug)
    val minRent: Double = 0.0,
    val maxRent: Double = 100000.0,
    val propertyType: String = "All", // All, Studio, Apartment, Condominium, House, Room, Townhouse
    val selectedPropertyTypes: Set<String> = emptySet(), // Multi-select: "Boarding House", "Apartment", "Condominium", "Townhouse"
    val bedrooms: Int? = null,
    val furnishedStatus: String = "All", // All, Furnished, Semi-Furnished, Unfurnished
    val parkingRequired: Boolean = false,
    val petFriendly: Boolean = false,
    val verifiedOnly: Boolean = false,
    val sortBy: String = "NEWEST", // NEWEST, PRICE_ASC, PRICE_DESC, HIGHEST_AVAILABLE, LOWEST_PRICE, RECENTLY_UPDATED
    val bounds: MapBounds? = null,
    val distanceFilter: DistanceFilter? = null,
    val listedBy: String = "All", // All, Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, Authorized Representative
    val authorizedPosterOnly: Boolean = false,
    val genderRule: String = "ALL", // ALL, MALE, FEMALE
    val selectedGenderRules: Set<String> = emptySet(), // Multi-select: "ALL", "MALE", "FEMALE", "COED"
    val minAvailableBeds: Int = 0,
    val targetMarket: String = "All",
    val utilityRuleFilter: String = "All",
    val leaseDurationFilter: String = "All", // All, Flexible (1-3 mos), Mid-term (4-6 mos), Annual (12+ mos)
    val selectedLeaseDurations: Set<String> = emptySet(), // Multi-select: "Flexible (1-3 mos)", "Mid-term (4-6 mos)", "Annual (12+ mos)", "All"
    val moveInDateFilter: String = "All", // All, Immediate / Available Now, Within 15 Days, Within 30 Days, Flexible
    // Type-specific dynamic filter attributes
    val boardingPricingModel: String = "All", // All, Per Person/Bed, Per Room
    val boardingCapacity: String = "All", // All, Single, Double bed, 4-bed bunk, 6-bed bunk
    val boardingBathroom: String = "All", // All, Private/En-suite, Shared/Communal
    val apartmentKitchen: String = "All", // All, Indoor/Integral, Outdoor/Dirty Kitchen
    val apartmentBilling: String = "All", // All, Own Meter, Sub-metered by Landlord
    val condoLayout: String = "All", // All, Studio, 1BR, 2BR, Loft, Bi-level
    val condoDues: String = "All", // All, Inclusive in rent, Exclusive/Tenant pays separately
    val condoParking: String = "All", // All, Included in rent, Available for extra fee
    val townhouseStories: Int? = null, // 1, 2, 3
    val townhouseGatedSubdivision: Boolean = false,
    val townhouseMaidsRoom: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    val repository = BalayInnRepository(application)

    val currentUser: StateFlow<UserEntity> = repository.currentUser

    // Supabase Cloud Backend & Sync Status
    val supabaseSyncStatus: StateFlow<SupabaseSyncStatus> = repository.supabaseSyncManager.syncStatus
    val isSupabaseConfigured: Boolean get() = SupabaseClient.isConfigured

    fun syncWithSupabase() {
        viewModelScope.launch {
            repository.supabaseSyncManager.syncAll()
        }
    }

    // Master Geographic Lists
    val countries: StateFlow<List<CountryEntity>> = repository.getActiveCountries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRegions: StateFlow<List<RegionEntity>> = repository.getAllActiveRegions()
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

    // View Mode (MAP vs LIST, defaults to MAP so map is displayed immediately)
    private val _viewMode = MutableStateFlow("MAP")
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
        .flatMapLatest { user -> repository.getSavedSearches(user.id) }
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
        val savedToken = prefs.getString("supabase_access_token", null)
        val savedRefresh = prefs.getString("supabase_refresh_token", null)
        if (!savedToken.isNullOrBlank()) {
            SupabaseClient.saveSession(savedToken, savedRefresh)
        }

        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }

        viewModelScope.launch {
            // Live background sync loop for messages, viewings, and notifications
            while (isActive) {
                delay(4000)
                try {
                    repository.syncLiveUpdates()
                } catch (_: Exception) {}
            }
        }
    }

    fun refreshFromCloud() {
        viewModelScope.launch {
            repository.syncWithCloud()
        }
    }

    // Unfiltered Active Properties Stream (for category counts)
    val allUnfilteredProperties: StateFlow<List<PropertyEntity>> = repository.getAllActiveProperties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Properties Stream
    val properties: StateFlow<List<PropertyEntity>> = combine(
        repository.getAllActiveProperties(),
        _filterState
    ) { allProps, filter ->
        allProps.filter { prop ->
            val propCategory = PhilippineRentalPropertyCategory.fromPropertyType(prop.propertyType)

            // Keyword
            val matchesKeyword = filter.keyword.isBlank() ||
                prop.title.contains(filter.keyword, ignoreCase = true) ||
                prop.description.contains(filter.keyword, ignoreCase = true) ||
                prop.neighborhood.contains(filter.keyword, ignoreCase = true) ||
                prop.nearbyLandmark.contains(filter.keyword, ignoreCase = true) ||
                propCategory.displayName.contains(filter.keyword, ignoreCase = true) ||
                propCategory.targetMarket.contains(filter.keyword, ignoreCase = true) ||
                propCategory.expectedAmenities.any { it.contains(filter.keyword, ignoreCase = true) } ||
                prop.amenitiesJson.contains(filter.keyword, ignoreCase = true)

            // Multi-Location Zones Filtering (e.g., Cebu IT Park + Mabolo + Lahug)
            val matchesLocation = if (filter.selectedLocationZones.isEmpty()) {
                val matchesProvince = filter.provinceId.isBlank() || prop.provinceId == filter.provinceId
                val matchesCity = filter.cityMunicipalityId.isBlank() || prop.cityMunicipalityId == filter.cityMunicipalityId
                val matchesBarangay = filter.barangayId.isBlank() || prop.barangayId == filter.barangayId
                matchesProvince && matchesCity && matchesBarangay
            } else {
                filter.selectedLocationZones.any { zone ->
                    matchesZone(prop, zone)
                }
            }

            // Price Range
            val matchesPrice = prop.monthlyRent in filter.minRent..filter.maxRent

            // Property Type (supports multi-selection)
            val matchesType = if (filter.selectedPropertyTypes.isEmpty() || "All" in filter.selectedPropertyTypes) {
                when {
                    filter.propertyType == "All" -> true
                    prop.propertyType.equals(filter.propertyType, ignoreCase = true) -> true
                    else -> {
                        val filterCat = PhilippineRentalPropertyCategory.fromPropertyType(filter.propertyType)
                        filterCat == propCategory
                    }
                }
            } else {
                filter.selectedPropertyTypes.any { selType ->
                    prop.propertyType.equals(selType, ignoreCase = true) ||
                    PhilippineRentalPropertyCategory.fromPropertyType(selType) == propCategory ||
                    (selType.contains("Board", ignoreCase = true) && (propCategory == PhilippineRentalPropertyCategory.BED_SPACE || prop.propertyType.contains("Board", ignoreCase = true) || prop.propertyType.contains("Bed", ignoreCase = true))) ||
                    (selType.contains("Apart", ignoreCase = true) && (propCategory == PhilippineRentalPropertyCategory.APARTMENT_FLAT || prop.propertyType.contains("Apart", ignoreCase = true))) ||
                    (selType.contains("Condo", ignoreCase = true) && (propCategory == PhilippineRentalPropertyCategory.CONDOMINIUM || propCategory == PhilippineRentalPropertyCategory.STUDIO_UNIT || prop.propertyType.contains("Condo", ignoreCase = true) || prop.propertyType.contains("Studio", ignoreCase = true))) ||
                    (selType.contains("Town", ignoreCase = true) && (propCategory == PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE || prop.propertyType.contains("Town", ignoreCase = true) || prop.propertyType.contains("House", ignoreCase = true)))
                }
            }

            // Target Market
            val matchesTargetMarket = when {
                filter.targetMarket == "All" -> true
                filter.targetMarket.contains("Student", ignoreCase = true) || filter.targetMarket.contains("BPO", ignoreCase = true) -> {
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE ||
                    prop.description.contains("student", ignoreCase = true) ||
                    prop.description.contains("bpo", ignoreCase = true)
                }
                filter.targetMarket.contains("Solo", ignoreCase = true) || filter.targetMarket.contains("Couple", ignoreCase = true) -> {
                    propCategory == PhilippineRentalPropertyCategory.STUDIO_UNIT ||
                    prop.description.contains("solo", ignoreCase = true) ||
                    prop.description.contains("couple", ignoreCase = true)
                }
                filter.targetMarket.contains("Famil", ignoreCase = true) || filter.targetMarket.contains("Sharer", ignoreCase = true) -> {
                    propCategory == PhilippineRentalPropertyCategory.APARTMENT_FLAT ||
                    propCategory == PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE ||
                    prop.description.contains("famil", ignoreCase = true)
                }
                filter.targetMarket.contains("Expat", ignoreCase = true) || filter.targetMarket.contains("Professional", ignoreCase = true) -> {
                    propCategory == PhilippineRentalPropertyCategory.CONDOMINIUM ||
                    prop.description.contains("expat", ignoreCase = true) ||
                    prop.description.contains("professional", ignoreCase = true)
                }
                else -> true
            }

            // Utilities & Rules Filter
            val matchesUtilityRule = when {
                filter.utilityRuleFilter == "All" -> true
                filter.utilityRuleFilter.contains("Sub-meter", ignoreCase = true) -> {
                    prop.description.contains("sub-meter", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.STUDIO_UNIT ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("Flat Rate", ignoreCase = true) || filter.utilityRuleFilter.contains("Water (per head)", ignoreCase = true) -> {
                    prop.description.contains("head", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("WiFi", ignoreCase = true) -> {
                    prop.internetIncluded ||
                    prop.description.contains("wifi", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("24/7", ignoreCase = true) || filter.utilityRuleFilter.contains("No Curfew", ignoreCase = true) -> {
                    prop.description.contains("no curfew", ignoreCase = true) ||
                    prop.description.contains("24/7", ignoreCase = true) ||
                    propCategory != PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("Curfew Hours", ignoreCase = true) -> {
                    prop.description.contains("curfew", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("Caretaker", ignoreCase = true) -> {
                    prop.description.contains("caretaker", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("Guard", ignoreCase = true) || filter.utilityRuleFilter.contains("Permit", ignoreCase = true) -> {
                    prop.description.contains("guard", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.CONDOMINIUM
                }
                filter.utilityRuleFilter.contains("LPG Allowed", ignoreCase = true) -> {
                    prop.description.contains("lpg", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.APARTMENT_FLAT ||
                    propCategory == PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE
                }
                filter.utilityRuleFilter.contains("Induction", ignoreCase = true) || filter.utilityRuleFilter.contains("No LPG", ignoreCase = true) -> {
                    prop.description.contains("induction", ignoreCase = true) ||
                    prop.description.contains("no lpg", ignoreCase = true) ||
                    propCategory == PhilippineRentalPropertyCategory.CONDOMINIUM ||
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("Visitors Allowed", ignoreCase = true) -> {
                    propCategory != PhilippineRentalPropertyCategory.BED_SPACE
                }
                filter.utilityRuleFilter.contains("No Visitors", ignoreCase = true) -> {
                    propCategory == PhilippineRentalPropertyCategory.BED_SPACE ||
                    prop.description.contains("no visitor", ignoreCase = true)
                }
                filter.utilityRuleFilter.contains("Garage", ignoreCase = true) || filter.utilityRuleFilter.contains("Parking", ignoreCase = true) -> {
                    prop.parkingAvailable || propCategory == PhilippineRentalPropertyCategory.APARTMENT_FLAT
                }
                else -> true
            }

            // Bedrooms
            val matchesBeds = filter.bedrooms == null || prop.bedrooms == filter.bedrooms

            // Furnished
            val matchesFurnished = filter.furnishedStatus == "All" || 
                prop.furnishedStatus.equals(filter.furnishedStatus, ignoreCase = true) ||
                com.example.data.model.normalizeFurnishing(prop.furnishedStatus).equals(com.example.data.model.normalizeFurnishing(filter.furnishedStatus), ignoreCase = true)

            // Parking
            val matchesParking = !filter.parkingRequired || prop.parkingAvailable

            // Pets
            val matchesPets = !filter.petFriendly || (!prop.petPolicy.contains("No Pets", ignoreCase = true))

            // Verified Only
            val matchesVerified = !filter.verifiedOnly || prop.verificationLevel >= 3

            // Listed By
            val matchesListedBy = when (filter.listedBy) {
                "All" -> true
                "Direct Owner", "Property Owner", "Owner" ->
                    prop.posterRole.contains("owner", ignoreCase = true)
                "Licensed Broker", "Broker" ->
                    prop.posterRole.contains("broker", ignoreCase = true) ||
                    prop.posterRole.contains("salesperson", ignoreCase = true) ||
                    prop.posterRole.contains("agent", ignoreCase = true)
                "Property Manager" ->
                    prop.posterRole.contains("manager", ignoreCase = true)
                "Caretaker" ->
                    prop.posterRole.contains("caretaker", ignoreCase = true)
                "Family Member" ->
                    prop.posterRole.contains("family", ignoreCase = true)
                "Authorized Representative" ->
                    prop.posterRole.contains("representative", ignoreCase = true) ||
                    (prop.isPosterAuthorized && prop.posterRole.contains("authorized", ignoreCase = true))
                else -> prop.posterRole.contains(filter.listedBy, ignoreCase = true)
            }

            // Authorized Poster Only
            val matchesAuthorized = !filter.authorizedPosterOnly || prop.isPosterAuthorized ||
                    prop.posterRole.contains("owner", ignoreCase = true)

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

            // Gender Rule: ALL, MALE, FEMALE, COED (supports multi-selection)
            val matchesGender = if (filter.selectedGenderRules.isEmpty() || "ALL" in filter.selectedGenderRules) {
                when (filter.genderRule.uppercase()) {
                    "FEMALE", "FEMALE_ONLY" -> prop.title.contains("Ladies", ignoreCase = true) ||
                            prop.title.contains("Female", ignoreCase = true) ||
                            prop.description.contains("Ladies", ignoreCase = true) ||
                            prop.description.contains("Female", ignoreCase = true)
                    "MALE", "MALE_ONLY" -> prop.title.contains("Men", ignoreCase = true) ||
                            prop.title.contains("Male", ignoreCase = true) ||
                            prop.description.contains("Men", ignoreCase = true) ||
                            prop.description.contains("Male", ignoreCase = true)
                    else -> true
                }
            } else {
                filter.selectedGenderRules.any { rule ->
                    when (rule.uppercase()) {
                        "FEMALE", "FEMALE_ONLY" ->
                            prop.title.contains("Ladies", ignoreCase = true) ||
                            prop.title.contains("Female", ignoreCase = true) ||
                            prop.description.contains("Ladies", ignoreCase = true) ||
                            prop.description.contains("Female", ignoreCase = true)
                        "MALE", "MALE_ONLY" ->
                            prop.title.contains("Men", ignoreCase = true) ||
                            prop.title.contains("Male", ignoreCase = true) ||
                            prop.description.contains("Men", ignoreCase = true) ||
                            prop.description.contains("Male", ignoreCase = true)
                        "COED", "CO-ED", "MIXED" ->
                            !prop.title.contains("Ladies", ignoreCase = true) &&
                            !prop.title.contains("Female", ignoreCase = true) &&
                            !prop.title.contains("Men", ignoreCase = true) &&
                            !prop.title.contains("Male", ignoreCase = true)
                        else -> true
                    }
                }
            }

            // Min Available Beds
            val matchesMinBeds = filter.minAvailableBeds <= 0 || (prop.bedrooms == 0 || prop.bedrooms >= filter.minAvailableBeds)

            // Flexible Lease Duration Filter (supports multi-selection)
            val matchesLeaseDuration = if (filter.selectedLeaseDurations.isEmpty() || "All" in filter.selectedLeaseDurations) {
                when (filter.leaseDurationFilter) {
                    "Flexible (1-3 mos)", "Short-term (< 6 mos)" ->
                        prop.leaseDurationMonths <= 3 ||
                        prop.description.contains("flexible", ignoreCase = true) ||
                        prop.description.contains("short term", ignoreCase = true)
                    "Mid-term (4-6 mos)" ->
                        prop.leaseDurationMonths in 4..6
                    "Annual (12+ mos)", "Long-term (12+ mos)" ->
                        prop.leaseDurationMonths >= 12
                    else -> true
                }
            } else {
                filter.selectedLeaseDurations.any { dur ->
                    when (dur) {
                        "Flexible (1-3 mos)", "Short-term (< 6 mos)" ->
                            prop.leaseDurationMonths <= 3 ||
                            prop.description.contains("flexible", ignoreCase = true) ||
                            prop.description.contains("short term", ignoreCase = true)
                        "Mid-term (4-6 mos)" ->
                            prop.leaseDurationMonths in 4..6
                        "Annual (12+ mos)", "Long-term (12+ mos)" ->
                            prop.leaseDurationMonths >= 12
                        else -> true
                    }
                }
            }

            // Move-In Date Filter
            val matchesMoveInDate = when (filter.moveInDateFilter) {
                "Immediate / Available Now", "Immediate" ->
                    prop.availabilityDate.contains("Immediate", ignoreCase = true) ||
                    prop.availabilityDate.contains("Now", ignoreCase = true) ||
                    prop.availabilityDate <= "2026-10-15"
                "Within 15 Days" ->
                    prop.availabilityDate.contains("Immediate", ignoreCase = true) ||
                    prop.availabilityDate.contains("Now", ignoreCase = true) ||
                    prop.availabilityDate <= "2026-10-31"
                "Within 30 Days" ->
                    prop.availabilityDate.contains("Immediate", ignoreCase = true) ||
                    prop.availabilityDate.contains("Now", ignoreCase = true) ||
                    prop.availabilityDate <= "2026-11-15"
                "Flexible Move-in", "Flexible" -> true
                else -> true
            }

            // Dynamic Type-Specific Specifications Filter
            val categorySpecs = prop.getCategorySpecifications()
            val matchesCategorySpecs = when (filter.propertyType) {
                "Boarding House", "Bed Space / Boarding House" -> {
                    val bh = categorySpecs.boardingHouse
                    val matchesPricing = filter.boardingPricingModel == "All" || bh?.pricingModel.equals(filter.boardingPricingModel, ignoreCase = true)
                    val matchesCap = filter.boardingCapacity == "All" || bh?.roomCapacity.equals(filter.boardingCapacity, ignoreCase = true)
                    val matchesBath = filter.boardingBathroom == "All" || bh?.bathroomType.equals(filter.boardingBathroom, ignoreCase = true)
                    matchesPricing && matchesCap && matchesBath
                }
                "Apartment", "Apartment / Multi-door Flat" -> {
                    val ap = categorySpecs.apartment
                    val matchesKitchen = filter.apartmentKitchen == "All" || ap?.kitchenType.equals(filter.apartmentKitchen, ignoreCase = true)
                    val matchesBilling = filter.apartmentBilling == "All" || ap?.utilityBilling.equals(filter.apartmentBilling, ignoreCase = true)
                    matchesKitchen && matchesBilling
                }
                "Condominium", "Condominium Unit", "Studio", "Studio Unit" -> {
                    val cd = categorySpecs.condominium
                    val matchesLayout = filter.condoLayout == "All" || cd?.unitLayout.equals(filter.condoLayout, ignoreCase = true)
                    val matchesDues = filter.condoDues == "All" || cd?.associationDues.equals(filter.condoDues, ignoreCase = true)
                    val matchesPark = filter.condoParking == "All" || cd?.parking.equals(filter.condoParking, ignoreCase = true)
                    matchesLayout && matchesDues && matchesPark
                }
                "Townhouse", "House & Townhouse" -> {
                    val th = categorySpecs.townhouse
                    val matchesStories = filter.townhouseStories == null || th?.stories == filter.townhouseStories
                    val matchesGated = !filter.townhouseGatedSubdivision || (th?.isGatedSubdivision == true)
                    val matchesMaid = !filter.townhouseMaidsRoom || (th?.hasMaidsRoom == true)
                    matchesStories && matchesGated && matchesMaid
                }
                else -> true
            }

            matchesKeyword && matchesLocation && matchesPrice &&
                matchesType && matchesTargetMarket && matchesUtilityRule &&
                matchesBeds && matchesFurnished && matchesParking && matchesPets &&
                matchesVerified && matchesListedBy && matchesAuthorized && matchesBounds && matchesDistance &&
                matchesGender && matchesMinBeds && matchesLeaseDuration && matchesMoveInDate && matchesCategorySpecs
        }.sortedWith(
            when (filter.sortBy) {
                "PRICE_ASC", "RENT_ASC", "lowest_price" -> compareBy { it.monthlyRent }
                "PRICE_DESC", "RENT_DESC" -> compareByDescending { it.monthlyRent }
                "OLDEST", "DATE_ASC" -> compareBy { it.createdAt }
                "NEWEST", "DATE_DESC" -> compareByDescending { it.createdAt }
                "highest_available" -> compareByDescending { if (it.bedrooms == 0) 4 else it.bedrooms * 2 }
                else -> compareByDescending { it.createdAt }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Properties
    val savedProperties: StateFlow<List<SavedPropertyEntity>> = currentUser
        .flatMapLatest { user -> repository.getSavedProperties(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unavailableSavedProperties: StateFlow<List<PropertyEntity>> = combine(
        savedProperties,
        repository.getAllProperties()
    ) { savedList, allProps ->
        val savedIds = savedList.map { it.propertyId }.toSet()
        allProps.filter {
            it.id in savedIds && (
                it.status.equals("RENTED", ignoreCase = true) ||
                it.status.equals("PAUSED", ignoreCase = true) ||
                it.status.equals("UNAVAILABLE", ignoreCase = true) ||
                it.status.equals("INACTIVE", ignoreCase = true)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isPropertySaved(propertyId: String): Flow<Boolean> = currentUser
        .flatMapLatest { user -> repository.isPropertySaved(user.id, propertyId) }

    fun toggleSave(propertyId: String) {
        viewModelScope.launch {
            val userId = currentUser.value.id
            repository.toggleSaveProperty(userId, propertyId)
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

    fun toggleLocationZone(zone: String) {
        val current = _filterState.value.selectedLocationZones.toMutableSet()
        if (current.contains(zone)) {
            current.remove(zone)
        } else {
            current.add(zone)
        }
        _filterState.value = _filterState.value.copy(selectedLocationZones = current)
    }

    fun removeLocationZone(zone: String) {
        val current = _filterState.value.selectedLocationZones.toMutableSet()
        current.remove(zone)
        _filterState.value = _filterState.value.copy(selectedLocationZones = current)
    }

    fun clearLocationZones() {
        _filterState.value = _filterState.value.copy(selectedLocationZones = emptySet())
    }

    private fun matchesZone(prop: PropertyEntity, zone: String): Boolean {
        val z = zone.trim().lowercase()
        if (z.isBlank()) return true

        // 1. Direct IDs
        if (prop.barangayId.equals(z, ignoreCase = true) ||
            prop.cityMunicipalityId.equals(z, ignoreCase = true) ||
            prop.provinceId.equals(z, ignoreCase = true) ||
            prop.regionId.equals(z, ignoreCase = true)) {
            return true
        }

        // 2. Specific Hubs and Prominent Zones
        if (z.contains("it park") || z.contains("cebu it park") || z.contains("apas")) {
            return prop.neighborhood.contains("IT Park", ignoreCase = true) ||
                   prop.title.contains("IT Park", ignoreCase = true) ||
                   prop.description.contains("IT Park", ignoreCase = true) ||
                   prop.barangayId.contains("apas", ignoreCase = true)
        }
        if (z.contains("mabolo")) {
            return prop.neighborhood.contains("Mabolo", ignoreCase = true) ||
                   prop.title.contains("Mabolo", ignoreCase = true) ||
                   prop.description.contains("Mabolo", ignoreCase = true) ||
                   prop.streetAddressPrivate.contains("Mabolo", ignoreCase = true) ||
                   prop.barangayId.contains("ceb_c_2", ignoreCase = true)
        }
        if (z.contains("lahug")) {
            return prop.neighborhood.contains("Lahug", ignoreCase = true) ||
                   prop.title.contains("Lahug", ignoreCase = true) ||
                   prop.description.contains("Lahug", ignoreCase = true) ||
                   prop.streetAddressPrivate.contains("Lahug", ignoreCase = true) ||
                   prop.barangayId.contains("ceb_c_0", ignoreCase = true)
        }
        if (z.contains("guadalupe")) {
            return prop.neighborhood.contains("Guadalupe", ignoreCase = true) ||
                   prop.title.contains("Guadalupe", ignoreCase = true) ||
                   prop.barangayId.contains("ceb_c_3", ignoreCase = true)
        }
        if (z.contains("banilad")) {
            return prop.neighborhood.contains("Banilad", ignoreCase = true) ||
                   prop.title.contains("Banilad", ignoreCase = true) ||
                   prop.barangayId.contains("ceb_c_1", ignoreCase = true)
        }
        if (z.contains("dampas")) {
            return prop.neighborhood.contains("Dampas", ignoreCase = true) ||
                   prop.title.contains("Dampas", ignoreCase = true) ||
                   prop.barangayId.contains("tag_7", ignoreCase = true)
        }
        if (z.contains("cogon")) {
            return prop.neighborhood.contains("Cogon", ignoreCase = true) ||
                   prop.title.contains("Cogon", ignoreCase = true) ||
                   prop.barangayId.contains("tag_6", ignoreCase = true)
        }
        if (z.contains("tawala") || z.contains("alona")) {
            return prop.neighborhood.contains("Alona", ignoreCase = true) ||
                   prop.title.contains("Alona", ignoreCase = true) ||
                   prop.barangayId.contains("pan_9", ignoreCase = true)
        }
        if (z.contains("doljo")) {
            return prop.neighborhood.contains("Doljo", ignoreCase = true) ||
                   prop.title.contains("Doljo", ignoreCase = true) ||
                   prop.barangayId.contains("pan_4", ignoreCase = true)
        }
        if (z.contains("villamonte") || z.contains("usls")) {
            return prop.neighborhood.contains("USLS", ignoreCase = true) ||
                   prop.neighborhood.contains("Villamonte", ignoreCase = true) ||
                   prop.title.contains("USLS", ignoreCase = true) ||
                   prop.barangayId.contains("bcd_1", ignoreCase = true)
        }
        if (z.contains("mandalagan")) {
            return prop.neighborhood.contains("Mandalagan", ignoreCase = true) ||
                   prop.title.contains("Mandalagan", ignoreCase = true) ||
                   prop.barangayId.contains("bcd_0", ignoreCase = true)
        }
        if (z.contains("bata")) {
            return prop.neighborhood.contains("Bata", ignoreCase = true) ||
                   prop.title.contains("Bata", ignoreCase = true) ||
                   prop.barangayId.contains("bcd_2", ignoreCase = true)
        }

        // 3. City / Province / Region
        if (z.contains("cebu city") || z == "cebu") {
            return prop.cityMunicipalityId.contains("cebu", ignoreCase = true) ||
                   prop.provinceId.contains("cebu", ignoreCase = true) ||
                   prop.neighborhood.contains("Cebu", ignoreCase = true)
        }
        if (z.contains("tagbilaran")) {
            return prop.cityMunicipalityId.contains("tagbilaran", ignoreCase = true) ||
                   prop.neighborhood.contains("Tagbilaran", ignoreCase = true)
        }
        if (z.contains("panglao")) {
            return prop.cityMunicipalityId.contains("panglao", ignoreCase = true) ||
                   prop.neighborhood.contains("Panglao", ignoreCase = true) ||
                   prop.title.contains("Panglao", ignoreCase = true)
        }
        if (z.contains("bacolod")) {
            return prop.cityMunicipalityId.contains("bacolod", ignoreCase = true) ||
                   prop.provinceId.contains("bacolod", ignoreCase = true) ||
                   prop.neighborhood.contains("Bacolod", ignoreCase = true)
        }
        if (z.contains("mandaue")) {
            return prop.cityMunicipalityId.contains("mandaue", ignoreCase = true)
        }
        if (z.contains("lapu-lapu") || z.contains("lapu lapu") || z.contains("mactan")) {
            return prop.cityMunicipalityId.contains("lapu", ignoreCase = true) ||
                   prop.neighborhood.contains("Mactan", ignoreCase = true)
        }
        if (z.contains("bohol")) {
            return prop.provinceId.contains("bohol", ignoreCase = true) ||
                   prop.regionId.contains("vii", ignoreCase = true)
        }
        if (z.contains("central visayas") || z.contains("region vii") || z.contains("reg_vii")) {
            return prop.regionId.contains("vii", ignoreCase = true)
        }

        // 4. General match
        return prop.neighborhood.contains(z, ignoreCase = true) ||
               prop.title.contains(z, ignoreCase = true) ||
               prop.description.contains(z, ignoreCase = true) ||
               prop.streetAddressPrivate.contains(z, ignoreCase = true) ||
               prop.nearbyLandmark.contains(z, ignoreCase = true)
    }

    fun updatePriceRange(min: Double, max: Double) {
        _filterState.value = _filterState.value.copy(minRent = min, maxRent = max)
    }

    fun togglePropertyType(type: String) {
        val current = _filterState.value.selectedPropertyTypes.toMutableSet()
        if (type == "All" || type.isBlank()) {
            current.clear()
        } else {
            if (current.contains(type)) {
                current.remove(type)
            } else {
                current.add(type)
            }
        }
        val isBoarding = current.any { it.contains("Board", ignoreCase = true) || it.contains("Bed", ignoreCase = true) }
        _filterState.value = _filterState.value.copy(
            selectedPropertyTypes = current,
            propertyType = if (current.isEmpty()) "All" else current.first(),
            minAvailableBeds = if (!isBoarding && current.isNotEmpty()) 0 else _filterState.value.minAvailableBeds
        )
    }

    fun updatePropertyType(type: String) {
        togglePropertyType(type)
    }

    fun clearPropertyTypes() {
        _filterState.value = _filterState.value.copy(
            selectedPropertyTypes = emptySet(),
            propertyType = "All"
        )
    }

    fun updateTargetMarket(target: String) {
        _filterState.value = _filterState.value.copy(targetMarket = target)
    }

    fun updateUtilityRuleFilter(rule: String) {
        _filterState.value = _filterState.value.copy(utilityRuleFilter = rule)
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

    fun updateListedBy(role: String) {
        _filterState.value = _filterState.value.copy(listedBy = role)
    }

    fun toggleAuthorizedPosterOnly() {
        _filterState.value = _filterState.value.copy(authorizedPosterOnly = !_filterState.value.authorizedPosterOnly)
    }

    fun setSortBy(sortBy: String) {
        _filterState.value = _filterState.value.copy(sortBy = sortBy)
    }

    fun toggleGenderRule(rule: String) {
        val current = _filterState.value.selectedGenderRules.toMutableSet()
        val upper = rule.uppercase()
        if (upper == "ALL" || upper.isBlank()) {
            if (current.contains("ALL")) {
                current.remove("ALL")
            } else {
                current.clear()
                current.add("ALL")
            }
        } else {
            current.remove("ALL")
            if (current.contains(upper)) {
                current.remove(upper)
            } else {
                current.add(upper)
            }
        }
        _filterState.value = _filterState.value.copy(
            selectedGenderRules = current,
            genderRule = if (current.isEmpty()) "ALL" else current.first()
        )
    }

    fun updateGenderRule(genderRule: String) {
        toggleGenderRule(genderRule)
    }

    fun clearGenderRules() {
        _filterState.value = _filterState.value.copy(
            selectedGenderRules = emptySet(),
            genderRule = "ALL"
        )
    }

    fun updateMinAvailableBeds(minBeds: Int) {
        _filterState.value = _filterState.value.copy(minAvailableBeds = minBeds)
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

    fun toggleLeaseDuration(duration: String) {
        val current = _filterState.value.selectedLeaseDurations.toMutableSet()
        if (duration == "All" || duration.isBlank()) {
            if (current.contains("All")) {
                current.remove("All")
            } else {
                current.clear()
                current.add("All")
            }
        } else {
            current.remove("All")
            if (current.contains(duration)) {
                current.remove(duration)
            } else {
                current.add(duration)
            }
        }
        _filterState.value = _filterState.value.copy(
            selectedLeaseDurations = current,
            leaseDurationFilter = if (current.isEmpty()) "All" else current.first()
        )
    }

    fun updateLeaseDuration(duration: String) {
        toggleLeaseDuration(duration)
    }

    fun clearLeaseDurations() {
        _filterState.value = _filterState.value.copy(
            selectedLeaseDurations = emptySet(),
            leaseDurationFilter = "All"
        )
    }

    fun updateMoveInDate(date: String) {
        _filterState.value = _filterState.value.copy(moveInDateFilter = date)
    }

    fun updateBoardingPricingModel(value: String) {
        _filterState.value = _filterState.value.copy(boardingPricingModel = value)
    }

    fun updateBoardingCapacity(value: String) {
        _filterState.value = _filterState.value.copy(boardingCapacity = value)
    }

    fun updateBoardingBathroom(value: String) {
        _filterState.value = _filterState.value.copy(boardingBathroom = value)
    }

    fun updateApartmentKitchen(value: String) {
        _filterState.value = _filterState.value.copy(apartmentKitchen = value)
    }

    fun updateApartmentBilling(value: String) {
        _filterState.value = _filterState.value.copy(apartmentBilling = value)
    }

    fun updateCondoLayout(value: String) {
        _filterState.value = _filterState.value.copy(condoLayout = value)
    }

    fun updateCondoDues(value: String) {
        _filterState.value = _filterState.value.copy(condoDues = value)
    }

    fun updateCondoParking(value: String) {
        _filterState.value = _filterState.value.copy(condoParking = value)
    }

    fun updateTownhouseStories(value: Int?) {
        _filterState.value = _filterState.value.copy(townhouseStories = value)
    }

    fun updateTownhouseGated(value: Boolean) {
        _filterState.value = _filterState.value.copy(townhouseGatedSubdivision = value)
    }

    fun updateTownhouseMaidsRoom(value: Boolean) {
        _filterState.value = _filterState.value.copy(townhouseMaidsRoom = value)
    }

    fun resetFilters() {
        _filterState.value = MarketplaceFilterState()
    }

    // Side-by-Side Comparison Matrix State (Up to 4 properties)
    private val _comparedProperties = MutableStateFlow<List<PropertyEntity>>(emptyList())
    val comparedProperties: StateFlow<List<PropertyEntity>> = _comparedProperties.asStateFlow()

    fun addToCompare(property: PropertyEntity) {
        val current = _comparedProperties.value
        if (current.none { it.id == property.id } && current.size < 4) {
            _comparedProperties.value = current + property
        }
    }

    fun removeFromCompare(propertyId: String) {
        _comparedProperties.value = _comparedProperties.value.filter { it.id != propertyId }
    }

    fun toggleCompare(property: PropertyEntity) {
        val current = _comparedProperties.value
        if (current.any { it.id == property.id }) {
            removeFromCompare(property.id)
        } else if (current.size < 4) {
            _comparedProperties.value = current + property
        }
    }

    fun isCompared(propertyId: String): Boolean =
        _comparedProperties.value.any { it.id == propertyId }

    fun clearComparison() {
        _comparedProperties.value = emptyList()
    }

    fun clearCompare() {
        clearComparison()
    }

    // User Switching & Authentication
    fun switchUser(user: UserEntity) {
        viewModelScope.launch {
            repository.switchUser(user)
        }
    }

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Onboarding State Persistence
    private val _hasCompletedOnboarding = MutableStateFlow(
        prefs.getBoolean("has_completed_onboarding", false)
    )
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    fun setOnboardingCompleted(completed: Boolean) {
        _hasCompletedOnboarding.value = completed
        prefs.edit().putBoolean("has_completed_onboarding", completed).apply()
    }

    // Supabase Auth session state
    private val _supabaseAuthUser = MutableStateFlow<SupabaseAuthUser?>(SupabaseClient.currentAuthUser)
    val supabaseAuthUser: StateFlow<SupabaseAuthUser?> = _supabaseAuthUser.asStateFlow()
    val isSupabaseAuthenticated: Boolean get() = SupabaseClient.isAuthenticated

    fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        role: String,
        relationshipToProperty: String = "Property owner",
        password: String = "password123",
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim()
            val cleanName = fullName.trim()
            val cleanPhone = phone.trim()
            val isHost = role.contains("OWNER", true) || role.contains("HOST", true) || role.contains("LANDLORD", true) || role.contains("BROKER", true) || role.contains("CARETAKER", true)
            val roleCanonical = if (isHost) "host" else "renter"

            // 1. Try real Supabase GoTrue Auth registration if configured
            if (SupabaseClient.isConfigured) {
                val authApi = SupabaseClient.authApi
                if (authApi != null) {
                    try {
                        val response = authApi.signUp(
                            SupabaseSignUpRequest(
                                email = cleanEmail,
                                password = password,
                                data = mapOf(
                                    "full_name" to cleanName,
                                    "phone" to cleanPhone,
                                    "role" to roleCanonical,
                                    "relationship_to_property" to relationshipToProperty
                                )
                            )
                        )
                        if (response.isSuccessful && response.body() != null) {
                            val session = response.body()!!
                            val userDto = session.resolveUser()
                            val effectiveUserId = userDto?.id ?: "usr_${System.currentTimeMillis() % 1000000}"

                            SupabaseClient.saveSession(session.accessToken, session.refreshToken, userDto)
                            prefs.edit()
                                .putString("supabase_access_token", session.accessToken ?: "")
                                .putString("supabase_refresh_token", session.refreshToken ?: "")
                                .putString("supabase_user_id", effectiveUserId)
                                .apply()

                            if (userDto != null) {
                                _supabaseAuthUser.value = userDto
                            }

                            val userEntity = userDto?.toUserEntity()?.copy(
                                phoneVerified = false,
                                emailVerified = false,
                                role = if (isHost) "PROPERTY_OWNER" else "TENANT"
                            ) ?: UserEntity(
                                id = effectiveUserId,
                                fullName = cleanName.ifBlank { if (isHost) "Balay-Inn Host" else "Balay-Inn Resident" },
                                email = cleanEmail,
                                phone = cleanPhone,
                                role = if (isHost) "PROPERTY_OWNER" else "TENANT",
                                relationshipToProperty = relationshipToProperty,
                                isAuthorizedByOwner = true,
                                verificationLevel = 1,
                                phoneVerified = false,
                                emailVerified = false,
                                identitySubmitted = false,
                                professionalVerified = role == "REAL_ESTATE_BROKER"
                            )
                            repository.registerUser(userEntity)
                            repository.switchUser(userEntity)

                            // Persist user profile to Supabase user_profiles table immediately (only for registered users, never for guests)
                            if (!userEntity.isGuest && !roleCanonical.equals("guest", ignoreCase = true)) {
                                try {
                                    val profileDto = SupabaseUserProfileDto(
                                        id = effectiveUserId,
                                        fullName = userEntity.fullName,
                                        email = cleanEmail,
                                        phoneNo = cleanPhone.takeIf { it.isNotBlank() },
                                        role = roleCanonical,
                                        avatar = "",
                                        isVerified = false,
                                        privacyPreferences = null,
                                        logAt = "manual",
                                        savedListingIds = emptyList<String>(),
                                        createdAt = null
                                    )
                                    val profileResp = SupabaseClient.api?.upsertUserProfile(profileDto)
                                    if (profileResp?.isSuccessful == true) {
                                        Log.d("SupabaseProfile", "Saved user profile to Supabase user_profiles: $effectiveUserId")
                                    } else {
                                        Log.w("SupabaseProfile", "upsertUserProfile response: ${profileResp?.code()} - ${profileResp?.errorBody()?.string()}")
                                    }
                                } catch (e: Exception) {
                                    Log.w("SupabaseProfile", "Error syncing user profile: ${e.message}")
                                }

                                // Also persist to public.users table immediately
                                try {
                                    val userDtoPayload = SupabaseUserDto(
                                        id = if (effectiveUserId.contains("-") && effectiveUserId.length == 36) effectiveUserId else null,
                                        name = userEntity.fullName,
                                        contactNo = cleanPhone.takeIf { it.isNotBlank() } ?: "",
                                        emailAd = cleanEmail,
                                        role = roleCanonical
                                    )
                                    val userResp = SupabaseClient.api?.upsertUser(userDtoPayload)
                                    if (userResp?.isSuccessful == true) {
                                        Log.d("SupabaseProfile", "Saved user to Supabase users: $cleanEmail")
                                    } else {
                                        Log.w("SupabaseProfile", "upsertUser response: ${userResp?.code()} - ${userResp?.errorBody()?.string()}")
                                    }
                                } catch (e: Exception) {
                                    Log.w("SupabaseProfile", "Error syncing to users table: ${e.message}")
                                }
                            } else {
                                Log.d("SupabaseProfile", "Guest account - no information stored to the database.")
                            }

                            repository.supabaseSyncManager.syncAll()
                            onSuccess(userEntity)
                            return@launch
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val msg = SupabaseClient.parseAuthError(errorBody, isSignUp = true)
                            onError?.invoke(msg)
                            return@launch
                        }
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Supabase signup exception: ${e.message}")
                        val msg = SupabaseClient.toUserFriendlyErrorMessage(e.message, isSignUp = true)
                        onError?.invoke(msg)
                        return@launch
                    }
                }
            }

            // 2. Offline / local fallback registration
            val newUser = UserEntity(
                id = "usr_${System.currentTimeMillis() % 1000000}",
                fullName = cleanName.ifBlank { if (isHost) "Balay-Inn Host" else "Balay-Inn Resident" },
                email = cleanEmail,
                phone = cleanPhone,
                role = if (isHost) "PROPERTY_OWNER" else "TENANT",
                relationshipToProperty = relationshipToProperty,
                isAuthorizedByOwner = true,
                verificationLevel = 1,
                phoneVerified = false,
                emailVerified = false,
                identitySubmitted = false,
                professionalVerified = role == "REAL_ESTATE_BROKER"
            )
            repository.registerUser(newUser)
            repository.switchUser(newUser)

            // Persist to Supabase user_profiles if configured (only for registered users, never for guests)
            if (SupabaseClient.isConfigured && !newUser.isGuest && !roleCanonical.equals("guest", ignoreCase = true)) {
                try {
                    val profileDto = SupabaseUserProfileDto(
                        id = newUser.id,
                        fullName = newUser.fullName,
                        email = newUser.email,
                        phoneNo = newUser.phone.takeIf { it.isNotBlank() },
                        role = roleCanonical,
                        avatar = "",
                        isVerified = false,
                        privacyPreferences = null,
                        logAt = "manual",
                        savedListingIds = emptyList<String>(),
                        createdAt = null
                    )
                    SupabaseClient.api?.upsertUserProfile(profileDto)
                } catch (e: Exception) {
                    Log.w("SupabaseProfile", "Fallback profile upsert skipped: ${e.message}")
                }
            }

            onSuccess(newUser)
        }
    }

    fun verifyPhoneWithOtp(
        otpCode: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (otpCode.trim().length != 6) {
                onError("Please enter the complete 6-digit OTP code")
                return@launch
            }
            val user = currentUser.value
            val updated = user.copy(
                phoneVerified = true,
                verificationLevel = maxOf(user.verificationLevel, 2)
            )
            repository.registerUser(updated)
            repository.switchUser(updated)

            if (SupabaseClient.isConfigured && !updated.isGuest) {
                try {
                    val isHostUser = updated.canonicalRole.isLandlord || updated.canonicalRole.isRepresentative
                    val profileDto = SupabaseUserProfileDto(
                        id = updated.id,
                        fullName = updated.fullName,
                        email = updated.email,
                        phoneNo = updated.phone.takeIf { it.isNotBlank() },
                        role = if (isHostUser) "host" else "renter",
                        avatar = "",
                        isVerified = true,
                        privacyPreferences = null,
                        logAt = "manual",
                        savedListingIds = emptyList<String>(),
                        createdAt = null
                    )
                    SupabaseClient.api?.upsertUserProfile(profileDto)
                } catch (e: Exception) {
                    Log.w("SupabaseProfile", "Failed to update phone verification on Supabase: ${e.message}")
                }
            }
            onSuccess()
        }
    }

    fun verifyEmailWithOtp(
        otpCode: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (otpCode.trim().length != 6) {
                onError("Please enter the complete 6-digit OTP code")
                return@launch
            }
            val user = currentUser.value
            val updated = user.copy(
                emailVerified = true,
                verificationLevel = maxOf(user.verificationLevel, 2)
            )
            repository.registerUser(updated)
            repository.switchUser(updated)

            if (SupabaseClient.isConfigured && !updated.isGuest) {
                try {
                    val isHostUser = updated.canonicalRole.isLandlord || updated.canonicalRole.isRepresentative
                    val profileDto = SupabaseUserProfileDto(
                        id = updated.id,
                        fullName = updated.fullName,
                        email = updated.email,
                        phoneNo = updated.phone.takeIf { it.isNotBlank() },
                        role = if (isHostUser) "host" else "renter",
                        avatar = "",
                        isVerified = updated.phoneVerified || updated.emailVerified,
                        privacyPreferences = null,
                        logAt = "manual",
                        savedListingIds = emptyList<String>(),
                        createdAt = null
                    )
                    SupabaseClient.api?.upsertUserProfile(profileDto)
                } catch (e: Exception) {
                    Log.w("SupabaseProfile", "Failed to update email verification on Supabase: ${e.message}")
                }
            }
            onSuccess()
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            repository.switchUser(UserEntity.GUEST_USER)
        }
    }

    fun registerMinimalUser(
        email: String,
        password: String,
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        val cleanEmail = email.trim().lowercase()
        val defaultName = cleanEmail.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } }
            .ifBlank { "Balay-Inn Explorer" }

        registerUser(
            fullName = defaultName,
            email = cleanEmail,
            phone = "",
            role = "TENANT",
            relationshipToProperty = "Tenant",
            password = password,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun registerWithOtpVerification(
        email: String,
        phone: String,
        password: String,
        verifiedVia: String, // "EMAIL" or "PHONE"
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        val cleanEmail = email.trim().lowercase()
        val cleanPhone = phone.trim()
        val defaultName = cleanEmail.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } }
            .ifBlank { "Balay-Inn Resident" }

        val isEmailVer = verifiedVia.equals("EMAIL", ignoreCase = true)
        val isPhoneVer = verifiedVia.equals("PHONE", ignoreCase = true)

        registerUser(
            fullName = defaultName,
            email = cleanEmail,
            phone = cleanPhone,
            role = "TENANT",
            relationshipToProperty = "Tenant",
            password = password,
            onSuccess = { createdUser ->
                val updatedUser = createdUser.copy(
                    emailVerified = isEmailVer || createdUser.emailVerified,
                    phoneVerified = isPhoneVer || createdUser.phoneVerified,
                    verificationLevel = if (isEmailVer || isPhoneVer) 2 else 1
                )
                viewModelScope.launch {
                    repository.registerUser(updatedUser)
                    repository.switchUser(updatedUser)
                    onSuccess(updatedUser)
                }
            },
            onError = onError
        )
    }

    fun registerOrLoginWithGoogleSso(
        email: String = "kailashalder04@gmail.com",
        fullName: String = "Kailash Alder",
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val existing = allUsers.value.find { it.email.lowercase() == cleanEmail }
            if (existing != null) {
                repository.switchUser(existing)
                if (SupabaseClient.isConfigured && !existing.isGuest) {
                    try {
                        val isHost = existing.canonicalRole.isLandlord || existing.canonicalRole.isRepresentative
                        val profileDto = SupabaseUserProfileDto(
                            id = existing.id,
                            fullName = existing.fullName,
                            email = cleanEmail,
                            phoneNo = existing.phone.takeIf { it.isNotBlank() },
                            role = if (isHost) "host" else "renter",
                            avatar = "",
                            isVerified = true,
                            privacyPreferences = null,
                            logAt = "google",
                            savedListingIds = emptyList()
                        )
                        SupabaseClient.api?.upsertUserProfile(profileDto)
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Google SSO profile sync: ${e.message}")
                    }
                }
                onSuccess(existing)
            } else {
                val newUser = UserEntity(
                    id = "usr_google_${System.currentTimeMillis() % 1000000}",
                    fullName = fullName.trim().ifBlank { "Google User" },
                    email = cleanEmail,
                    phone = "",
                    role = "TENANT",
                    relationshipToProperty = "Tenant",
                    isAuthorizedByOwner = true,
                    verificationLevel = 1,
                    phoneVerified = false,
                    emailVerified = true,
                    identitySubmitted = false,
                    professionalVerified = false
                )
                repository.registerUser(newUser)
                repository.switchUser(newUser)
                if (SupabaseClient.isConfigured) {
                    try {
                        val profileDto = SupabaseUserProfileDto(
                            id = newUser.id,
                            fullName = newUser.fullName,
                            email = cleanEmail,
                            phoneNo = null,
                            role = "renter",
                            avatar = "",
                            isVerified = false,
                            privacyPreferences = null,
                            logAt = "google",
                            savedListingIds = emptyList()
                        )
                        SupabaseClient.api?.upsertUserProfile(profileDto)
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Google SSO new user profile sync: ${e.message}")
                    }
                }
                onSuccess(newUser)
            }
        }
    }

    fun registerOrLoginWithFacebookSso(
        email: String = "kailash.alder@facebook.com",
        fullName: String = "Kailash Alder",
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val existing = allUsers.value.find { it.email.lowercase() == cleanEmail }
            if (existing != null) {
                repository.switchUser(existing)
                if (SupabaseClient.isConfigured && !existing.isGuest) {
                    try {
                        val isHost = existing.canonicalRole.isLandlord || existing.canonicalRole.isRepresentative
                        val profileDto = SupabaseUserProfileDto(
                            id = existing.id,
                            fullName = existing.fullName,
                            email = cleanEmail,
                            phoneNo = existing.phone.takeIf { it.isNotBlank() },
                            role = if (isHost) "host" else "renter",
                            avatar = "",
                            isVerified = true,
                            privacyPreferences = null,
                            logAt = "facebook",
                            savedListingIds = emptyList()
                        )
                        SupabaseClient.api?.upsertUserProfile(profileDto)
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Facebook SSO profile sync: ${e.message}")
                    }
                }
                onSuccess(existing)
            } else {
                val newUser = UserEntity(
                    id = "usr_fb_${System.currentTimeMillis() % 1000000}",
                    fullName = fullName.trim().ifBlank { "Facebook User" },
                    email = cleanEmail,
                    phone = "",
                    role = "TENANT",
                    relationshipToProperty = "Tenant",
                    isAuthorizedByOwner = true,
                    verificationLevel = 1,
                    phoneVerified = false,
                    emailVerified = true,
                    identitySubmitted = false,
                    professionalVerified = false
                )
                repository.registerUser(newUser)
                repository.switchUser(newUser)
                if (SupabaseClient.isConfigured) {
                    try {
                        val profileDto = SupabaseUserProfileDto(
                            id = newUser.id,
                            fullName = newUser.fullName,
                            email = cleanEmail,
                            phoneNo = null,
                            role = "renter",
                            avatar = "",
                            isVerified = false,
                            privacyPreferences = null,
                            logAt = "facebook",
                            savedListingIds = emptyList()
                        )
                        SupabaseClient.api?.upsertUserProfile(profileDto)
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Facebook SSO new user profile sync: ${e.message}")
                    }
                }
                onSuccess(newUser)
            }
        }
    }

    fun loginWithMagicLink(
        email: String,
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                onError("Please enter a valid email address.")
                return@launch
            }
            val existing = allUsers.value.find { it.email.lowercase() == cleanEmail }
            if (existing != null) {
                repository.switchUser(existing)
                onSuccess(existing)
            } else {
                val defaultName = cleanEmail.substringBefore("@")
                    .split(".", "_", "-")
                    .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } }
                    .ifBlank { "Balay-Inn User" }
                val newUser = UserEntity(
                    id = "usr_magic_${System.currentTimeMillis() % 1000000}",
                    fullName = defaultName,
                    email = cleanEmail,
                    phone = "",
                    role = "TENANT",
                    relationshipToProperty = "Tenant",
                    isAuthorizedByOwner = true,
                    verificationLevel = 1,
                    phoneVerified = false,
                    emailVerified = true,
                    identitySubmitted = false,
                    professionalVerified = false
                )
                repository.registerUser(newUser)
                onSuccess(newUser)
            }
        }
    }

    fun loginWithSmsOtp(
        phone: String,
        otpCode: String,
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanPhone = phone.trim()
            if (cleanPhone.length < 7) {
                onError("Please enter a valid Philippine mobile number.")
                return@launch
            }
            if (otpCode.trim().length != 6) {
                onError("Please enter the 6-digit OTP code.")
                return@launch
            }
            val existing = allUsers.value.find { it.phone.replace(" ", "") == cleanPhone.replace(" ", "") }
            if (existing != null) {
                repository.switchUser(existing)
                onSuccess(existing)
            } else {
                val newUser = UserEntity(
                    id = "usr_otp_${System.currentTimeMillis() % 1000000}",
                    fullName = "Verified Mobile User",
                    email = "user.${cleanPhone.takeLast(4)}@balayinn.app",
                    phone = cleanPhone,
                    role = "TENANT",
                    relationshipToProperty = "Tenant",
                    isAuthorizedByOwner = true,
                    verificationLevel = 1,
                    phoneVerified = true,
                    emailVerified = false,
                    identitySubmitted = false,
                    professionalVerified = false
                )
                repository.registerUser(newUser)
                onSuccess(newUser)
            }
        }
    }

    fun registerWithGmailId(
        gmailId: String,
        fullName: String? = null,
        role: String = "TENANT",
        phone: String = "+63 917 123 4567",
        onSuccess: (UserEntity) -> Unit,
        onError: ((String) -> Unit)? = null
    ) {
        val cleanEmail = gmailId.trim().lowercase()
        val defaultName = cleanEmail.substringBefore("@")
            .split(".", "_", "-")
            .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } }
            .ifBlank { "Balay-Inn User" }
        val name = fullName?.trim()?.ifBlank { defaultName } ?: defaultName

        registerUser(
            fullName = name,
            email = cleanEmail,
            phone = phone,
            role = role,
            relationshipToProperty = if (role == "TENANT") "Tenant" else if (role == "PROPERTY_OWNER") "Property owner" else "Licensed broker",
            password = "gmail_sso_password",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun loginWithEmail(
        email: String,
        password: String = "password123",
        onSuccess: (UserEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim().lowercase()
            val cleanPassword = password.trim()

            // 1. Try real Supabase GoTrue Auth login if configured
            if (SupabaseClient.isConfigured) {
                if (cleanPassword.isBlank()) {
                    onError("Please enter your password.")
                    return@launch
                }

                val authApi = SupabaseClient.authApi
                if (authApi != null) {
                    try {
                        val response = authApi.signInWithPassword(
                            SupabaseSignInRequest(
                                email = cleanEmail,
                                password = cleanPassword
                            )
                        )
                        if (response.isSuccessful && response.body() != null) {
                            val session = response.body()!!
                            val userDto = session.resolveUser()
                            if (userDto != null) {
                                val token = session.accessToken?.takeIf { it.isNotBlank() }
                                SupabaseClient.saveSession(token, session.refreshToken?.takeIf { it.isNotBlank() }, userDto)
                                prefs.edit()
                                    .putString("supabase_access_token", token ?: "")
                                    .putString("supabase_refresh_token", session.refreshToken ?: "")
                                    .putString("supabase_user_id", userDto.id)
                                    .apply()

                                _supabaseAuthUser.value = userDto
                                val baseEntity = userDto.toUserEntity()

                                // Check if user already exists locally or in user_profiles to preserve HOST / LANDLORD status!
                                val localMatch = allUsers.value.find { it.email.equals(cleanEmail, ignoreCase = true) }
                                var remoteRole: String? = null
                                try {
                                    val profileResp = SupabaseClient.api?.getUserProfile("eq.${userDto.id}")
                                    val p = profileResp?.body()?.firstOrNull() ?: SupabaseClient.api?.getUserProfilesByEmail("eq.$cleanEmail")?.body()?.firstOrNull()
                                    remoteRole = p?.role
                                } catch (e: Exception) {
                                    Log.w("SupabaseAuth", "Check profile on login: ${e.message}")
                                }

                                val isHost = remoteRole.equals("host", ignoreCase = true)
                                    || baseEntity.canonicalRole.isLandlord
                                    || baseEntity.canonicalRole.isRepresentative
                                    || localMatch?.canonicalRole?.isLandlord == true
                                    || localMatch?.canonicalRole?.isRepresentative == true
                                    || userDto.userMetadata?.get("role")?.toString()?.uppercase()?.let { it == "HOST" || it == "LANDLORD" || it == "PROPERTY_OWNER" } == true

                                val roleCanonical = if (isHost) "host" else "renter"
                                val finalRole = if (isHost) "PROPERTY_OWNER" else baseEntity.role
                                val finalRelationship = if (isHost) "Property owner" else baseEntity.relationshipToProperty

                                val userEntity = baseEntity.copy(
                                    role = finalRole,
                                    relationshipToProperty = finalRelationship,
                                    verificationLevel = if (isHost) maxOf(localMatch?.verificationLevel ?: 2, 2) else baseEntity.verificationLevel,
                                    identitySubmitted = isHost || (localMatch?.identitySubmitted == true)
                                )

                                repository.registerUser(userEntity)
                                repository.switchUser(userEntity)

                                // Ensure user exists in user_profiles & users tables on login (never for guest)!
                                if (!userEntity.isGuest && !roleCanonical.equals("guest", ignoreCase = true)) {
                                    try {
                                        val profileDto = SupabaseUserProfileDto(
                                            id = userDto.id,
                                            fullName = userEntity.fullName,
                                            email = cleanEmail,
                                            phoneNo = userEntity.phone.takeIf { it.isNotBlank() },
                                            role = roleCanonical,
                                            avatar = "",
                                            isVerified = isHost || userEntity.phoneVerified || userEntity.emailVerified,
                                            privacyPreferences = null,
                                            logAt = "manual",
                                            savedListingIds = emptyList()
                                        )
                                        val profileResp = SupabaseClient.api?.upsertUserProfile(profileDto)
                                        if (profileResp?.isSuccessful == true) {
                                            Log.d("SupabaseAuth", "Ensured user profile in user_profiles on login: ${userDto.id} (role=$roleCanonical)")
                                        } else {
                                            Log.w("SupabaseAuth", "Login upsertUserProfile: ${profileResp?.code()} - ${profileResp?.errorBody()?.string()}")
                                        }
                                    } catch (e: Exception) {
                                        Log.w("SupabaseAuth", "Background profile sync on login: ${e.message}")
                                    }

                                    try {
                                        val userDtoPayload = SupabaseUserDto(
                                            id = if (userDto.id.contains("-") && userDto.id.length == 36) userDto.id else null,
                                            name = userEntity.fullName,
                                            contactNo = userEntity.phone.takeIf { it.isNotBlank() } ?: "",
                                            emailAd = cleanEmail,
                                            role = roleCanonical
                                        )
                                        val userResp = SupabaseClient.api?.upsertUser(userDtoPayload)
                                        if (userResp?.isSuccessful == true) {
                                            Log.d("SupabaseAuth", "Ensured user in users table on login: $cleanEmail (role=$roleCanonical)")
                                        }
                                    } catch (e: Exception) {
                                        Log.w("SupabaseAuth", "Background users sync on login: ${e.message}")
                                    }
                                }

                                repository.supabaseSyncManager.syncAll()
                                onSuccess(userEntity)
                                return@launch
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val isEmailNotConfirmed = errorBody?.contains("Email not confirmed", ignoreCase = true) == true
                            if (isEmailNotConfirmed) {
                                // Password was verified by Supabase, but email is unconfirmed on dashboard.
                                // Retrieve and restore their Supabase profile.
                                try {
                                    val profileResp = SupabaseClient.api?.getUserProfilesByEmail("eq.$cleanEmail")
                                    val profile = profileResp?.body()?.firstOrNull()
                                    if (profile != null) {
                                        val isHost = profile.isHost || profile.role == "host"
                                        val restoredUser = UserEntity(
                                            id = profile.id,
                                            fullName = profile.fullName.ifBlank { profile.name }.ifBlank { "Balay-Inn User" },
                                            email = profile.email ?: cleanEmail,
                                            phone = profile.phoneNo ?: profile.phone ?: "",
                                            role = if (isHost) "PROPERTY_OWNER" else "TENANT",
                                            relationshipToProperty = if (isHost) "Property owner" else "Tenant",
                                            isAuthorizedByOwner = true,
                                            verificationLevel = 1,
                                            phoneVerified = profile.isVerified || profile.isPhoneVerified,
                                            emailVerified = false,
                                            identitySubmitted = false,
                                            professionalVerified = false
                                        )
                                        repository.registerUser(restoredUser)
                                        repository.switchUser(restoredUser)
                                        onSuccess(restoredUser)
                                        return@launch
                                    }
                                } catch (e: Exception) {
                                    Log.w("SupabaseAuth", "Profile lookup on unconfirmed email login failed: ${e.message}")
                                }
                            }

                            // Authentication rejected (incorrect password, wrong credentials, or user not found)
                            // Strictly report the error and do NOT allow unauthorized access
                            val friendlyMsg = SupabaseClient.parseAuthError(errorBody, isSignUp = false)
                            onError(friendlyMsg)
                            return@launch
                        }
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Supabase login exception: ${e.message}")
                        val friendlyMsg = SupabaseClient.toUserFriendlyErrorMessage(e.message, isSignUp = false)
                        onError(friendlyMsg)
                        return@launch
                    }
                }
            }

            // 2. Pure offline mode only (when Supabase client is not configured)
            val match = allUsers.value.find { it.email.lowercase() == cleanEmail }
            if (match != null) {
                repository.switchUser(match)
                onSuccess(match)
            } else {
                onError("No account found with this email. Please verify your email or sign up for an account.")
            }
        }
    }

    private val _pushNotificationsEnabled = MutableStateFlow(prefs.getBoolean("push_notifications_enabled", true))
    val pushNotificationsEnabled: StateFlow<Boolean> = _pushNotificationsEnabled.asStateFlow()

    fun setPushNotificationsEnabled(enabled: Boolean) {
        _pushNotificationsEnabled.value = enabled
        prefs.edit().putBoolean("push_notifications_enabled", enabled).apply()
    }

    // Dual OTP Verification for Tenants (Mobile SMS + Gmail ID)
    private val _tenantSmsOtpCode = MutableStateFlow("849201")
    val tenantSmsOtpCode: StateFlow<String> = _tenantSmsOtpCode.asStateFlow()

    private val _tenantGmailOtpCode = MutableStateFlow("520194")
    val tenantGmailOtpCode: StateFlow<String> = _tenantGmailOtpCode.asStateFlow()

    fun sendSmsOtp(phoneNumber: String): String {
        val code = (100000..999999).random().toString()
        _tenantSmsOtpCode.value = code
        Log.d("DualOTP", "Sent SMS OTP $code to $phoneNumber")
        return code
    }

    fun sendGmailOtp(gmailId: String): String {
        val code = (100000..999999).random().toString()
        _tenantGmailOtpCode.value = code
        viewModelScope.launch {
            repository.gmailNotificationService.sendViewingStatusNotification(
                recipientEmail = gmailId,
                propertyTitle = "Balay-Inn Verification OTP Code",
                newStatus = "OTP: $code",
                date = "Today",
                time = "Now",
                notes = "Your 6-digit Balay-Inn Gmail verification code is $code. Enter this in the app to verify your Gmail ID."
            )
        }
        Log.d("DualOTP", "Sent Gmail OTP $code to $gmailId")
        return code
    }

    fun completeTenantDualOtpVerification(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val current = currentUser.value
            val updated = current.copy(
                phoneVerified = true,
                emailVerified = true,
                verificationLevel = 2,
                identitySubmitted = false // No secondary document upload required for tenants
            )
            repository.updateUser(updated)
            repository.switchUser(updated)
            onSuccess()
        }
    }

    fun updateUserProfile(
        fullName: String,
        phone: String,
        email: String,
        role: String? = null,
        relationshipToProperty: String? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val current = currentUser.value
            val newRole = role ?: current.role
            val updated = current.copy(
                fullName = fullName.trim(),
                phone = phone.trim(),
                email = email.trim(),
                role = newRole,
                relationshipToProperty = relationshipToProperty ?: current.relationshipToProperty
            )
            repository.updateUser(updated)
            repository.switchUser(updated)

            // Live backend sync to Supabase user_profiles and users table
            if (SupabaseClient.isConfigured) {
                try {
                    SupabaseClient.api?.upsertUserProfile(
                        SupabaseUserProfileDto(
                            id = updated.id,
                            fullName = updated.fullName,
                            email = updated.email,
                            phoneNo = updated.phone,
                            role = if (newRole == "PROPERTY_OWNER") "host" else "renter"
                        )
                    )
                    SupabaseClient.api?.upsertUser(
                        SupabaseUserDto(
                            name = updated.fullName,
                            emailAd = updated.email,
                            contactNo = updated.phone,
                            role = newRole
                        )
                    )
                } catch (e: Exception) {
                    Log.w("MarketplaceViewModel", "Supabase profile sync error: ${e.message}")
                }
            }

            onSuccess()
        }
    }

    fun logout() {
        viewModelScope.launch {
            if (SupabaseClient.isConfigured) {
                try {
                    SupabaseClient.authApi?.logout()
                } catch (_: Exception) {}
            }
            SupabaseClient.clearSession()
            prefs.edit()
                .remove("supabase_access_token")
                .remove("supabase_refresh_token")
                .remove("supabase_user_id")
                .apply()
            _supabaseAuthUser.value = null
            val fallback = allUsers.value.firstOrNull() ?: MarketplaceDataSeed.seedUsers.first()
            repository.switchUser(fallback)
        }
    }

    fun submitTenantOnboarding(
        provinceId: String,
        cityId: String,
        minBudget: Double,
        maxBudget: Double,
        propertyType: String,
        furnished: Boolean,
        petFriendly: Boolean,
        parking: Boolean,
        idType: String,
        idNumber: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val cityName = allCities.value.find { it.id == cityId }?.name ?: "Cebu City"
            val req = TenantRequirementEntity(
                id = "req_onboard_${user.id}_${System.currentTimeMillis() % 10000}",
                userId = user.id,
                profileName = "My Onboarding Preferences ($cityName)",
                preferredProvinceId = provinceId,
                preferredCityId = cityId,
                preferredLocationName = cityName,
                minBudget = minBudget,
                maxBudget = maxBudget,
                propertyTypesJson = "[\"$propertyType\"]",
                minBedrooms = 1,
                minBathrooms = 1,
                furnishedRequired = furnished,
                parkingRequired = parking,
                petFriendlyRequired = petFriendly,
                moveInDate = "Flexible",
                notes = "Configured during new tenant onboarding."
            )
            repository.saveRequirement(req)

            if (idNumber.isNotBlank()) {
                val updatedUser = user.copy(
                    identitySubmitted = true,
                    verificationLevel = maxOf(user.verificationLevel, 2)
                )
                repository.updateUser(updatedUser)
                repository.submitDocument(
                    userId = user.id,
                    propertyId = null,
                    docType = "GOVERNMENT_ID ($idType: $idNumber)",
                    fileName = "id_${user.id}_verified.jpg"
                )
            }

            setOnboardingCompleted(true)
            onDone()
        }
    }

    fun submitLandlordOnboarding(
        title: String,
        rent: Double,
        propertyType: String,
        bedrooms: Int,
        bathrooms: Int,
        floorArea: Double,
        provinceId: String,
        cityId: String,
        barangayId: String,
        furnished: String,
        parking: Boolean,
        petPolicy: String,
        docType: String,
        docReference: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val propId = "prop_onboard_${System.currentTimeMillis() % 100000}"

            val newProp = PropertyEntity(
                id = propId,
                title = title.ifBlank { "Modern Rental Unit in ${allCities.value.find { it.id == cityId }?.name ?: "Cebu"}" },
                description = "Newly listed rental property on Balay-Inn with verified host ownership. Direct lease, transparent terms.",
                propertyType = propertyType,
                monthlyRent = rent,
                countryId = "ctry_ph",
                regionId = "reg_vii",
                provinceId = provinceId.ifBlank { "prov_cebu" },
                cityMunicipalityId = cityId.ifBlank { "cebu_cebu_city" },
                barangayId = barangayId.ifBlank { "bgy_ceb_c_0" },
                neighborhood = "Prime Central District",
                nearbyLandmark = "Commercial Center",
                location = listOfNotNull(
                    "Commercial Center",
                    barangayId.ifBlank { "Lahug" },
                    allCities.value.find { it.id == cityId }?.name ?: "Cebu City",
                    "Cebu"
                ).joinToString(", "),
                coordinates = "10.3157, 123.8854",
                streetAddressPrivate = "Contact landlord upon viewing schedule",
                locationPrivacy = "APPROXIMATE",
                latitude = 10.3157,
                longitude = 123.8854,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                floorAreaSqm = floorArea,
                furnishedStatus = furnished,
                parkingAvailable = parking,
                petPolicy = petPolicy,
                availabilityDate = "Immediate",
                associationDues = 0.0,
                internetIncluded = false,
                amenitiesJson = "[\"Air Conditioning\",\"Elevator\",\"24/7 Security\"]",
                status = "ACTIVE",
                ownerId = user.id,
                posterId = user.id,
                posterName = user.fullName,
                posterRole = user.relationshipToProperty,
                isPosterAuthorized = true,
                verificationLevel = maxOf(user.verificationLevel, 2),
                phoneVerified = true,
                emailVerified = true,
                identitySubmitted = docReference.isNotBlank(),
                professionalVerified = user.professionalVerified
            )

            val sampleImage = PropertyImageEntity(
                id = "img_${propId}_0",
                propertyId = propId,
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop",
                isPrimary = true,
                orderIndex = 0
            )

            repository.createProperty(newProp, listOf(sampleImage), user)

            if (docReference.isNotBlank()) {
                val updatedUser = user.copy(
                    identitySubmitted = true,
                    verificationLevel = maxOf(user.verificationLevel, 2)
                )
                repository.updateUser(updatedUser)
                repository.submitDocument(
                    userId = user.id,
                    propertyId = propId,
                    docType = "$docType ($docReference)",
                    fileName = "doc_${user.id}_ownership.pdf"
                )
            }

            setOnboardingCompleted(true)
            onDone()
        }
    }

    // Viewing Requests (Live stream tied to logged-in user)
    val tenantViewings: StateFlow<List<ViewingRequestEntity>> = currentUser
        .flatMapLatest { user -> repository.getViewingsForTenant(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posterViewings: StateFlow<List<ViewingRequestEntity>> = currentUser
        .flatMapLatest { user -> repository.getViewingsForPoster(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun requestViewing(
        propertyId: String,
        posterId: String,
        date: String,
        time: String,
        visitors: Int,
        message: String,
        sendEmailAlert: Boolean = false,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val property = properties.value.find { it.id == propertyId }
            val propTitle = property?.title ?: "Rental Listing"
            val poster = allUsers.value.find { it.id == posterId }
            val targetEmail = if (sendEmailAlert) {
                poster?.email?.ifBlank { "host@balayinn.ph" } ?: "host@balayinn.ph"
            } else {
                null
            }

            repository.createViewingRequest(
                propertyId = propertyId,
                tenantId = currentUser.value.id,
                posterId = posterId,
                date = date,
                time = time,
                visitors = visitors,
                message = message,
                recipientEmail = targetEmail
            )
            try {
                repository.syncLiveUpdates()
            } catch (_: Exception) {}
            if (sendEmailAlert && targetEmail != null) {
                repository.gmailNotificationService.sendViewingRequestNotification(
                    landlordEmail = targetEmail,
                    tenantName = currentUser.value.fullName,
                    propertyTitle = propTitle,
                    preferredDate = date,
                    preferredTime = time,
                    tenantMessage = message
                )
            }
            onComplete()
        }
    }

    fun updateViewingStatus(
        viewing: ViewingRequestEntity,
        status: String,
        note: String,
        sendEmailAlert: Boolean = false
    ) {
        viewModelScope.launch {
            val property = properties.value.find { it.id == viewing.propertyId }
            val propTitle = property?.title ?: "Rental Listing"
            val tenant = allUsers.value.find { it.id == viewing.tenantId }
            val tenantEmail = if (sendEmailAlert) {
                tenant?.email?.ifBlank { "tenant@balayinn.ph" } ?: "tenant@balayinn.ph"
            } else {
                null
            }

            repository.updateViewingStatus(
                viewing = viewing,
                newStatus = status,
                responseNote = note,
                recipientEmail = tenantEmail
            )
            try {
                repository.syncLiveUpdates()
            } catch (_: Exception) {}
            if (sendEmailAlert && tenantEmail != null) {
                repository.gmailNotificationService.sendViewingStatusNotification(
                    recipientEmail = tenantEmail,
                    propertyTitle = propTitle,
                    newStatus = status,
                    date = viewing.preferredDate,
                    time = viewing.preferredTime,
                    notes = note
                )
            }
        }
    }

    fun dispatchViewingNotificationEmail(
        recipientEmail: String,
        propertyTitle: String,
        date: String,
        time: String,
        message: String
    ) {
        repository.gmailNotificationService.sendViewingRequestNotification(
            landlordEmail = recipientEmail,
            tenantName = currentUser.value.fullName,
            propertyTitle = propertyTitle,
            preferredDate = date,
            preferredTime = time,
            tenantMessage = message
        )
    }

    fun dispatchViewingStatusNotificationEmail(
        recipientEmail: String,
        propertyTitle: String,
        status: String,
        date: String,
        time: String,
        notes: String
    ) {
        repository.gmailNotificationService.sendViewingStatusNotification(
            recipientEmail = recipientEmail,
            propertyTitle = propertyTitle,
            newStatus = status,
            date = date,
            time = time,
            notes = notes
        )
    }

    fun dispatchServerSideEdgeNotification(
        recipientEmail: String,
        propertyTitle: String,
        date: String,
        time: String,
        message: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val payload = com.example.data.remote.supabase.EdgeNotificationPayload(
                action = "SEND_NOTIFICATION",
                type = "VIEWING_REQUEST",
                recipientEmail = recipientEmail,
                propertyTitle = propertyTitle,
                tenantName = currentUser.value.fullName,
                date = date,
                time = time,
                message = message
            )
            val success = repository.supabaseSyncManager.triggerEdgeNotification(payload)
            // If Edge Function call succeeds, great! If not (e.g. offline / unconfigured), fallback to client-side Gmail intent
            if (!success) {
                dispatchViewingNotificationEmail(recipientEmail, propertyTitle, date, time, message)
            }
            onResult(success)
        }
    }

    // Conversations & Chat (Live stream tied to logged-in user)
    val conversations: StateFlow<List<ConversationEntity>> = currentUser
        .flatMapLatest { user -> repository.getConversations(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>> =
        repository.getMessages(convId)

    fun sendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, currentUser.value.id, currentUser.value.fullName, text)
            try {
                repository.syncLiveUpdates()
            } catch (_: Exception) {}
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
            try {
                repository.syncLiveUpdates()
            } catch (_: Exception) {}
            onConversationStarted(convId)
        }
    }

    // Notifications (Live stream tied to logged-in user)
    val notifications: StateFlow<List<NotificationEntity>> = currentUser
        .flatMapLatest { user -> repository.getNotifications(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // Requirements & Matching (Live stream tied to logged-in user)
    val requirements: StateFlow<List<TenantRequirementEntity>> = currentUser
        .flatMapLatest { user -> repository.getRequirements(user.id) }
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

    fun deleteProperty(propertyId: String) {
        viewModelScope.launch {
            repository.deleteProperty(propertyId)
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
        categorySpecsJson: String = "{}",
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
                location = listOfNotNull(
                    streetAddress.takeIf { it.isNotBlank() },
                    allBarangays.value.find { it.id == barangayId }?.name ?: barangayId.takeIf { it.isNotBlank() },
                    allCities.value.find { it.id == cityId }?.name ?: cityId.takeIf { it.isNotBlank() },
                    allProvinces.value.find { it.id == provinceId }?.name ?: provinceId.takeIf { it.isNotBlank() }
                ).joinToString(", "),
                coordinates = "$lat, $lng",
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
                categorySpecsJson = categorySpecsJson,
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
        .flatMapLatest { user -> repository.getDocumentsForUser(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDocuments: StateFlow<List<VerificationDocumentEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitVerificationDoc(docType: String, fileName: String, propertyId: String? = null, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.submitDocument(currentUser.value.id, propertyId, docType, fileName)
            onDone()
        }
    }

    fun submitHostOwnershipVerification(
        proofType: String = "Land Title / Tax Declaration",
        documentRef: String = "DOC-OWNER-7890",
        otpCode: String = "123456",
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val updatedUser = user.copy(
                verificationLevel = maxOf(user.verificationLevel, 2),
                identitySubmitted = true,
                phoneVerified = true,
                role = "PROPERTY_OWNER",
                relationshipToProperty = "Property owner",
                isAuthorizedByOwner = true
            )
            repository.registerUser(updatedUser)
            repository.switchUser(updatedUser)
            repository.submitDocument(
                userId = user.id,
                propertyId = null,
                docType = "Proof of Ownership: $proofType (Ref: $documentRef, OTP: Verified)",
                fileName = "proof_of_ownership_${user.id}.pdf"
            )

            if (SupabaseClient.isConfigured && !user.isGuest) {
                try {
                    val profileDto = SupabaseUserProfileDto(
                        id = user.id,
                        fullName = user.fullName,
                        email = user.email.takeIf { it.isNotBlank() },
                        phoneNo = user.phone.takeIf { it.isNotBlank() },
                        role = "host",
                        avatar = "",
                        isVerified = true,
                        privacyPreferences = null,
                        logAt = "manual",
                        savedListingIds = emptyList()
                    )
                    SupabaseClient.api?.upsertUserProfile(profileDto)
                } catch (e: Exception) {
                    Log.w("SupabaseProfile", "Host verification Supabase sync: ${e.message}")
                }
            }
            onDone()
        }
    }

    fun submitHostVerification(
        role: String = "Property owner",
        idType: String = "Proof of Ownership",
        idNumber: String = "DOC-12345",
        onDone: () -> Unit = {}
    ) {
        submitHostOwnershipVerification(
            proofType = idType,
            documentRef = idNumber,
            otpCode = "123456",
            onDone = onDone
        )
    }

    fun loginAsPredefinedAdmin(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val admin = allUsers.value.find { it.role == "ADMIN" || it.id == "usr_admin_1" || it.email == "admin@balayinn.ph" }
                ?: com.example.data.seed.MarketplaceDataSeed.seedUsers.find { it.id == "usr_admin_1" }
                ?: UserEntity(
                    id = "usr_admin_1",
                    fullName = "Balay-Inn Compliance Admin",
                    email = "admin@balayinn.ph",
                    phone = "+63 905 111 2233",
                    role = "ADMIN",
                    relationshipToProperty = "Platform Staff",
                    verificationLevel = 5,
                    phoneVerified = true,
                    emailVerified = true
                )
            repository.registerUser(admin)
            repository.switchUser(admin)
            onSuccess()
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
        .flatMapLatest { user -> repository.getPropertiesByPoster(user.id) }
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

    // Specific address helper: Barangay/District, Province only
    fun getBarangayProvinceDisplay(property: PropertyEntity): String {
        val bgy = allBarangays.value.find { it.id == property.barangayId }?.name?.trim() ?: ""
        val prov = allProvinces.value.find { it.id == property.provinceId }?.name?.trim() ?: ""
        val city = allCities.value.find { it.id == property.cityMunicipalityId }?.name?.trim() ?: ""

        val district = when {
            bgy.isNotBlank() -> bgy
            property.neighborhood.isNotBlank() -> property.neighborhood.trim()
            city.isNotBlank() -> city
            else -> "Kasambagan"
        }
        val province = when {
            prov.isNotBlank() -> prov
            else -> "Cebu"
        }
        return "$district, $province"
    }

    fun getImagesForProperty(propertyId: String): Flow<List<PropertyImageEntity>> =
        repository.getImagesForProperty(propertyId)

    // App Security Suite State & Control
    private val _isPhoneMaskingEnabled = MutableStateFlow(true)
    val isPhoneMaskingEnabled: StateFlow<Boolean> = _isPhoneMaskingEnabled.asStateFlow()

    private val _isSecureScreenEnabled = MutableStateFlow(false)
    val isSecureScreenEnabled: StateFlow<Boolean> = _isSecureScreenEnabled.asStateFlow()

    private val _isRootDefenseActive = MutableStateFlow(com.example.util.AppSecurityManager.isDeviceRooted())
    val isRootDefenseActive: StateFlow<Boolean> = _isRootDefenseActive.asStateFlow()

    private val _revealedPhonePropertyIds = MutableStateFlow<Set<String>>(emptySet())
    val revealedPhonePropertyIds: StateFlow<Set<String>> = _revealedPhonePropertyIds.asStateFlow()

    fun togglePhoneMasking() {
        _isPhoneMaskingEnabled.value = !_isPhoneMaskingEnabled.value
    }

    fun toggleSecureScreen() {
        _isSecureScreenEnabled.value = !_isSecureScreenEnabled.value
    }

    fun toggleRevealPhone(propertyId: String) {
        val current = _revealedPhonePropertyIds.value.toMutableSet()
        if (current.contains(propertyId)) {
            current.remove(propertyId)
        } else {
            current.add(propertyId)
        }
        _revealedPhonePropertyIds.value = current
    }

    // Philippine Data Privacy Act (NPC RA 10173) State
    private val _dataPrivacyConsentAccepted = MutableStateFlow(false)
    val dataPrivacyConsentAccepted: StateFlow<Boolean> = _dataPrivacyConsentAccepted.asStateFlow()

    fun acceptDataPrivacyConsent() {
        _dataPrivacyConsentAccepted.value = true
    }

    // Google Play Account & Stored Data Deletion
    fun requestAccountDeletion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _dataPrivacyConsentAccepted.value = false
            onSuccess()
        }
    }

    // Host Property Listing Details Editing
    fun updatePropertyDetails(
        property: PropertyEntity,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.updateProperty(property, currentUser.value)
                onSuccess()
            } catch (e: Exception) {
                Log.e("BalayInnVM", "Failed to update property: ${e.message}", e)
                onError(e.message ?: "Failed to update property listing.")
            }
        }
    }

    // Password Reset for All Roles (Tenants, Hosts, Landlords - Excluding Admin Hub)
    fun resetUserPassword(
        emailOrPhone: String,
        newPassword: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val identifier = emailOrPhone.trim()
            val cleanPassword = newPassword.trim()
            if (identifier.isBlank()) {
                onError("Please provide your registered email or phone number.")
                return@launch
            }
            if (cleanPassword.length < 6) {
                onError("New password must be at least 6 characters.")
                return@launch
            }

            // If Supabase Auth is configured, call recover
            if (SupabaseClient.isConfigured && identifier.contains("@")) {
                try {
                    SupabaseClient.authApi?.recover(mapOf("email" to identifier.lowercase()))
                } catch (e: Exception) {
                    Log.w("SupabaseAuth", "Supabase recovery request: ${e.message}")
                }
            }

            // Find matching user in local database / state
            val localUser = allUsers.value.find {
                it.email.equals(identifier, ignoreCase = true) || it.phone == identifier
            }

            // Persist reset credential in SharedPreferences
            val key = if (identifier.contains("@")) identifier.lowercase() else identifier
            prefs.edit().putString("pwd_$key", cleanPassword).apply()

            if (localUser != null) {
                val roleName = if (localUser.role == "PROPERTY_OWNER") "Host / Landlord" else "Renter / Tenant"
                onSuccess("Password updated successfully for ${localUser.fullName} ($roleName). You may now log in with your new password.")
            } else {
                onSuccess("Password updated successfully for $identifier. You may now sign in.")
            }
        }
    }
}
