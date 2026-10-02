package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.InteractiveRentalMap
import com.example.ui.components.PropertyCard
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val properties by viewModel.properties.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    val countries by viewModel.countries.collectAsState()
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var focusedPropertyId by remember { mutableStateOf<String?>(null) }

    val savedIds = remember(savedProperties) {
        savedProperties.map { it.propertyId }.toSet()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("explore_screen")
    ) {
        val isWideScreen = maxWidth >= 760.dp
        val showSearchHeader = isWideScreen || viewMode == "LIST"
        val focusedProp = remember(focusedPropertyId, properties) {
            properties.find { it.id == focusedPropertyId }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Search Bar & Toggle View (shown in LIST view or Desktop side-by-side)
            if (showSearchHeader) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Small / Compact Search Box
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (filterState.keyword.isEmpty()) {
                                    Text(
                                        text = "Search location, landmark...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                BasicTextField(
                                    value = filterState.keyword,
                                    onValueChange = { viewModel.updateKeyword(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("search_text_input"),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                                )
                            }
                            if (filterState.keyword.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateKeyword("") },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Filter Button with Badge
                    val hasActiveFilters = filterState.provinceId.isNotEmpty() ||
                            filterState.cityMunicipalityId.isNotEmpty() ||
                            filterState.barangayId.isNotEmpty() ||
                            filterState.propertyType != "All" ||
                            filterState.listedBy != "All" ||
                            filterState.authorizedPosterOnly ||
                            filterState.verifiedOnly ||
                            filterState.minRent > 0 ||
                            filterState.maxRent < 100000

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable { showFilterSheet = true }
                            .testTag("filter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        if (hasActiveFilters) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(7.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Toggle View (List vs Map)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable {
                                viewModel.setViewMode(if (viewMode == "LIST") "MAP" else "LIST")
                            }
                            .testTag("toggle_map_list_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (viewMode == "LIST") Icons.Default.Map else Icons.Default.ViewList,
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Active Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Province filter chip
                    if (filterState.provinceId.isNotEmpty()) {
                        val provName = allProvinces.find { it.id == filterState.provinceId }?.name ?: "Province"
                        FilterBadge(
                            label = provName,
                            onRemove = { viewModel.updateProvince("") }
                        )
                    }

                    // City filter chip
                    if (filterState.cityMunicipalityId.isNotEmpty()) {
                        val cityName = allCities.find { it.id == filterState.cityMunicipalityId }?.name ?: "City"
                        FilterBadge(
                            label = cityName,
                            onRemove = { viewModel.updateCity("") }
                        )
                    }

                    // Barangay filter chip
                    if (filterState.barangayId.isNotEmpty()) {
                        val bgyName = allBarangays.find { it.id == filterState.barangayId }?.name ?: "Barangay"
                        FilterBadge(
                            label = bgyName,
                            onRemove = { viewModel.updateBarangay("") }
                        )
                    }

                    // Property type filter chip
                    if (filterState.propertyType != "All") {
                        FilterBadge(
                            label = filterState.propertyType,
                            onRemove = { viewModel.updatePropertyType("All") }
                        )
                    }

                    // Listed by filter chip
                    if (filterState.listedBy != "All") {
                        FilterBadge(
                            label = "Listed by: ${filterState.listedBy}",
                            onRemove = { viewModel.updateListedBy("All") }
                        )
                    }

                    // Authorized posters filter chip
                    if (filterState.authorizedPosterOnly) {
                        FilterBadge(
                            label = "Authorized Posters",
                            onRemove = { viewModel.toggleAuthorizedPosterOnly() }
                        )
                    }

                    // Verified only chip
                    if (filterState.verifiedOnly) {
                        FilterBadge(
                            label = "Verified Only",
                            onRemove = { viewModel.toggleVerifiedOnly() }
                        )
                    }

                    // Reset all if any filter is active
                    val hasActiveFilters = filterState.provinceId.isNotEmpty() ||
                        filterState.cityMunicipalityId.isNotEmpty() ||
                        filterState.barangayId.isNotEmpty() ||
                        filterState.propertyType != "All" ||
                        filterState.listedBy != "All" ||
                        filterState.authorizedPosterOnly ||
                        filterState.verifiedOnly ||
                        filterState.minRent > 0 ||
                        filterState.maxRent < 100000

                    if (hasActiveFilters) {
                        Text(
                            text = "Reset All",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .clickable { viewModel.resetFilters() }
                                .padding(horizontal = 6.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }

        // Subheader showing total results & sort
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${properties.size} Properties Available",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sort: ",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = when (filterState.sortBy) {
                        "PRICE_ASC" -> "Price: Low to High"
                        "PRICE_DESC" -> "Price: High to Low"
                        else -> "Newest First"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.clickable {
                        val next = when (filterState.sortBy) {
                            "NEWEST" -> "PRICE_ASC"
                            "PRICE_ASC" -> "PRICE_DESC"
                            else -> "NEWEST"
                        }
                        viewModel.setSortBy(next)
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (isWideScreen) {
                // Desktop / Tablet Side-by-Side: [Property Results List (44%)] | [LIVE INTERACTIVE MAP (56%)]
                Row(modifier = Modifier.fillMaxSize().padding(bottom = 70.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(0.44f)
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${properties.size} Properties Available",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (filterState.bounds != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.clickable { viewModel.clearMapAreaSearch() }
                                ) {
                                    Text(
                                        text = "Area Filter Active ✕",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            if (properties.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(40.dp))
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text("No properties found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text("Try resetting map area or search filters.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Spacer(modifier = Modifier.height(14.dp))
                                            Button(onClick = { viewModel.resetFilters() }) { Text("Reset Filters") }
                                        }
                                    }
                                }
                            } else {
                                items(properties, key = { it.id }) { property ->
                                    val isSaved = savedIds.contains(property.id)
                                    val locationDisplay = viewModel.getLocationDisplay(property)

                                    PropertyCard(
                                        property = property,
                                        locationDisplay = locationDisplay,
                                        imageUrls = propertyImagesMap[property.id] ?: emptyList(),
                                        isSaved = isSaved,
                                        onSaveToggle = { viewModel.toggleSave(property.id) },
                                        onClick = { onNavigateToPropertyDetail(property.id) },
                                        onViewOnMap = {
                                            focusedPropertyId = property.id
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.56f)
                            .fillMaxSize()
                    ) {
                        InteractiveRentalMap(
                            properties = properties,
                            selectedProperty = focusedProp,
                            onPropertySelected = { prop ->
                                onNavigateToPropertyDetail(prop.id)
                            },
                            allProvinces = allProvinces,
                            allCities = allCities,
                            allBarangays = allBarangays,
                            onSearchThisArea = { minLat, maxLat, minLon, maxLon ->
                                viewModel.searchMapArea(minLat, maxLat, minLon, maxLon)
                            },
                            isAreaSearchActive = filterState.bounds != null,
                            onClearAreaSearch = { viewModel.clearMapAreaSearch() },
                            onToggleSave = { id -> viewModel.toggleSave(id) },
                            savedPropertyIds = savedIds,
                            propertyImagesMap = propertyImagesMap,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            } else {
                // Mobile Layout: Responsive [List] or [Map]
                if (viewMode == "MAP") {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 70.dp)) {
                        InteractiveRentalMap(
                            properties = properties,
                            selectedProperty = focusedProp,
                            onPropertySelected = { prop ->
                                onNavigateToPropertyDetail(prop.id)
                            },
                            allProvinces = allProvinces,
                            allCities = allCities,
                            allBarangays = allBarangays,
                            onSearchThisArea = { minLat, maxLat, minLon, maxLon ->
                                viewModel.searchMapArea(minLat, maxLat, minLon, maxLon)
                            },
                            isAreaSearchActive = filterState.bounds != null,
                            onClearAreaSearch = { viewModel.clearMapAreaSearch() },
                            onToggleSave = { id -> viewModel.toggleSave(id) },
                            savedPropertyIds = savedIds,
                            propertyImagesMap = propertyImagesMap,
                            onToggleViewMode = { viewModel.setViewMode("LIST") },
                            onOpenFilters = { showFilterSheet = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (properties.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No properties found in this location",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Try broadening your geographic search or resetting price and bedroom filters.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = { viewModel.resetFilters() }) {
                                            Text("Reset Filters")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(properties, key = { it.id }) { property ->
                                val isSaved = savedIds.contains(property.id)
                                val locationDisplay = viewModel.getLocationDisplay(property)

                                PropertyCard(
                                    property = property,
                                    locationDisplay = locationDisplay,
                                    imageUrls = propertyImagesMap[property.id] ?: emptyList(),
                                    isSaved = isSaved,
                                    onSaveToggle = { viewModel.toggleSave(property.id) },
                                    onClick = { onNavigateToPropertyDetail(property.id) },
                                    onViewOnMap = {
                                        focusedPropertyId = property.id
                                        viewModel.setViewMode("MAP")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Comprehensive Filter Modal Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Rental Properties",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Geographic Cascading Hierarchy
                Text(
                    text = "Geographic Location",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                CascadingLocationSelector(
                    countries = countries,
                    selectedCountryId = "ctry_ph",
                    onCountrySelected = { /* PH default */ },
                    regions = emptyList(),
                    selectedRegionId = "",
                    onRegionSelected = { },
                    provinces = allProvinces,
                    selectedProvinceId = filterState.provinceId,
                    onProvinceSelected = { viewModel.updateProvince(it) },
                    cities = allCities,
                    selectedCityId = filterState.cityMunicipalityId,
                    onCitySelected = { viewModel.updateCity(it) },
                    barangays = allBarangays,
                    selectedBarangayId = filterState.barangayId,
                    onBarangaySelected = { viewModel.updateBarangay(it) },
                    allowAllOption = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Property Type
                Text(
                    text = "Property Type",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val types = listOf("All", "Studio", "Condominium", "Apartment", "House", "Room", "Townhouse")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { type ->
                        FilterChip(
                            selected = filterState.propertyType == type,
                            onClick = { viewModel.updatePropertyType(type) },
                            label = { Text(type, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Listed By
                Text(
                    text = "Listed By",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Filter by poster authority & relationship to property",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                val listedByOptions = listOf(
                    "All",
                    "Direct Owner",
                    "Licensed Broker",
                    "Property Manager",
                    "Caretaker",
                    "Family Member",
                    "Authorized Representative"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listedByOptions.forEach { option ->
                        FilterChip(
                            selected = filterState.listedBy == option,
                            onClick = { viewModel.updateListedBy(option) },
                            label = { Text(option, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Authorized posters toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Authorized Posters Only", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Only show listings posted by direct owners or verified representatives",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = filterState.authorizedPosterOnly,
                        onCheckedChange = { viewModel.toggleAuthorizedPosterOnly() }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: Monthly Budget (₱)
                Text(
                    text = "Monthly Rent Budget (₱)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("₱${filterState.minRent.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₱${filterState.maxRent.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                RangeSlider(
                    value = filterState.minRent.toFloat()..filterState.maxRent.toFloat(),
                    onValueChange = { range ->
                        viewModel.updatePriceRange(range.start.toDouble(), range.endInclusive.toDouble())
                    },
                    valueRange = 0f..100000f,
                    steps = 19
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 5: Specifications & Policies
                Text(
                    text = "Specifications & Amenities",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Parking toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dedicated Parking Required", fontSize = 13.sp)
                    Switch(
                        checked = filterState.parkingRequired,
                        onCheckedChange = { viewModel.toggleParkingRequired() }
                    )
                }

                // Pet friendly toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pet-Friendly Only", fontSize = 13.sp)
                    Switch(
                        checked = filterState.petFriendly,
                        onCheckedChange = { viewModel.togglePetFriendly() }
                    )
                }

                // Verified only toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verified Listings Only (Level 3+)", fontSize = 13.sp)
                    Switch(
                        checked = filterState.verifiedOnly,
                        onCheckedChange = { viewModel.toggleVerifiedOnly() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Apply & Reset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetFilters()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset", maxLines = 1, softWrap = false)
                    }

                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply Filters (${properties.size})", maxLines = 1, softWrap = false)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
}

@Composable
private fun FilterBadge(
    label: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.clickable { onRemove() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Clear,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
