package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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

    val isDark = isSystemInDarkTheme()
    val exploreTeal = if (isDark) MaterialTheme.colorScheme.primary else SteelBlue
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val dimTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val groups = remember { DestinationGroup.entries.toList() }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { groups.size })
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("explore_neighbourhood_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(1.dp, SteelBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // 1. Header: "➔ Explore Neighbourhood"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = exploreTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explore Neighbourhood",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = exploreTeal,
                        softWrap = true
                    )
                }
            }

            // 2. Tabs: TRANSIT | ESSENTIALS | UTILITY with smooth clickable sliding
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
                            .padding(top = 4.dp)
                            .testTag("tab_${group.name.lowercase()}")
                    ) {
                        Text(
                            text = group.name,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) exploreTeal else dimTextColor,
                            letterSpacing = 0.4.sp,
                            softWrap = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(2.5.dp)
                                .background(if (isSelected) exploreTeal else Color.Transparent)
                        )
                    }
                }
            }

            // Subtle base line below the tabs
            HorizontalDivider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // 3. Tab Content: Smooth HorizontalPager sliding left to right and vice versa
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = dimTextColor,
                        softWrap = true,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        subcategoryMap.forEach { (subcategory, places) ->
                            SubcategoryBlock(
                                subcategory = subcategory,
                                places = places,
                                accentColor = exploreTeal,
                                dividerColor = dividerColor
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
    accentColor: Color,
    dividerColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subcategory_${subcategory.lowercase().replace(" ", "_")}"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Subcategory Title Row with icon in teal
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = getSubcategoryIcon(subcategory),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = subcategory,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor,
                softWrap = true,
                modifier = Modifier.weight(1f)
            )
        }

        // Thin divider below subcategory title
        HorizontalDivider(
            color = dividerColor,
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
        )

        // Places rows - same text font, weight, and size for Place Name, distance, and time
        places.forEach { place ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("place_row_${place.id}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Place Name
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                // Distance and Time formatted with matching font style and size (13.sp)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = place.formattedDistanceAndTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        softWrap = true,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
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
