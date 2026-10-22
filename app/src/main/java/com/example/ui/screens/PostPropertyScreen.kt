package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ApartmentDetails
import com.example.data.model.BoardingHouseDetails
import com.example.data.model.CondominiumDetails
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.data.model.PropertyCategorySpecifications
import com.example.data.model.RoleVerificationDetails
import com.example.data.model.TownhouseDetails
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.SearchableDropdownField
import com.example.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostPropertyScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Multi-Step Progress State (1: Role, 2: Base Info, 3: Specifications, 4: Review & Publish)
    var currentStep by remember { mutableIntStateOf(1) }

    // -------------------------------------------------------------
    // STEP 1: ROLE-BASED VERIFICATION STATES
    // -------------------------------------------------------------
    var userRole by remember { mutableStateOf("Property owner") } // "Property owner", "Authorized representative / Caretaker", "Sublessor / Master tenant"
    // Owner specific
    var isTitleOwnerConfirmed by remember { mutableStateOf(true) }
    var ownerIdType by remember { mutableStateOf("Philippine National ID (PhilSys)") }
    var ownerIdNumber by remember { mutableStateOf("") }
    var ownerContactPref by remember { mutableStateOf("Direct Phone Call & SMS") }
    // Representative specific
    var landlordFullName by remember { mutableStateOf("") }
    var repRelationship by remember { mutableStateOf("Caretaker / Property Custodian") }
    var isAuthorizedByOwner by remember { mutableStateOf("Yes") } // "Yes", "No"
    var authorizationDocType by remember { mutableStateOf("Special Power of Attorney (SPA)") }
    var repPhoneNumber by remember { mutableStateOf("") }
    // Sublessor specific
    var masterLandlordName by remember { mutableStateOf("") }
    var hasSubleasePermission by remember { mutableStateOf("Yes") }
    var masterLeaseDuration by remember { mutableStateOf("6 to 12 months") }

    // -------------------------------------------------------------
    // STEP 2: BASE PROPERTY INFORMATION STATES
    // -------------------------------------------------------------
    var selectedProvinceId by remember { mutableStateOf("prov_bohol") }
    var selectedCityId by remember { mutableStateOf("bohol_panglao") }
    var selectedBarangayId by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }

    // Supported Core Property Types: Boarding House, Apartment, Condominium, Studio, Townhouse
    var selectedPropertyType by remember { mutableStateOf("Boarding House") }

    // Media & Marketing
    val uploadedPhotos = remember { mutableStateListOf<String>() }
    var listingTitle by remember { mutableStateOf("") }
    var listingDescription by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            scope.launch(Dispatchers.IO) {
                uris.forEach { uri ->
                    val path = copyUriToInternalStorage(context, uri)
                    if (path.isNotBlank() && !uploadedPhotos.contains(path)) {
                        uploadedPhotos.add(path)
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------
    // STEP 3: CONDITIONAL SPECIFICATIONS STATES
    // -------------------------------------------------------------
    // Condition 1: Boarding House
    var bhPricingModel by remember { mutableStateOf("Per Person/Bed") } // "Per Person/Bed" vs "Per Room"
    var bhMonthlyRentStr by remember { mutableStateOf("2500") }
    var bhRoomCapacity by remember { mutableStateOf("4-bed bunk") } // "Single", "Double bed", "4-bed bunk", "6-bed bunk"
    var bhTenantPreference by remember { mutableStateOf("Mixed") } // "Male Only", "Female Only", "Mixed"
    var bhBathroomType by remember { mutableStateOf("Shared/Communal") } // "Private/En-suite" vs "Shared/Communal"
    val bhUtilities = remember { mutableStateListOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included") }

    // Condition 2: Apartment
    var aptMonthlyRentStr by remember { mutableStateOf("12000") }
    var aptFloorAreaStr by remember { mutableStateOf("42") }
    var aptBedrooms by remember { mutableIntStateOf(1) }
    var aptBathrooms by remember { mutableIntStateOf(1) }
    var aptLeaseTermMin by remember { mutableStateOf("1 year") } // "6 months", "1 year"
    var aptFurnishing by remember { mutableStateOf("Semi-Furnished") } // "Bare", "Semi-Furnished", "Fully Furnished"
    var aptKitchenType by remember { mutableStateOf("Indoor/Integral") } // "Indoor/Integral" vs "Outdoor/Dirty Kitchen"
    var aptUtilityBilling by remember { mutableStateOf("Own Meter") } // "Own Meter" vs "Sub-metered by Landlord"

    // Condition 3: Condominium & Studio
    var condoMonthlyRentStr by remember { mutableStateOf("22000") }
    var condoAssocDuesStatus by remember { mutableStateOf("Inclusive in rent") } // "Inclusive in rent" vs "Exclusive / Tenant pays separately"
    var condoUnitLayout by remember { mutableStateOf("Studio") } // "Studio", "1BR", "2BR", "Loft", "Bi-level"
    var condoFloorAreaStr by remember { mutableStateOf("28") }
    var condoFloorLevel by remember { mutableStateOf("14th Floor") }
    var condoParking by remember { mutableStateOf("Available for extra fee") } // "Included in rent", "Available for extra fee", "None"
    val condoAmenities = remember { mutableStateListOf("Pool", "Gym", "24/7 Security") }
    var condoPetPolicy by remember { mutableStateOf("Pet-Friendly") } // "Strictly No Pets" vs "Pet-Friendly"

    // Condition 4: Townhouse
    var thMonthlyRentStr by remember { mutableStateOf("30000") }
    var thLotAreaStr by remember { mutableStateOf("120") }
    var thFloorAreaStr by remember { mutableStateOf("95") }
    var thStories by remember { mutableIntStateOf(2) }
    var thBedrooms by remember { mutableIntStateOf(3) }
    var thBathrooms by remember { mutableIntStateOf(2) }
    var thGarageCapacity by remember { mutableIntStateOf(2) }
    var thHasMaidsRoom by remember { mutableStateOf(false) }
    var thIsGatedSubdivision by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List Rental Property", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Step Progress Indicator Header
            MultiStepProgressHeader(
                currentStep = currentStep,
                onStepClick = { step ->
                    if (step < currentStep) currentStep = step
                }
            )

            // Step Content Animated
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "step_content"
            ) { step ->
                when (step) {
                    1 -> Step1RoleVerification(
                        userRole = userRole,
                        onRoleChange = { userRole = it },
                        isTitleOwnerConfirmed = isTitleOwnerConfirmed,
                        onTitleOwnerConfirmedChange = { isTitleOwnerConfirmed = it },
                        ownerIdType = ownerIdType,
                        onOwnerIdTypeChange = { ownerIdType = it },
                        ownerIdNumber = ownerIdNumber,
                        onOwnerIdNumberChange = { ownerIdNumber = it },
                        ownerContactPref = ownerContactPref,
                        onOwnerContactPrefChange = { ownerContactPref = it },
                        landlordFullName = landlordFullName,
                        onLandlordFullNameChange = { landlordFullName = it },
                        repRelationship = repRelationship,
                        onRepRelationshipChange = { repRelationship = it },
                        isAuthorizedByOwner = isAuthorizedByOwner,
                        onIsAuthorizedByOwnerChange = { isAuthorizedByOwner = it },
                        authorizationDocType = authorizationDocType,
                        onAuthorizationDocTypeChange = { authorizationDocType = it },
                        repPhoneNumber = repPhoneNumber,
                        onRepPhoneNumberChange = { repPhoneNumber = it },
                        masterLandlordName = masterLandlordName,
                        onMasterLandlordNameChange = { masterLandlordName = it },
                        hasSubleasePermission = hasSubleasePermission,
                        onHasSubleasePermissionChange = { hasSubleasePermission = it },
                        masterLeaseDuration = masterLeaseDuration,
                        onMasterLeaseDurationChange = { masterLeaseDuration = it },
                        onNext = {
                            // Validate Step 1
                            when (userRole) {
                                "Property owner" -> {
                                    if (!isTitleOwnerConfirmed) {
                                        scope.launch { snackbarHostState.showSnackbar("Please confirm property ownership declaration.") }
                                        return@Step1RoleVerification
                                    }
                                }
                                "Authorized representative / Caretaker" -> {
                                    if (landlordFullName.isBlank()) {
                                        scope.launch { snackbarHostState.showSnackbar("Please enter the property owner's full name.") }
                                        return@Step1RoleVerification
                                    }
                                    if (isAuthorizedByOwner != "Yes") {
                                        scope.launch { snackbarHostState.showSnackbar("You must be authorized by the owner to list this property.") }
                                        return@Step1RoleVerification
                                    }
                                }
                                else -> {
                                    if (masterLandlordName.isBlank()) {
                                        scope.launch { snackbarHostState.showSnackbar("Please enter the head landlord's name.") }
                                        return@Step1RoleVerification
                                    }
                                }
                            }
                            currentStep = 2
                        }
                    )
                    2 -> Step2BasePropertyInformation(
                        allProvinces = allProvinces,
                        allCities = allCities,
                        allBarangays = allBarangays,
                        selectedProvinceId = selectedProvinceId,
                        onProvinceSelected = { selectedProvinceId = it },
                        selectedCityId = selectedCityId,
                        onCitySelected = { selectedCityId = it },
                        selectedBarangayId = selectedBarangayId,
                        onBarangaySelected = { selectedBarangayId = it },
                        streetAddress = streetAddress,
                        onStreetAddressChange = { streetAddress = it },
                        neighborhood = neighborhood,
                        onNeighborhoodChange = { neighborhood = it },
                        landmark = landmark,
                        onLandmarkChange = { landmark = it },
                        selectedPropertyType = selectedPropertyType,
                        onPropertyTypeSelected = { selectedPropertyType = it },
                        uploadedPhotos = uploadedPhotos,
                        onLaunchPhotoPicker = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRemovePhoto = { idx -> uploadedPhotos.removeAt(idx) },
                        listingTitle = listingTitle,
                        onListingTitleChange = { listingTitle = it },
                        listingDescription = listingDescription,
                        onListingDescriptionChange = { listingDescription = it },
                        onBack = { currentStep = 1 },
                        onNext = {
                            if (listingTitle.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Please provide a listing title.") }
                                return@Step2BasePropertyInformation
                            }
                            if (streetAddress.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Please provide the street address or building name.") }
                                return@Step2BasePropertyInformation
                            }
                            if (uploadedPhotos.size < 3) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please upload at least 3 photos (currently ${uploadedPhotos.size}/10).")
                                }
                                return@Step2BasePropertyInformation
                            }
                            currentStep = 3
                        }
                    )
                    3 -> Step3ConditionalSpecifications(
                        propertyType = selectedPropertyType,
                        // Boarding House
                        bhPricingModel = bhPricingModel,
                        onBhPricingModelChange = { bhPricingModel = it },
                        bhMonthlyRentStr = bhMonthlyRentStr,
                        onBhMonthlyRentChange = { bhMonthlyRentStr = it },
                        bhRoomCapacity = bhRoomCapacity,
                        onBhRoomCapacityChange = { bhRoomCapacity = it },
                        bhTenantPreference = bhTenantPreference,
                        onBhTenantPreferenceChange = { bhTenantPreference = it },
                        bhBathroomType = bhBathroomType,
                        onBhBathroomTypeChange = { bhBathroomType = it },
                        bhUtilities = bhUtilities,
                        onToggleBhUtility = { u ->
                            if (bhUtilities.contains(u)) bhUtilities.remove(u) else bhUtilities.add(u)
                        },
                        // Apartment
                        aptMonthlyRentStr = aptMonthlyRentStr,
                        onAptMonthlyRentChange = { aptMonthlyRentStr = it },
                        aptFloorAreaStr = aptFloorAreaStr,
                        onAptFloorAreaChange = { aptFloorAreaStr = it },
                        aptBedrooms = aptBedrooms,
                        onAptBedroomsChange = { aptBedrooms = it },
                        aptBathrooms = aptBathrooms,
                        onAptBathroomsChange = { aptBathrooms = it },
                        aptLeaseTermMin = aptLeaseTermMin,
                        onAptLeaseTermMinChange = { aptLeaseTermMin = it },
                        aptFurnishing = aptFurnishing,
                        onAptFurnishingChange = { aptFurnishing = it },
                        aptKitchenType = aptKitchenType,
                        onAptKitchenTypeChange = { aptKitchenType = it },
                        aptUtilityBilling = aptUtilityBilling,
                        onAptUtilityBillingChange = { aptUtilityBilling = it },
                        // Condo & Studio
                        condoMonthlyRentStr = condoMonthlyRentStr,
                        onCondoMonthlyRentChange = { condoMonthlyRentStr = it },
                        condoAssocDuesStatus = condoAssocDuesStatus,
                        onCondoAssocDuesStatusChange = { condoAssocDuesStatus = it },
                        condoUnitLayout = condoUnitLayout,
                        onCondoUnitLayoutChange = { condoUnitLayout = it },
                        condoFloorAreaStr = condoFloorAreaStr,
                        onCondoFloorAreaChange = { condoFloorAreaStr = it },
                        condoFloorLevel = condoFloorLevel,
                        onCondoFloorLevelChange = { condoFloorLevel = it },
                        condoParking = condoParking,
                        onCondoParkingChange = { condoParking = it },
                        condoAmenities = condoAmenities,
                        onToggleCondoAmenity = { a ->
                            if (condoAmenities.contains(a)) condoAmenities.remove(a) else condoAmenities.add(a)
                        },
                        condoPetPolicy = condoPetPolicy,
                        onCondoPetPolicyChange = { condoPetPolicy = it },
                        // Townhouse
                        thMonthlyRentStr = thMonthlyRentStr,
                        onThMonthlyRentChange = { thMonthlyRentStr = it },
                        thLotAreaStr = thLotAreaStr,
                        onThLotAreaChange = { thLotAreaStr = it },
                        thFloorAreaStr = thFloorAreaStr,
                        onThFloorAreaChange = { thFloorAreaStr = it },
                        thStories = thStories,
                        onThStoriesChange = { thStories = it },
                        thBedrooms = thBedrooms,
                        onThBedroomsChange = { thBedrooms = it },
                        thBathrooms = thBathrooms,
                        onThBathroomsChange = { thBathrooms = it },
                        thGarageCapacity = thGarageCapacity,
                        onThGarageCapacityChange = { thGarageCapacity = it },
                        thHasMaidsRoom = thHasMaidsRoom,
                        onThHasMaidsRoomChange = { thHasMaidsRoom = it },
                        thIsGatedSubdivision = thIsGatedSubdivision,
                        onThIsGatedSubdivisionChange = { thIsGatedSubdivision = it },
                        onBack = { currentStep = 2 },
                        onNext = {
                            val rent = when (selectedPropertyType) {
                                "Boarding House" -> bhMonthlyRentStr.toDoubleOrNull() ?: 0.0
                                "Apartment" -> aptMonthlyRentStr.toDoubleOrNull() ?: 0.0
                                "Condominium", "Studio" -> condoMonthlyRentStr.toDoubleOrNull() ?: 0.0
                                else -> thMonthlyRentStr.toDoubleOrNull() ?: 0.0
                            }
                            if (rent <= 0.0) {
                                scope.launch { snackbarHostState.showSnackbar("Please enter a valid monthly rent amount.") }
                                return@Step3ConditionalSpecifications
                            }
                            currentStep = 4
                        }
                    )
                    4 -> Step4ReviewAndPublish(
                        userRole = userRole,
                        propertyType = selectedPropertyType,
                        title = listingTitle,
                        description = listingDescription,
                        streetAddress = streetAddress,
                        photoCount = uploadedPhotos.size,
                        // Specs summary data
                        bhPricingModel = bhPricingModel,
                        bhRent = bhMonthlyRentStr.toDoubleOrNull() ?: 0.0,
                        bhCapacity = bhRoomCapacity,
                        bhBathroom = bhBathroomType,
                        aptRent = aptMonthlyRentStr.toDoubleOrNull() ?: 0.0,
                        aptBeds = aptBedrooms,
                        aptBaths = aptBathrooms,
                        aptFurnishing = aptFurnishing,
                        condoRent = condoMonthlyRentStr.toDoubleOrNull() ?: 0.0,
                        condoLayout = condoUnitLayout,
                        condoFloor = condoFloorLevel,
                        condoDues = condoAssocDuesStatus,
                        thRent = thMonthlyRentStr.toDoubleOrNull() ?: 0.0,
                        thStories = thStories,
                        thBedrooms = thBedrooms,
                        thGarage = thGarageCapacity,
                        thGated = thIsGatedSubdivision,
                        onBack = { currentStep = 3 },
                        onPublish = {
                            // Construct typed specs
                            val roleVerification = RoleVerificationDetails(
                                role = userRole,
                                isTitleOwnerConfirmed = isTitleOwnerConfirmed,
                                ownerIdType = ownerIdType,
                                ownerIdNumber = ownerIdNumber,
                                contactPreference = ownerContactPref,
                                landlordFullName = landlordFullName,
                                representativeRelationship = repRelationship,
                                isAuthorizedByOwner = isAuthorizedByOwner == "Yes",
                                authorizationDocType = authorizationDocType,
                                representativePhone = repPhoneNumber,
                                masterLandlordName = masterLandlordName,
                                hasSubleasePermission = hasSubleasePermission == "Yes",
                                masterLeaseExpiry = masterLeaseDuration
                            )

                            val categorySpecifications = when (selectedPropertyType) {
                                "Boarding House" -> PropertyCategorySpecifications(
                                    category = "Boarding House",
                                    boardingHouse = BoardingHouseDetails(
                                        pricingModel = bhPricingModel,
                                        monthlyRent = bhMonthlyRentStr.toDoubleOrNull() ?: 2500.0,
                                        roomCapacity = bhRoomCapacity,
                                        tenantPreference = bhTenantPreference,
                                        bathroomType = bhBathroomType,
                                        utilities = bhUtilities.toList()
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Apartment" -> PropertyCategorySpecifications(
                                    category = "Apartment",
                                    apartment = ApartmentDetails(
                                        monthlyRent = aptMonthlyRentStr.toDoubleOrNull() ?: 12000.0,
                                        totalFloorAreaSqm = aptFloorAreaStr.toDoubleOrNull() ?: 42.0,
                                        bedrooms = aptBedrooms,
                                        bathrooms = aptBathrooms,
                                        leaseTermMinimum = aptLeaseTermMin,
                                        furnishing = aptFurnishing,
                                        kitchenType = aptKitchenType,
                                        utilityBilling = aptUtilityBilling
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Condominium", "Studio" -> PropertyCategorySpecifications(
                                    category = selectedPropertyType,
                                    condominium = CondominiumDetails(
                                        monthlyRent = condoMonthlyRentStr.toDoubleOrNull() ?: 22000.0,
                                        associationDues = condoAssocDuesStatus,
                                        unitLayout = condoUnitLayout,
                                        totalFloorAreaSqm = condoFloorAreaStr.toDoubleOrNull() ?: 28.0,
                                        floorLevel = condoFloorLevel,
                                        parking = condoParking,
                                        amenities = condoAmenities.toList(),
                                        petPolicy = condoPetPolicy
                                    ),
                                    roleVerification = roleVerification
                                )
                                else -> PropertyCategorySpecifications(
                                    category = "Townhouse",
                                    townhouse = TownhouseDetails(
                                        monthlyRent = thMonthlyRentStr.toDoubleOrNull() ?: 30000.0,
                                        lotAreaSqm = thLotAreaStr.toDoubleOrNull() ?: 120.0,
                                        floorAreaSqm = thFloorAreaStr.toDoubleOrNull() ?: 95.0,
                                        stories = thStories,
                                        bedrooms = thBedrooms,
                                        bathrooms = thBathrooms,
                                        garageCapacity = thGarageCapacity,
                                        hasMaidsRoom = thHasMaidsRoom,
                                        isGatedSubdivision = thIsGatedSubdivision
                                    ),
                                    roleVerification = roleVerification
                                )
                            }

                            val rent = when (selectedPropertyType) {
                                "Boarding House" -> bhMonthlyRentStr.toDoubleOrNull() ?: 2500.0
                                "Apartment" -> aptMonthlyRentStr.toDoubleOrNull() ?: 12000.0
                                "Condominium", "Studio" -> condoMonthlyRentStr.toDoubleOrNull() ?: 22000.0
                                else -> thMonthlyRentStr.toDoubleOrNull() ?: 30000.0
                            }

                            val beds = when (selectedPropertyType) {
                                "Apartment" -> aptBedrooms
                                "Townhouse" -> thBedrooms
                                "Condominium", "Studio" -> if (condoUnitLayout == "Studio") 0 else if (condoUnitLayout == "2BR") 2 else 1
                                else -> 1
                            }

                            val baths = when (selectedPropertyType) {
                                "Apartment" -> aptBathrooms
                                "Townhouse" -> thBathrooms
                                else -> 1
                            }

                            val area = when (selectedPropertyType) {
                                "Apartment" -> aptFloorAreaStr.toDoubleOrNull() ?: 42.0
                                "Condominium", "Studio" -> condoFloorAreaStr.toDoubleOrNull() ?: 28.0
                                "Townhouse" -> thFloorAreaStr.toDoubleOrNull() ?: 95.0
                                else -> 20.0
                            }

                            val dues = if (selectedPropertyType.contains("Condo") && condoAssocDuesStatus.contains("Exclusive")) 2000.0 else 0.0

                            viewModel.postProperty(
                                title = listingTitle,
                                description = listingDescription.ifBlank { "Clean, secure Philippine rental listing with authorized representation and verified amenities." },
                                type = selectedPropertyType,
                                rent = rent,
                                provinceId = selectedProvinceId,
                                cityId = selectedCityId,
                                barangayId = selectedBarangayId,
                                neighborhood = neighborhood,
                                landmark = landmark,
                                streetAddress = streetAddress,
                                privacy = "APPROXIMATE",
                                lat = 9.589,
                                lng = 123.774,
                                beds = beds,
                                baths = baths,
                                area = area,
                                furnished = if (selectedPropertyType == "Apartment") aptFurnishing else "Furnished",
                                parking = if (selectedPropertyType == "Condominium") !condoParking.contains("None") else true,
                                pets = if (selectedPropertyType == "Condominium") condoPetPolicy else "Pets Allowed",
                                dues = dues,
                                internetIncluded = bhUtilities.contains("Free Wi-Fi included") || condoAmenities.contains("24/7 Security"),
                                relationship = userRole,
                                isAuthorized = if (userRole == "Property owner") true else (isAuthorizedByOwner == "Yes"),
                                amenities = listOf("Air Conditioning", "WiFi", "24/7 Security"),
                                imageUrls = uploadedPhotos.toList(),
                                categorySpecsJson = categorySpecifications.toJson(),
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Property listed successfully!")
                                        onSuccess()
                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// MULTI-STEP PROGRESS HEADER
// -----------------------------------------------------------------------------
@Composable
private fun MultiStepProgressHeader(
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    val stepTitles = listOf("1. Role", "2. Base Info", "3. Specs", "4. Publish")

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                stepTitles.forEachIndexed { index, title ->
                    val stepNum = index + 1
                    val isActive = stepNum == currentStep
                    val isCompleted = stepNum < currentStep

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(enabled = isCompleted) { onStepClick(stepNum) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isActive -> MaterialTheme.colorScheme.primary
                                isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isCompleted) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$stepNum",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = title,
                            fontSize = 11.5.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { currentStep / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 1: ROLE-BASED VERIFICATION
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step1RoleVerification(
    userRole: String,
    onRoleChange: (String) -> Unit,
    isTitleOwnerConfirmed: Boolean,
    onTitleOwnerConfirmedChange: (Boolean) -> Unit,
    ownerIdType: String,
    onOwnerIdTypeChange: (String) -> Unit,
    ownerIdNumber: String,
    onOwnerIdNumberChange: (String) -> Unit,
    ownerContactPref: String,
    onOwnerContactPrefChange: (String) -> Unit,
    landlordFullName: String,
    onLandlordFullNameChange: (String) -> Unit,
    repRelationship: String,
    onRepRelationshipChange: (String) -> Unit,
    isAuthorizedByOwner: String,
    onIsAuthorizedByOwnerChange: (String) -> Unit,
    authorizationDocType: String,
    onAuthorizationDocTypeChange: (String) -> Unit,
    repPhoneNumber: String,
    onRepPhoneNumberChange: (String) -> Unit,
    masterLandlordName: String,
    onMasterLandlordNameChange: (String) -> Unit,
    hasSubleasePermission: String,
    onHasSubleasePermissionChange: (String) -> Unit,
    masterLeaseDuration: String,
    onMasterLeaseDurationChange: (String) -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 1: Role-Based Verification",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Philippine marketplace regulations require transparent disclosure of your relationship to the property.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Role Selector Chips
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "What is your role?",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    val roles = listOf(
                        "Property owner",
                        "Authorized representative / Caretaker",
                        "Sublessor / Master tenant"
                    )

                    roles.forEach { role ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (userRole == role) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = if (userRole == role) BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRoleChange(role) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = userRole == role,
                                    onClick = { onRoleChange(role) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = role,
                                        fontSize = 13.5.sp,
                                        fontWeight = if (userRole == role) FontWeight.Bold else FontWeight.Medium,
                                        color = if (userRole == role) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = when (role) {
                                            "Property owner" -> "Direct registered owner or title deed holder"
                                            "Authorized representative / Caretaker" -> "Caretaker, manager, or broker with owner authorization"
                                            else -> "Tenant leasing out spare rooms or master sublease"
                                        },
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Branching Details based on Your Role
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (userRole) {
                        "Property owner" -> {
                            Text(
                                text = "Owner Verification & Contact Details",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTitleOwnerConfirmedChange(!isTitleOwnerConfirmed) }
                            ) {
                                Checkbox(
                                    checked = isTitleOwnerConfirmed,
                                    onCheckedChange = onTitleOwnerConfirmedChange
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I confirm that I am the registered title deed holder or legitimate owner of this property.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            SearchableDropdownField(
                                label = "Verification Government ID",
                                selectedText = ownerIdType,
                                items = listOf(
                                    "Philippine National ID (PhilSys)",
                                    "Philippine Passport",
                                    "UMID / SSS ID",
                                    "Driver's License (LTO)",
                                    "PRC Professional ID"
                                ),
                                onItemSelected = onOwnerIdTypeChange
                            )

                            OutlinedTextField(
                                value = ownerIdNumber,
                                onValueChange = onOwnerIdNumberChange,
                                label = { Text("Government ID Reference (Optional)") },
                                placeholder = { Text("e.g. 1234-5678-9012") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            SearchableDropdownField(
                                label = "Preferred Inquiry Contact Channel",
                                selectedText = ownerContactPref,
                                items = listOf(
                                    "Direct Phone Call & SMS",
                                    "In-App Chat Only",
                                    "Viber / WhatsApp"
                                ),
                                onItemSelected = onOwnerContactPrefChange
                            )
                        }

                        "Authorized representative / Caretaker" -> {
                            Text(
                                text = "Owner Authorization & Caretaker Information",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = landlordFullName,
                                onValueChange = onLandlordFullNameChange,
                                label = { Text("Property Owner / Landlord's Full Name *") },
                                placeholder = { Text("e.g. Don Ramon Valdez") },
                                modifier = Modifier.fillMaxWidth().testTag("owner_name_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            SearchableDropdownField(
                                label = "Your Relationship to Owner",
                                selectedText = repRelationship,
                                items = listOf(
                                    "Caretaker / Property Custodian",
                                    "Family Member / Relative",
                                    "Property Manager",
                                    "Licensed Real-Estate Broker / Agent"
                                ),
                                onItemSelected = onRepRelationshipChange
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Are you authorized by the owner to advertise this property? *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    listOf("Yes", "No").forEach { opt ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isAuthorizedByOwner == opt,
                                                onClick = { onIsAuthorizedByOwnerChange(opt) }
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(opt, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            SearchableDropdownField(
                                label = "Authorization Document Held",
                                selectedText = authorizationDocType,
                                items = listOf(
                                    "Special Power of Attorney (SPA)",
                                    "Written Authorization Letter",
                                    "Notarized Management Agreement",
                                    "Verbal Authorization with Owner Contact"
                                ),
                                onItemSelected = onAuthorizationDocTypeChange
                            )

                            OutlinedTextField(
                                value = repPhoneNumber,
                                onValueChange = onRepPhoneNumberChange,
                                label = { Text("Representative Phone Number (Optional)") },
                                placeholder = { Text("0917-XXX-XXXX") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        else -> {
                            // Sublessor / Master tenant
                            Text(
                                text = "Sublease Permission & Master Lease",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            OutlinedTextField(
                                value = masterLandlordName,
                                onValueChange = onMasterLandlordNameChange,
                                label = { Text("Head Landlord / Building Owner Name *") },
                                placeholder = { Text("e.g. Tagbilaran Realty Corp") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Has the head landlord granted permission to sublease? *",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    listOf("Yes", "No").forEach { opt ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = hasSubleasePermission == opt,
                                                onClick = { onHasSubleasePermissionChange(opt) }
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(opt, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            SearchableDropdownField(
                                label = "Remaining Master Lease Duration",
                                selectedText = masterLeaseDuration,
                                items = listOf(
                                    "Less than 6 months",
                                    "6 to 12 months",
                                    "1 to 2 years",
                                    "2+ years"
                                ),
                                onItemSelected = onMasterLeaseDurationChange
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step1_continue_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue to Base Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 2: BASE PROPERTY INFORMATION (ALWAYS VISIBLE)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step2BasePropertyInformation(
    allProvinces: List<com.example.data.local.entity.ProvinceEntity>,
    allCities: List<com.example.data.local.entity.CityMunicipalityEntity>,
    allBarangays: List<com.example.data.local.entity.BarangayEntity>,
    selectedProvinceId: String,
    onProvinceSelected: (String) -> Unit,
    selectedCityId: String,
    onCitySelected: (String) -> Unit,
    selectedBarangayId: String,
    onBarangaySelected: (String) -> Unit,
    streetAddress: String,
    onStreetAddressChange: (String) -> Unit,
    neighborhood: String,
    onNeighborhoodChange: (String) -> Unit,
    landmark: String,
    onLandmarkChange: (String) -> Unit,
    selectedPropertyType: String,
    onPropertyTypeSelected: (String) -> Unit,
    uploadedPhotos: List<String>,
    onLaunchPhotoPicker: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
    listingTitle: String,
    onListingTitleChange: (String) -> Unit,
    listingDescription: String,
    onListingDescriptionChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 2: Base Property Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Universal fields required across all rental listings in the Philippines.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Location Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Geographic Location",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    CascadingLocationSelector(
                        countries = emptyList(),
                        selectedCountryId = "ctry_ph",
                        onCountrySelected = {},
                        regions = emptyList(),
                        selectedRegionId = "",
                        onRegionSelected = {},
                        provinces = allProvinces,
                        selectedProvinceId = selectedProvinceId,
                        onProvinceSelected = onProvinceSelected,
                        cities = allCities,
                        selectedCityId = selectedCityId,
                        onCitySelected = onCitySelected,
                        barangays = allBarangays,
                        selectedBarangayId = selectedBarangayId,
                        onBarangaySelected = onBarangaySelected,
                        allowAllOption = false
                    )

                    OutlinedTextField(
                        value = streetAddress,
                        onValueChange = onStreetAddressChange,
                        label = { Text("Street Address / Building Name *") },
                        placeholder = { Text("e.g. Unit 402, Sunset Boulevard Condos") },
                        modifier = Modifier.fillMaxWidth().testTag("post_address_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = neighborhood,
                            onValueChange = onNeighborhoodChange,
                            label = { Text("Subdivision / Zone") },
                            placeholder = { Text("e.g. Alona Beach Zone") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = onLandmarkChange,
                            label = { Text("Nearby Landmark") },
                            placeholder = { Text("Near BPI / Mall") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Property Type Dropdown / Chip Selector (Triggers Step 3 Conditional Logic)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Property Type *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Select one. This selection triggers the conditional specifications in Step 3:",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val types = listOf(
                        "Boarding House" to "🛏️",
                        "Apartment" to "🚪",
                        "Condominium" to "🏢",
                        "Studio" to "🛋️",
                        "Townhouse" to "🏡"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        types.forEach { (type, emoji) ->
                            val isSelected = selectedPropertyType == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { onPropertyTypeSelected(type) },
                                label = { Text("$emoji $type", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Media & Marketing Card (Min 3 Photos, Max 10 Photos)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Property Photos *",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (uploadedPhotos.size >= 3) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = "${uploadedPhotos.size} / 10 (Min 3)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uploadedPhotos.size >= 3) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (uploadedPhotos.isEmpty()) {
                        OutlinedButton(
                            onClick = onLaunchPhotoPicker,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .testTag("pick_photos_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload Property Photos (Min 3, Max 10)")
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(uploadedPhotos) { index, photoPath ->
                                Box(modifier = Modifier.size(80.dp)) {
                                    AsyncImage(
                                        model = photoPath,
                                        contentDescription = "Photo $index",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { onRemovePhoto(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(22.dp)
                                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                            if (uploadedPhotos.size < 10) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clickable { onLaunchPhotoPicker() }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add More", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = listingTitle,
                        onValueChange = onListingTitleChange,
                        label = { Text("Listing Title *") },
                        placeholder = { Text("e.g. Modern Furnished Studio near IT Park") },
                        modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = listingDescription,
                        onValueChange = onListingDescriptionChange,
                        label = { Text("Property Description *") },
                        placeholder = { Text("Describe property space, locality, nearby landmarks, transit access, and tenant rules...") },
                        modifier = Modifier.fillMaxWidth().testTag("post_desc_input"),
                        maxLines = 4,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Navigation Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Role")
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.5f).height(50.dp).testTag("step2_continue_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue to Specs", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 3: CONDITIONAL PROPERTY SPECIFICATIONS
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step3ConditionalSpecifications(
    propertyType: String,
    // Boarding House
    bhPricingModel: String,
    onBhPricingModelChange: (String) -> Unit,
    bhMonthlyRentStr: String,
    onBhMonthlyRentChange: (String) -> Unit,
    bhRoomCapacity: String,
    onBhRoomCapacityChange: (String) -> Unit,
    bhTenantPreference: String,
    onBhTenantPreferenceChange: (String) -> Unit,
    bhBathroomType: String,
    onBhBathroomTypeChange: (String) -> Unit,
    bhUtilities: List<String>,
    onToggleBhUtility: (String) -> Unit,
    // Apartment
    aptMonthlyRentStr: String,
    onAptMonthlyRentChange: (String) -> Unit,
    aptFloorAreaStr: String,
    onAptFloorAreaChange: (String) -> Unit,
    aptBedrooms: Int,
    onAptBedroomsChange: (Int) -> Unit,
    aptBathrooms: Int,
    onAptBathroomsChange: (Int) -> Unit,
    aptLeaseTermMin: String,
    onAptLeaseTermMinChange: (String) -> Unit,
    aptFurnishing: String,
    onAptFurnishingChange: (String) -> Unit,
    aptKitchenType: String,
    onAptKitchenTypeChange: (String) -> Unit,
    aptUtilityBilling: String,
    onAptUtilityBillingChange: (String) -> Unit,
    // Condo & Studio
    condoMonthlyRentStr: String,
    onCondoMonthlyRentChange: (String) -> Unit,
    condoAssocDuesStatus: String,
    onCondoAssocDuesStatusChange: (String) -> Unit,
    condoUnitLayout: String,
    onCondoUnitLayoutChange: (String) -> Unit,
    condoFloorAreaStr: String,
    onCondoFloorAreaChange: (String) -> Unit,
    condoFloorLevel: String,
    onCondoFloorLevelChange: (String) -> Unit,
    condoParking: String,
    onCondoParkingChange: (String) -> Unit,
    condoAmenities: List<String>,
    onToggleCondoAmenity: (String) -> Unit,
    condoPetPolicy: String,
    onCondoPetPolicyChange: (String) -> Unit,
    // Townhouse
    thMonthlyRentStr: String,
    onThMonthlyRentChange: (String) -> Unit,
    thLotAreaStr: String,
    onThLotAreaChange: (String) -> Unit,
    thFloorAreaStr: String,
    onThFloorAreaChange: (String) -> Unit,
    thStories: Int,
    onThStoriesChange: (Int) -> Unit,
    thBedrooms: Int,
    onThBedroomsChange: (Int) -> Unit,
    thBathrooms: Int,
    onThBathroomsChange: (Int) -> Unit,
    thGarageCapacity: Int,
    onThGarageCapacityChange: (Int) -> Unit,
    thHasMaidsRoom: Boolean,
    onThHasMaidsRoomChange: (Boolean) -> Unit,
    thIsGatedSubdivision: Boolean,
    onThIsGatedSubdivisionChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 3: Conditional Specifications",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "Active Property Type: $propertyType",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // DYNAMIC SPECIFICATIONS CARD DEPENDING ON SELECTED PROPERTY TYPE
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    when (propertyType) {
                        "Boarding House" -> {
                            // Condition 1: Boarding House
                            Text("Boarding House Setup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            // Pricing Model Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Pricing Model *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Per Person/Bed", "Per Room").forEach { model ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = bhPricingModel == model, onClick = { onBhPricingModelChange(model) })
                                            Text(model, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = bhMonthlyRentStr,
                                onValueChange = onBhMonthlyRentChange,
                                label = { Text("Monthly Rent (PHP) *") },
                                placeholder = { Text("2500") },
                                modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            SearchableDropdownField(
                                label = "Room Capacity",
                                selectedText = bhRoomCapacity,
                                items = listOf("Single", "Double bed", "4-bed bunk", "6-bed bunk"),
                                onItemSelected = onBhRoomCapacityChange
                            )

                            SearchableDropdownField(
                                label = "Tenant Preference",
                                selectedText = bhTenantPreference,
                                items = listOf("Male Only", "Female Only", "Mixed"),
                                onItemSelected = onBhTenantPreferenceChange
                            )

                            // Bathroom Type Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Bathroom Type *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Private/En-suite", "Shared/Communal").forEach { bType ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = bhBathroomType == bType, onClick = { onBhBathroomTypeChange(bType) })
                                            Text(bType, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Utilities Checkboxes
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Utilities & Amenities Included:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                listOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included").forEach { util ->
                                    val isChecked = bhUtilities.contains(util)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().clickable { onToggleBhUtility(util) }
                                    ) {
                                        Checkbox(checked = isChecked, onCheckedChange = { onToggleBhUtility(util) })
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(util, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        "Apartment" -> {
                            // Condition 2: Apartment
                            Text("Apartment Living Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            OutlinedTextField(
                                value = aptMonthlyRentStr,
                                onValueChange = onAptMonthlyRentChange,
                                label = { Text("Monthly Rent (PHP) *") },
                                placeholder = { Text("12000") },
                                modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = aptFloorAreaStr,
                                onValueChange = onAptFloorAreaChange,
                                label = { Text("Total Floor Area (sqm) *") },
                                placeholder = { Text("42") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Steppers for Bedrooms and Bathrooms
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CounterStepper(
                                    label = "Bedrooms",
                                    value = aptBedrooms,
                                    onValueChange = onAptBedroomsChange,
                                    modifier = Modifier.weight(1f)
                                )
                                CounterStepper(
                                    label = "Bathrooms",
                                    value = aptBathrooms,
                                    onValueChange = onAptBathroomsChange,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            SearchableDropdownField(
                                label = "Lease Term Minimum",
                                selectedText = aptLeaseTermMin,
                                items = listOf("6 months", "1 year"),
                                onItemSelected = onAptLeaseTermMinChange
                            )

                            SearchableDropdownField(
                                label = "Furnishing",
                                selectedText = aptFurnishing,
                                items = listOf("Bare", "Semi-Furnished", "Fully Furnished"),
                                onItemSelected = onAptFurnishingChange
                            )

                            // Kitchen Type Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Kitchen Type *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Indoor/Integral", "Outdoor/Dirty Kitchen").forEach { kType ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = aptKitchenType == kType, onClick = { onAptKitchenTypeChange(kType) })
                                            Text(kType, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Utility Billing Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Utility Billing *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Own Meter", "Sub-metered by Landlord").forEach { bType ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = aptUtilityBilling == bType, onClick = { onAptUtilityBillingChange(bType) })
                                            Text(bType, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        "Condominium", "Studio" -> {
                            // Condition 3: Condominium & Studio
                            Text("Condominium & Studio Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            OutlinedTextField(
                                value = condoMonthlyRentStr,
                                onValueChange = onCondoMonthlyRentChange,
                                label = { Text("Monthly Rent (PHP) *") },
                                placeholder = { Text("22000") },
                                modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Association Dues Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Association Dues *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Inclusive in rent", "Exclusive / Tenant pays separately").forEach { dType ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = condoAssocDuesStatus == dType, onClick = { onCondoAssocDuesStatusChange(dType) })
                                            Text(dType, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            SearchableDropdownField(
                                label = "Unit Layout",
                                selectedText = condoUnitLayout,
                                items = listOf("Studio", "1BR", "2BR", "Loft", "Bi-level"),
                                onItemSelected = onCondoUnitLayoutChange
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = condoFloorAreaStr,
                                    onValueChange = onCondoFloorAreaChange,
                                    label = { Text("Total Floor Area (sqm) *") },
                                    placeholder = { Text("28") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = condoFloorLevel,
                                    onValueChange = onCondoFloorLevelChange,
                                    label = { Text("Floor Level *") },
                                    placeholder = { Text("14th Floor") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            SearchableDropdownField(
                                label = "Parking",
                                selectedText = condoParking,
                                items = listOf("Included in rent", "Available for extra fee", "None"),
                                onItemSelected = onCondoParkingChange
                            )

                            // Amenities Checkboxes
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Amenities Included:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Pool", "Gym", "24/7 Security", "Balcony").forEach { am ->
                                        val isChecked = condoAmenities.contains(am)
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onToggleCondoAmenity(am) }) {
                                            Checkbox(checked = isChecked, onCheckedChange = { onToggleCondoAmenity(am) })
                                            Text(am, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Pet Policy Toggle
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Pet Policy *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    listOf("Strictly No Pets", "Pet-Friendly").forEach { pType ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(selected = condoPetPolicy == pType, onClick = { onCondoPetPolicyChange(pType) })
                                            Text(pType, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        else -> {
                            // Condition 4: Townhouse
                            Text("Townhouse Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                            OutlinedTextField(
                                value = thMonthlyRentStr,
                                onValueChange = onThMonthlyRentChange,
                                label = { Text("Monthly Rent (PHP) *") },
                                placeholder = { Text("30000") },
                                modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = thLotAreaStr,
                                    onValueChange = onThLotAreaChange,
                                    label = { Text("Lot Area (sqm) *") },
                                    placeholder = { Text("120") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                OutlinedTextField(
                                    value = thFloorAreaStr,
                                    onValueChange = onThFloorAreaChange,
                                    label = { Text("Floor Area (sqm) *") },
                                    placeholder = { Text("95") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CounterStepper(label = "Stories/Floors", value = thStories, onValueChange = onThStoriesChange, modifier = Modifier.weight(1f))
                                CounterStepper(label = "Carport/Garage", value = thGarageCapacity, onValueChange = onThGarageCapacityChange, modifier = Modifier.weight(1f))
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                CounterStepper(label = "Bedrooms", value = thBedrooms, onValueChange = onThBedroomsChange, modifier = Modifier.weight(1f))
                                CounterStepper(label = "Bathrooms", value = thBathrooms, onValueChange = onThBathroomsChange, modifier = Modifier.weight(1f))
                            }

                            // Maid's Room Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Maid's Room / Service Quarters Included?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Switch(checked = thHasMaidsRoom, onCheckedChange = onThHasMaidsRoomChange)
                            }

                            // Gated Subdivision Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Located inside a gated subdivision?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Switch(checked = thIsGatedSubdivision, onCheckedChange = onThIsGatedSubdivisionChange)
                            }
                        }
                    }
                }
            }
        }

        // Navigation Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Base")
                }
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1.5f).height(50.dp).testTag("step3_continue_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Review Details", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 4: SUBMISSION STATE (REVIEW & PUBLISH)
// -----------------------------------------------------------------------------
@Composable
private fun Step4ReviewAndPublish(
    userRole: String,
    propertyType: String,
    title: String,
    description: String,
    streetAddress: String,
    photoCount: Int,
    bhPricingModel: String,
    bhRent: Double,
    bhCapacity: String,
    bhBathroom: String,
    aptRent: Double,
    aptBeds: Int,
    aptBaths: Int,
    aptFurnishing: String,
    condoRent: Double,
    condoLayout: String,
    condoFloor: String,
    condoDues: String,
    thRent: Double,
    thStories: Int,
    thBedrooms: Int,
    thGarage: Int,
    thGated: Boolean,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH")).apply {
        maximumFractionDigits = 0
    }

    val finalRent = when (propertyType) {
        "Boarding House" -> bhRent
        "Apartment" -> aptRent
        "Condominium", "Studio" -> condoRent
        else -> thRent
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 4: Review & Publish",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Review your listing details before publishing to Balay-Inn renters.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Summary Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = title.ifBlank { "Untitled Listing" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₱${currencyFormat.format(finalRent).replace("PHP", "").trim()} / month",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ReviewDetailRow("Role Verification", userRole)
                    ReviewDetailRow("Property Type", propertyType)
                    ReviewDetailRow("Address", streetAddress)
                    ReviewDetailRow("Photos Uploaded", "$photoCount verified photos")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("Type-Specific Specifications", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = MaterialTheme.colorScheme.primary)

                    when (propertyType) {
                        "Boarding House" -> {
                            ReviewDetailRow("Pricing Model", bhPricingModel)
                            ReviewDetailRow("Room Capacity", bhCapacity)
                            ReviewDetailRow("Bathroom Setup", bhBathroom)
                        }
                        "Apartment" -> {
                            ReviewDetailRow("Rooms", "$aptBeds Bed, $aptBaths Bath")
                            ReviewDetailRow("Furnishing", aptFurnishing)
                        }
                        "Condominium", "Studio" -> {
                            ReviewDetailRow("Layout", "$condoLayout ($condoFloor)")
                            ReviewDetailRow("Assoc Dues", condoDues)
                        }
                        else -> {
                            ReviewDetailRow("Structure", "$thStories-Storey, $thBedrooms Bed")
                            ReviewDetailRow("Carport / Garage", "$thGarage Car(s)")
                            ReviewDetailRow("Gated Subdivision", if (thGated) "Yes" else "No")
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "All required fields based on your active role and property type are validated and ready to save.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit Specs")
                }
                Button(
                    onClick = onPublish,
                    modifier = Modifier
                        .weight(1.8f)
                        .height(52.dp)
                        .testTag("publish_listing_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish Listing", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReviewDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun CounterStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable(enabled = value > 1) { onValueChange(value - 1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "$value",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onValueChange(value + 1) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Copies a selected image from Android device memory/content provider into the app's
 * private internal storage, ensuring persistent local file access that never expires.
 */
private fun copyUriToInternalStorage(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val dir = File(context.filesDir, "property_photos").apply { if (!exists()) mkdirs() }
            val fileName = "prop_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val file = File(dir, fileName)
            file.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        } else {
            uri.toString()
        }
    } catch (_: Exception) {
        uri.toString()
    }
}
