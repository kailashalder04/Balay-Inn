package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.CommuteDestination
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.SteelBlueBorder
import com.example.ui.theme.SteelBlueDarkBg
import com.example.ui.theme.SteelBlueDarkBorder
import com.example.ui.theme.SteelBlueLightBg
import com.example.ui.theme.SteelBlueText
import com.example.ui.theme.SteelBlueTextDark
import com.example.ui.theme.PropertyBoxBg
import com.example.ui.theme.PropertyBoxBgDark
import com.example.ui.theme.PropertyBoxBorder
import com.example.ui.theme.PropertyBoxBorderDark
import com.example.data.model.CommuteGuideProvider
import com.example.data.model.DestinationGroup
import kotlinx.coroutines.launch

/**
 * Explore Neighbourhood / Nearby Destination Section
 * Fully dark-mode and light-mode adaptive:
 * - "➔ Explore Neighbourhood" title with smooth sliding left to right and vice versa
 * - HorizontalPager providing smooth gesture-based sliding and tab navigation
 * - Wrap hidden texts across multiple lines without truncating
 * - 3 Tabs: TRANSIT | ESSENTIALS | UTILITY with smooth active underline tracker
 * - Left/Right navigation chevrons for smooth sliding
 */
@Composable
fun CommuteRouteGuideSection(
    property: PropertyEntity,
    modifier: Modifier = Modifier
) {
    NearbyDestinationSection(property = property, modifier = modifier)
}

@Composable
fun NearbyDestinationSection(
    property: PropertyEntity,
    modifier: Modifier = Modifier
) {
    val allDestinations = remember(property) {
        CommuteGuideProvider.getCommuteDestinations(property)
    }

    if (allDestinations.isEmpty()) return

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val exploreTeal = if (isDark) MaterialTheme.colorScheme.primary else SteelBlue
    val dividerColor = if (isDark) MaterialTheme.colorScheme.outlineVariant else Color(0xFFE2E8F0)
    val dimTextColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)

    val groups = remember { DestinationGroup.entries.toList() }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { groups.size })
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("explore_neighbourhood_section"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) PropertyBoxBgDark else Color.White,
            contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
        ),
        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tabs: TRANSIT | ESSENTIALS | UTILITY with smooth clickable sliding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                groups.forEachIndexed { index, group ->
                    val isSelected = pagerState.currentPage == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = index,
                                        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                                    )
                                }
                            }
                            .padding(top = 2.dp)
                            .testTag("tab_${group.name.lowercase()}")
                    ) {
                        Text(
                            text = group.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)),
                            letterSpacing = 0.4.sp,
                            softWrap = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(2.5.dp)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                    }
                }
            }

            // Base line below tabs matching other container dividers
            HorizontalDivider(
                color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                thickness = 0.8.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tab Content: Smooth HorizontalPager sliding
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val group = groups[pageIndex]
                val subcategoryMap = remember(group, property) {
                    CommuteGuideProvider.getDestinationsBySubcategory(property, group)
                }

                if (subcategoryMap.isEmpty()) {
                    Text(
                        text = "No recorded destinations nearby for ${group.title}.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        softWrap = true,
                        modifier = Modifier.padding(vertical = 14.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        subcategoryMap.forEach { (subcategory, places) ->
                            SubcategoryBlock(
                                subcategory = subcategory,
                                places = places,
                                isDark = isDark
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubcategoryBlock(
    subcategory: String,
    places: List<CommuteDestination>,
    isDark: Boolean
) {
    // Show maximum three places only with minimum distance and time
    val displayPlaces = remember(places) {
        places
            .sortedWith(compareBy({ it.distanceMeters }, { it.timeToReachMinutes }))
            .take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subcategory_${subcategory.lowercase().replace(" ", "_")}"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Subcategory Title Row matching the other cards' subheaders
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = getSubcategoryIcon(subcategory),
                contentDescription = null,
                tint = SteelBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = subcategory,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                softWrap = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Thin divider below subcategory title matching other cards
        HorizontalDivider(
            color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
        )

        // Places rows - showing max 3 places with minimum distance and time
        displayPlaces.forEach { place ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("place_row_${place.id}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Place Name
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                    softWrap = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                // Distance and Time formatted matching other badge pills
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) PropertyBoxBgDark else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder)
                ) {
                    Text(
                        text = place.formattedDistanceAndTime,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) SteelBlueTextDark else SteelBlueText,
                        softWrap = true,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

private fun getSubcategoryIcon(subcategory: String): ImageVector {
    val s = subcategory.lowercase()
    return when {
        s.contains("bus") -> Icons.Default.DirectionsBus
        s.contains("metro") || s.contains("rail") || s.contains("train") -> Icons.Default.DirectionsSubway
        s.contains("jeep") || s.contains("transit") || s.contains("depot") -> Icons.Default.AirportShuttle
        s.contains("port") || s.contains("ferri") || s.contains("wharf") || s.contains("pier") -> Icons.Default.DirectionsBoat
        s.contains("airport") || s.contains("shuttle") || s.contains("flight") -> Icons.Default.Flight
        s.contains("college") || s.contains("universit") -> Icons.Default.School
        s.contains("hospital") || s.contains("medical") || s.contains("clinic") -> Icons.Default.LocalHospital
        s.contains("school") -> Icons.AutoMirrored.Filled.MenuBook
        s.contains("mall") -> Icons.Default.LocalMall
        s.contains("movie") || s.contains("cinema") || s.contains("theater") -> Icons.Default.Movie
        s.contains("pharmac") || s.contains("drug") -> Icons.Default.LocalPharmacy
        else -> Icons.Default.Storefront
    }
}
