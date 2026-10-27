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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.BalayInnLogoHeader
import com.example.ui.components.ComparisonMatrixSheet
import com.example.ui.components.GuestAuthPromptDialog
import com.example.ui.components.PropertyCard
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
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val allUnfilteredProperties by viewModel.allUnfilteredProperties.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val recentlyViewed by viewModel.recentlyViewedProperties.collectAsState()
    val recommended by viewModel.recommendedProperties.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    val savedPropIds = savedProperties.map { it.propertyId }.toSet()
    val savedItems = properties.filter { it.id in savedPropIds }
    val comparedProperties by viewModel.comparedProperties.collectAsState()
    val comparedPropertyIds = remember(comparedProperties) { comparedProperties.map { it.id }.toSet() }
    var showComparisonMatrix by remember { mutableStateOf(false) }
    var guestActionPromptReason by remember { mutableStateOf<String?>(null) }

    val categoryConfigs = remember {
        listOf(
            HomeCategoryConfig(
                id = "Boarding House",
                title = "Boarding House",
                subtitle = "Budget rooms, bed spaces & student dorms",
                emoji = "🛏️",
                matcher = { prop ->
                    val t = prop.propertyType.lowercase()
                    t.contains("board") || t.contains("bed") || t.contains("dorm") || t == "room"
                }
            ),
            HomeCategoryConfig(
                id = "Apartment",
                title = "Apartment",
                subtitle = "Multi-room flats & independent units",
                emoji = "🏢",
                matcher = { prop ->
                    val t = prop.propertyType.lowercase()
                    t.contains("apart") || t.contains("flat")
                }
            ),
            HomeCategoryConfig(
                id = "Condominium",
                title = "Condominium",
                subtitle = "Mid & high-rise units with pool & amenities",
                emoji = "🏙️",
                matcher = { prop ->
                    val t = prop.propertyType.lowercase()
                    t.contains("condo")
                }
            ),
            HomeCategoryConfig(
                id = "Studio",
                title = "Studio",
                subtitle = "Compact open-plan spaces for solo living",
                emoji = "👤",
                matcher = { prop ->
                    val t = prop.propertyType.lowercase()
                    t.contains("studio")
                }
            ),
            HomeCategoryConfig(
                id = "Townhouse",
                title = "Townhouse",
                subtitle = "Multi-level family homes with carports",
                emoji = "🏡",
                matcher = { prop ->
                    val t = prop.propertyType.lowercase()
                    t.contains("town") || (t.contains("house") && !t.contains("board"))
                }
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Full-width App name bar matching other screens
        BalayInnLogoHeader(
            isDark = isDark,
            onToggleDarkMode = { viewModel.toggleDarkMode() }
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("tenant_home_screen"),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
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
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (currentUser.isGuest) Color(0xFFFEF3C7) else SoftGreenBg,
                                border = BorderStroke(0.8.dp, if (currentUser.isGuest) Color(0xFFF59E0B) else SoftGreenBorder)
                            ) {
                                Text(
                                    text = if (currentUser.isGuest) "Guest Explorer Mode" else "Level ${currentUser.verificationLevel} Verified Renter",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (currentUser.isGuest) Color(0xFF92400E) else SoftGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            if (currentUser.isGuest) {
                                Button(
                                    onClick = onNavigateToLogin,
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp).testTag("guest_hero_signin_btn")
                                ) {
                                    Text("Sign In", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                IconButton(
                                    onClick = onNavigateToVerificationCenter,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(
                                            if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Verification Center",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (currentUser.isGuest) "Mabuhay, Explorer!" else "Mabuhay!",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        val tenantNameFontSize = when {
                            currentUser.fullName.length > 30 -> 14.5.sp
                            currentUser.fullName.length > 24 -> 16.5.sp
                            currentUser.fullName.length > 18 -> 18.5.sp
                            else -> 22.sp
                        }

                        Text(
                            text = if (currentUser.isGuest) "Find Your Next Home" else currentUser.fullName,
                            fontSize = tenantNameFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (currentUser.isGuest) {
                                "Explore verified rental listings, dorms & apartments across the Philippines. Sign in anytime to bookmark favorites or message hosts."
                            } else {
                                "Rent verified, budget-friendly rooms & dorms across Cebu, Bohol, and Bacolod with zero hidden fees."
                            },
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

            // 2. Recently Viewed Properties
            if (recentlyViewed.isNotEmpty()) {
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
                                locationDisplay = viewModel.getBarangayProvinceDisplay(prop),
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

            // 3. Property Categories List with Horizontal Sliding Carousels
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ViewCarousel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Property Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    TextButton(onClick = onNavigateToExplore) {
                        Text("Explore All (${allUnfilteredProperties.size})", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }
            }

            categoryConfigs.forEach { config ->
                val categoryProperties = allUnfilteredProperties.filter { config.matcher(it) }
                if (categoryProperties.isNotEmpty()) {
                    item(key = "cat_header_${config.id}") {
                        Spacer(modifier = Modifier.height(2.dp))
                        CategoryCarouselHeader(
                            title = config.title,
                            subtitle = config.subtitle,
                            emoji = config.emoji,
                            count = categoryProperties.size,
                            isDark = isDark,
                            onSeeAll = {
                                viewModel.updatePropertyType(config.id)
                                onNavigateToExplore()
                            }
                        )
                    }

                    item(key = "cat_carousel_${config.id}") {
                        CategoryPropertySnapRow(
                            categoryProperties = categoryProperties,
                            propertyImagesMap = propertyImagesMap,
                            savedPropIds = savedPropIds,
                            comparedPropertyIds = comparedPropertyIds,
                            viewModel = viewModel,
                            onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                            onSaveToggle = { prop ->
                                if (currentUser.isGuest) {
                                    guestActionPromptReason = "Sign in to save properties."
                                } else {
                                    viewModel.toggleSave(prop.id)
                                }
                            },
                            isDark = isDark,
                            testTag = "category_carousel_${config.id.lowercase().replace(" ", "_")}"
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            // Fallback for any other properties outside core 5 categories
            val otherProperties = allUnfilteredProperties.filter { prop ->
                categoryConfigs.none { it.matcher(prop) }
            }
            if (otherProperties.isNotEmpty()) {
                item(key = "cat_header_other") {
                    Spacer(modifier = Modifier.height(2.dp))
                    CategoryCarouselHeader(
                        title = "Other Rental Spaces",
                        subtitle = "Additional verified rooms & spaces",
                        emoji = "✨",
                        count = otherProperties.size,
                        isDark = isDark,
                        onSeeAll = onNavigateToExplore
                    )
                }

                item(key = "cat_carousel_other") {
                    CategoryPropertySnapRow(
                        categoryProperties = otherProperties,
                        propertyImagesMap = propertyImagesMap,
                        savedPropIds = savedPropIds,
                        comparedPropertyIds = comparedPropertyIds,
                        viewModel = viewModel,
                        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                        onSaveToggle = { prop ->
                            if (currentUser.isGuest) {
                                guestActionPromptReason = "Sign in to save properties."
                            } else {
                                viewModel.toggleSave(prop.id)
                            }
                        },
                        isDark = isDark,
                        testTag = "category_carousel_other"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
                                fontWeight = FontWeight.Black,
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
                        locationDisplay = viewModel.getBarangayProvinceDisplay(prop),
                        imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                        isSaved = true,
                        onSaveToggle = {
                            if (currentUser.isGuest) {
                                guestActionPromptReason = "Sign in to save properties."
                            } else {
                                viewModel.toggleSave(prop.id)
                            }
                        },
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
        androidx.compose.animation.AnimatedVisibility(
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

    guestActionPromptReason?.let { reason ->
        GuestAuthPromptDialog(
            reason = reason,
            onDismiss = { guestActionPromptReason = null },
            onSignInWithEmail = {
                guestActionPromptReason = null
                onNavigateToLogin()
            },
            onGoogleSso = {
                viewModel.registerOrLoginWithGoogleSso(onSuccess = {})
            },
            onFacebookSso = {
                viewModel.registerOrLoginWithFacebookSso(onSuccess = {})
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
                Triple("usr_tenant_1", "Joshua Alcantara (Tenant)", "TENANT"),
                Triple("usr_owner_1", "Donato Yap (Owner)", "PROPERTY_OWNER"),
                Triple("usr_broker_marco", "Marco Villanueva (Broker)", "REAL_ESTATE_BROKER"),
                Triple("usr_caretaker_mark", "Kuya Mark (Caretaker)", "CARETAKER"),
                Triple("usr_admin_1", "Balay-Inn Compliance Admin (Admin)", "ADMIN")
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
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        maxLines = 1,
                        softWrap = false
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

data class HomeCategoryConfig(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val matcher: (PropertyEntity) -> Boolean
)

@Composable
fun CategoryCarouselHeader(
    title: String,
    subtitle: String,
    emoji: String,
    count: Int,
    isDark: Boolean,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isDark) Color(0xFF1E293B) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = emoji,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color.White else Color(0xFF0F172A),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        TextButton(
            onClick = onSeeAll,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("See All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryPropertySnapRow(
    categoryProperties: List<PropertyEntity>,
    propertyImagesMap: Map<String, List<String>>,
    savedPropIds: Set<String>,
    comparedPropertyIds: Set<String>,
    viewModel: MarketplaceViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
    onSaveToggle: (PropertyEntity) -> Unit,
    isDark: Boolean,
    testTag: String
) {
    val listState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val configuration = LocalConfiguration.current
    // Roughly 78% of the screen width so the edge of the second card is naturally visible
    val cardWidth = (configuration.screenWidthDp.dp * 0.78f).coerceIn(275.dp, 360.dp)

    LazyRow(
        state = listState,
        flingBehavior = snapFlingBehavior,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
    ) {
        items(categoryProperties, key = { "snap_${testTag}_${it.id}" }) { prop ->
            HorizontalCategoryPropertyCard(
                property = prop,
                locationDisplay = viewModel.getBarangayProvinceDisplay(prop),
                imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                isSaved = prop.id in savedPropIds,
                onSaveToggle = { onSaveToggle(prop) },
                isCompared = prop.id in comparedPropertyIds,
                onCompareToggle = { viewModel.toggleCompare(prop) },
                onClick = {
                    viewModel.recordPropertyView(prop.id)
                    onNavigateToPropertyDetail(prop.id)
                },
                isDark = isDark,
                cardWidth = cardWidth
            )
        }
    }
}

@Composable
fun HorizontalCategoryPropertyCard(
    property: PropertyEntity,
    locationDisplay: String,
    imageUrls: List<String>,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    isCompared: Boolean = false,
    onCompareToggle: () -> Unit = {},
    onClick: () -> Unit,
    isDark: Boolean = false,
    cardWidth: Dp? = null,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    // Roughly 78% of the screen width so the edge of the second card is naturally visible
    val effectiveCardWidth = cardWidth ?: (configuration.screenWidthDp.dp * 0.78f).coerceIn(275.dp, 360.dp)

    PropertyCard(
        property = property,
        locationDisplay = locationDisplay,
        imageUrls = imageUrls,
        isSaved = isSaved,
        onSaveToggle = onSaveToggle,
        isCompared = isCompared,
        onCompareToggle = onCompareToggle,
        onClick = onClick,
        modifier = modifier.width(effectiveCardWidth)
    )
}



