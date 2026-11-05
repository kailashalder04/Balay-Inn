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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.model.ApartmentDetails
import com.example.data.model.BoardingHouseDetails
import com.example.data.model.CondominiumDetails
import com.example.data.model.PropertyCategorySpecifications
import com.example.data.model.RoleVerificationDetails
import com.example.data.model.TownhouseDetails
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.SearchableDropdownField
import com.example.ui.viewmodel.MarketplaceViewModel
import com.example.util.LocationCoordinatesHelper
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
    val currentUser by viewModel.currentUser.collectAsState()
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // CONSTRAINT 1: Host must be verified before adding a property
    if (!currentUser.isHostVerified) {
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
            modifier = modifier
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.size(76.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Verification Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Sorry, verification is needed before adding property.",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Philippine marketplace guidelines and Balay-Inn trust policies require all property hosts, caretakers, and brokers to complete role & identity verification in Profile Settings before publishing listings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verify in Profile Setting", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    // -------------------------------------------------------------
    // 3-STEP STREAMLINED FLOW (Role verification is housed in Profile)
    // Step 1: Base Property Info & Photos
    // Step 2: Specifications, Utilities & Terms
    // Step 3: Review & Publish
    // -------------------------------------------------------------
    var currentStep by remember { mutableIntStateOf(1) }

    // STEP 1 STATE: Location & Base Information
    var selectedProvinceId by remember { mutableStateOf("prov_bohol") }
    var selectedCityId by remember { mutableStateOf("bohol_panglao") }
    var selectedBarangayId by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var selectedLandmarkOption by remember { mutableStateOf("") }
    var customLandmarkText by remember { mutableStateOf("") }

    // Auto Location Coordinates Pin
    var currentLatitude by remember { mutableDoubleStateOf(9.5800) }
    var currentLongitude by remember { mutableDoubleStateOf(123.7714) }

    // Property Type (RADIO BUTTON REQUIREMENT)
    var selectedPropertyType by remember { mutableStateOf("Boarding House") }

    // Photos
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

    // STEP 2 STATE: Specifications, Utilities & Terms
    var monthlyRentStr by remember { mutableStateOf("2500") }
    var depositTerms by remember { mutableStateOf("1 Month Advance + 1 Month Deposit") }
    var genderAllowed by remember { mutableStateOf("Any / Co-ed / Mixed") }
    var waterBillingSetup by remember { mutableStateOf("Sub-metered Water (pay based on actual cu.m reading)") }
    var electricityBillingSetup by remember { mutableStateOf("Individual Electric Sub-meter") }
    var freeWifiIncluded by remember { mutableStateOf(true) }

    // Room Specs
    var roomLayoutType by remember { mutableStateOf("Bedspace / Dormitory") }
    var totalBeds by remember { mutableIntStateOf(4) }
    var availableBeds by remember { mutableIntStateOf(2) }
    var totalCapacity by remember { mutableIntStateOf(4) }
    var bathroomType by remember { mutableStateOf("Shared / Communal Bathroom") }
    var airConditioningType by remember { mutableStateOf("Air-Conditioned") }
    var floorAreaStr by remember { mutableStateOf("18") }
    var furnishing by remember { mutableStateOf("Fully-Furnished") }
    var cookingRule by remember { mutableStateOf("Light Cooking Allowed (Electric kettle/rice cooker)") }
    var curfewRule by remember { mutableStateOf("10:00 PM Gate Lock") }
    var visitorsRule by remember { mutableStateOf("Lobby Visitors Only") }
    var petPolicy by remember { mutableStateOf("Strictly No Pets") }
    var parkingPolicy by remember { mutableStateOf("Motorcycle Parking Included") }
    var leaseTerm by remember { mutableStateOf("6 months") }

    // Effective landmark text
    val effectiveLandmark = if (selectedLandmarkOption == "Others (Specify Custom Landmark)") {
        customLandmarkText
    } else {
        selectedLandmarkOption.ifBlank { customLandmarkText }
    }

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
            // Step Progress Header (3 Steps)
            MultiStepProgressHeader(
                currentStep = currentStep,
                onStepClick = { step ->
                    if (step < currentStep) currentStep = step
                }
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                label = "post_property_flow"
            ) { step ->
                when (step) {
                    1 -> Step1PropertyInfoAndPhotos(
                        allProvinces = allProvinces,
                        allCities = allCities,
                        allBarangays = allBarangays,
                        selectedProvinceId = selectedProvinceId,
                        onProvinceSelected = { provId ->
                            selectedProvinceId = provId
                            val (lat, lng) = LocationCoordinatesHelper.resolveCoordinates(provId, selectedCityId)
                            currentLatitude = lat
                            currentLongitude = lng
                        },
                        selectedCityId = selectedCityId,
                        onCitySelected = { cityId ->
                            selectedCityId = cityId
                            val (lat, lng) = LocationCoordinatesHelper.resolveCoordinates(selectedProvinceId, cityId)
                            currentLatitude = lat
                            currentLongitude = lng
                        },
                        selectedBarangayId = selectedBarangayId,
                        onBarangaySelected = { selectedBarangayId = it },
                        streetAddress = streetAddress,
                        onStreetAddressChange = { streetAddress = it },
                        neighborhood = neighborhood,
                        onNeighborhoodChange = { neighborhood = it },
                        selectedLandmarkOption = selectedLandmarkOption,
                        onLandmarkOptionSelected = { selectedLandmarkOption = it },
                        customLandmarkText = customLandmarkText,
                        onCustomLandmarkChange = { customLandmarkText = it },
                        currentLatitude = currentLatitude,
                        currentLongitude = currentLongitude,
                        onOpenMaps = {
                            LocationCoordinatesHelper.openInGoogleMaps(
                                context = context,
                                latitude = currentLatitude,
                                longitude = currentLongitude,
                                label = listingTitle.ifBlank { "Rental Property Location" }
                            )
                        },
                        selectedPropertyType = selectedPropertyType,
                        onPropertyTypeSelected = { selectedPropertyType = it },
                        uploadedPhotos = uploadedPhotos,
                        onLaunchPhotoPicker = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onAddDemoPhotos = {
                            val samplePhotos = listOf(
                                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop",
                                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop",
                                "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800&auto=format&fit=crop",
                                "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&auto=format&fit=crop"
                            )
                            samplePhotos.forEach { url ->
                                if (!uploadedPhotos.contains(url)) uploadedPhotos.add(url)
                            }
                            scope.launch {
                                snackbarHostState.showSnackbar("Added 4 authentic sample demo property photos.")
                            }
                        },
                        onRemovePhoto = { idx -> uploadedPhotos.removeAt(idx) },
                        listingTitle = listingTitle,
                        onListingTitleChange = { listingTitle = it },
                        listingDescription = listingDescription,
                        onListingDescriptionChange = { listingDescription = it },
                        onNext = {
                            if (listingTitle.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Please enter a listing title.") }
                                return@Step1PropertyInfoAndPhotos
                            }
                            if (streetAddress.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Please enter the street address or building name.") }
                                return@Step1PropertyInfoAndPhotos
                            }
                            if (uploadedPhotos.isEmpty()) {
                                scope.launch { snackbarHostState.showSnackbar("Please add at least 1 photo or click 'Add Sample Photos (Demo)'.") }
                                return@Step1PropertyInfoAndPhotos
                            }
                            currentStep = 2
                        }
                    )

                    2 -> Step2SpecificationsUtilitiesTerms(
                        propertyType = selectedPropertyType,
                        monthlyRentStr = monthlyRentStr,
                        onMonthlyRentChange = { monthlyRentStr = it },
                        depositTerms = depositTerms,
                        onDepositTermsChange = { depositTerms = it },
                        genderAllowed = genderAllowed,
                        onGenderAllowedChange = { genderAllowed = it },
                        waterBillingSetup = waterBillingSetup,
                        onWaterBillingChange = { waterBillingSetup = it },
                        electricityBillingSetup = electricityBillingSetup,
                        onElectricityBillingChange = { electricityBillingSetup = it },
                        freeWifiIncluded = freeWifiIncluded,
                        onFreeWifiChange = { freeWifiIncluded = it },
                        roomLayoutType = roomLayoutType,
                        onRoomLayoutChange = { roomLayoutType = it },
                        totalBeds = totalBeds,
                        onTotalBedsChange = { totalBeds = it },
                        availableBeds = availableBeds,
                        onAvailableBedsChange = { availableBeds = it },
                        totalCapacity = totalCapacity,
                        onTotalCapacityChange = { totalCapacity = it },
                        bathroomType = bathroomType,
                        onBathroomTypeChange = { bathroomType = it },
                        airConditioningType = airConditioningType,
                        onAirConditioningChange = { airConditioningType = it },
                        floorAreaStr = floorAreaStr,
                        onFloorAreaChange = { floorAreaStr = it },
                        furnishing = furnishing,
                        onFurnishingChange = { furnishing = it },
                        cookingRule = cookingRule,
                        onCookingRuleChange = { cookingRule = it },
                        curfewRule = curfewRule,
                        onCurfewRuleChange = { curfewRule = it },
                        visitorsRule = visitorsRule,
                        onVisitorsRuleChange = { visitorsRule = it },
                        petPolicy = petPolicy,
                        onPetPolicyChange = { petPolicy = it },
                        parkingPolicy = parkingPolicy,
                        onParkingPolicyChange = { parkingPolicy = it },
                        leaseTerm = leaseTerm,
                        onLeaseTermChange = { leaseTerm = it },
                        onBack = { currentStep = 1 },
                        onNext = {
                            val rent = monthlyRentStr.toDoubleOrNull() ?: 0.0
                            if (rent <= 0.0) {
                                scope.launch { snackbarHostState.showSnackbar("Please enter a valid monthly rent amount.") }
                                return@Step2SpecificationsUtilitiesTerms
                            }
                            currentStep = 3
                        }
                    )

                    3 -> Step3ReviewAndPublish(
                        currentUser = currentUser,
                        propertyType = selectedPropertyType,
                        title = listingTitle,
                        description = listingDescription,
                        streetAddress = streetAddress,
                        neighborhood = neighborhood,
                        landmark = effectiveLandmark,
                        latitude = currentLatitude,
                        longitude = currentLongitude,
                        photoCount = uploadedPhotos.size,
                        photos = uploadedPhotos.toList(),
                        monthlyRent = monthlyRentStr.toDoubleOrNull() ?: 2500.0,
                        depositTerms = depositTerms,
                        genderAllowed = genderAllowed,
                        waterBilling = waterBillingSetup,
                        electricityBilling = electricityBillingSetup,
                        freeWifi = freeWifiIncluded,
                        roomLayout = roomLayoutType,
                        totalBeds = totalBeds,
                        availableBeds = availableBeds,
                        bathroomType = bathroomType,
                        airConditioning = airConditioningType,
                        floorArea = floorAreaStr.toDoubleOrNull() ?: 18.0,
                        furnishing = furnishing,
                        cookingRule = cookingRule,
                        curfew = curfewRule,
                        visitors = visitorsRule,
                        petPolicy = petPolicy,
                        parking = parkingPolicy,
                        leaseTerm = leaseTerm,
                        onBack = { currentStep = 2 },
                        onOpenMaps = {
                            LocationCoordinatesHelper.openInGoogleMaps(
                                context = context,
                                latitude = currentLatitude,
                                longitude = currentLongitude,
                                label = listingTitle.ifBlank { "Rental Property Location" }
                            )
                        },
                        onPublish = {
                            val roleVerification = RoleVerificationDetails(
                                role = currentUser.relationshipToProperty.ifBlank { "Property owner" },
                                isTitleOwnerConfirmed = true,
                                ownerIdType = "Verified National ID (PhilSys)",
                                ownerIdNumber = "PH-VERIFIED-LEVEL-2",
                                contactPreference = "Direct Phone & App Chat",
                                landlordFullName = currentUser.fullName,
                                representativeRelationship = currentUser.relationshipToProperty,
                                isAuthorizedByOwner = currentUser.isAuthorizedByOwner
                            )

                            val categorySpecifications = when (selectedPropertyType) {
                                "Boarding House" -> PropertyCategorySpecifications(
                                    category = "Boarding House",
                                    boardingHouse = BoardingHouseDetails(
                                        pricingModel = if (roomLayoutType.contains("Bedspace")) "Per Person/Bed" else "Per Room",
                                        monthlyRent = monthlyRentStr.toDoubleOrNull() ?: 2500.0,
                                        roomCapacity = "$totalBeds-bed bunk",
                                        tenantPreference = genderAllowed,
                                        bathroomType = bathroomType,
                                        utilities = listOf(waterBillingSetup, electricityBillingSetup).let {
                                            if (freeWifiIncluded) it + "Free Wi-Fi included" else it
                                        },
                                        floorAreaSqm = floorAreaStr.toDoubleOrNull() ?: 18.0,
                                        furnishing = furnishing,
                                        parking = parkingPolicy,
                                        curfewRule = curfewRule,
                                        visitorsRule = visitorsRule,
                                        petPolicy = petPolicy,
                                        cookingRule = cookingRule,
                                        leaseTermMinimum = leaseTerm
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Apartment" -> PropertyCategorySpecifications(
                                    category = "Apartment",
                                    apartment = ApartmentDetails(
                                        monthlyRent = monthlyRentStr.toDoubleOrNull() ?: 12000.0,
                                        totalFloorAreaSqm = floorAreaStr.toDoubleOrNull() ?: 42.0,
                                        bedrooms = maxOf(1, totalBeds / 2),
                                        bathrooms = if (bathroomType.contains("Private")) 1 else 1,
                                        leaseTermMinimum = leaseTerm,
                                        furnishing = furnishing,
                                        kitchenType = cookingRule,
                                        utilityBilling = "$waterBillingSetup • $electricityBillingSetup",
                                        parking = parkingPolicy,
                                        petPolicy = petPolicy,
                                        curfewRule = curfewRule
                                    ),
                                    roleVerification = roleVerification
                                )
                                "Condominium", "Condo", "Studio" -> PropertyCategorySpecifications(
                                    category = selectedPropertyType,
                                    condominium = CondominiumDetails(
                                        monthlyRent = monthlyRentStr.toDoubleOrNull() ?: 22000.0,
                                        associationDues = "Inclusive in rent",
                                        unitLayout = if (selectedPropertyType == "Studio") "Studio" else "1-Bedroom",
                                        totalFloorAreaSqm = floorAreaStr.toDoubleOrNull() ?: 28.0,
                                        floorLevel = "High Floor",
                                        parking = parkingPolicy,
                                        amenities = listOf("Pool", "Gym", "24/7 Security", "WiFi"),
                                        petPolicy = petPolicy,
                                        furnishing = furnishing,
                                        leaseTermMinimum = leaseTerm,
                                        visitorsRule = visitorsRule,
                                        kitchenType = cookingRule
                                    ),
                                    roleVerification = roleVerification
                                )
                                else -> PropertyCategorySpecifications(
                                    category = "Townhouse",
                                    townhouse = TownhouseDetails(
                                        monthlyRent = monthlyRentStr.toDoubleOrNull() ?: 30000.0,
                                        lotAreaSqm = 120.0,
                                        floorAreaSqm = floorAreaStr.toDoubleOrNull() ?: 95.0,
                                        stories = 2,
                                        bedrooms = 3,
                                        bathrooms = 2,
                                        garageCapacity = 2,
                                        hasMaidsRoom = false,
                                        isGatedSubdivision = true,
                                        furnishing = furnishing,
                                        petPolicy = petPolicy,
                                        associationDues = "Inclusive in rent",
                                        leaseTermMinimum = leaseTerm,
                                        kitchenType = cookingRule
                                    ),
                                    roleVerification = roleVerification
                                )
                            }

                            val rent = monthlyRentStr.toDoubleOrNull() ?: 2500.0
                            val beds = when (selectedPropertyType) {
                                "Apartment" -> maxOf(1, totalBeds / 2)
                                "Townhouse" -> 3
                                "Studio" -> 0
                                else -> totalBeds
                            }
                            val baths = if (bathroomType.contains("Private")) 1 else 1
                            val area = floorAreaStr.toDoubleOrNull() ?: 18.0

                            viewModel.postProperty(
                                title = listingTitle,
                                description = listingDescription.ifBlank {
                                    "$selectedPropertyType in $streetAddress. Features $genderAllowed occupancy, $waterBillingSetup, $electricityBillingSetup, $depositTerms."
                                },
                                type = selectedPropertyType,
                                rent = rent,
                                provinceId = selectedProvinceId,
                                cityId = selectedCityId,
                                barangayId = selectedBarangayId,
                                neighborhood = neighborhood,
                                landmark = effectiveLandmark,
                                streetAddress = streetAddress,
                                privacy = "EXACT",
                                lat = currentLatitude,
                                lng = currentLongitude,
                                beds = beds,
                                baths = baths,
                                area = area,
                                furnished = furnishing,
                                parking = !parkingPolicy.contains("None"),
                                pets = petPolicy,
                                dues = 0.0,
                                internetIncluded = freeWifiIncluded,
                                relationship = currentUser.relationshipToProperty.ifBlank { "Property owner" },
                                isAuthorized = currentUser.isAuthorizedByOwner,
                                amenities = listOfNotNull(
                                    airConditioningType,
                                    if (freeWifiIncluded) "WiFi" else null,
                                    "24/7 Security",
                                    if (bathroomType.contains("Private")) "Private Bathroom" else null
                                ),
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
// MULTI-STEP PROGRESS HEADER (3 Steps)
// -----------------------------------------------------------------------------
@Composable
private fun MultiStepProgressHeader(
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    val stepTitles = listOf("1. Property Info", "2. Specs & Utilities", "3. Review & Publish")

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
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { currentStep / 3f },
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
// STEP 1: PROPERTY INFORMATION & PHOTOS
// -----------------------------------------------------------------------------
@Composable
private fun Step1PropertyInfoAndPhotos(
    allProvinces: List<ProvinceEntity>,
    allCities: List<CityMunicipalityEntity>,
    allBarangays: List<BarangayEntity>,
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
    selectedLandmarkOption: String,
    onLandmarkOptionSelected: (String) -> Unit,
    customLandmarkText: String,
    onCustomLandmarkChange: (String) -> Unit,
    currentLatitude: Double,
    currentLongitude: Double,
    onOpenMaps: () -> Unit,
    selectedPropertyType: String,
    onPropertyTypeSelected: (String) -> Unit,
    uploadedPhotos: List<String>,
    onLaunchPhotoPicker: () -> Unit,
    onAddDemoPhotos: () -> Unit,
    onRemovePhoto: (Int) -> Unit,
    listingTitle: String,
    onListingTitleChange: (String) -> Unit,
    listingDescription: String,
    onListingDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val suggestedLandmarks = remember(selectedCityId, selectedProvinceId) {
        LocationCoordinatesHelper.getSuggestedLandmarks(selectedCityId, selectedProvinceId)
    }

    val selectedCityName = allCities.find { it.id == selectedCityId }?.name ?: "Selected Location"
    val selectedProvName = allProvinces.find { it.id == selectedProvinceId }?.name ?: "Central Visayas"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 1: Property Information & Photos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Location, photos, and property category for your Philippine rental space.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. PROPERTY TYPE SELECTOR (RADIO BUTTON REQUIREMENT)
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
                        text = "Select the rental category that best fits your space:",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val propertyTypes = listOf(
                        Triple("Boarding House", "Boarding House", "Bedspacer & room rentals with shared or private amenities"),
                        Triple("Apartment", "Apartment", "Self-contained multi-room units with kitchen & living space"),
                        Triple("Condominium", "Condominium", "High-rise residential units with building amenities & security"),
                        Triple("Studio", "Studio", "Compact open-plan single living unit with private bath"),
                        Triple("Townhouse", "Townhouse", "Multi-floor residential home or attached dwelling unit")
                    )

                    propertyTypes.forEach { (typeKey, typeLabel, typeDesc) ->
                        val isSelected = selectedPropertyType == typeKey
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPropertyTypeSelected(typeKey) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onPropertyTypeSelected(typeKey) },
                                    modifier = Modifier.testTag("radio_prop_type_$typeKey")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = typeLabel,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 13.5.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = typeDesc,
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

        // 2. GEOGRAPHIC LOCATION (REGION VII & REGION VIII CASCADING)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Geographic Location",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Region VII & VIII",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Cascading Province -> City -> Barangay
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
                        placeholder = { Text("e.g. Unit 302, Greenview Residences, Mango Ave") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_address_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = onNeighborhoodChange,
                        label = { Text("Subdivision / Zone / Compound") },
                        placeholder = { Text("e.g. Villa Aurora Subdivision") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Nearby Landmark (Dropdown with standard landmarks + Others)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Nearby Landmark", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        val landmarkOptions = suggestedLandmarks + listOf("Others (Specify Custom Landmark)")

                        SearchableDropdownField(
                            label = "Select Nearby Landmark",
                            selectedText = selectedLandmarkOption.ifBlank { "Select landmark or choose Others" },
                            items = landmarkOptions,
                            onItemSelected = { chosen ->
                                onLandmarkOptionSelected(chosen)
                                if (chosen != "Others (Specify Custom Landmark)") {
                                    onCustomLandmarkChange(chosen)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.testTag("landmark_dropdown")
                        )

                        if (selectedLandmarkOption == "Others (Specify Custom Landmark)" || selectedLandmarkOption.isEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = customLandmarkText,
                                onValueChange = onCustomLandmarkChange,
                                label = { Text("Custom Nearby Landmark / Reference") },
                                placeholder = { Text("e.g. Beside 7-Eleven, 200m from Church") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    // Location Coordinates Display below subdivision and landmark (Clickable Google Maps)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMaps() }
                            .testTag("location_coordinates_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Pinned GPS Coordinates",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = String.format(Locale.US, "%.4f° N, %.4f° E • %s, %s", currentLatitude, currentLongitude, selectedCityName, selectedProvName),
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tap to preview pin in Google Maps",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = onOpenMaps,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. PROPERTY PHOTOS & DEMO ASSETS
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
                            color = if (uploadedPhotos.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${uploadedPhotos.size} photo(s)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Upload real photos of the room, bedspace, bathroom, and kitchen.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onLaunchPhotoPicker,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Photos", fontSize = 12.sp)
                        }

                        // DEMO PHOTO BUTTON (REQUIREMENT 3)
                        OutlinedButton(
                            onClick = onAddDemoPhotos,
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Sample Photos (Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uploadedPhotos.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            itemsIndexed(uploadedPhotos) { index, photoUrl ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Photo $index",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.65f),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(22.dp)
                                            .clickable { onRemovePhoto(index) }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. LISTING TITLE & DESCRIPTION
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Listing Overview *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = listingTitle,
                        onValueChange = onListingTitleChange,
                        label = { Text("Listing Title *") },
                        placeholder = { Text("e.g. Spacious Air-conditioned Bedspace near IT Park") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = listingDescription,
                        onValueChange = onListingDescriptionChange,
                        label = { Text("Description & House Rules") },
                        placeholder = { Text("Describe room amenities, accessibility to public transport, curfew, and terms.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("post_description_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Next Button
        item {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("step1_next_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue to Specs & Utilities", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 2: SPECIFICATIONS, UTILITIES & TERMS
// -----------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step2SpecificationsUtilitiesTerms(
    propertyType: String,
    monthlyRentStr: String,
    onMonthlyRentChange: (String) -> Unit,
    depositTerms: String,
    onDepositTermsChange: (String) -> Unit,
    genderAllowed: String,
    onGenderAllowedChange: (String) -> Unit,
    waterBillingSetup: String,
    onWaterBillingChange: (String) -> Unit,
    electricityBillingSetup: String,
    onElectricityBillingChange: (String) -> Unit,
    freeWifiIncluded: Boolean,
    onFreeWifiChange: (Boolean) -> Unit,
    roomLayoutType: String,
    onRoomLayoutChange: (String) -> Unit,
    totalBeds: Int,
    onTotalBedsChange: (Int) -> Unit,
    availableBeds: Int,
    onAvailableBedsChange: (Int) -> Unit,
    totalCapacity: Int,
    onTotalCapacityChange: (Int) -> Unit,
    bathroomType: String,
    onBathroomTypeChange: (String) -> Unit,
    airConditioningType: String,
    onAirConditioningChange: (String) -> Unit,
    floorAreaStr: String,
    onFloorAreaChange: (String) -> Unit,
    furnishing: String,
    onFurnishingChange: (String) -> Unit,
    cookingRule: String,
    onCookingRuleChange: (String) -> Unit,
    curfewRule: String,
    onCurfewRuleChange: (String) -> Unit,
    visitorsRule: String,
    onVisitorsRuleChange: (String) -> Unit,
    petPolicy: String,
    onPetPolicyChange: (String) -> Unit,
    parkingPolicy: String,
    onParkingPolicyChange: (String) -> Unit,
    leaseTerm: String,
    onLeaseTermChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 2: Specifications, Utilities & Terms",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Deposit terms, allowed gender, water & electric billing, and room layout details.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. RENT & DEPOSIT TERMS (REQUIREMENT 4)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Rent & Deposit Terms *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = monthlyRentStr,
                        onValueChange = onMonthlyRentChange,
                        label = { Text("Monthly Rent (PHP) *") },
                        placeholder = { Text("2500") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("post_rent_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Deposit Question
                    Text(
                        text = "Advance Rent & Security Deposit Terms *",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp
                    )
                    val depositOptions = listOf(
                        "1 Month Advance + 1 Month Deposit",
                        "1 Month Advance + 2 Months Deposit",
                        "1 Month Advance Only (No Deposit)",
                        "No Advance / No Deposit"
                    )

                    depositOptions.forEach { option ->
                        val isSelected = depositTerms == option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDepositTermsChange(option) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onDepositTermsChange(option) }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Lease term
                    Text("Minimum Lease Duration", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        listOf("1 month", "3 months", "6 months", "1 year").forEach { term ->
                            FilterChip(
                                selected = leaseTerm == term,
                                onClick = { onLeaseTermChange(term) },
                                label = { Text(term, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 2. GENDER POLICY (REQUIREMENT 4: "What gender is allowed?")
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Allowed Gender Policy *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "What gender is allowed to rent this space?",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val genderOptions = listOf(
                        Triple("Any / Co-ed / Mixed", "👫 Any / Co-ed / Mixed", "Open to all genders, working professionals, students, couples"),
                        Triple("Female Only", "👩 Female Only", "Strictly female tenants only (All-female boarding house or dorm)"),
                        Triple("Male Only", "👨 Male Only", "Strictly male tenants only (All-male boarding house or dorm)")
                    )

                    genderOptions.forEach { (key, label, desc) ->
                        val isSelected = genderAllowed == key
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = if (isSelected) BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onGenderAllowedChange(key) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onGenderAllowedChange(key) }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. WATER, UTILITIES & ELECTRICITY (REQUIREMENT 4: "the most important is water, utilities")
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Water & Utilities Billing Setup *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Water Billing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Water Billing Setup *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    val waterOptions = listOf(
                        "Sub-metered Water (pay based on actual cu.m reading)",
                        "Fixed Monthly Rate (e.g. ₱150/head/month)",
                        "Free / Inclusive in Monthly Rent",
                        "Direct Water District Meter (MCWD / BWUI / etc.)"
                    )

                    waterOptions.forEach { opt ->
                        val isSelected = waterBillingSetup == opt
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWaterBillingChange(opt) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { onWaterBillingChange(opt) })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(opt, fontSize = 12.sp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Electricity Billing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Electricity Billing Setup *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    val electricityOptions = listOf(
                        "Individual Electric Sub-meter",
                        "Shared Equal Split among Tenants",
                        "Inclusive in Monthly Rent (with appliance surcharge)",
                        "Direct Utility Meter (VECO / MECO / BOHECO / SAMELCO)"
                    )

                    electricityOptions.forEach { opt ->
                        val isSelected = electricityBillingSetup == opt
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onElectricityBillingChange(opt) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { onElectricityBillingChange(opt) })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(opt, fontSize = 12.sp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    // Wi-Fi toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Free High-Speed Wi-Fi Included", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Included in rent with no extra charge", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = freeWifiIncluded,
                            onCheckedChange = onFreeWifiChange
                        )
                    }
                }
            }
        }

        // 4. ROOM SPECIFICATIONS (REQUIREMENT 4: "and the room specs")
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Room Specifications *",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Room Layout
                    Text("Room Layout Type", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        listOf("Bedspace / Dormitory", "Private Room", "Entire Unit / Studio").forEach { layout ->
                            FilterChip(
                                selected = roomLayoutType == layout,
                                onClick = { onRoomLayoutChange(layout) },
                                label = { Text(layout, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Beds & Capacity steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CounterStepper(
                            label = "Total Beds",
                            value = totalBeds,
                            onValueChange = onTotalBedsChange,
                            modifier = Modifier.weight(1f)
                        )
                        CounterStepper(
                            label = "Available Beds",
                            value = availableBeds,
                            onValueChange = onAvailableBedsChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = floorAreaStr,
                        onValueChange = onFloorAreaChange,
                        label = { Text("Room Floor Area (sq.m)") },
                        placeholder = { Text("18") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Bathroom Setup
                    Text("Bathroom Setup", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Private En-Suite Bathroom", "Shared / Communal Bathroom").forEach { bath ->
                            FilterChip(
                                selected = bathroomType == bath,
                                onClick = { onBathroomTypeChange(bath) },
                                label = { Text(bath, fontSize = 11.5.sp) }
                            )
                        }
                    }

                    // Air Conditioning
                    Text("Cooling & Airflow", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Air-Conditioned", "Non-Aircon / Fan-Cooled").forEach { ac ->
                            FilterChip(
                                selected = airConditioningType == ac,
                                onClick = { onAirConditioningChange(ac) },
                                label = { Text(ac, fontSize = 11.5.sp) }
                            )
                        }
                    }

                    // Furnishing
                    Text("Furnishing Level", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Fully-Furnished", "Semi-Furnished", "Unfurnished").forEach { f ->
                            FilterChip(
                                selected = furnishing == f,
                                onClick = { onFurnishingChange(f) },
                                label = { Text(f, fontSize = 11.5.sp) }
                            )
                        }
                    }

                    // Kitchen & Cooking
                    Text("Cooking Policy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    val cookingOptions = listOf(
                        "Light Cooking Allowed (Electric kettle/rice cooker)",
                        "Full Cooking Allowed (Dirty Kitchen / Stove)",
                        "Strictly No Cooking Allowed"
                    )
                    cookingOptions.forEach { cook ->
                        val isSelected = cookingRule == cook
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCookingRuleChange(cook) }
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { onCookingRuleChange(cook) })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(cook, fontSize = 12.sp)
                        }
                    }

                    // Curfew & Visitors
                    Text("Curfew & Gate Schedule", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        listOf("No Curfew (24/7 Access)", "10:00 PM Gate Lock", "11:00 PM Gate Lock").forEach { c ->
                            FilterChip(
                                selected = curfewRule == c,
                                onClick = { onCurfewRuleChange(c) },
                                label = { Text(c, fontSize = 11.5.sp) }
                            )
                        }
                    }

                    // Pet Policy
                    Text("Pet Policy", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Strictly No Pets", "Pets Allowed").forEach { pet ->
                            FilterChip(
                                selected = petPolicy == pet,
                                onClick = { onPetPolicyChange(pet) },
                                label = { Text(pet, fontSize = 11.5.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Navigation Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(50.dp)
                        .testTag("step2_next_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Review & Publish", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// STEP 3: REVIEW & PUBLISH
// -----------------------------------------------------------------------------
@Composable
private fun Step3ReviewAndPublish(
    currentUser: com.example.data.local.entity.UserEntity,
    propertyType: String,
    title: String,
    description: String,
    streetAddress: String,
    neighborhood: String,
    landmark: String,
    latitude: Double,
    longitude: Double,
    photoCount: Int,
    photos: List<String>,
    monthlyRent: Double,
    depositTerms: String,
    genderAllowed: String,
    waterBilling: String,
    electricityBilling: String,
    freeWifi: Boolean,
    roomLayout: String,
    totalBeds: Int,
    availableBeds: Int,
    bathroomType: String,
    airConditioning: String,
    floorArea: Double,
    furnishing: String,
    cookingRule: String,
    curfew: String,
    visitors: String,
    petPolicy: String,
    parking: String,
    leaseTerm: String,
    onBack: () -> Unit,
    onOpenMaps: () -> Unit,
    onPublish: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Step 3: Review & Publish",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Verify your listing specifications, utilities, and terms before publishing.",
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
                        text = "₱${currencyFormat.format(monthlyRent).replace("PHP", "").trim()} / month",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    ReviewDetailRow("Property Type", propertyType)
                    ReviewDetailRow("Address", streetAddress)
                    if (neighborhood.isNotBlank()) ReviewDetailRow("Subdivision / Zone", neighborhood)
                    if (landmark.isNotBlank()) ReviewDetailRow("Landmark", landmark)
                    ReviewDetailRow("Photos", "$photoCount photo(s) uploaded")
                }
            }
        }

        // Auto Coordinates & Map
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Pinned Coordinates", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(
                            String.format(Locale.US, "%.4f° N, %.4f° E", latitude, longitude),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onOpenMaps,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open Map", fontSize = 11.sp)
                    }
                }
            }
        }

        // Specifications & Terms
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Specifications & Utilities",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    ReviewDetailRow("Security Deposit Terms", depositTerms)
                    ReviewDetailRow("Gender Allowed", genderAllowed)
                    ReviewDetailRow("Water Billing", waterBilling)
                    ReviewDetailRow("Electricity Billing", electricityBilling)
                    ReviewDetailRow("Wi-Fi Included", if (freeWifi) "Yes (Free)" else "No")
                    ReviewDetailRow("Room Setup", roomLayout)
                    ReviewDetailRow("Bed Capacity", "$totalBeds total / $availableBeds available")
                    ReviewDetailRow("Bathroom Setup", bathroomType)
                    ReviewDetailRow("Air Conditioning", airConditioning)
                    ReviewDetailRow("Floor Area", "$floorArea sq.m")
                    ReviewDetailRow("Furnishing", furnishing)
                    ReviewDetailRow("Cooking Allowed", cookingRule)
                    ReviewDetailRow("Curfew Policy", curfew)
                    ReviewDetailRow("Pet Policy", petPolicy)
                }
            }
        }

        // Publish Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit Specs")
                }

                Button(
                    onClick = onPublish,
                    modifier = Modifier
                        .weight(1.6f)
                        .height(50.dp)
                        .testTag("publish_listing_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Property", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// HELPER COMPONENTS
// -----------------------------------------------------------------------------
@Composable
private fun ReviewDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
    }
}

@Composable
private fun CounterStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            IconButton(
                onClick = { if (value > 1) onValueChange(value - 1) },
                modifier = Modifier.size(36.dp)
            ) {
                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "$value",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            IconButton(
                onClick = { onValueChange(value + 1) },
                modifier = Modifier.size(36.dp)
            ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
