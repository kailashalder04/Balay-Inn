package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.TextButton
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
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.UUID
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.LocationMapPicker
import com.example.ui.components.SearchableDropdownField
import com.example.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch

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

    // Form Fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var propertyType by remember { mutableStateOf("Apartment") }
    var monthlyRentStr by remember { mutableStateOf("") }
    var associationDuesStr by remember { mutableStateOf("") }

    // Poster & Owner Relationship (Crucial Marketplace Rules 1 & 4)
    var posterRelationship by remember { mutableStateOf("Property owner") }
    var isAuthorized by remember { mutableStateOf("Yes") }

    // Cascading Location
    var selectedProvinceId by remember { mutableStateOf("prov_bohol") }
    var selectedCityId by remember { mutableStateOf("bohol_panglao") }
    var selectedBarangayId by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var streetAddress by remember { mutableStateOf("") }
    var locationPrivacy by remember { mutableStateOf("APPROXIMATE") }
    var propertyLat by remember { mutableDoubleStateOf(9.589) }
    var propertyLng by remember { mutableDoubleStateOf(123.774) }

    // Specs
    var bedrooms by remember { mutableIntStateOf(1) }
    var bathrooms by remember { mutableIntStateOf(1) }
    var floorAreaStr by remember { mutableStateOf("35") }
    var furnishedStatus by remember { mutableStateOf("Furnished") }
    var parkingAvailable by remember { mutableStateOf(true) }
    var petPolicy by remember { mutableStateOf("Pets Allowed") }
    var internetIncluded by remember { mutableStateOf(true) }

    // Amenities
    val availableAmenities = listOf("Air Conditioning", "Fiber Internet", "Balcony", "Private Pool", "Gated Security", "CCTV", "Hot Water", "Washing Machine", "Generator")
    val selectedAmenities = remember { mutableStateListOf("Air Conditioning", "Fiber Internet") }

    val context = LocalContext.current
    val uploadedPhotos = remember { mutableStateListOf<String>() }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Property for Rent") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Advertise your rental listing on Balay-Inn across Bohol, Cebu, Bacolod City, and all Philippine provinces.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Step 1: Poster Relationship to Property
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "1. Poster & Ownership Relationship",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "What is your relationship to the property?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        val relationships = listOf(
                            "Property owner",
                            "Family member",
                            "Caretaker",
                            "Property manager",
                            "Licensed broker",
                            "Real-estate salesperson",
                            "Authorized representative"
                        )

                        SearchableDropdownField(
                            label = "Your Role",
                            selectedText = posterRelationship,
                            items = relationships,
                            onItemSelected = { posterRelationship = it },
                            modifier = Modifier.testTag("poster_relationship_dropdown")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Are you authorized by the owner to advertise this property?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("Yes", "No", "Not sure").forEach { option ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isAuthorized == option,
                                        onClick = { isAuthorized = option }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(option, fontSize = 12.sp, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }

            // Step 2: Basic Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "2. Property Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Listing Title *") },
                            placeholder = { Text("e.g. Modern Furnished Studio near Beach") },
                            modifier = Modifier.fillMaxWidth().testTag("post_title_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Property Description *") },
                            placeholder = { Text("Describe living space, surroundings, lease terms...") },
                            modifier = Modifier.fillMaxWidth().testTag("post_desc_input"),
                            maxLines = 4,
                            shape = RoundedCornerShape(10.dp)
                        )

                        val types = listOf("Apartment", "Condominium", "House", "Studio", "Room", "Townhouse")
                        SearchableDropdownField(
                            label = "Property Type",
                            selectedText = propertyType,
                            items = types,
                            onItemSelected = { propertyType = it }
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = monthlyRentStr,
                                onValueChange = { monthlyRentStr = it },
                                label = { Text("Monthly Rent (₱) *") },
                                placeholder = { Text("15000") },
                                modifier = Modifier.weight(1f).testTag("post_rent_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = associationDuesStr,
                                onValueChange = { associationDuesStr = it },
                                label = { Text("Assoc. Dues (₱)") },
                                placeholder = { Text("0") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Step 3: Hierarchical Location
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "3. Geographic Location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Select authoritative geographic hierarchy. Full coverage for Bohol (48 LGUs), Cebu, and Bacolod & its component cities.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Quick Region Selection Chips for Landlords
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedProvinceId == "prov_bacolod",
                                onClick = {
                                    selectedProvinceId = "prov_bacolod"
                                    selectedCityId = "bacolod_city"
                                    selectedBarangayId = ""
                                    propertyLat = 10.6766
                                    propertyLng = 122.9509
                                },
                                label = { Text("🎭 Bacolod & Cities", fontSize = 11.sp) },
                                modifier = Modifier.testTag("quick_chip_bacolod")
                            )
                            FilterChip(
                                selected = selectedProvinceId == "prov_bohol",
                                onClick = {
                                    selectedProvinceId = "prov_bohol"
                                    selectedCityId = "bohol_tagbilaran"
                                    selectedBarangayId = ""
                                    propertyLat = 9.589
                                    propertyLng = 123.774
                                },
                                label = { Text("🏝️ Bohol", fontSize = 11.sp) },
                                modifier = Modifier.testTag("quick_chip_bohol")
                            )
                            FilterChip(
                                selected = selectedProvinceId == "prov_cebu",
                                onClick = {
                                    selectedProvinceId = "prov_cebu"
                                    selectedCityId = "cebu_cebu_city"
                                    selectedBarangayId = ""
                                    propertyLat = 10.3157
                                    propertyLng = 123.8854
                                },
                                label = { Text("🏙️ Cebu", fontSize = 11.sp) },
                                modifier = Modifier.testTag("quick_chip_cebu")
                            )
                        }

                        CascadingLocationSelector(
                            countries = emptyList(),
                            selectedCountryId = "ctry_ph",
                            onCountrySelected = {},
                            regions = emptyList(),
                            selectedRegionId = "",
                            onRegionSelected = {},
                            provinces = allProvinces,
                            selectedProvinceId = selectedProvinceId,
                            onProvinceSelected = {
                                selectedProvinceId = it
                                when (it) {
                                    "prov_bohol" -> {
                                        propertyLat = 9.589
                                        propertyLng = 123.774
                                    }
                                    "prov_bacolod" -> {
                                        propertyLat = 10.6766
                                        propertyLng = 122.9509
                                    }
                                    else -> {
                                        propertyLat = 10.315
                                        propertyLng = 123.885
                                    }
                                }
                            },
                            cities = allCities,
                            selectedCityId = selectedCityId,
                            onCitySelected = { cityId ->
                                selectedCityId = cityId
                                when (cityId) {
                                    "bacolod_city" -> { propertyLat = 10.6766; propertyLng = 122.9509 }
                                    "bacolod_talisay" -> { propertyLat = 10.7333; propertyLng = 122.9667 }
                                    "bacolod_silay" -> { propertyLat = 10.7967; propertyLng = 122.9772 }
                                    "bacolod_bago" -> { propertyLat = 10.5375; propertyLng = 122.8361 }
                                    "bacolod_murcia" -> { propertyLat = 10.6039; propertyLng = 123.0422 }
                                    "bacolod_victorias" -> { propertyLat = 10.9000; propertyLng = 123.0833 }
                                    "bacolod_cadiz" -> { propertyLat = 10.9575; propertyLng = 123.2847 }
                                    "bacolod_sagay" -> { propertyLat = 10.9000; propertyLng = 123.4167 }
                                    "bacolod_san_carlos" -> { propertyLat = 10.4833; propertyLng = 123.4167 }
                                    "bacolod_kabankalan" -> { propertyLat = 9.9889; propertyLng = 122.8125 }
                                    "bacolod_himamaylan" -> { propertyLat = 10.1000; propertyLng = 122.8667 }
                                    "bacolod_la_carlota" -> { propertyLat = 10.4242; propertyLng = 122.9231 }
                                    "bacolod_escalante" -> { propertyLat = 10.8333; propertyLng = 123.5000 }
                                    "bacolod_sipalay" -> { propertyLat = 9.7500; propertyLng = 122.4000 }
                                    "bohol_tagbilaran" -> { propertyLat = 9.6500; propertyLng = 123.8550 }
                                    "bohol_panglao" -> { propertyLat = 9.5800; propertyLng = 123.7700 }
                                    "cebu_cebu_city" -> { propertyLat = 10.3157; propertyLng = 123.8854 }
                                    "cebu_mandaue_city" -> { propertyLat = 10.3340; propertyLng = 123.9400 }
                                    "cebu_lapu_lapu_city" -> { propertyLat = 10.3120; propertyLng = 123.9780 }
                                }
                            },
                            barangays = allBarangays,
                            selectedBarangayId = selectedBarangayId,
                            onBarangaySelected = { selectedBarangayId = it },
                            allowAllOption = false
                        )

                        OutlinedTextField(
                            value = neighborhood,
                            onValueChange = { neighborhood = it },
                            label = { Text("Neighborhood / Sub-area (Optional)") },
                            placeholder = { Text("e.g. Alona Beach, IT Park") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            label = { Text("Nearby Landmark") },
                            placeholder = { Text("e.g. Near Bellevue Resort, Behind Mall") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = streetAddress,
                            onValueChange = { streetAddress = it },
                            label = { Text("Exact Street Address (Confidential)") },
                            placeholder = { Text("Protected by privacy policy, hidden until viewing") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Text("Location Privacy Setting:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("APPROXIMATE" to "Approximate Area", "EXACT" to "Exact", "HIDDEN" to "Hidden").forEach { (code, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = locationPrivacy == code,
                                        onClick = { locationPrivacy = code }
                                    )
                                    Text(label, fontSize = 11.sp, maxLines = 1, softWrap = false)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Property Map Pin (Interactive Pin Placement):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Tap or drag anywhere on the live map to pinpoint approximate property location.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LocationMapPicker(
                            initialLat = propertyLat,
                            initialLng = propertyLng,
                            onLocationPicked = { lat, lng ->
                                propertyLat = lat
                                propertyLng = lng
                            }
                        )
                    }
                }
            }

            // Step 4: Specifications & Amenities
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "4. Specifications & Amenities",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bedrooms: $bedrooms", fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { if (bedrooms > 0) bedrooms-- }, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("-") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("$bedrooms", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { bedrooms++ }, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("+") }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Bathrooms: $bathrooms", fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { if (bathrooms > 1) bathrooms-- }, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("-") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("$bathrooms", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { bathrooms++ }, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) { Text("+") }
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Dedicated Parking Available", fontSize = 12.sp)
                            Switch(checked = parkingAvailable, onCheckedChange = { parkingAvailable = it })
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Internet / WiFi Included", fontSize = 12.sp)
                            Switch(checked = internetIncluded, onCheckedChange = { internetIncluded = it })
                        }

                        Text("Amenities Available:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableAmenities.forEach { amenity ->
                                val isChecked = selectedAmenities.contains(amenity)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = {
                                        if (isChecked) selectedAmenities.remove(amenity) else selectedAmenities.add(amenity)
                                    },
                                    label = { Text(amenity, fontSize = 11.sp, maxLines = 1, softWrap = false) }
                                )
                            }
                        }
                    }
                }
            }

            // Step 5: Property Photos (Upload from Memory)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "5. Property Photos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (uploadedPhotos.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "${uploadedPhotos.size} photo(s)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Upload real photos directly from your device memory / gallery. Multiple photos are supported.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Main Direct Upload Button from Memory
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("upload_images_from_memory_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Upload Photos",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uploadedPhotos.isEmpty()) "Upload Images from Memory" else "Add More Images from Memory",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Preview of uploaded images with delete button
                        if (uploadedPhotos.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                itemsIndexed(uploadedPhotos) { index, photoPath ->
                                    Box(
                                        modifier = Modifier
                                            .size(width = 90.dp, height = 90.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        AsyncImage(
                                            model = photoPath,
                                            contentDescription = "Uploaded Photo ${index + 1}",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Remove button
                                        IconButton(
                                            onClick = { uploadedPhotos.removeAt(index) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(Color.Black.copy(alpha = 0.65f))
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove photo",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        // Main cover badge for first photo
                                        if (index == 0) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(bottomEnd = 8.dp),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text(
                                                    text = "Cover",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            TextButton(
                                onClick = {
                                    uploadedPhotos.addAll(
                                        listOf(
                                            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop",
                                            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&auto=format&fit=crop"
                                        )
                                    )
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    "Or load sample photos for demo",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val rent = monthlyRentStr.toDoubleOrNull()
                        if (title.isBlank() || rent == null || rent <= 0) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please enter a valid title and monthly rent.")
                            }
                            return@Button
                        }

                        val finalImages = if (uploadedPhotos.isNotEmpty()) {
                            uploadedPhotos.toList()
                        } else {
                            listOf("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop")
                        }

                        viewModel.postProperty(
                            title = title,
                            description = description.ifBlank { "Modern comfortable rental property located in ${neighborhood.ifBlank { "peaceful area" }}." },
                            type = propertyType,
                            rent = rent,
                            provinceId = selectedProvinceId,
                            cityId = selectedCityId,
                            barangayId = selectedBarangayId,
                            neighborhood = neighborhood,
                            landmark = landmark,
                            streetAddress = streetAddress,
                            privacy = locationPrivacy,
                            lat = propertyLat,
                            lng = propertyLng,
                            beds = bedrooms,
                            baths = bathrooms,
                            area = floorAreaStr.toDoubleOrNull() ?: 35.0,
                            furnished = furnishedStatus,
                            parking = parkingAvailable,
                            pets = petPolicy,
                            dues = associationDuesStr.toDoubleOrNull() ?: 0.0,
                            internetIncluded = internetIncluded,
                            relationship = posterRelationship,
                            isAuthorized = isAuthorized == "Yes",
                            amenities = selectedAmenities.toList(),
                            imageUrls = finalImages,
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Property listed successfully!")
                                    onSuccess()
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("publish_listing_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Publish Rental Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                }

                Spacer(modifier = Modifier.height(30.dp))
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
