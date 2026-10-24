package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.CommuteDestination
import com.example.data.model.CommuteGuideProvider
import com.example.data.model.DestinationGroup
import com.example.ui.theme.PropertyBoxBgDark
import com.example.ui.theme.PropertyBoxBorder
import com.example.ui.theme.PropertyBoxBorderDark
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.SteelBlueLight
import com.example.ui.theme.SteelBlueText
import com.example.ui.theme.SteelBlueTextDark
import kotlin.math.abs

/**
 * Explore Neighborhood / Nearby Destination Section
 * Matches design specification:
 * - "Explore Neighborhood" header matching theme
 * - Three flat tabs: TRANSIT | ESSENTIALS | UTILITY with active steel blue underline indicator
 * - Smooth horizontal sliding feature across tabs via swipe gestures and tab clicks
 * - Subcategory rows with steel blue icon, steel blue title, and clean divider line
 * - Clean, non-hidden place names and right-aligned distance/time format "X.X km|YY mins"
 * - Compact, elegant layout with no clipped text
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
    val steelBlueIndicator = if (isDark) SteelBlueLight else SteelBlue
    val steelBlueSubheadingText = if (isDark) SteelBlueTextDark else SteelBlueText
    val steelBlueSubheadingIcon = if (isDark) SteelBlueLight else SteelBlue
    val tabDividerColor = if (isDark) PropertyBoxBorderDark else Color(0xFFE2E8F0)
    val groups = remember { DestinationGroup.entries.toList() }
    var selectedGroupIndex by remember { mutableIntStateOf(0) }
    val selectedGroup = groups[selectedGroupIndex.coerceIn(0, groups.size - 1)]

    var totalDrag by remember { mutableFloatStateOf(0f) }

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
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // 1. Header: Explore Neighborhood
            Text(
                text = "Explore Neighborhood",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )

            // 2. Tabs: TRANSIT | ESSENTIALS | UTILITY with Teal Underline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                groups.forEachIndexed { index, group ->
                    val isSelected = selectedGroupIndex == index
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedGroupIndex = index }
                            .padding(top = 2.dp)
                            .testTag("tab_${group.name.lowercase()}")
                    ) {
                        Text(
                            text = group.name,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) {
                                if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                            } else {
                                if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            },
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.5.dp)
                                .background(if (isSelected) steelBlueIndicator else Color.Transparent)
                        )
                    }
                }
            }

            // Base Divider under the tab row
            HorizontalDivider(
                color = tabDividerColor,
                thickness = 0.8.dp,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // 3. Tab Content with Smooth Horizontal Sliding Feature
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(selectedGroupIndex) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (totalDrag < -35f && selectedGroupIndex < groups.size - 1) {
                                    selectedGroupIndex++
                                } else if (totalDrag > 35f && selectedGroupIndex > 0) {
                                    selectedGroupIndex--
                                }
                                totalDrag = 0f
                            },
                            onDragCancel = {
                                totalDrag = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                totalDrag += dragAmount
                                if (abs(totalDrag) > 20f) {
                                    change.consume()
                                }
                            }
                        )
                    }
            ) {
                AnimatedContent(
                    targetState = selectedGroup,
                    transitionSpec = {
                        val isForward = targetState.ordinal > initialState.ordinal
                        val enter = slideInHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { width ->
                            if (isForward) width else -width
                        } + fadeIn(tween(220))
                        val exit = slideOutHorizontally(animationSpec = tween(280, easing = FastOutSlowInEasing)) { width ->
                            if (isForward) -width else width
                        } + fadeOut(tween(180))
                        enter togetherWith exit
                    },
                    label = "explore_neighbourhood_sliding",
                    modifier = Modifier.fillMaxWidth()
                ) { group ->
                    val subcategoryMap = remember(group, property) {
                        CommuteGuideProvider.getDestinationsBySubcategory(property, group)
                    }

                    if (subcategoryMap.isEmpty()) {
                        Text(
                            text = "No recorded destinations nearby for ${group.title}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
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
                                    subheadingTextColor = steelBlueSubheadingText,
                                    subheadingIconColor = steelBlueSubheadingIcon,
                                    isDark = isDark
                                )
                            }
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
    subheadingTextColor: Color,
    subheadingIconColor: Color,
    isDark: Boolean
) {
    // Show maximum three places only with minimum distance and time
    val displayPlaces = remember(places) {
        places
            .sortedWith(compareBy({ it.distanceMeters }, { it.timeToReachMinutes }))
            .take(3)
    }

    val subcategoryLineColor = if (isDark) Color(0xFF334155) else Color(0xFFE5E7EB)
    val itemTextColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF374151)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subcategory_${subcategory.lowercase().replace(" ", "_")}")
    ) {
        // Subcategory Title Row with Steel Blue Icon & Steel Blue Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = getSubcategoryIcon(subcategory),
                contentDescription = null,
                tint = subheadingIconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = subcategory,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = subheadingTextColor,
                modifier = Modifier.weight(1f)
            )
        }

        // Full-width subtle horizontal line directly underneath subcategory title
        HorizontalDivider(
            color = subcategoryLineColor,
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
        )

        // Places rows - matching image: clean place name on left, formatted distance|time on right
        displayPlaces.forEach { place ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .testTag("place_row_${place.id}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Place Name: fully visible without truncation
                Text(
                    text = place.name,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = itemTextColor,
                    softWrap = true,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                // Distance and Time: right aligned, plain text format "7.1 km|20 mins"
                Text(
                    text = place.formattedDistanceAndTime,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = itemTextColor,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

private fun getSubcategoryIcon(subcategory: String): ImageVector {
    val s = subcategory.lowercase()
    return when {
        s.contains("metro") || s.contains("rail") || s.contains("train") -> Icons.Default.DirectionsSubway
        s.contains("bus") -> Icons.Default.DirectionsBus
        s.contains("jeep") || s.contains("transit") || s.contains("depot") -> Icons.Default.AirportShuttle
        s.contains("port") || s.contains("ferri") || s.contains("wharf") || s.contains("pier") -> Icons.Default.DirectionsBoat
        s.contains("airport") || s.contains("flight") -> Icons.Default.Flight
        s.contains("college") || s.contains("universit") -> Icons.Default.School
        s.contains("hospital") || s.contains("medical") || s.contains("clinic") -> Icons.Default.LocalHospital
        s.contains("school") -> Icons.AutoMirrored.Filled.MenuBook
        s.contains("mall") -> Icons.Default.LocalMall
        s.contains("movie") || s.contains("cinema") || s.contains("theater") -> Icons.Default.Movie
        s.contains("pharmac") || s.contains("drug") -> Icons.Default.LocalPharmacy
        else -> Icons.Default.Storefront
    }
}
