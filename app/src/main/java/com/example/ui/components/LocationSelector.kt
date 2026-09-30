package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.CountryEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.RegionEntity

@Composable
fun CascadingLocationSelector(
    modifier: Modifier = Modifier,
    countries: List<CountryEntity>,
    selectedCountryId: String,
    onCountrySelected: (String) -> Unit,
    regions: List<RegionEntity>,
    selectedRegionId: String,
    onRegionSelected: (String) -> Unit,
    provinces: List<ProvinceEntity>,
    selectedProvinceId: String,
    onProvinceSelected: (String) -> Unit,
    cities: List<CityMunicipalityEntity>,
    selectedCityId: String,
    onCitySelected: (String) -> Unit,
    barangays: List<BarangayEntity>,
    selectedBarangayId: String,
    onBarangaySelected: (String) -> Unit,
    allowAllOption: Boolean = true
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Province Selector
        val selectedProvince = provinces.find { it.id == selectedProvinceId }
        SearchableDropdownField(
            label = "Province",
            selectedText = if (selectedProvinceId.isEmpty() && allowAllOption) "All Provinces (Philippines)" else selectedProvince?.name ?: "Select Province",
            items = if (allowAllOption) listOf("All Provinces") + provinces.map { it.name } else provinces.map { it.name },
            onItemSelected = { selectedName ->
                if (selectedName == "All Provinces") {
                    onProvinceSelected("")
                    onCitySelected("")
                    onBarangaySelected("")
                } else {
                    val prov = provinces.find { it.name == selectedName }
                    if (prov != null && prov.id != selectedProvinceId) {
                        onProvinceSelected(prov.id)
                        onCitySelected("")
                        onBarangaySelected("")
                    }
                }
            },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = "Province", tint = MaterialTheme.colorScheme.primary)
            },
            modifier = Modifier.testTag("province_selector")
        )

        // City / Municipality Selector (Enabled if province selected or all)
        val selectedCity = cities.find { it.id == selectedCityId }
        val availableCities = if (selectedProvinceId.isNotEmpty()) {
            cities.filter { it.provinceId == selectedProvinceId }
        } else {
            cities
        }

        SearchableDropdownField(
            label = "City / Municipality",
            selectedText = if (selectedCityId.isEmpty() && allowAllOption) "All Cities / Municipalities" else selectedCity?.name ?: "Select City / Municipality",
            items = if (allowAllOption) listOf("All Cities / Municipalities") + availableCities.map { it.name } else availableCities.map { it.name },
            onItemSelected = { selectedName ->
                if (selectedName == "All Cities / Municipalities") {
                    onCitySelected("")
                    onBarangaySelected("")
                } else {
                    val city = availableCities.find { it.name == selectedName }
                    if (city != null && city.id != selectedCityId) {
                        onCitySelected(city.id)
                        onBarangaySelected("")
                    }
                }
            },
            enabled = availableCities.isNotEmpty(),
            modifier = Modifier.testTag("city_selector")
        )

        // Barangay Selector (Enabled if city selected)
        val selectedBarangay = barangays.find { it.id == selectedBarangayId }
        val availableBarangays = if (selectedCityId.isNotEmpty()) {
            barangays.filter { it.cityMunicipalityId == selectedCityId }
        } else {
            emptyList()
        }

        SearchableDropdownField(
            label = "Barangay",
            selectedText = if (selectedBarangayId.isEmpty() && allowAllOption) "All Barangays" else selectedBarangay?.name ?: if (selectedCityId.isEmpty()) "Select City First" else "Select Barangay",
            items = if (allowAllOption && availableBarangays.isNotEmpty()) listOf("All Barangays") + availableBarangays.map { it.name } else availableBarangays.map { it.name },
            onItemSelected = { selectedName ->
                if (selectedName == "All Barangays") {
                    onBarangaySelected("")
                } else {
                    val bgy = availableBarangays.find { it.name == selectedName }
                    if (bgy != null) {
                        onBarangaySelected(bgy.id)
                    }
                }
            },
            enabled = availableBarangays.isNotEmpty(),
            modifier = Modifier.testTag("barangay_selector")
        )
    }
}

@Composable
fun SearchableDropdownField(
    label: String,
    selectedText: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
                Text(
                    text = selectedText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    maxLines = 1,
                    softWrap = false,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }

    if (showDialog && enabled) {
        SearchSelectionDialog(
            title = "Select $label",
            items = items,
            onDismiss = { showDialog = false },
            onSelect = {
                onItemSelected(it)
                showDialog = false
            }
        )
    }
}

@Composable
fun SearchSelectionDialog(
    title: String,
    items: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items else items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search location...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    if (filtered.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No locations found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        items(filtered) { itemText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(itemText) }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = itemText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
