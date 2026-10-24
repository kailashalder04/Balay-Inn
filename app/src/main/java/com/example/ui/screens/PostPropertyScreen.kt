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
import androidx.compose.foundation.layout.ColumnScope
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

class BhFormState {
    var pricingModel by mutableStateOf("Per Person/Bed")
    var monthlyRentStr by mutableStateOf("2500")
    var floorAreaStr by mutableStateOf("18")
    var roomCapacity by mutableStateOf("4-bed bunk")
    var tenantPreference by mutableStateOf("Mixed")
    var bathroomType by mutableStateOf("Shared/Communal")
    var furnishing by mutableStateOf("Fully-Furnished")
    var parking by mutableStateOf("Motorcycle Parking Included")
    var curfew by mutableStateOf("10:00 PM Gate Lock")
    var visitors by mutableStateOf("Lobby Visitors Only")
    var petPolicy by mutableStateOf("Strictly No Pets")
    var cooking by mutableStateOf("Light Cooking Allowed")
    var leaseTerm by mutableStateOf("6 months")
    val utilities = mutableStateListOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included")
}

class AptFormState {
    var monthlyRentStr by mutableStateOf("12000")
    var floorAreaStr by mutableStateOf("42")
    var bedrooms by mutableIntStateOf(1)
    var bathrooms by mutableIntStateOf(1)
    var leaseTermMin by mutableStateOf("1 year")
    var furnishing by mutableStateOf("Semi-Furnished")
    var kitchenType by mutableStateOf("Indoor/Integral")
    var utilityBilling by mutableStateOf("Own Meter")
    var parking by mutableStateOf("Covered Garage Slot")
    var petPolicy by mutableStateOf("Pets Allowed")
    var curfew by mutableStateOf("No Curfew (24/7 Access)")
}

class CondoFormState {
    var monthlyRentStr by mutableStateOf("22000")
    var assocDuesStatus by mutableStateOf("Inclusive in rent")
    var unitLayout by mutableStateOf("Studio")
    var floorAreaStr by mutableStateOf("28")
    var floorLevel by mutableStateOf("14th Floor")
    var parking by mutableStateOf("Available for extra fee")
    val amenities = mutableStateListOf("Pool", "Gym", "24/7 Security")
    var petPolicy by mutableStateOf("Pet-Friendly")
    var furnishing by mutableStateOf("Fully-Furnished")
    var leaseTerm by mutableStateOf("1 year")
    var visitors by mutableStateOf("24/7 Concierge & Guest Registration")
    var kitchen by mutableStateOf("Equipped Kitchenette")
}

class TownhouseFormState {
    var monthlyRentStr by mutableStateOf("30000")
    var lotAreaStr by mutableStateOf("120")
    var floorAreaStr by mutableStateOf("95")
    var stories by mutableIntStateOf(2)
    var bedrooms by mutableIntStateOf(3)
    var bathrooms by mutableIntStateOf(2)
    var garageCapacity by mutableIntStateOf(2)
    var hasMaidsRoom by mutableStateOf(false)
    var isGatedSubdivision by mutableStateOf(true)
    var furnishing by mutableStateOf("Semi-Furnished")
    var petPolicy by mutableStateOf("Pet-Friendly")
    var assocDues by mutableStateOf("Inclusive in rent")
    var leaseTerm by mutableStateOf("1 year")
    var kitchen by mutableStateOf("Indoor Kitchen + Dirty Kitchen")
}

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
    // STEP 3: CONDITIONAL SPECIFICATIONS STATES (Universal Categories)
    // -------------------------------------------------------------
    val bhState = remember { BhFormState() }
    val aptState = remember { AptFormState() }
    val condoState = remember { CondoFormState() }
    val thState = remember { TownhouseFormState() }

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
                        bhState = bhState,
                        aptState = aptState,
                        condoState = condoState,
                        thState = thState,
                        onBack = { currentStep = 2 },
                        onNext = {
                            val rent = when (selectedPropertyType) {
                                "Boarding House" -> bhState.monthlyRentStr.toDoubleOrNull() ?: 0.0
                                "Apartment" -> aptState.monthlyRentStr.toDoubleOrNull() ?: 0.0
                                "Condo", "Condominium", "Studio" -> condoState.monthlyRentStr.toDoubleOrNull() ?: 0.0
                                else -> thState.monthlyRentStr.toDoubleOrNull() ?: 0.0
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
                        bhState = bhState,
                        aptState = aptState,
                        condoState = condoState,
                        thState = thState,
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
                                        pricingModel = bhState.pricingModel,
                                        monthlyRent = bhState.monthlyRentStr.toDoubleOrNull() ?: 2500.0,
                                        roomCapacity = bhState.roomCapacity,
                                        tenantPreference = bhState.tenantPreference,
                                        bathroomType = bhState.bathroomType,
                                        utilities = bhState.utilities.toList(),
                                        floorAreaSqm = bhState.floorAreaStr.toDoubleOrNull() ?: 18.0,
                                        furnishing = bhState.furnishing,
                                        parking = bhState.parking,
                                        curfewRule = bhState.curfew,
                                        visitorsRule = bhState.visitors,
                                        petPolicy = bhState.petPolicy,
                                        cookingRule = bhState.cooking,
                                        leaseTermMinimum = bhState.leaseTerm
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Apartment" -> PropertyCategorySpecifications(
                                    category = "Apartment",
                                    apartment = ApartmentDetails(
                                        monthlyRent = aptState.monthlyRentStr.toDoubleOrNull() ?: 12000.0,
                                        totalFloorAreaSqm = aptState.floorAreaStr.toDoubleOrNull() ?: 42.0,
                                        bedrooms = aptState.bedrooms,
                                        bathrooms = aptState.bathrooms,
                                        leaseTermMinimum = aptState.leaseTermMin,
                                        furnishing = aptState.furnishing,
                                        kitchenType = aptState.kitchenType,
                                        utilityBilling = aptState.utilityBilling,
                                        parking = aptState.parking,
                                        petPolicy = aptState.petPolicy,
                                        curfewRule = aptState.curfew
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Condo", "Condominium", "Studio" -> PropertyCategorySpecifications(
                                    category = selectedPropertyType,
                                    condominium = CondominiumDetails(
                                        monthlyRent = condoState.monthlyRentStr.toDoubleOrNull() ?: 22000.0,
                                        associationDues = condoState.assocDuesStatus,
                                        unitLayout = condoState.unitLayout,
                                        totalFloorAreaSqm = condoState.floorAreaStr.toDoubleOrNull() ?: 28.0,
                                        floorLevel = condoState.floorLevel,
                                        parking = condoState.parking,
                                        amenities = condoState.amenities.toList(),
                                        petPolicy = condoState.petPolicy,
                                        furnishing = condoState.furnishing,
                                        leaseTermMinimum = condoState.leaseTerm,
                                        visitorsRule = condoState.visitors,
                                        kitchenType = condoState.kitchen
                                    ),
                                    roleVerification = roleVerification
                                )
                                else -> PropertyCategorySpecifications(
                                    category = "Townhouse",
                                    townhouse = TownhouseDetails(
                                        monthlyRent = thState.monthlyRentStr.toDoubleOrNull() ?: 30000.0,
                                        lotAreaSqm = thState.lotAreaStr.toDoubleOrNull() ?: 120.0,
                                        floorAreaSqm = thState.floorAreaStr.toDoubleOrNull() ?: 95.0,
                                        stories = thState.stories,
                                        bedrooms = thState.bedrooms,
                                        bathrooms = thState.bathrooms,
                                        garageCapacity = thState.garageCapacity,
                                        hasMaidsRoom = thState.hasMaidsRoom,
                                        isGatedSubdivision = thState.isGatedSubdivision,
                                        furnishing = thState.furnishing,
                                        petPolicy = thState.petPolicy,
                                        associationDues = thState.assocDues,
                                        leaseTermMinimum = thState.leaseTerm,
                                        kitchenType = thState.kitchen
                                    ),
                                    roleVerification = roleVerification
                                )
                            }

                            val rent = when (selectedPropertyType) {
                                "Boarding House" -> bhState.monthlyRentStr.toDoubleOrNull() ?: 2500.0
                                "Apartment" -> aptState.monthlyRentStr.toDoubleOrNull() ?: 12000.0
                                "Condo", "Condominium", "Studio" -> condoState.monthlyRentStr.toDoubleOrNull() ?: 22000.0
                                else -> thState.monthlyRentStr.toDoubleOrNull() ?: 30000.0
                            }

                            val beds = when (selectedPropertyType) {
                                "Apartment" -> aptState.bedrooms
                                "Townhouse" -> thState.bedrooms
                                "Condo", "Condominium", "Studio" -> if (condoState.unitLayout == "Studio") 0 else if (condoState.unitLayout == "2BR") 2 else 1
                                else -> 1
                            }

                            val baths = when (selectedPropertyType) {
                                "Apartment" -> aptState.bathrooms
                                "Townhouse" -> thState.bathrooms
                                else -> 1
                            }

                            val area = when (selectedPropertyType) {
                                "Apartment" -> aptState.floorAreaStr.toDoubleOrNull() ?: 42.0
                                "Condo", "Condominium", "Studio" -> condoState.floorAreaStr.toDoubleOrNull() ?: 28.0
                                "Townhouse" -> thState.floorAreaStr.toDoubleOrNull() ?: 95.0
                                else -> bhState.floorAreaStr.toDoubleOrNull() ?: 18.0
                            }

                            val dues = when (selectedPropertyType) {
                                "Condo", "Condominium", "Studio" -> if (condoState.assocDuesStatus.contains("Exclusive")) 2000.0 else 0.0
                                "Townhouse" -> if (thState.assocDues.contains("Exclusive")) 1500.0 else 0.0
                                else -> 0.0
                            }

                            val furnished = when (selectedPropertyType) {
                                "Apartment" -> aptState.furnishing
                                "Condo", "Condominium", "Studio" -> condoState.furnishing
                                "Townhouse" -> thState.furnishing
                                else -> bhState.furnishing
                            }

                            val parking = when (selectedPropertyType) {
                                "Condo", "Condominium", "Studio" -> !condoState.parking.contains("None")
                                "Townhouse" -> thState.garageCapacity > 0
                                "Apartment" -> !aptState.parking.contains("None")
                                else -> !bhState.parking.contains("None")
                            }

                            val pets = when (selectedPropertyType) {
                                "Condo", "Condominium", "Studio" -> condoState.petPolicy
                                "Townhouse" -> thState.petPolicy
                                "Apartment" -> aptState.petPolicy
                                else -> bhState.petPolicy
                            }

                            val internetIncluded = when (selectedPropertyType) {
                                "Boarding House" -> bhState.utilities.contains("Free Wi-Fi included")
                                "Condo", "Condominium", "Studio" -> condoState.amenities.contains("24/7 Security") || condoState.amenities.contains("WiFi")
                                else -> true
                            }

                            viewModel.postProperty(
                                title = listingTitle,
                                description = listingDescription.ifBlank { "Clean, secure Philippine rental listing with verified universal specifications." },
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
                                furnished = furnished,
                                parking = parking,
                                pets = pets,
                                dues = dues,
                                internetIncluded = internetIncluded,
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
                        "Condo" to "🏢",
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
    bhState: BhFormState,
    aptState: AptFormState,
    condoState: CondoFormState,
    thState: TownhouseFormState,
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

        // =====================================================================
        // UNIVERSAL HEADING 1: UNIT SPECIFICATIONS
        // =====================================================================
        item {
            FormSectionCard(
                title = "Unit Specifications"
            ) {
                when (propertyType) {
                    "Boarding House" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Pricing Model *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("Per Person/Bed", "Per Room").forEach { model ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = bhState.pricingModel == model,
                                            onClick = { bhState.pricingModel = model }
                                        )
                                        Text(model, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = bhState.monthlyRentStr,
                            onValueChange = { bhState.monthlyRentStr = it },
                            label = { Text("Monthly Rent (PHP) *") },
                            placeholder = { Text("2500") },
                            modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = bhState.floorAreaStr,
                            onValueChange = { bhState.floorAreaStr = it },
                            label = { Text("Room Floor Area (sq.m)") },
                            placeholder = { Text("18") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ChipSelectGroup(
                            label = "Room Capacity",
                            options = listOf("Single occupancy", "2-bed bunk", "4-bed bunk", "6-bed bunk"),
                            selected = bhState.roomCapacity,
                            onSelected = { bhState.roomCapacity = it }
                        )

                        ChipSelectGroup(
                            label = "Bathroom Setup",
                            options = listOf("Shared/Communal", "Private en-suite"),
                            selected = bhState.bathroomType,
                            onSelected = { bhState.bathroomType = it }
                        )

                        ChipSelectGroup(
                            label = "Furnishing Level",
                            options = listOf("Unfurnished", "Semi-Furnished", "Fully-Furnished"),
                            selected = bhState.furnishing,
                            onSelected = { bhState.furnishing = it }
                        )

                        ChipSelectGroup(
                            label = "Tenant Preference",
                            options = listOf("Mixed", "Female Only", "Male Only", "Students Only"),
                            selected = bhState.tenantPreference,
                            onSelected = { bhState.tenantPreference = it }
                        )
                    }

                    "Apartment" -> {
                        OutlinedTextField(
                            value = aptState.monthlyRentStr,
                            onValueChange = { aptState.monthlyRentStr = it },
                            label = { Text("Monthly Rent (PHP) *") },
                            placeholder = { Text("12000") },
                            modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = aptState.floorAreaStr,
                            onValueChange = { aptState.floorAreaStr = it },
                            label = { Text("Total Floor Area (sq.m)") },
                            placeholder = { Text("42") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CounterStepper(
                                label = "Bedrooms",
                                value = aptState.bedrooms,
                                onValueChange = { aptState.bedrooms = it },
                                modifier = Modifier.weight(1f)
                            )
                            CounterStepper(
                                label = "Bathrooms",
                                value = aptState.bathrooms,
                                onValueChange = { aptState.bathrooms = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        ChipSelectGroup(
                            label = "Furnishing Level",
                            options = listOf("Unfurnished", "Semi-Furnished", "Fully-Furnished"),
                            selected = aptState.furnishing,
                            onSelected = { aptState.furnishing = it }
                        )

                        ChipSelectGroup(
                            label = "Kitchen Setup",
                            options = listOf("Indoor/Integral", "Studio Kitchenette", "Dirty Kitchen (Outdoor)"),
                            selected = aptState.kitchenType,
                            onSelected = { aptState.kitchenType = it }
                        )
                    }

                    "Condo", "Condominium", "Studio" -> {
                        OutlinedTextField(
                            value = condoState.monthlyRentStr,
                            onValueChange = { condoState.monthlyRentStr = it },
                            label = { Text("Monthly Rent (PHP) *") },
                            placeholder = { Text("22000") },
                            modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ChipSelectGroup(
                            label = "Unit Layout",
                            options = listOf("Studio", "1-Bedroom", "2-Bedroom", "3-Bedroom", "Penthouse / Loft"),
                            selected = condoState.unitLayout,
                            onSelected = { condoState.unitLayout = it }
                        )

                        OutlinedTextField(
                            value = condoState.floorAreaStr,
                            onValueChange = { condoState.floorAreaStr = it },
                            label = { Text("Floor Area (sq.m)") },
                            placeholder = { Text("28") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = condoState.floorLevel,
                            onValueChange = { condoState.floorLevel = it },
                            label = { Text("Floor Level / Unit Position") },
                            placeholder = { Text("14th Floor, Tower 1") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        ChipSelectGroup(
                            label = "Furnishing Level",
                            options = listOf("Unfurnished", "Semi-Furnished", "Fully-Furnished"),
                            selected = condoState.furnishing,
                            onSelected = { condoState.furnishing = it }
                        )

                        ChipSelectGroup(
                            label = "Kitchen Setup",
                            options = listOf("Equipped Kitchenette", "Full Modular Kitchen", "Open Concept Kitchen"),
                            selected = condoState.kitchen,
                            onSelected = { condoState.kitchen = it }
                        )
                    }

                    else -> { // Townhouse
                        OutlinedTextField(
                            value = thState.monthlyRentStr,
                            onValueChange = { thState.monthlyRentStr = it },
                            label = { Text("Monthly Rent (PHP) *") },
                            placeholder = { Text("30000") },
                            modifier = Modifier.fillMaxWidth().testTag("post_rent_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = thState.lotAreaStr,
                                onValueChange = { thState.lotAreaStr = it },
                                label = { Text("Lot Area (sq.m)") },
                                placeholder = { Text("120") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = thState.floorAreaStr,
                                onValueChange = { thState.floorAreaStr = it },
                                label = { Text("Floor Area (sq.m)") },
                                placeholder = { Text("95") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CounterStepper(
                                label = "Stories",
                                value = thState.stories,
                                onValueChange = { thState.stories = it },
                                modifier = Modifier.weight(1f)
                            )
                            CounterStepper(
                                label = "Bedrooms",
                                value = thState.bedrooms,
                                onValueChange = { thState.bedrooms = it },
                                modifier = Modifier.weight(1f)
                            )
                            CounterStepper(
                                label = "Bathrooms",
                                value = thState.bathrooms,
                                onValueChange = { thState.bathrooms = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Maid's / Helper Room Included?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = thState.hasMaidsRoom,
                                onCheckedChange = { thState.hasMaidsRoom = it }
                            )
                        }

                        ChipSelectGroup(
                            label = "Furnishing Level",
                            options = listOf("Unfurnished", "Semi-Furnished", "Fully-Furnished"),
                            selected = thState.furnishing,
                            onSelected = { thState.furnishing = it }
                        )

                        ChipSelectGroup(
                            label = "Kitchen Setup",
                            options = listOf("Indoor Kitchen + Dirty Kitchen", "Single Modern Kitchen", "Enclosed Chef's Kitchen"),
                            selected = thState.kitchen,
                            onSelected = { thState.kitchen = it }
                        )
                    }
                }
            }
        }

        // =====================================================================
        // UNIVERSAL HEADING 2: AMENITIES & FACILITIES
        // =====================================================================
        item {
            FormSectionCard(
                title = "Amenities & Facilities"
            ) {
                when (propertyType) {
                    "Boarding House" -> {
                        ChipSelectGroup(
                            label = "Vehicle Parking",
                            options = listOf("Motorcycle Parking Included", "Street Parking Only", "No Parking", "Paid Car Parking"),
                            selected = bhState.parking,
                            onSelected = { bhState.parking = it }
                        )

                        ChipSelectGroup(
                            label = "Kitchen & Cooking Access",
                            options = listOf("No Cooking", "Light Cooking Allowed", "Full Shared Kitchen Access"),
                            selected = bhState.cooking,
                            onSelected = { bhState.cooking = it }
                        )

                        MultiChipSelectGroup(
                            label = "Utilities & Amenities Included",
                            options = listOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included", "Filtered Drinking Water", "CCTV Security", "Study Lounge"),
                            selectedItems = bhState.utilities,
                            onToggle = { item ->
                                if (bhState.utilities.contains(item)) bhState.utilities.remove(item)
                                else bhState.utilities.add(item)
                            }
                        )
                    }

                    "Apartment" -> {
                        ChipSelectGroup(
                            label = "Utility Billing Setup",
                            options = listOf("Own Meter", "Sub-metered", "Fixed Rate"),
                            selected = aptState.utilityBilling,
                            onSelected = { aptState.utilityBilling = it }
                        )

                        ChipSelectGroup(
                            label = "Vehicle Parking",
                            options = listOf("Covered Garage Slot", "Street Parking", "Motorcycle Only", "No Parking"),
                            selected = aptState.parking,
                            onSelected = { aptState.parking = it }
                        )
                    }

                    "Condo", "Condominium", "Studio" -> {
                        ChipSelectGroup(
                            label = "Vehicle Parking",
                            options = listOf("Dedicated Parking Slot Included", "Available for extra fee", "No Parking Slot"),
                            selected = condoState.parking,
                            onSelected = { condoState.parking = it }
                        )

                        MultiChipSelectGroup(
                            label = "Building Amenities & Facilities",
                            options = listOf("Pool", "Gym", "24/7 Security", "Function Room", "Sky Deck", "Backup Generator"),
                            selectedItems = condoState.amenities,
                            onToggle = { item ->
                                if (condoState.amenities.contains(item)) condoState.amenities.remove(item)
                                else condoState.amenities.add(item)
                            }
                        )
                    }

                    else -> { // Townhouse
                        CounterStepper(
                            label = "Garage / Carport Capacity (Cars)",
                            value = thState.garageCapacity,
                            onValueChange = { thState.garageCapacity = it }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Located inside a gated subdivision?", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Switch(
                                checked = thState.isGatedSubdivision,
                                onCheckedChange = { thState.isGatedSubdivision = it }
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // UNIVERSAL HEADING 3: RULES & LEASE TERMS
        // =====================================================================
        item {
            FormSectionCard(
                title = "Rules & Lease Terms"
            ) {
                when (propertyType) {
                    "Boarding House" -> {
                        ChipSelectGroup(
                            label = "Curfew & Gate Access",
                            options = listOf("No Curfew (24/7 Access)", "10:00 PM Gate Lock", "11:00 PM Gate Lock"),
                            selected = bhState.curfew,
                            onSelected = { bhState.curfew = it }
                        )

                        ChipSelectGroup(
                            label = "Visitor Policy",
                            options = listOf("Visitors Allowed Anytime", "Lobby Visitors Only", "Strictly No Visitors"),
                            selected = bhState.visitors,
                            onSelected = { bhState.visitors = it }
                        )

                        ChipSelectGroup(
                            label = "Pet Policy",
                            options = listOf("Strictly No Pets", "Small Pets Allowed", "Pets Allowed"),
                            selected = bhState.petPolicy,
                            onSelected = { bhState.petPolicy = it }
                        )

                        ChipSelectGroup(
                            label = "Minimum Lease Requirement",
                            options = listOf("1 month", "3 months", "6 months", "1 year"),
                            selected = bhState.leaseTerm,
                            onSelected = { bhState.leaseTerm = it }
                        )
                    }

                    "Apartment" -> {
                        ChipSelectGroup(
                            label = "Minimum Lease Requirement",
                            options = listOf("6 months", "1 year", "2 years"),
                            selected = aptState.leaseTermMin,
                            onSelected = { aptState.leaseTermMin = it }
                        )

                        ChipSelectGroup(
                            label = "Pet Policy",
                            options = listOf("Pets Allowed", "No Pets Allowed", "Cats Only", "Small Dogs Allowed"),
                            selected = aptState.petPolicy,
                            onSelected = { aptState.petPolicy = it }
                        )

                        ChipSelectGroup(
                            label = "Curfew / Access",
                            options = listOf("No Curfew (24/7 Access)", "Main Gate Locked at 11PM"),
                            selected = aptState.curfew,
                            onSelected = { aptState.curfew = it }
                        )
                    }

                    "Condo", "Condominium", "Studio" -> {
                        ChipSelectGroup(
                            label = "Condo / Association Dues",
                            options = listOf("Inclusive in rent", "Exclusive (Paid separately)", "Included with dues receipt"),
                            selected = condoState.assocDuesStatus,
                            onSelected = { condoState.assocDuesStatus = it }
                        )

                        ChipSelectGroup(
                            label = "Minimum Lease Requirement",
                            options = listOf("6 months", "1 year", "2 years"),
                            selected = condoState.leaseTerm,
                            onSelected = { condoState.leaseTerm = it }
                        )

                        ChipSelectGroup(
                            label = "Pet Policy",
                            options = listOf("Pet-Friendly", "Small Pets with Admin Permit", "Strictly No Pets"),
                            selected = condoState.petPolicy,
                            onSelected = { condoState.petPolicy = it }
                        )

                        ChipSelectGroup(
                            label = "Visitor & Guest Registration",
                            options = listOf("24/7 Concierge & Guest Registration", "Guest ID Required at Lobby", "Restricted Visitors"),
                            selected = condoState.visitors,
                            onSelected = { condoState.visitors = it }
                        )
                    }

                    else -> { // Townhouse
                        ChipSelectGroup(
                            label = "Village / HOA Dues",
                            options = listOf("Inclusive in rent", "Exclusive (Paid to HOA)", "No HOA Dues"),
                            selected = thState.assocDues,
                            onSelected = { thState.assocDues = it }
                        )

                        ChipSelectGroup(
                            label = "Minimum Lease Requirement",
                            options = listOf("1 year", "2 years"),
                            selected = thState.leaseTerm,
                            onSelected = { thState.leaseTerm = it }
                        )

                        ChipSelectGroup(
                            label = "Pet Policy",
                            options = listOf("Pet-Friendly", "Pets Allowed with Security Deposit", "Strictly No Pets"),
                            selected = thState.petPolicy,
                            onSelected = { thState.petPolicy = it }
                        )
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
// HELPER FORM COMPONENTS
// -----------------------------------------------------------------------------
@Composable
private fun FormSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipSelectGroup(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelected(option) },
                    label = { Text(option, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MultiChipSelectGroup(
    label: String,
    options: List<String>,
    selectedItems: List<String>,
    onToggle: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedItems.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = { onToggle(option) },
                    label = { Text(option, fontSize = 11.5.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 4: SUBMISSION STATE (REVIEW & PUBLISH - UNIVERSAL HEADINGS)
// -----------------------------------------------------------------------------
@Composable
private fun Step4ReviewAndPublish(
    userRole: String,
    propertyType: String,
    title: String,
    description: String,
    streetAddress: String,
    photoCount: Int,
    bhState: BhFormState,
    aptState: AptFormState,
    condoState: CondoFormState,
    thState: TownhouseFormState,
    onBack: () -> Unit,
    onPublish: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH")).apply {
        maximumFractionDigits = 0
    }

    val finalRent = when (propertyType) {
        "Boarding House" -> bhState.monthlyRentStr.toDoubleOrNull() ?: 0.0
        "Apartment" -> aptState.monthlyRentStr.toDoubleOrNull() ?: 0.0
        "Condo", "Condominium", "Studio" -> condoState.monthlyRentStr.toDoubleOrNull() ?: 0.0
        else -> thState.monthlyRentStr.toDoubleOrNull() ?: 0.0
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
                text = "Review your listing details organized under the Three Universal Headings.",
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
                }
            }
        }

        // 1. UNIT SPECIFICATIONS CARD
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Unit Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    when (propertyType) {
                        "Boarding House" -> {
                            ReviewDetailRow("Pricing Model", bhState.pricingModel)
                            ReviewDetailRow("Floor Area", "${bhState.floorAreaStr} sq.m")
                            ReviewDetailRow("Room Capacity", bhState.roomCapacity)
                            ReviewDetailRow("Bathroom Setup", bhState.bathroomType)
                            ReviewDetailRow("Furnishing Level", bhState.furnishing)
                            ReviewDetailRow("Tenant Preference", bhState.tenantPreference)
                        }
                        "Apartment" -> {
                            ReviewDetailRow("Floor Area", "${aptState.floorAreaStr} sq.m")
                            ReviewDetailRow("Rooms", "${aptState.bedrooms} Bed, ${aptState.bathrooms} Bath")
                            ReviewDetailRow("Furnishing Level", aptState.furnishing)
                            ReviewDetailRow("Kitchen Setup", aptState.kitchenType)
                        }
                        "Condo", "Condominium", "Studio" -> {
                            ReviewDetailRow("Unit Layout", condoState.unitLayout)
                            ReviewDetailRow("Floor Area", "${condoState.floorAreaStr} sq.m")
                            ReviewDetailRow("Floor Level", condoState.floorLevel)
                            ReviewDetailRow("Furnishing Level", condoState.furnishing)
                            ReviewDetailRow("Kitchen Setup", condoState.kitchen)
                        }
                        else -> { // Townhouse
                            ReviewDetailRow("Lot & Floor Area", "${thState.lotAreaStr} sqm lot / ${thState.floorAreaStr} sqm floor")
                            ReviewDetailRow("Structure", "${thState.stories} Stories, ${thState.bedrooms} Beds, ${thState.bathrooms} Baths")
                            ReviewDetailRow("Maid's Room", if (thState.hasMaidsRoom) "Yes" else "No")
                            ReviewDetailRow("Furnishing Level", thState.furnishing)
                            ReviewDetailRow("Kitchen Setup", thState.kitchen)
                        }
                    }
                }
            }
        }

        // 2. AMENITIES & FACILITIES CARD
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Amenities & Facilities",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    when (propertyType) {
                        "Boarding House" -> {
                            ReviewDetailRow("Vehicle Parking", bhState.parking)
                            ReviewDetailRow("Kitchen Access", bhState.cooking)
                            ReviewDetailRow("Utilities / Inclusions", if (bhState.utilities.isEmpty()) "None selected" else bhState.utilities.joinToString(", "))
                        }
                        "Apartment" -> {
                            ReviewDetailRow("Vehicle Parking", aptState.parking)
                            ReviewDetailRow("Utility Billing", aptState.utilityBilling)
                        }
                        "Condo", "Condominium", "Studio" -> {
                            ReviewDetailRow("Vehicle Parking", condoState.parking)
                            ReviewDetailRow("Building Amenities", if (condoState.amenities.isEmpty()) "None selected" else condoState.amenities.joinToString(", "))
                        }
                        else -> { // Townhouse
                            ReviewDetailRow("Garage Capacity", "${thState.garageCapacity} Car Slot(s)")
                            ReviewDetailRow("Gated Subdivision", if (thState.isGatedSubdivision) "Yes" else "No")
                        }
                    }
                }
            }
        }

        // 3. RULES & LEASE TERMS CARD
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Rules & Lease Terms",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    when (propertyType) {
                        "Boarding House" -> {
                            ReviewDetailRow("Curfew Rule", bhState.curfew)
                            ReviewDetailRow("Visitor Policy", bhState.visitors)
                            ReviewDetailRow("Pet Policy", bhState.petPolicy)
                            ReviewDetailRow("Minimum Lease", bhState.leaseTerm)
                        }
                        "Apartment" -> {
                            ReviewDetailRow("Minimum Lease", aptState.leaseTermMin)
                            ReviewDetailRow("Pet Policy", aptState.petPolicy)
                            ReviewDetailRow("Curfew / Access", aptState.curfew)
                        }
                        "Condo", "Condominium", "Studio" -> {
                            ReviewDetailRow("HOA / Condo Dues", condoState.assocDuesStatus)
                            ReviewDetailRow("Minimum Lease", condoState.leaseTerm)
                            ReviewDetailRow("Pet Policy", condoState.petPolicy)
                            ReviewDetailRow("Visitor Rules", condoState.visitors)
                        }
                        else -> { // Townhouse
                            ReviewDetailRow("HOA / Village Dues", thState.assocDues)
                            ReviewDetailRow("Minimum Lease", thState.leaseTerm)
                            ReviewDetailRow("Pet Policy", thState.petPolicy)
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
