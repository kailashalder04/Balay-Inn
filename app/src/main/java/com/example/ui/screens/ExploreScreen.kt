package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.ComparisonMatrixSheet
import com.example.ui.components.InteractiveRentalMap
import com.example.ui.components.PropertyCard
import com.example.ui.components.SelectedCategoryHeaderCard
import com.example.ui.theme.SteelBlue
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToChat: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val allUnfilteredProperties by viewModel.allUnfilteredProperties.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()
    val comparedProperties by viewModel.comparedProperties.collectAsState()
    val comparedPropertyIds = remember(comparedProperties) { comparedProperties.map { it.id }.toSet() }

    val countries by viewModel.countries.collectAsState()
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showComparisonMatrix by remember { mutableStateOf(false) }
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
        val showSearchHeader = true
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
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
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
                            filterState.maxRent < 100000 ||
                            filterState.leaseDurationFilter != "All" ||
                            filterState.moveInDateFilter != "All"

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

                // Philippine Rental Categories Bar (Visible to tenants/renters)
                Box(modifier = Modifier.padding(top = 10.dp)) {
                    CategoryFilterRow(
                        selectedCategory = filterState.propertyType,
                        allProperties = allUnfilteredProperties,
                        onSelectCategory = { selectedCat ->
                            viewModel.updatePropertyType(selectedCat)
                        },
                        isDark = isDark
                    )
                }

                // Active Category Market Standard Info Card for Renters
                SelectedCategoryHeaderCard(
                    selectedCategory = filterState.propertyType,
                    filteredCount = properties.size,
                    onResetCategory = { viewModel.updatePropertyType("All") },
                    isDark = isDark,
                    modifier = Modifier.padding(top = 8.dp)
                )

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

                    // Target market filter chip
                    if (filterState.targetMarket != "All") {
                        FilterBadge(
                            label = "Target: ${filterState.targetMarket}",
                            onRemove = { viewModel.updateTargetMarket("All") }
                        )
                    }

                    // Utility and rules filter chip
                    if (filterState.utilityRuleFilter != "All") {
                        FilterBadge(
                            label = "Rule: ${filterState.utilityRuleFilter}",
                            onRemove = { viewModel.updateUtilityRuleFilter("All") }
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

                    // Gender policy chip
                    if (filterState.genderRule != "all") {
                        FilterBadge(
                            label = if (filterState.genderRule == "female_only") "Female Only" else "Male Only",
                            onRemove = { viewModel.updateGenderRule("all") }
                        )
                    }

                    // Min beds chip
                    if (filterState.minAvailableBeds > 0) {
                        FilterBadge(
                            label = "${filterState.minAvailableBeds}+ Beds",
                            onRemove = { viewModel.updateMinAvailableBeds(0) }
                        )
                    }

                    // Flexible Lease Duration chip
                    if (filterState.leaseDurationFilter != "All") {
                        FilterBadge(
                            label = "Lease: ${filterState.leaseDurationFilter}",
                            onRemove = { viewModel.updateLeaseDuration("All") }
                        )
                    }

                    // Move-in Date filter chip
                    if (filterState.moveInDateFilter != "All") {
                        FilterBadge(
                            label = "Move-in: ${filterState.moveInDateFilter}",
                            onRemove = { viewModel.updateMoveInDate("All") }
                        )
                    }

                    // Sort By chip
                    if (filterState.sortBy != "NEWEST") {
                        val sortLabel = when (filterState.sortBy) {
                            "highest_available" -> "Most Available Beds"
                            "PRICE_ASC", "lowest_price" -> "Lowest Price"
                            "PRICE_DESC" -> "Highest Price"
                            else -> filterState.sortBy
                        }
                        FilterBadge(
                            label = "Sort: $sortLabel",
                            onRemove = { viewModel.setSortBy("NEWEST") }
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
                        filterState.genderRule != "all" ||
                        filterState.minAvailableBeds > 0 ||
                        filterState.sortBy != "NEWEST" ||
                        filterState.minRent > 0 ||
                        filterState.maxRent < 100000 ||
                        filterState.leaseDurationFilter != "All" ||
                        filterState.moveInDateFilter != "All"

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
                                    val locationDisplay = viewModel.getBarangayProvinceDisplay(property)

                                    PropertyCard(
                                        property = property,
                                        locationDisplay = locationDisplay,
                                        imageUrls = propertyImagesMap[property.id] ?: emptyList(),
                                        isSaved = isSaved,
                                        onSaveToggle = { viewModel.toggleSave(property.id) },
                                        isCompared = comparedPropertyIds.contains(property.id),
                                        onCompareToggle = { viewModel.toggleCompare(property) },
                                        onClick = { onNavigateToPropertyDetail(property.id) },
                                        onViewOnMap = {
                                            focusedPropertyId = property.id
                                        },
                                        onMessageHost = if (onNavigateToChat != null) {
                                            {
                                                viewModel.startConversation(property, "Hi ${property.posterName}! I am interested in '${property.title}'. Is this room available for viewing?") { convId ->
                                                    onNavigateToChat(convId)
                                                }
                                            }
                                        } else null
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
                                val locationDisplay = viewModel.getBarangayProvinceDisplay(property)

                                PropertyCard(
                                    property = property,
                                    locationDisplay = locationDisplay,
                                    imageUrls = propertyImagesMap[property.id] ?: emptyList(),
                                    isSaved = isSaved,
                                    onSaveToggle = { viewModel.toggleSave(property.id) },
                                    isCompared = comparedPropertyIds.contains(property.id),
                                    onCompareToggle = { viewModel.toggleCompare(property) },
                                    onClick = { onNavigateToPropertyDetail(property.id) },
                                    onViewOnMap = {
                                        focusedPropertyId = property.id
                                        viewModel.setViewMode("MAP")
                                    },
                                    onMessageHost = if (onNavigateToChat != null) {
                                        {
                                            viewModel.startConversation(property, "Hi ${property.posterName}! I am interested in '${property.title}'. Is this room available for viewing?") { convId ->
                                                onNavigateToChat(convId)
                                            }
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }

            // Floating Comparison Bottom Bar inside Box
            androidx.compose.animation.AnimatedVisibility(
                visible = comparedProperties.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 75.dp, start = 16.dp, end = 16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFF0F2B48),
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFF3B82F6).copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("floating_compare_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(Color(0xFF3B82F6), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CompareArrows,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${comparedProperties.size} Selected for Comparison",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (comparedProperties.size >= 2) "Ready for side-by-side matrix" else "Select at least 2 to compare",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(
                                onClick = { viewModel.clearCompare() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Clear", color = Color(0xFFFCA5A5), fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Button(
                                onClick = { showComparisonMatrix = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2563EB),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("launch_compare_matrix_button")
                            ) {
                                Text("Compare", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Side-by-Side Comparison Matrix Bottom Sheet
    if (showComparisonMatrix) {
        ComparisonMatrixSheet(
            properties = comparedProperties,
            imagesMap = propertyImagesMap,
            onDismiss = { showComparisonMatrix = false },
            onRemoveProperty = { propertyId -> viewModel.removeFromCompare(propertyId) },
            onClearAll = {
                viewModel.clearCompare()
                showComparisonMatrix = false
            },
            onNavigateToDetail = { id ->
                showComparisonMatrix = false
                onNavigateToPropertyDetail(id)
            }
        )
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

                // Section 2: Property Type (Philippine Rental Market Standards)
                Text(
                    text = "Property Type (Philippine Standards)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Select from the 4 primary Philippine rental categories or locality residential houses",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterState.propertyType == "All",
                        onClick = { viewModel.updatePropertyType("All") },
                        label = { Text("All", fontSize = 12.sp, maxLines = 1, softWrap = false) }
                    )
                    PhilippineRentalPropertyCategory.ALL_CATEGORIES.forEach { cat ->
                        val isSelected = filterState.propertyType == cat.displayName || filterState.propertyType == cat.shortLabel
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updatePropertyType(cat.displayName) },
                            label = { Text("${cat.targetEmoji} ${cat.shortLabel}", fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                // If a property type is selected, show its Philippine Rental Market Standard profile card
                if (filterState.propertyType != "All") {
                    val activeTypeCat = PhilippineRentalPropertyCategory.fromPropertyType(filterState.propertyType)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "${activeTypeCat.targetEmoji} ${activeTypeCat.displayName} Specifications",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "👤 Target: ${activeTypeCat.targetMarket}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "🏠 Expected Amenities: ${activeTypeCat.expectedAmenities.joinToString(", ")}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "⚡ Utilities: ${activeTypeCat.waterDefault} • ${activeTypeCat.electricityDefault} • ${activeTypeCat.internetDefault}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "📋 House Rules: ${activeTypeCat.curfewDefault} • ${activeTypeCat.securityDefault} • ${activeTypeCat.visitorsDefault} • ${activeTypeCat.cookingDefault} • ${activeTypeCat.petPolicyDefault} • ${activeTypeCat.parkingDefault}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Dynamic Type-Specific Filters (Condition-Driven)
                val isBoarding = filterState.propertyType.contains("Board", ignoreCase = true) || filterState.propertyType.contains("Bed", ignoreCase = true)
                val isApartment = filterState.propertyType.contains("Apart", ignoreCase = true) || filterState.propertyType.contains("Flat", ignoreCase = true)
                val isCondo = filterState.propertyType.contains("Condo", ignoreCase = true) || filterState.propertyType.contains("Studio", ignoreCase = true)
                val isTownhouse = filterState.propertyType.contains("Town", ignoreCase = true) || filterState.propertyType.contains("House", ignoreCase = true)

                if (isBoarding) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🛏️ Boarding House Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Pricing Model:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Per Person/Bed", "Per Room").forEach { opt ->
                            FilterChip(
                                selected = filterState.boardingPricingModel == opt,
                                onClick = { viewModel.updateBoardingPricingModel(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Room Capacity:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Single", "Double bed", "4-bed bunk", "6-bed bunk").forEach { opt ->
                            FilterChip(
                                selected = filterState.boardingCapacity == opt,
                                onClick = { viewModel.updateBoardingCapacity(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Bathroom Setup:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Private/En-suite", "Shared/Communal").forEach { opt ->
                            FilterChip(
                                selected = filterState.boardingBathroom == opt,
                                onClick = { viewModel.updateBoardingBathroom(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                } else if (isApartment) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🚪 Apartment Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Kitchen Setup:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Indoor/Integral", "Outdoor/Dirty Kitchen").forEach { opt ->
                            FilterChip(
                                selected = filterState.apartmentKitchen == opt,
                                onClick = { viewModel.updateApartmentKitchen(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Utility Billing:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Own Meter", "Sub-metered by Landlord").forEach { opt ->
                            FilterChip(
                                selected = filterState.apartmentBilling == opt,
                                onClick = { viewModel.updateApartmentBilling(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                } else if (isCondo) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🏢 Condominium & Studio Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Unit Layout:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Studio", "1BR", "2BR", "Loft", "Bi-level").forEach { opt ->
                            FilterChip(
                                selected = filterState.condoLayout == opt,
                                onClick = { viewModel.updateCondoLayout(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Association Dues:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Inclusive in rent", "Exclusive/Tenant pays separately").forEach { opt ->
                            FilterChip(
                                selected = filterState.condoDues == opt,
                                onClick = { viewModel.updateCondoDues(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Parking Arrangement:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Included in rent", "Available for extra fee").forEach { opt ->
                            FilterChip(
                                selected = filterState.condoParking == opt,
                                onClick = { viewModel.updateCondoParking(opt) },
                                label = { Text(opt, fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }
                } else if (isTownhouse) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "🏡 Townhouse Specifications",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Number of Stories:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterState.townhouseStories == null,
                            onClick = { viewModel.updateTownhouseStories(null) },
                            label = { Text("All", fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                        )
                        listOf(1, 2, 3).forEach { st ->
                            FilterChip(
                                selected = filterState.townhouseStories == st,
                                onClick = { viewModel.updateTownhouseStories(st) },
                                label = { Text("$st-Storey", fontSize = 11.5.sp, maxLines = 1, softWrap = false) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterState.townhouseGatedSubdivision,
                            onClick = { viewModel.updateTownhouseGated(!filterState.townhouseGatedSubdivision) },
                            label = { Text(if (filterState.townhouseGatedSubdivision) "✓ Gated Subdivision" else "Gated Subdivision", fontSize = 11.5.sp) }
                        )
                        FilterChip(
                            selected = filterState.townhouseMaidsRoom,
                            onClick = { viewModel.updateTownhouseMaidsRoom(!filterState.townhouseMaidsRoom) },
                            label = { Text(if (filterState.townhouseMaidsRoom) "✓ Maid's Quarters" else "Maid's Quarters", fontSize = 11.5.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2B: Target Market / Demographics
                Text(
                    text = "Target Market / Demographics",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val targetOptions = listOf(
                        "All",
                        "Students & Budget BPO",
                        "Solo Renters & Couples",
                        "Small Families & Sharers",
                        "High-earning Professionals & Expats"
                    )
                    targetOptions.forEach { opt ->
                        FilterChip(
                            selected = filterState.targetMarket == opt,
                            onClick = { viewModel.updateTargetMarket(opt) },
                            label = { Text(opt, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2C: Utilities & House Rules Setup
                Text(
                    text = "Utilities & House Rules Standards",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val utilityRuleOptions = listOf(
                        "All",
                        "Sub-metered Electricity",
                        "Flat Rate Water",
                        "Shared WiFi",
                        "24/7 Gate Key (No Curfew)",
                        "Curfew Hours",
                        "Caretaker On-Site",
                        "24/7 Roaming Guards",
                        "Strictly Induction (No LPG)",
                        "LPG Allowed",
                        "Daytime Visitors Allowed",
                        "Strictly No Visitors"
                    )
                    utilityRuleOptions.forEach { opt ->
                        FilterChip(
                            selected = filterState.utilityRuleFilter == opt,
                            onClick = { viewModel.updateUtilityRuleFilter(opt) },
                            label = { Text(opt, fontSize = 12.sp, maxLines = 1, softWrap = false) }
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

                Spacer(modifier = Modifier.height(18.dp))

                // Section 6: Gender Rule Policy
                Text(
                    text = "Gender Policy (Boarding House)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val genderOptions = listOf(
                        "all" to "All Genders / Mixed",
                        "female_only" to "Female Only (Ladies)",
                        "male_only" to "Male Only (Men)"
                    )
                    genderOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = filterState.genderRule == key,
                            onClick = { viewModel.updateGenderRule(key) },
                            label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 7: Minimum Available Beds
                Text(
                    text = "Available Bed Vacancies",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val bedOptions = listOf(
                        0 to "Any Beds",
                        1 to "1+ Beds Vacant",
                        2 to "2+ Beds Vacant",
                        4 to "4+ Beds Vacant"
                    )
                    bedOptions.forEach { (beds, label) ->
                        FilterChip(
                            selected = filterState.minAvailableBeds == beds,
                            onClick = { viewModel.updateMinAvailableBeds(beds) },
                            label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 8: Flexible Lease Duration
                Text(
                    text = "Flexible Lease Duration",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Find short stays, academic semester terms, or long-term annual rentals",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val leaseOptions = listOf(
                        "All" to "All Terms",
                        "Flexible (1-3 mos)" to "⚡ Flexible (1-3 mos)",
                        "Mid-term (4-6 mos)" to "📅 Mid-term (4-6 mos)",
                        "Academic / Semester" to "🎓 Academic / Semester",
                        "Annual (12+ mos)" to "🏠 Annual (12+ mos)"
                    )
                    leaseOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = filterState.leaseDurationFilter == key,
                            onClick = { viewModel.updateLeaseDuration(key) },
                            label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 9: Move-In Date & Availability Window
                Text(
                    text = "Move-In Date & Availability",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Filter properties ready for immediate occupancy or upcoming move-in dates",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val moveInOptions = listOf(
                        "All" to "Any Move-in Date",
                        "Immediate / Today" to "⚡ Immediate / Ready Today",
                        "Within 7 Days" to "📦 Within 7 Days",
                        "Within 15 Days" to "🗓️ Within 15 Days",
                        "Within 30 Days" to "🗓️ Within 30 Days"
                    )
                    moveInOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = filterState.moveInDateFilter == key,
                            onClick = { viewModel.updateMoveInDate(key) },
                            label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 10: Sort Order
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val sortOptions = listOf(
                        "NEWEST" to "Recently Updated",
                        "highest_available" to "Most Available Beds",
                        "PRICE_ASC" to "Lowest Price",
                        "PRICE_DESC" to "Highest Price"
                    )
                    sortOptions.forEach { (key, label) ->
                        FilterChip(
                            selected = filterState.sortBy == key,
                            onClick = { viewModel.setSortBy(key) },
                            label = { Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false) }
                        )
                    }
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
