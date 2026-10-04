package com.example.ui.components

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.CommuteDestination
import com.example.data.model.CommuteGuideProvider
import com.example.data.model.DestinationGroup

/**
 * Explore Neighbourhood / Nearby Destination Section
 * Fully dark-mode and light-mode adaptive:
 * - "➔ Explore Neighbourhood" title in dynamic primary teal
 * - 3 Tabs: TRANSIT | ESSENTIALS | UTILITY with active underline indicator
 * - Subcategory headers with icons in theme-adaptive teal and subtle dividers
 * - List of places with onSurface high-contrast text and onSurfaceVariant travel times
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
    val exploreTeal = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF00796B)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val dimTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    var selectedGroup by remember { mutableStateOf(DestinationGroup.TRANSIT) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("explore_neighbourhood_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, dividerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // 1. Header: "➔ Explore Neighbourhood"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
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
                    color = exploreTeal
                )
            }

            // 2. Tabs: TRANSIT | ESSENTIALS | UTILITY
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DestinationGroup.entries.forEach { group ->
                    val isSelected = selectedGroup == group
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedGroup = group }
                            .padding(top = 4.dp)
                            .testTag("tab_${group.name.lowercase()}")
                    ) {
                        Text(
                            text = group.name,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) exploreTeal else dimTextColor,
                            letterSpacing = 0.4.sp
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
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 3. Tab Content: Subcategories with places list
            Crossfade(
                targetState = selectedGroup,
                label = "neighbourhood_tab_transition"
            ) { group ->
                val subcategoryMap = remember(group, property) {
                    CommuteGuideProvider.getDestinationsBySubcategory(property, group)
                }

                if (subcategoryMap.isEmpty()) {
                    Text(
                        text = "No recorded destinations nearby.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = dimTextColor,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
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
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }

        // Thin divider below subcategory title
        HorizontalDivider(
            color = dividerColor,
            thickness = 0.8.dp,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
        )

        // Places rows
        places.forEach { place ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .testTag("place_row_${place.id}"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Place Name with full dark/light theme visibility
                Text(
                    text = place.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )

                // Distance and Time formatted as "7.1 km|20 mins" with full dark/light contrast
                Text(
                    text = place.formattedDistanceAndTime,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
