package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.BalayInnLogoHeader
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.ComparisonMatrixSheet
import com.example.ui.components.PropertyCard
import com.example.ui.components.SelectedCategoryHeaderCard
import com.example.ui.components.VerifiedIcon
import com.example.ui.components.VerifiedPillBadge
import com.example.ui.components.isPlatformVerified
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel
import androidx.compose.ui.text.style.TextAlign
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TenantHomeScreen(
    viewModel: MarketplaceViewModel,
    currentUser: UserEntity,
    onNavigateToExplore: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val allUnfilteredProperties by viewModel.allUnfilteredProperties.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val recentlyViewed by viewModel.recentlyViewedProperties.collectAsState()
    val recommended by viewModel.recommendedProperties.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    val savedPropIds = savedProperties.map { it.propertyId }.toSet()
    val savedItems = properties.filter { it.id in savedPropIds }
    val comparedProperties by viewModel.comparedProperties.collectAsState()
    val comparedPropertyIds = remember(comparedProperties) { comparedProperties.map { it.id }.toSet() }
    var showComparisonMatrix by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tenant_home_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 0. Balay-Inn Brand Logo Header
            item {
                BalayInnLogoHeader(
                    isDark = isDark,
                    onToggleDarkMode = { viewModel.toggleDarkMode() }
                )
            }

            // 1. Personalized Welcome & Discovery Hero
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tenant_welcome_hero"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mabuhay, ${currentUser.fullName}!",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SoftGreenBg,
                                        border = BorderStroke(0.8.dp, SoftGreenBorder)
                                    ) {
                                        Text(
                                            text = "Level ${currentUser.verificationLevel} Verified Renter",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SoftGreenDark,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = onNavigateToVerificationCenter,
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Verification Center",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Rent verified, budget-friendly rooms & dorms across Cebu, Bohol, and Bacolod with zero hidden fees.",
                            fontSize = 12.5.sp,
                            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                            lineHeight = 18.sp
                        )

                        // Demo Persona Switcher
                        QuickPersonaSwitcherRow(
                            viewModel = viewModel,
                            currentUser = currentUser,
                            isDark = isDark
                        )
                    }
                }
            }

            // 1.5 Category Filter System Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_filter_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.FilterList,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Property Categories",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0F172A)
                                )
                            }

                            if (filterState.propertyType != "All") {
                                TextButton(
                                    onClick = { viewModel.updatePropertyType("All") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Reset Filter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        CategoryFilterRow(
                            selectedCategory = filterState.propertyType,
                            allProperties = allUnfilteredProperties,
                            onSelectCategory = { selectedCat ->
                                viewModel.updatePropertyType(selectedCat)
                            },
                            isDark = isDark
                        )

                        SelectedCategoryHeaderCard(
                            selectedCategory = filterState.propertyType,
                            filteredCount = properties.size,
                            onResetCategory = { viewModel.updatePropertyType("All") },
                            isDark = isDark,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            // 2. Recently Viewed Properties
            if (filterState.propertyType == "All" && recentlyViewed.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recently Viewed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Text(
                            text = "${recentlyViewed.size} items",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentlyViewed, key = { "recent_${it.id}" }) { prop ->
                            RecentPropertyMiniCard(
                                property = prop,
                                locationDisplay = viewModel.getLocationDisplay(prop),
                                onClick = {
                                    viewModel.recordPropertyView(prop.id)
                                    onNavigateToPropertyDetail(prop.id)
                                },
                                isDark = isDark
                            )
                        }
                    }
                }
            }

            // 3. Recommended / Category Filtered Properties
            if (filterState.propertyType == "All") {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BadgeGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recommended for You",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        TextButton(onClick = onNavigateToExplore) {
                            Text("Explore All (${properties.size})", fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }

                items(recommended.take(6), key = { "rec_${it.id}" }) { prop ->
                    PropertyCard(
                        property = prop,
                        locationDisplay = viewModel.getLocationDisplay(prop),
                        imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                        isSaved = prop.id in savedPropIds,
                        onSaveToggle = { viewModel.toggleSave(prop.id) },
                        isCompared = prop.id in comparedPropertyIds,
                        onCompareToggle = { viewModel.toggleCompare(prop) },
                        onClick = {
                            viewModel.recordPropertyView(prop.id)
                            onNavigateToPropertyDetail(prop.id)
                        },
                        onViewOnMap = onNavigateToExplore
                    )
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BadgeGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Filtered Listings (${properties.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        TextButton(onClick = { viewModel.updatePropertyType("All") }) {
                            Text("Show All", fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }

                if (properties.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No properties found in this category",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = if (isDark) Color.White else Color(0xFF0F172A),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing category filters or exploring other areas across Cebu, Bohol, and Bacolod.",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.updatePropertyType("All") },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Reset Filters", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    items(properties, key = { "cat_filtered_${it.id}" }) { prop ->
                        PropertyCard(
                            property = prop,
                            locationDisplay = viewModel.getLocationDisplay(prop),
                            imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                            isSaved = prop.id in savedPropIds,
                            onSaveToggle = { viewModel.toggleSave(prop.id) },
                            isCompared = prop.id in comparedPropertyIds,
                            onCompareToggle = { viewModel.toggleCompare(prop) },
                            onClick = {
                                viewModel.recordPropertyView(prop.id)
                                onNavigateToPropertyDetail(prop.id)
                            },
                            onViewOnMap = onNavigateToExplore
                        )
                    }
                }
            }

            // 4. Saved Properties Preview
            if (savedItems.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your Saved Properties",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Text("${savedItems.size} saved", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
                    }
                }

                items(savedItems.take(2), key = { "saved_home_${it.id}" }) { prop ->
                    PropertyCard(
                        property = prop,
                        locationDisplay = viewModel.getLocationDisplay(prop),
                        imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                        isSaved = true,
                        onSaveToggle = { viewModel.toggleSave(prop.id) },
                        isCompared = prop.id in comparedPropertyIds,
                        onCompareToggle = { viewModel.toggleCompare(prop) },
                        onClick = {
                            viewModel.recordPropertyView(prop.id)
                            onNavigateToPropertyDetail(prop.id)
                        },
                        onViewOnMap = onNavigateToExplore
                    )
                }
            }
        }

        // Floating Comparison Bottom Bar
        AnimatedVisibility(
            visible = comparedProperties.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showComparisonMatrix = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CompareArrows, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Compare ${comparedProperties.size} Properties",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Tap to view side-by-side rent & features",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "Compare Now",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

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
}

@Composable
fun QuickPersonaSwitcherRow(
    viewModel: MarketplaceViewModel,
    currentUser: UserEntity,
    isDark: Boolean
) {
    val allUsers by viewModel.allUsers.collectAsState()

    Column(modifier = Modifier.padding(top = 10.dp)) {
        Text(
            text = "DEMO PERSONA SWITCHER",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val personas = listOf(
                Triple("usr_tenant_1", "Joshua (Tenant)", "TENANT"),
                Triple("usr_owner_1", "Donato (Owner)", "PROPERTY_OWNER"),
                Triple("usr_broker_marco", "Marco (Broker)", "REAL_ESTATE_BROKER"),
                Triple("usr_caretaker_mark", "Mark (Caretaker)", "CARETAKER"),
                Triple("usr_admin_1", "Admin (Compliance)", "ADMIN")
            )

            items(personas) { (id, label, roleStr) ->
                val isSelected = currentUser.id == id || currentUser.role.contains(roleStr, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isDark -> Color(0xFF0F172A)
                        else -> Color(0xFFF1F5F9)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .clickable {
                            val match = allUsers.find { it.id == id } ?: allUsers.find { it.role.contains(roleStr, ignoreCase = true) }
                            if (match != null) {
                                viewModel.switchUser(match)
                            }
                        }
                        .testTag("persona_chip_$id")
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentPropertyMiniCard(
    property: PropertyEntity,
    locationDisplay: String,
    onClick: () -> Unit,
    isDark: Boolean = false
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
                ) {
                    Text(
                        text = property.propertyType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SteelBlueTextDark else SteelBlueText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
                if (property.isPlatformVerified()) {
                    VerifiedPillBadge(isDark = isDark)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (property.isPlatformVerified()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    VerifiedIcon(size = 14.dp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(11.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = locationDisplay,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) SteelBlueTextDark else SteelBlueText,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${currencyFormat.format(property.monthlyRent).replace("PHP", "₱")} / mo",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
