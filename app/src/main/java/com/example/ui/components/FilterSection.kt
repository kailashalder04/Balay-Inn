package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.GenderPolicy
import com.example.model.RoomType
import com.example.viewmodel.FilterState

@Composable
fun FilterSection(
    filterState: FilterState,
    onSelectRoomType: (RoomType?) -> Unit,
    onSelectGenderPolicy: (GenderPolicy?) -> Unit,
    onToggleVerified: () -> Unit,
    onToggleWifi: () -> Unit,
    onToggleNoCurfew: () -> Unit,
    onSetMaxRent: (Int) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Room Types Scrollable Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterState.roomType == null,
                onClick = { onSelectRoomType(null) },
                label = { Text("All Rooms") },
                modifier = Modifier.testTag("filter_chip_all_rooms")
            )

            RoomType.entries.forEach { type ->
                FilterChip(
                    selected = filterState.roomType == type,
                    onClick = { onSelectRoomType(type) },
                    label = { Text(type.displayName) },
                    leadingIcon = if (filterState.roomType == type) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("filter_chip_${type.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Secondary Filters: Gender Policy & Amenities
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Female Only
            FilterChip(
                selected = filterState.genderPolicy == GenderPolicy.FEMALE_ONLY,
                onClick = { onSelectGenderPolicy(GenderPolicy.FEMALE_ONLY) },
                label = { Text("Female Only") },
                modifier = Modifier.testTag("filter_chip_female_only")
            )

            // Male Only
            FilterChip(
                selected = filterState.genderPolicy == GenderPolicy.MALE_ONLY,
                onClick = { onSelectGenderPolicy(GenderPolicy.MALE_ONLY) },
                label = { Text("Male Only") },
                modifier = Modifier.testTag("filter_chip_male_only")
            )

            // Verified Landlord
            FilterChip(
                selected = filterState.onlyVerified,
                onClick = onToggleVerified,
                label = { Text("Verified Landlords") },
                leadingIcon = if (filterState.onlyVerified) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                modifier = Modifier.testTag("filter_chip_verified")
            )

            // Wi-Fi Included
            FilterChip(
                selected = filterState.onlyWifi,
                onClick = onToggleWifi,
                label = { Text("Wi-Fi Included") },
                leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("filter_chip_wifi")
            )

            // No Curfew
            FilterChip(
                selected = filterState.onlyNoCurfew,
                onClick = onToggleNoCurfew,
                label = { Text("No Curfew") },
                modifier = Modifier.testTag("filter_chip_no_curfew")
            )
        }

        // Budget Slider bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Max Rent: $${filterState.maxRent}/mo",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val isFiltered = filterState.roomType != null ||
                            filterState.genderPolicy != null ||
                            filterState.onlyVerified ||
                            filterState.onlyWifi ||
                            filterState.onlyNoCurfew ||
                            filterState.maxRent < 350 ||
                            filterState.searchQuery.isNotBlank()

                    if (isFiltered) {
                        TextButton(
                            onClick = onResetFilters,
                            modifier = Modifier.testTag("reset_filters_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Slider(
                    value = filterState.maxRent.toFloat(),
                    onValueChange = { onSetMaxRent(it.toInt()) },
                    valueRange = 50f..350f,
                    steps = 5,
                    modifier = Modifier.fillMaxWidth().testTag("max_rent_slider")
                )
            }
        }
    }
}
