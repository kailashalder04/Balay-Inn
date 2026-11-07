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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.zIndex
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.ComparisonMatrixSheet
import com.example.ui.components.InteractiveRentalMap
import com.example.ui.components.PropertyCard
import com.example.ui.theme.SteelBlue
import com.example.ui.viewmodel.MarketplaceViewModel
data class PremiumLocationHub(
    val name: String,
    val icon: String,
    val subtitle: String,
    val description: String
)

data class GroupedLocationItem(
    val id: String,
    val name: String,
    val category: String, // "Region", "Province / City", "Barangay"
    val subtitle: String,
    val filterToken: String,
    val icon: String
)

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
    val allRegions by viewModel.allRegions.collectAsState()
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()

    var showLocationSuggestions by remember { mutableStateOf(false) }

    val premiumLocations = remember {
        listOf(
            PremiumLocationHub("Cebu IT Park", "🏙️", "Apas, Cebu City", "Tech & BPO Hub"),
            PremiumLocationHub("Lahug", "🌿", "Salinas Drive", "UP Cebu & Dining"),
            PremiumLocationHub("Mabolo", "🏢", "SM City Corridor", "Central Transit"),
            PremiumLocationHub("Panglao", "🏖️", "Alona & Doljo", "Beach Resorts"),
            PremiumLocationHub("Tagbilaran City", "🏛️", "Dampas & Cogon", "Bohol Capital"),
            PremiumLocationHub("USLS Area", "🎓", "Villamonte, Bacolod", "University Belt"),
            PremiumLocationHub("Guadalupe", "🌳", "V. Rama, Cebu", "Residential & Med"),
            PremiumLocationHub("Mandaue City", "🌇", "Subangdaku", "Metro Gateway")
        )
    }

    val allLocationSuggestions = remember(allRegions, allProvinces, allCities, allBarangays) {
        val list = mutableListOf<GroupedLocationItem>()
        // 1. Regions
        list.add(GroupedLocationItem("reg_vii", "Central Visayas (Region VII)", "Region", "Cebu, Bohol & Negros Oriental", "Central Visayas", "🗺️"))
        list.add(GroupedLocationItem("reg_vi", "Western Visayas (Region VI)", "Region", "Negros Occidental & Iloilo", "Western Visayas", "🗺️"))
        list.add(GroupedLocationItem("reg_ncr", "National Capital Region (NCR)", "Region", "Metro Manila Hub", "National Capital Region", "🗺️"))
        allRegions.forEach { r ->
            if (list.none { it.id == r.id }) {
                list.add(GroupedLocationItem(r.id, r.name, "Region", "Administrative Region", r.name, "🗺️"))
            }
        }

        // 2. Province / City
        list.add(GroupedLocationItem("city_cebu", "Cebu City", "Province / City", "Highly Urbanized City • Metro Cebu", "Cebu City", "🏙️"))
        list.add(GroupedLocationItem("city_tagbilaran", "Tagbilaran City", "Province / City", "Component City • Bohol Capital", "Tagbilaran City", "🏙️"))
        list.add(GroupedLocationItem("city_bacolod", "Bacolod City", "Province / City", "Highly Urbanized City • Negros Occ.", "Bacolod City", "🏙️"))
        list.add(GroupedLocationItem("city_mandaue", "Mandaue City", "Province / City", "Component City • Metro Cebu", "Mandaue City", "🏙️"))
        list.add(GroupedLocationItem("city_lapu_lapu", "Lapu-Lapu City", "Province / City", "Mactan Island • Metro Cebu", "Lapu-Lapu City", "🏙️"))
        list.add(GroupedLocationItem("city_panglao", "Panglao", "Province / City", "Municipality • Bohol", "Panglao", "🏖️"))
        list.add(GroupedLocationItem("city_dauis", "Dauis", "Province / City", "Municipality • Bohol", "Dauis", "🏝️"))
        list.add(GroupedLocationItem("prov_cebu", "Cebu", "Province / City", "Province • Central Visayas", "Cebu", "🏝️"))
        list.add(GroupedLocationItem("prov_bohol", "Bohol", "Province / City", "Province • Central Visayas", "Bohol", "🏝️"))
        list.add(GroupedLocationItem("prov_bacolod", "Negros Occidental", "Province / City", "Province • Western Visayas", "Negros Occidental", "🌾"))
        allCities.forEach { c ->
            if (list.none { it.name.equals(c.name, true) }) {
                list.add(GroupedLocationItem(c.id, c.name, "Province / City", "${c.type} • Active LGU", c.name, "🏙️"))
            }
        }

        // 3. Barangay / Hubs
        list.add(GroupedLocationItem("hub_it_park", "Cebu IT Park", "Barangay", "Barangay Apas, Cebu City • BPO & Tech Hub", "Cebu IT Park", "📍"))
        list.add(GroupedLocationItem("hub_mabolo", "Mabolo", "Barangay", "Cebu City • Commercial & SM City Hub", "Mabolo", "📍"))
        list.add(GroupedLocationItem("hub_lahug", "Lahug", "Barangay", "Cebu City • Salinas Drive & UP Cebu", "Lahug", "📍"))
        list.add(GroupedLocationItem("hub_guadalupe", "Guadalupe", "Barangay", "Cebu City • Residential & Chong Hua", "Guadalupe", "📍"))
        list.add(GroupedLocationItem("hub_banilad", "Banilad", "Barangay", "Cebu City • Country Mall Strip", "Banilad", "📍"))
        list.add(GroupedLocationItem("hub_talamban", "Talamban", "Barangay", "Cebu City • USC Talamban Campus", "Talamban", "📍"))
        list.add(GroupedLocationItem("hub_capitol", "Capitol Site", "Barangay", "Cebu City • Provincial Capitol & Hospitals", "Capitol Site", "📍"))
        list.add(GroupedLocationItem("hub_fuente", "Fuente Osmeña", "Barangay", "Cebu City • Uptown Rotunda & Malls", "Fuente Osmeña", "📍"))
        list.add(GroupedLocationItem("hub_dampas", "Dampas", "Barangay", "Tagbilaran City, Bohol • Near HNU & ICM", "Dampas", "📍"))
        list.add(GroupedLocationItem("hub_cogon", "Cogon", "Barangay", "Tagbilaran City, Bohol • J.A. Clarin St", "Cogon", "📍"))
        list.add(GroupedLocationItem("hub_tawala", "Tawala", "Barangay", "Panglao, Bohol • Alona Beach Resort", "Tawala", "📍"))
        list.add(GroupedLocationItem("hub_doljo", "Doljo", "Barangay", "Panglao, Bohol • Beach Villa & Resorts", "Doljo", "📍"))
        list.add(GroupedLocationItem("hub_villamonte", "Villamonte", "Barangay", "Bacolod City • USLS University Belt", "Villamonte", "📍"))
        list.add(GroupedLocationItem("hub_mandalagan", "Mandalagan", "Barangay", "Bacolod City • Lacson St Strip", "Mandalagan", "📍"))
        list.add(GroupedLocationItem("hub_bata", "Bata", "Barangay", "Bacolod City • North Bus Terminal", "Bata", "📍"))
        list.add(GroupedLocationItem("hub_songculan", "Songculan", "Barangay", "Dauis, Bohol • Coastal Highway", "Songculan", "📍"))
        list.add(GroupedLocationItem("hub_tipolo", "Tipolo", "Barangay", "Mandaue City • Commercial Area", "Tipolo", "📍"))
        list.add(GroupedLocationItem("hub_subangdaku", "Subangdaku", "Barangay", "Mandaue City • Inn & Border Corridor", "Subangdaku", "📍"))

        list
    }

    val searchInput = filterState.keyword.trim()
    val regionSuggestions = remember(searchInput, allLocationSuggestions) {
        allLocationSuggestions.filter {
            it.category == "Region" &&
            (searchInput.isEmpty() || it.name.contains(searchInput, ignoreCase = true) || it.subtitle.contains(searchInput, ignoreCase = true))
        }
    }
    val citySuggestions = remember(searchInput, allLocationSuggestions) {
        allLocationSuggestions.filter {
            it.category == "Province / City" &&
            (searchInput.isEmpty() || it.name.contains(searchInput, ignoreCase = true) || it.subtitle.contains(searchInput, ignoreCase = true))
        }
    }
    val barangaySuggestions = remember(searchInput, allLocationSuggestions) {
        allLocationSuggestions.filter {
            it.category == "Barangay" &&
            (searchInput.isEmpty() || it.name.contains(searchInput, ignoreCase = true) || it.subtitle.contains(searchInput, ignoreCase = true))
        }
    }

    var showFilterSheet by remember { mutableStateOf(false) }
    var showComparisonMatrix by remember { mutableStateOf(false) }
    var focusedPropertyId by remember { mutableStateOf<String?>(null) }

    val savedIds = remember(savedProperties) {
        savedProperties.map { it.propertyId }.toSet()
    }

    val propertyTypeGridOptions = remember {
        listOf(
            SelectableBarOption("Boarding House", "Boarding House", "Beds & dorm spaces", "🛏️"),
            SelectableBarOption("Apartment", "Apartment", "Flats & multi-door", "🚪"),
            SelectableBarOption("Condominium", "Condominium", "High-rise & studios", "🏢"),
            SelectableBarOption("Townhouse", "Townhouse", "Houses & compound units", "🏡")
        )
    }

    val genderGridOptions = remember {
        listOf(
            SelectableBarOption("ALL", "ALL Genders", "Any gender welcome", "👥"),
            SelectableBarOption("COED", "Mixed / Co-Ed", "Shared co-living", "👫"),
            SelectableBarOption("MALE", "Male Only", "Men / Boys only", "👨"),
            SelectableBarOption("FEMALE", "Female Only", "Ladies / Women only", "👩")
        )
    }

    val leaseGridOptions = remember {
        listOf(
            SelectableBarOption("Flexible (1-3 mos)", "Flexible (1-3m)", "Short-stay flexibility", "⚡"),
            SelectableBarOption("Mid-term (4-6 mos)", "Mid-term (4-6m)", "Semester or 6 months", "📅"),
            SelectableBarOption("Annual (12+ mos)", "Annual (12m+)", "Long-term year lease", "🏠"),
            SelectableBarOption("All", "All Terms", "Any lease duration", "⏱️")
        )
    }

    val bedGridOptions = remember {
        listOf(
            SelectableBarOption("0", "Any Vacancies", "All open boarding houses", "🛏️"),
            SelectableBarOption("1", "1+ Bed Vacant", "At least 1 open bed", "1️⃣"),
            SelectableBarOption("2", "2+ Beds Vacant", "Pairs / 2 available", "2️⃣"),
            SelectableBarOption("4", "4+ Beds Vacant", "Groups / 4+ available", "4️⃣")
        )
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
            // Fixed Top Bar (Always visible and permanently anchored above map and list)
            if (showSearchHeader) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(50f),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
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
                                    onValueChange = {
                                        viewModel.updateKeyword(it)
                                        showLocationSuggestions = true
                                    },
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
                            IconButton(
                                onClick = { showLocationSuggestions = !showLocationSuggestions },
                                modifier = Modifier.size(26.dp).testTag("toggle_location_suggestions_btn")
                            ) {
                                Icon(
                                    if (showLocationSuggestions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (showLocationSuggestions) "Hide suggestions" else "Show location suggestions",
                                    modifier = Modifier.size(20.dp),
                                    tint = if (showLocationSuggestions) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
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

                    // Segmented Control (Map vs List View)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (viewMode == "MAP") MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .clickable { viewModel.setViewMode("MAP") }
                                    .testTag("toggle_map_view_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Map,
                                        contentDescription = "Map View",
                                        tint = if (viewMode == "MAP") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Map",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == "MAP") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (viewMode == "LIST") MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .clickable { viewModel.setViewMode("LIST") }
                                    .testTag("toggle_list_view_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ViewList,
                                        contentDescription = "List View",
                                        tint = if (viewMode == "LIST") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "List",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (viewMode == "LIST") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Search Bar Auto-Suggest with Grouped Results & Multi-Location Selection
                if (showLocationSuggestions) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("location_autosuggest_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Location Auto-Suggestions",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (filterState.selectedLocationZones.isNotEmpty()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            modifier = Modifier.padding(end = 6.dp)
                                        ) {
                                            Text(
                                                text = "${filterState.selectedLocationZones.size} selected",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    IconButton(
                                        onClick = { showLocationSuggestions = false },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Close suggestions",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Select multiple nearby zones at once (e.g., [✓] Cebu IT Park + [✓] Mabolo + [✓] Lahug)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )

                            // Scrollable Categorized List
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 280.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 1. REGION CATEGORY
                                if (regionSuggestions.isNotEmpty()) {
                                    LocationCategoryGroup(
                                        categoryTitle = "1. Region",
                                        categoryIcon = "🗺️",
                                        items = regionSuggestions,
                                        selectedZones = filterState.selectedLocationZones,
                                        onToggle = { viewModel.toggleLocationZone(it) }
                                    )
                                }

                                // 2. PROVINCE / CITY CATEGORY
                                if (citySuggestions.isNotEmpty()) {
                                    LocationCategoryGroup(
                                        categoryTitle = "2. Province / City",
                                        categoryIcon = "🏙️",
                                        items = citySuggestions,
                                        selectedZones = filterState.selectedLocationZones,
                                        onToggle = { viewModel.toggleLocationZone(it) }
                                    )
                                }

                                // 3. BARANGAY CATEGORY
                                if (barangaySuggestions.isNotEmpty()) {
                                    LocationCategoryGroup(
                                        categoryTitle = "3. Barangay",
                                        categoryIcon = "📍",
                                        items = barangaySuggestions,
                                        selectedZones = filterState.selectedLocationZones,
                                        onToggle = { viewModel.toggleLocationZone(it) }
                                    )
                                }

                                if (regionSuggestions.isEmpty() && citySuggestions.isEmpty() && barangaySuggestions.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No locations matching '${filterState.keyword}'.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Footer Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (filterState.selectedLocationZones.isNotEmpty()) {
                                    Text(
                                        text = "Clear Selected (${filterState.selectedLocationZones.size})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .clickable { viewModel.clearLocationZones() }
                                            .padding(4.dp)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Button(
                                    onClick = { showLocationSuggestions = false },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(
                                        text = if (filterState.selectedLocationZones.isNotEmpty())
                                            "Apply (${properties.size} properties)"
                                        else "Done",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Active Multi-Selected Location Zones Row
                if (filterState.selectedLocationZones.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${filterState.selectedLocationZones.size} Zones Active:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        filterState.selectedLocationZones.forEach { zone ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier.clickable { viewModel.removeLocationZone(zone) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📍 $zone",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove $zone",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Clear All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .clickable { viewModel.clearLocationZones() }
                                .padding(horizontal = 6.dp)
                        )
                    }
                }

                // ✨ Premium Locations Quick-Selection Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "✨",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Premium Locations",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Curated high-demand hubs",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        premiumLocations.forEach { loc ->
                            val isSelected = filterState.selectedLocationZones.contains(loc.name)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier
                                    .clickable { viewModel.toggleLocationZone(loc.name) }
                                    .testTag("premium_location_${loc.name.lowercase().replace(" ", "_")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = loc.icon, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = loc.name,
                                                fontSize = 12.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = loc.subtitle,
                                            fontSize = 10.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Active Filter Chips Row (Only takes vertical space when filters are active)
                val hasAnyFilterPill = filterState.selectedLocationZones.isNotEmpty() ||
                        filterState.provinceId.isNotEmpty() ||
                        filterState.cityMunicipalityId.isNotEmpty() ||
                        filterState.barangayId.isNotEmpty() ||
                        filterState.propertyType != "All" ||
                        filterState.utilityRuleFilter != "All" ||
                        filterState.listedBy != "All" ||
                        filterState.authorizedPosterOnly ||
                        filterState.verifiedOnly ||
                        (filterState.genderRule.uppercase() != "ALL") ||
                        filterState.minAvailableBeds > 0 ||
                        filterState.sortBy != "NEWEST" ||
                        filterState.minRent > 0 ||
                        filterState.maxRent < 100000 ||
                        filterState.leaseDurationFilter != "All" ||
                        filterState.moveInDateFilter != "All"

                if (hasAnyFilterPill) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                    // Multi-Selected Location Zones filter badges
                    filterState.selectedLocationZones.forEach { zone ->
                        FilterBadge(
                            label = "📍 $zone",
                            onRemove = { viewModel.removeLocationZone(zone) }
                        )
                    }

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

                    // Property type filter chips (multi-select)
                    if (filterState.selectedPropertyTypes.isNotEmpty()) {
                        filterState.selectedPropertyTypes.forEach { pType ->
                            FilterBadge(
                                label = pType,
                                onRemove = { viewModel.togglePropertyType(pType) }
                            )
                        }
                    } else if (filterState.propertyType != "All") {
                        FilterBadge(
                            label = filterState.propertyType,
                            onRemove = { viewModel.clearPropertyTypes() }
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

                    // Gender policy chips (multi-select)
                    if (filterState.selectedGenderRules.isNotEmpty() && "ALL" !in filterState.selectedGenderRules) {
                        filterState.selectedGenderRules.forEach { gRule ->
                            val gLabel = when (gRule.uppercase()) {
                                "FEMALE", "FEMALE_ONLY" -> "Female Only"
                                "MALE", "MALE_ONLY" -> "Male Only"
                                "COED", "CO-ED" -> "Co-Ed / Mixed"
                                else -> gRule
                            }
                            FilterBadge(
                                label = "Gender: $gLabel",
                                onRemove = { viewModel.toggleGenderRule(gRule) }
                            )
                        }
                    } else if (filterState.genderRule.uppercase() != "ALL") {
                        val gLabel = when (filterState.genderRule.uppercase()) {
                            "FEMALE", "FEMALE_ONLY" -> "Female Only"
                            "MALE", "MALE_ONLY" -> "Male Only"
                            "COED", "CO-ED" -> "Co-Ed / Mixed"
                            else -> filterState.genderRule
                        }
                        FilterBadge(
                            label = "Gender: $gLabel",
                            onRemove = { viewModel.clearGenderRules() }
                        )
                    }

                    // Min beds chip
                    if (filterState.minAvailableBeds > 0) {
                        FilterBadge(
                            label = "${filterState.minAvailableBeds}+ Beds",
                            onRemove = { viewModel.updateMinAvailableBeds(0) }
                        )
                    }

                    // Flexible Lease Duration chips (multi-select)
                    if (filterState.selectedLeaseDurations.isNotEmpty() && "All" !in filterState.selectedLeaseDurations) {
                        filterState.selectedLeaseDurations.forEach { lDur ->
                            FilterBadge(
                                label = "Lease: $lDur",
                                onRemove = { viewModel.toggleLeaseDuration(lDur) }
                            )
                        }
                    } else if (filterState.leaseDurationFilter != "All") {
                        FilterBadge(
                            label = "Lease: ${filterState.leaseDurationFilter}",
                            onRemove = { viewModel.clearLeaseDurations() }
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
                            "PRICE_ASC", "RENT_ASC", "lowest_price" -> "Rent: Low to High"
                            "PRICE_DESC", "RENT_DESC" -> "Rent: High to Low"
                            "OLDEST", "DATE_ASC" -> "Date: Oldest First"
                            "highest_available" -> "Most Available Beds"
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
                        filterState.selectedPropertyTypes.isNotEmpty() ||
                        filterState.propertyType != "All" ||
                        filterState.listedBy != "All" ||
                        filterState.authorizedPosterOnly ||
                        filterState.verifiedOnly ||
                        (filterState.selectedGenderRules.isNotEmpty() && "ALL" !in filterState.selectedGenderRules) ||
                        (filterState.genderRule.uppercase() != "ALL") ||
                        filterState.minAvailableBeds > 0 ||
                        filterState.sortBy != "NEWEST" ||
                        filterState.minRent > 0 ||
                        filterState.maxRent < 100000 ||
                        (filterState.selectedLeaseDurations.isNotEmpty() && "All" !in filterState.selectedLeaseDurations) ||
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
    }

        // Subheader showing total results & sort (shown in LIST view on mobile)
        if (viewMode == "LIST" && !isWideScreen) {
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

                SortByDropdownMenu(
                    currentSort = filterState.sortBy,
                    onSelectSort = { viewModel.setSortBy(it) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth().clipToBounds()) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
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
                                SortByDropdownMenu(
                                    currentSort = filterState.sortBy,
                                    onSelectSort = { viewModel.setSortBy(it) }
                                )
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
                            .clipToBounds()
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
                    Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
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
                    Box(modifier = Modifier.fillMaxSize()) {
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

    // Comprehensive Interactive Filter Modal Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Filter Rental Properties",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Interactive dropdowns • ${properties.size} properties match",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close filter menu")
                    }
                }

                // 1. TOP MENU: Monthly Rent Budget (₱)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Monthly Rent Budget (₱)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "₱${filterState.minRent.toInt()} – ₱${filterState.maxRent.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        RangeSlider(
                            value = filterState.minRent.toFloat()..filterState.maxRent.toFloat(),
                            onValueChange = { range ->
                                viewModel.updatePriceRange(range.start.toDouble(), range.endInclusive.toDouble())
                            },
                            valueRange = 0f..100000f,
                            steps = 19
                        )

                        // Quick budget preset dropdown
                        val currentBudgetPreset = when {
                            filterState.minRent == 0.0 && filterState.maxRent >= 100000.0 -> "all"
                            filterState.minRent == 1000.0 && filterState.maxRent == 5000.0 -> "budget"
                            filterState.minRent == 5000.0 && filterState.maxRent == 15000.0 -> "mid"
                            filterState.minRent == 15000.0 && filterState.maxRent == 35000.0 -> "premium"
                            filterState.minRent == 35000.0 && filterState.maxRent == 100000.0 -> "luxury"
                            else -> "custom"
                        }

                        val budgetPresets = listOf(
                            "all" to "Any Budget (₱0 – ₱100,000+)",
                            "budget" to "Budget Friendly (₱1,000 – ₱5,000)",
                            "mid" to "Mid-Range (₱5,000 – ₱15,000)",
                            "premium" to "Premium Units (₱15,000 – ₱35,000)",
                            "luxury" to "Luxury & Townhouses (₱35,000 – ₱100,000)",
                            "custom" to "Custom (₱${filterState.minRent.toInt()} – ₱${filterState.maxRent.toInt()})"
                        )

                        InteractiveFilterDropdown(
                            title = "Quick Budget Range Preset",
                            selectedValue = currentBudgetPreset,
                            options = budgetPresets,
                            onSelect = { presetKey ->
                                when (presetKey) {
                                    "all" -> viewModel.updatePriceRange(0.0, 100000.0)
                                    "budget" -> viewModel.updatePriceRange(1000.0, 5000.0)
                                    "mid" -> viewModel.updatePriceRange(5000.0, 15000.0)
                                    "premium" -> viewModel.updatePriceRange(15000.0, 35000.0)
                                    "luxury" -> viewModel.updatePriceRange(35000.0, 100000.0)
                                }
                            }
                        )
                    }
                }

                // 2. Property Types 2X2 Selectable Bar (Multi-select)
                SelectableGrid2x2(
                    title = "Property Types",
                    subtitle = "Select one or more rental categories as per your requirements",
                    options = propertyTypeGridOptions,
                    selectedKeys = filterState.selectedPropertyTypes,
                    onToggle = { viewModel.togglePropertyType(it) },
                    isDark = isDark
                )

                // 3. Gender Policy 2X2 Selectable Bar (Multi-select)
                SelectableGrid2x2(
                    title = "Gender Policy",
                    subtitle = "Select allowed gender policies (ALL, MALE, FEMALE, Mixed)",
                    options = genderGridOptions,
                    selectedKeys = filterState.selectedGenderRules,
                    onToggle = { viewModel.toggleGenderRule(it) },
                    isDark = isDark
                )

                // 4. Lease Duration 2X2 Selectable Bar (Multi-select)
                SelectableGrid2x2(
                    title = "Lease Duration",
                    subtitle = "Select flexible lease terms (Flexible, Mid-term, Annual, All)",
                    options = leaseGridOptions,
                    selectedKeys = filterState.selectedLeaseDurations,
                    onToggle = { viewModel.toggleLeaseDuration(it) },
                    isDark = isDark
                )

                // 5. Available Bed Vacancies 2X2 Selectable Bar (ONLY when Boarding House is selected)
                val isBoardingInSheet = filterState.selectedPropertyTypes.any { it.contains("Board", ignoreCase = true) || it.contains("Bed", ignoreCase = true) } ||
                        filterState.propertyType.contains("Board", ignoreCase = true) ||
                        filterState.propertyType.contains("Bed", ignoreCase = true)

                if (isBoardingInSheet) {
                    SelectableGrid2x2(
                        title = "Available Bed Vacancies",
                        subtitle = "Minimum open beds currently vacant in boarding house",
                        options = bedGridOptions,
                        selectedKeys = if (filterState.minAvailableBeds > 0) setOf(filterState.minAvailableBeds.toString()) else setOf("0"),
                        onToggle = { key ->
                            val beds = key.toIntOrNull() ?: 0
                            viewModel.updateMinAvailableBeds(if (filterState.minAvailableBeds == beds) 0 else beds)
                        },
                        isDark = isDark
                    )
                }

                // 6. Move-In Date Dropdown
                val moveInOptions = listOf(
                    "All" to "Any Move-in Date",
                    "Immediate / Today" to "⚡ Immediate / Ready Today",
                    "Within 7 Days" to "📦 Within 7 Days",
                    "Within 15 Days" to "🗓️ Within 15 Days",
                    "Within 30 Days" to "🗓️ Within 30 Days"
                )

                InteractiveFilterDropdown(
                    title = "Move-In Date & Availability",
                    subtitle = "Target occupancy availability",
                    selectedValue = filterState.moveInDateFilter,
                    options = moveInOptions,
                    onSelect = { viewModel.updateMoveInDate(it) }
                )

                // 7. Listed By Dropdown
                val listedByOptions = listOf(
                    "All" to "All (Any Host / Poster)",
                    "Host" to "Host (Direct Owner)",
                    "Property Owner" to "Property Owner",
                    "Caretaker" to "Caretaker (Authorized)",
                    "Property Manager" to "Property Manager",
                    "Licensed Broker" to "Licensed Broker",
                    "Family Member" to "Family Member",
                    "Authorized Representative" to "Authorized Representative"
                )

                InteractiveFilterDropdown(
                    title = "Listed By (Poster Authority)",
                    subtitle = "Filter by poster relation to property",
                    selectedValue = filterState.listedBy,
                    options = listedByOptions,
                    onSelect = { viewModel.updateListedBy(it) }
                )

                // 8. Utilities & House Rules Dropdown
                val utilityRuleOptions = listOf(
                    "All" to "All Utilities & Rules",
                    "Sub-metered Electricity" to "Sub-metered Electricity",
                    "Flat Rate Water" to "Flat Rate Water",
                    "Shared WiFi" to "Shared WiFi",
                    "24/7 Gate Key (No Curfew)" to "24/7 Gate Key (No Curfew)",
                    "Curfew Hours" to "Curfew Hours",
                    "Caretaker On-Site" to "Caretaker On-Site",
                    "24/7 Roaming Guards" to "24/7 Roaming Guards",
                    "Strictly Induction (No LPG)" to "Strictly Induction (No LPG)",
                    "LPG Allowed" to "LPG Allowed",
                    "Daytime Visitors Allowed" to "Daytime Visitors Allowed",
                    "Strictly No Visitors" to "Strictly No Visitors"
                )

                InteractiveFilterDropdown(
                    title = "Utilities & House Rules Standards",
                    subtitle = "Standard Philippine boarding house & rental policies",
                    selectedValue = filterState.utilityRuleFilter,
                    options = utilityRuleOptions,
                    onSelect = { viewModel.updateUtilityRuleFilter(it) }
                )

                // 9. Top-Down List of Key Requirements & Features
                Text(
                    text = "Key Listing Requirements",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Authorized posters only
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Authorized Posters Only", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Direct owners and verified legal representatives only",
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

                        // Dedicated parking required
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dedicated Parking Required", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Requires reserved car or motorcycle parking space",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = filterState.parkingRequired,
                                onCheckedChange = { viewModel.toggleParkingRequired() }
                            )
                        }

                        // Pet friendly only
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pet-Friendly Only", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Accepts dogs, cats, or small pets per house rules",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = filterState.petFriendly,
                                onCheckedChange = { viewModel.togglePetFriendly() }
                            )
                        }

                        // Verified listings only (Level 3+)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Verified Listings Only (Level 3+)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Barangay clearance, proof of authority, or title validated",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = filterState.verifiedOnly,
                                onCheckedChange = { viewModel.toggleVerifiedOnly() }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Apply & Reset Sticky Action Buttons
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
                        Text("Reset All", maxLines = 1, softWrap = false)
                    }

                    Button(
                        onClick = { showFilterSheet = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply Filters (${properties.size})", maxLines = 1, softWrap = false)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
}

@Composable
private fun InteractiveFilterDropdown(
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>, // (key, label)
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = options.find { it.first.equals(selectedValue, ignoreCase = true) }?.second ?: selectedValue

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    1.dp,
                    if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand $title menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.88f)
            ) {
                options.forEach { (key, label) ->
                    val isSelected = key.equals(selectedValue, ignoreCase = true)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        }
                    )
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

@Composable
private fun SortByDropdownMenu(
    currentSort: String,
    onSelectSort: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayLabel = when (currentSort) {
        "PRICE_ASC", "RENT_ASC", "lowest_price" -> "Rent (Low to High)"
        "PRICE_DESC", "RENT_DESC" -> "Rent (High to Low)"
        "OLDEST", "DATE_ASC" -> "Date (Oldest First)"
        else -> "Date (Newest First)"
    }

    Box(modifier = modifier) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sort By: ",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = displayLabel,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Open Sort Options",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val sortOptions = listOf(
                "PRICE_ASC" to "Rent (Low to High)",
                "PRICE_DESC" to "Rent (High to Low)",
                "NEWEST" to "Date (Newest First)",
                "OLDEST" to "Date (Oldest First)"
            )
            sortOptions.forEach { (key, label) ->
                val isSelected = when (key) {
                    "PRICE_ASC" -> currentSort in listOf("PRICE_ASC", "RENT_ASC", "lowest_price")
                    "PRICE_DESC" -> currentSort in listOf("PRICE_DESC", "RENT_DESC")
                    "OLDEST" -> currentSort in listOf("OLDEST", "DATE_ASC")
                    else -> currentSort !in listOf("PRICE_ASC", "RENT_ASC", "lowest_price", "PRICE_DESC", "RENT_DESC", "OLDEST", "DATE_ASC")
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    onClick = {
                        onSelectSort(key)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterPillBar(
    title: String,
    selectedValue: String,
    options: List<Pair<String, String>>, // (key, label)
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { (key, label) ->
                val isSelected = key.equals(selectedValue, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.clickable { onSelect(key) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

data class SelectableBarOption(
    val key: String,
    val label: String,
    val subtitle: String? = null,
    val iconEmoji: String? = null
)

@Composable
fun SelectableGrid2x2(
    title: String,
    subtitle: String? = null,
    options: List<SelectableBarOption>,
    selectedKeys: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (selectedKeys.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${selectedKeys.size} selected",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 2x2 Grid Layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val rows = options.chunked(2)
            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (opt in row) {
                        val isSelected = selectedKeys.contains(opt.key)
                        Surface(
                            onClick = { onToggle(opt.key) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                if (isDark) Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            },
                            border = BorderStroke(
                                width = if (isSelected) 1.8.dp else 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp)
                                .testTag("selectable_bar_${opt.key.lowercase().replace(" ", "_").replace("/", "_")}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    if (opt.iconEmoji != null) {
                                        Text(
                                            text = opt.iconEmoji,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = opt.label,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            },
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (opt.subtitle != null) {
                                            Text(
                                                text = opt.subtitle,
                                                fontSize = 10.5.sp,
                                                color = if (isSelected) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCategoryGroup(
    categoryTitle: String,
    categoryIcon: String,
    items: List<GroupedLocationItem>,
    selectedZones: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$categoryIcon $categoryTitle",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                val countInGroup = items.count { it.filterToken in selectedZones }
                if (countInGroup > 0) {
                    Text(
                        text = "$countInGroup selected",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items.forEach { item ->
            val isChecked = selectedZones.contains(item.filterToken)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(item.filterToken) }
                    .testTag("location_item_${item.filterToken.lowercase().replace(" ", "_").replace("/", "_")}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { onToggle(item.filterToken) },
                        modifier = Modifier.size(32.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 13.sp,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium,
                            color = if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
