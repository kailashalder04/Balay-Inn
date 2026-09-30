package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.PropertyCard
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.viewmodel.MarketplaceViewModel
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
    val properties by viewModel.properties.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val tenantViewings by viewModel.tenantViewings.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val savedSearches by viewModel.savedSearches.collectAsState()
    val recentlyViewed by viewModel.recentlyViewedProperties.collectAsState()
    val newMatches by viewModel.newMatchesProperties.collectAsState()
    val recommended by viewModel.recommendedProperties.collectAsState()
    val requirements by viewModel.requirements.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    val savedPropIds = savedProperties.map { it.propertyId }.toSet()
    val savedItems = properties.filter { it.id in savedPropIds }

    LazyColumn(
        modifier = modifier
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
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Personalized Rental Discovery",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (currentUser.verificationLevel >= 2) Color(0xFF10B981) else Color(0xFFF59E0B)
                            ) {
                                Text(
                                    text = "L${currentUser.verificationLevel} Verified",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Mabuhay, ${currentUser.fullName}!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Discover verified, scam-protected homes, condos, and beach apartments in Bohol & Cebu.",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onNavigateToExplore,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("tenant_search_btn")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Search Available Rentals",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        // 2. Tenant Rental Preferences Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your Rental Preferences",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Text(
                            text = if (requirements.isNotEmpty()) "${requirements.size} Active Profile" else "Default Filters",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val activeReq = requirements.firstOrNull()
                    val prefLocation = activeReq?.preferredLocationName ?: "Bohol (Panglao/Tagbilaran) & Cebu"
                    val prefBudget = activeReq?.maxBudget?.toInt() ?: 25000

                    Text(
                        text = "Preferred Area: $prefLocation • Target: ≤ ₱$prefBudget/mo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Requirements: Fully Furnished • Direct In-App Chat • Verified Landlords Only",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        // 3. Saved Search Summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToExplore() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Your Saved Search Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = if (savedSearches.isNotEmpty()) "${savedSearches.size} active alert(s) for Bohol coastal & Cebu City" else "Instant notifications for new properties matching your budget",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }
        }

        // 4. Tenant Activity Summary (Inquiries, Viewings, Verification)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Your Activity & Inquiries",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        softWrap = false
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ActivityChip(
                            icon = Icons.Default.Chat,
                            label = "Inquiries",
                            value = "${conversations.size}",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ActivityChip(
                            icon = Icons.Default.CalendarMonth,
                            label = "Viewings",
                            value = "${tenantViewings.size}",
                            color = Color(0xFF2563EB),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ActivityChip(
                            icon = Icons.Default.Favorite,
                            label = "Favorites",
                            value = "${savedProperties.size}",
                            color = Color(0xFFE11D48),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = BadgeGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verification Status: Level ${currentUser.verificationLevel}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BadgeGreen,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        TextButton(
                            onClick = onNavigateToVerificationCenter,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("Boost Trust", fontSize = 11.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }
        }

        // 5. New Matches Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = BadgeAmber, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "New Matches",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                TextButton(onClick = onNavigateToExplore) {
                    Text("Explore All (${properties.size})", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
        }

        items(newMatches.take(3), key = { "new_match_${it.id}" }) { prop ->
            PropertyCard(
                property = prop,
                locationDisplay = viewModel.getLocationDisplay(prop),
                imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                isSaved = prop.id in savedPropIds,
                onSaveToggle = { viewModel.toggleSave(prop.id) },
                onClick = {
                    viewModel.recordPropertyView(prop.id)
                    onNavigateToPropertyDetail(prop.id)
                }
            )
        }

        // 6. Recently Viewed Properties
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
                            }
                        )
                    }
                }
            }
        }

        // 7. Recommended for You
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
                        maxLines = 1,
                        softWrap = false
                    )
                }
                Text("Verified Listings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
            }
        }

        items(recommended.take(3), key = { "rec_${it.id}" }) { prop ->
            PropertyCard(
                property = prop,
                locationDisplay = viewModel.getLocationDisplay(prop),
                imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                isSaved = prop.id in savedPropIds,
                onSaveToggle = { viewModel.toggleSave(prop.id) },
                onClick = {
                    viewModel.recordPropertyView(prop.id)
                    onNavigateToPropertyDetail(prop.id)
                }
            )
        }

        // 8. Saved Properties Preview
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
                    primaryImageUrl = null,
                    isSaved = true,
                    onSaveToggle = { viewModel.toggleSave(prop.id) },
                    onClick = {
                        viewModel.recordPropertyView(prop.id)
                        onNavigateToPropertyDetail(prop.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun ActivityChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color, maxLines = 1, softWrap = false)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
private fun RecentPropertyMiniCard(
    property: PropertyEntity,
    locationDisplay: String,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = property.propertyType,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1,
                    softWrap = false
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = property.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = locationDisplay,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${currencyFormat.format(property.monthlyRent).replace("PHP", "₱")} / mo",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
