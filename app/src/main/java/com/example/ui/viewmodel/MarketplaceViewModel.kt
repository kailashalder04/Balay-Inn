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
import com.example.data.seed.MarketplaceDataSeed
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
    val sortBy: String = "NEWEST", // NEWEST, PRICE_ASC, PRICE_DESC, HIGHEST_AVAILABLE, LOWEST_PRICE, RECENTLY_UPDATED
    val bounds: MapBounds? = null,
    val distanceFilter: DistanceFilter? = null,
    val listedBy: String = "All", // All, Direct Owner, Licensed Broker, Property Manager, Caretaker, Family Member, Authorized Representative
    val authorizedPosterOnly: Boolean = false,
    val genderRule: String = "all", // all, female_only, male_only
    val minAvailableBeds: Int = 0,
    val targetMarket: String = "All",
    val utilityRuleFilter: String = "All",
    val leaseDurationFilter: String = "All", // All, Flexible (1-3 mos), Mid-term (4-6 mos), Annual (12+ mos)
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
        val savedToken = prefs.getString("supabase_access_token", null)
        val savedRefresh = prefs.getString("supabase_refresh_token", null)
        if (!savedToken.isNullOrBlank()) {
            SupabaseClient.saveSession(savedToken, savedRefresh)
        }

        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
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

            // Province
            val matchesProvince = filter.provinceId.isBlank() || prop.provinceId == filter.provinceId

            // City
            val matchesCity = filter.cityMunicipalityId.isBlank() || prop.cityMunicipalityId == filter.cityMunicipalityId

            // Barangay
            val matchesBarangay = filter.barangayId.isBlank() || prop.barangayId == filter.barangayId

            // Price Range
            val matchesPrice = prop.monthlyRent in filter.minRent..filter.maxRent

            // Property Type
            val matchesType = when {
                filter.propertyType == "All" -> true
                prop.propertyType.equals(filter.propertyType, ignoreCase = true) -> true
                else -> {
                    val filterCat = PhilippineRentalPropertyCategory.fromPropertyType(filter.propertyType)
                    filterCat == propCategory
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

            // Gender Rule
            val matchesGender = when (filter.genderRule) {
                "female_only" -> prop.title.contains("Ladies", ignoreCase = true) ||
                        prop.title.contains("Female", ignoreCase = true) ||
                        prop.description.contains("Ladies only", ignoreCase = true) ||
                        prop.description.contains("Female only", ignoreCase = true)
                "male_only" -> prop.title.contains("Men", ignoreCase = true) ||
                        prop.title.contains("Male", ignoreCase = true) ||
                        prop.description.contains("Men only", ignoreCase = true) ||
                        prop.description.contains("Male only", ignoreCase = true)
                else -> true
            }

            // Min Available Beds
            val matchesMinBeds = filter.minAvailableBeds <= 0 || (prop.bedrooms == 0 || prop.bedrooms >= filter.minAvailableBeds)

            // Flexible Lease Duration Filter
            val matchesLeaseDuration = when (filter.leaseDurationFilter) {
                "Flexible (1-3 mos)", "Short-term (< 6 mos)" ->
                    prop.leaseDurationMonths <= 3 ||
                    prop.description.contains("flexible", ignoreCase = true) ||
                    prop.description.contains("short term", ignoreCase = true)
                "Mid-term (4-6 mos)", "Semester (4-6 mos)" ->
                    prop.leaseDurationMonths in 4..6 ||
                    prop.description.contains("semester", ignoreCase = true)
                "Annual (12+ mos)", "Long-term (12+ mos)" ->
                    prop.leaseDurationMonths >= 12
                else -> true
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

            matchesKeyword && matchesProvince && matchesCity && matchesBarangay && matchesPrice &&
                matchesType && matchesTargetMarket && matchesUtilityRule &&
                matchesBeds && matchesFurnished && matchesParking && matchesPets &&
                matchesVerified && matchesListedBy && matchesAuthorized && matchesBounds && matchesDistance &&
                matchesGender && matchesMinBeds && matchesLeaseDuration && matchesMoveInDate && matchesCategorySpecs
        }.sortedWith(
            when (filter.sortBy) {
                "PRICE_ASC", "lowest_price" -> compareBy { it.monthlyRent }
                "PRICE_DESC" -> compareByDescending { it.monthlyRent }
                "highest_available" -> compareByDescending { if (it.bedrooms == 0) 4 else it.bedrooms * 2 }
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

    fun updateGenderRule(genderRule: String) {
        _filterState.value = _filterState.value.copy(genderRule = genderRule)
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

    fun updateLeaseDuration(duration: String) {
        _filterState.value = _filterState.value.copy(leaseDurationFilter = duration)
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
            val cleanPassword = password.trim().ifBlank { "password123" }

            // 1. Try real Supabase GoTrue Auth registration if configured
            if (SupabaseClient.isConfigured) {
                val authApi = SupabaseClient.authApi
                if (authApi != null) {
                    try {
                        val response = authApi.signUp(
                            SupabaseSignUpRequest(
                                email = cleanEmail,
                                password = cleanPassword,
                                data = mapOf(
                                    "full_name" to cleanName,
                                    "phone" to cleanPhone,
                                    "role" to role,
                                    "relationship_to_property" to relationshipToProperty
                                )
                            )
                        )
                        if (response.isSuccessful && response.body() != null) {
                            val session = response.body()!!
                            val userDto = session.user
                            if (userDto != null) {
                                SupabaseClient.saveSession(session.accessToken, session.refreshToken, userDto)
                                prefs.edit()
                                    .putString("supabase_access_token", session.accessToken)
                                    .putString("supabase_refresh_token", session.refreshToken)
                                    .putString("supabase_user_id", userDto.id)
                                    .apply()

                                _supabaseAuthUser.value = userDto
                                val userEntity = userDto.toUserEntity()
                                repository.registerUser(userEntity)
                                repository.supabaseSyncManager.syncAll()
                                onSuccess(userEntity)
                                return@launch
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val msg = SupabaseClient.parseAuthError(errorBody)
                            if (response.code() in 400..499) {
                                onError?.invoke(msg)
                                return@launch
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Supabase signup exception: ${e.message}. Falling back to local profile registration.")
                    }
                }
            }

            // 2. Offline / local fallback registration
            val newUser = UserEntity(
                id = "usr_${System.currentTimeMillis() % 1000000}",
                fullName = cleanName,
                email = cleanEmail,
                phone = cleanPhone,
                role = role,
                relationshipToProperty = relationshipToProperty,
                isAuthorizedByOwner = true,
                verificationLevel = 1,
                phoneVerified = true,
                emailVerified = true,
                identitySubmitted = false,
                professionalVerified = role == "REAL_ESTATE_BROKER"
            )
            repository.registerUser(newUser)
            onSuccess(newUser)
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
            if (SupabaseClient.isConfigured && cleanPassword.isNotBlank()) {
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
                            val userDto = session.user
                            if (userDto != null) {
                                SupabaseClient.saveSession(session.accessToken, session.refreshToken, userDto)
                                prefs.edit()
                                    .putString("supabase_access_token", session.accessToken)
                                    .putString("supabase_refresh_token", session.refreshToken)
                                    .putString("supabase_user_id", userDto.id)
                                    .apply()

                                _supabaseAuthUser.value = userDto
                                val userEntity = userDto.toUserEntity()
                                repository.registerUser(userEntity)
                                repository.supabaseSyncManager.syncAll()
                                onSuccess(userEntity)
                                return@launch
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            val msg = SupabaseClient.parseAuthError(errorBody)
                            if (response.code() == 400 || response.code() == 401) {
                                onError(msg)
                                return@launch
                            }
                        }
                    } catch (e: Exception) {
                        Log.w("SupabaseAuth", "Supabase login exception: ${e.message}. Checking local profiles.")
                    }
                }
            }

            // 2. Offline / local fallback or Demo user matching
            val match = allUsers.value.find { it.email.lowercase() == cleanEmail }
            if (match != null) {
                repository.switchUser(match)
                onSuccess(match)
            } else {
                val partialMatch = allUsers.value.find { it.fullName.contains(cleanEmail, ignoreCase = true) }
                if (partialMatch != null) {
                    repository.switchUser(partialMatch)
                    onSuccess(partialMatch)
                } else {
                    onError("No account found for '$email'. Please sign up or select a quick demo profile.")
                }
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
            val updated = current.copy(
                fullName = fullName.trim(),
                phone = phone.trim(),
                email = email.trim(),
                role = role ?: current.role,
                relationshipToProperty = relationshipToProperty ?: current.relationshipToProperty
            )
            repository.updateUser(updated)
            repository.switchUser(updated)
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
        sendEmailAlert: Boolean = false,
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
            if (sendEmailAlert) {
                val property = properties.value.find { it.id == propertyId }
                val propTitle = property?.title ?: "Rental Listing"
                val poster = allUsers.value.find { it.id == posterId }
                val targetEmail = poster?.email?.ifBlank { "host@balayinn.ph" } ?: "host@balayinn.ph"
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
            repository.updateViewingStatus(viewing, status, note)
            if (sendEmailAlert) {
                val property = properties.value.find { it.id == viewing.propertyId }
                val propTitle = property?.title ?: "Rental Listing"
                val tenant = allUsers.value.find { it.id == viewing.tenantId }
                val tenantEmail = tenant?.email?.ifBlank { "tenant@balayinn.ph" } ?: "tenant@balayinn.ph"
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
}
