package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhilippineRentalPropertyCategory

/**
 * Dedicated category-specific details section displayed during property posting.
 *
 * Caters to:
 * 1. Studio & Condominium: Developer Name, Building Name, Tower, Floor Level, Unit Number (option to hide),
 *    Floor Area (sqm), Balcony (Yes/No), Association Dues (Inclusive/Exclusive), Amenities (Pool, Gym, Security,
 *    Function Hall, Backup Power), Rules (Pet-friendly).
 * 2. Apartment (Multi-door): Number of Units, Floor Level, Utilities (Separate Meralco Meter, Separate Maynilad/Water Sub-meter),
 *    Parking (Street, Garage, First-come), Layout (Beds, Baths).
 * 3. House & Townhouse: Lot Area (sqm), Floor Area (sqm), Subdivision/Village Name, HOA Dues, Unit Type (Inner, End, Corner),
 *    Floors, Garage Capacity, Maid's Room, Garden/Yard, Security (Gated, Roving Guards).
 * 4. Room (Bedspace / Co-living): Occupancy Type (Solo, Shared, Bedspace with head count), Target Tenant (Female Only, Male Only, Mixed),
 *    Facilities (Shared vs En-suite bath, Cooking rules, Visitors), Inclusions (WiFi, Water, Electricity), Furnishing (Double-deck, single bed, cabinets, aircon/fan).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySpecificationsPostingSection(
    category: PhilippineRentalPropertyCategory,
    // Studio & Condominium
    condoDeveloperName: String,
    onCondoDeveloperNameChange: (String) -> Unit,
    condoBuildingName: String,
    onCondoBuildingNameChange: (String) -> Unit,
    condoTower: String,
    onCondoTowerChange: (String) -> Unit,
    condoFloorLevel: String,
    onCondoFloorLevelChange: (String) -> Unit,
    condoUnitNumber: String,
    onCondoUnitNumberChange: (String) -> Unit,
    condoHideUnitNumber: Boolean,
    onCondoHideUnitNumberChange: (Boolean) -> Unit,
    condoFloorAreaStr: String,
    onCondoFloorAreaChange: (String) -> Unit,
    condoHasBalcony: Boolean,
    onCondoHasBalconyChange: (Boolean) -> Unit,
    condoAssocDuesStatus: String,
    onCondoAssocDuesStatusChange: (String) -> Unit,
    condoAmenities: SnapshotStateList<String>,
    onToggleCondoAmenity: (String) -> Unit,
    condoPetFriendly: Boolean,
    onCondoPetFriendlyChange: (Boolean) -> Unit,
    // Apartment (Multi-door)
    aptNumberOfUnits: String,
    onAptNumberOfUnitsChange: (String) -> Unit,
    aptFloorLevel: String,
    onAptFloorLevelChange: (String) -> Unit,
    aptElectricityMeter: String,
    onAptElectricityMeterChange: (String) -> Unit,
    aptWaterMeter: String,
    onAptWaterMeterChange: (String) -> Unit,
    aptParkingType: String,
    onAptParkingTypeChange: (String) -> Unit,
    aptBeds: Int,
    onAptBedsChange: (Int) -> Unit,
    aptBaths: Int,
    onAptBathsChange: (Int) -> Unit,
    // House & Townhouse
    houseLotAreaStr: String,
    onHouseLotAreaChange: (String) -> Unit,
    houseFloorAreaStr: String,
    onHouseFloorAreaChange: (String) -> Unit,
    houseSubdivisionName: String,
    onHouseSubdivisionNameChange: (String) -> Unit,
    houseHoaDues: String,
    onHouseHoaDuesChange: (String) -> Unit,
    houseUnitType: String,
    onHouseUnitTypeChange: (String) -> Unit,
    houseFloors: String,
    onHouseFloorsChange: (String) -> Unit,
    houseGarageCapacity: String,
    onHouseGarageCapacityChange: (String) -> Unit,
    houseHasMaidsRoom: Boolean,
    onHouseHasMaidsRoomChange: (Boolean) -> Unit,
    houseHasGardenYard: Boolean,
    onHouseHasGardenYardChange: (Boolean) -> Unit,
    houseSecurityType: String,
    onHouseSecurityTypeChange: (String) -> Unit,
    // Room (Bedspace / Co-living)
    roomOccupancyType: String,
    onRoomOccupancyTypeChange: (String) -> Unit,
    roomBedspaceHeadCount: Int,
    onRoomBedspaceHeadCountChange: (Int) -> Unit,
    roomTargetTenant: String,
    onRoomTargetTenantChange: (String) -> Unit,
    roomBathFacility: String,
    onRoomBathFacilityChange: (String) -> Unit,
    roomCookingRules: String,
    onRoomCookingRulesChange: (String) -> Unit,
    roomVisitorPolicy: String,
    onRoomVisitorPolicyChange: (String) -> Unit,
    roomInclusions: SnapshotStateList<String>,
    onToggleRoomInclusion: (String) -> Unit,
    roomFurnishing: SnapshotStateList<String>,
    onToggleRoomFurnishing: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_specifications_posting_section"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = category.targetEmoji,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${category.displayName} Specifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Mandatory category details for property cards & tenant filters",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when (category) {
                PhilippineRentalPropertyCategory.CONDOMINIUM,
                PhilippineRentalPropertyCategory.STUDIO_UNIT -> {
                    // 1. Studio & Condominium Fields
                    OutlinedTextField(
                        value = condoDeveloperName,
                        onValueChange = onCondoDeveloperNameChange,
                        label = { Text("Developer Name") },
                        placeholder = { Text("e.g. Ayala Land, Megaworld, SMDC, Cebu Landmasters") },
                        modifier = Modifier.fillMaxWidth().testTag("condo_developer_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = condoBuildingName,
                            onValueChange = onCondoBuildingNameChange,
                            label = { Text("Building Name *") },
                            placeholder = { Text("e.g. Avida Towers, Solinea") },
                            modifier = Modifier.weight(1f).testTag("condo_building_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = condoTower,
                            onValueChange = onCondoTowerChange,
                            label = { Text("Tower") },
                            placeholder = { Text("e.g. Tower 1") },
                            modifier = Modifier.weight(1f).testTag("condo_tower_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = condoFloorLevel,
                            onValueChange = onCondoFloorLevelChange,
                            label = { Text("Floor Level *") },
                            placeholder = { Text("e.g. 15th Floor") },
                            modifier = Modifier.weight(1f).testTag("condo_floor_level_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = condoFloorAreaStr,
                            onValueChange = onCondoFloorAreaChange,
                            label = { Text("Floor Area (sqm) *") },
                            placeholder = { Text("e.g. 28") },
                            modifier = Modifier.weight(1f).testTag("condo_floor_area_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = condoUnitNumber,
                        onValueChange = onCondoUnitNumberChange,
                        label = { Text("Unit Number") },
                        placeholder = { Text("e.g. Unit 1502") },
                        modifier = Modifier.fillMaxWidth().testTag("condo_unit_number_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Option to hide Unit Number
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hide Unit Number on Public Listing",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (condoHideUnitNumber) "Hidden from cards (Shared only upon confirmed viewing)" else "Visible to public search",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = condoHideUnitNumber,
                                onCheckedChange = onCondoHideUnitNumberChange,
                                modifier = Modifier.testTag("condo_hide_unit_switch")
                            )
                        }
                    }

                    // Balcony (Yes/No)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Balcony Provision:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(true to "Balcony: Yes", false to "Balcony: No").forEach { (value, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = condoHasBalcony == value,
                                        onClick = { onCondoHasBalconyChange(value) }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Association Dues (Inclusive/Exclusive)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Condo Association Dues:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("Inclusive in Rent", "Exclusive (Billed Separately)").forEach { opt ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = condoAssocDuesStatus == opt || (opt.startsWith("Inclusive") && condoAssocDuesStatus == "Inclusive"),
                                        onClick = { onCondoAssocDuesStatusChange(if (opt.startsWith("Inclusive")) "Inclusive" else "Exclusive") }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(opt, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Amenities (Pool, Gym, Security, Function Hall, Backup Power)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Condominium Amenities:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Swimming Pool", "Gym / Fitness Center", "24/7 Security", "Function Hall", "Backup Power / Generator").forEach { amen ->
                                val isChecked = condoAmenities.contains(amen) || condoAmenities.any { it.contains(amen.take(4), ignoreCase = true) }
                                FilterChip(
                                    selected = isChecked,
                                    onClick = { onToggleCondoAmenity(amen) },
                                    label = { Text(amen, fontSize = 11.sp) },
                                    leadingIcon = if (isChecked) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Rules (Pet-friendly)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Building Pet Policy:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf(true to "Pet-friendly (Allowed)", false to "Strictly No Pets").forEach { (value, label) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = condoPetFriendly == value,
                                        onClick = { onCondoPetFriendlyChange(value) }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(label, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                PhilippineRentalPropertyCategory.APARTMENT_FLAT -> {
                    // 2. Apartment (Multi-door) Fields
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Number of Units (Multi-door setup):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("2-door", "4-door", "6-door", "8-door", "10-door multi-unit").forEach { opt ->
                                val isSelected = aptNumberOfUnits == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onAptNumberOfUnitsChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Floor Level:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Ground / 1st Floor", "2nd Floor", "3rd Floor", "4th Floor").forEach { opt ->
                                val isSelected = aptFloorLevel == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onAptFloorLevelChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Electric Metering Utility:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Separate Meralco Meter", "Separate VECO Meter", "Separate CENECO/BOHECO Meter", "Sub-metered").forEach { opt ->
                                val isSelected = aptElectricityMeter == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onAptElectricityMeterChange(opt) },
                                    label = { Text(opt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Water Metering Utility:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Separate Maynilad/Water Sub-meter", "Separate MCWD Meter", "Individual Water Tank/Pump").forEach { opt ->
                                val isSelected = aptWaterMeter == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onAptWaterMeterChange(opt) },
                                    label = { Text(opt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Parking Provision:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Garage Parking", "Street Parking", "First-come, first-served").forEach { opt ->
                                val isSelected = aptParkingType == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onAptParkingTypeChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    // Layout (Beds, Baths)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bedrooms:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..4).forEach { count ->
                                    FilterChip(
                                        selected = aptBeds == count,
                                        onClick = { onAptBedsChange(count) },
                                        label = { Text("$count") }
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Bathrooms:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                (1..3).forEach { count ->
                                    FilterChip(
                                        selected = aptBaths == count,
                                        onClick = { onAptBathsChange(count) },
                                        label = { Text("$count") }
                                    )
                                }
                            }
                        }
                    }
                }

                PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE -> {
                    // 3. House & Townhouse Fields
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = houseLotAreaStr,
                            onValueChange = onHouseLotAreaChange,
                            label = { Text("Lot Area (sqm) *") },
                            placeholder = { Text("e.g. 120") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = houseFloorAreaStr,
                            onValueChange = onHouseFloorAreaChange,
                            label = { Text("Floor Area (sqm) *") },
                            placeholder = { Text("e.g. 95") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = houseSubdivisionName,
                        onValueChange = onHouseSubdivisionNameChange,
                        label = { Text("Subdivision / Village Name *") },
                        placeholder = { Text("e.g. Villa Leona Subdivision, Deca Homes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = houseHoaDues,
                        onValueChange = onHouseHoaDuesChange,
                        label = { Text("HOA Dues") },
                        placeholder = { Text("e.g. ₱500/mo (Inclusive in rent)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Unit Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Inner", "End", "Corner").forEach { opt ->
                                val isSelected = houseUnitType == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onHouseUnitTypeChange(opt) },
                                    label = { Text("$opt Unit", fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Floors:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("1-Storey", "2-Storey", "3-Storey").forEach { flr ->
                                    FilterChip(
                                        selected = houseFloors == flr,
                                        onClick = { onHouseFloorsChange(flr) },
                                        label = { Text(flr, fontSize = 10.5.sp) }
                                    )
                                }
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Garage Capacity:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("1-Car", "2-Car", "3+ Cars").forEach { gar ->
                                    FilterChip(
                                        selected = houseGarageCapacity == gar,
                                        onClick = { onHouseGarageCapacityChange(gar) },
                                        label = { Text(gar, fontSize = 10.5.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Maid's Room:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = houseHasMaidsRoom,
                                onCheckedChange = onHouseHasMaidsRoomChange
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Garden / Yard:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = houseHasGardenYard,
                                onCheckedChange = onHouseHasGardenYardChange
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Subdivision Security:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Gated & Roving Guards", "Gated with Guardhouse", "Roving Village Guards").forEach { opt ->
                                val isSelected = houseSecurityType == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onHouseSecurityTypeChange(opt) },
                                    label = { Text(opt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }

                PhilippineRentalPropertyCategory.BED_SPACE -> {
                    // 4. Room (Bedspace / Co-living) Fields
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Occupancy Type:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Solo", "Shared", "Bedspace").forEach { opt ->
                                val isSelected = roomOccupancyType == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onRoomOccupancyTypeChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    if (roomOccupancyType == "Bedspace" || roomOccupancyType == "Shared") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Bedspace Head Count (pax per room):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(2, 4, 6, 8).forEach { count ->
                                    FilterChip(
                                        selected = roomBedspaceHeadCount == count,
                                        onClick = { onRoomBedspaceHeadCountChange(count) },
                                        label = { Text("$count-person") }
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Target Tenant Gender Policy:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Female Only", "Male Only", "Mixed").forEach { opt ->
                                val isSelected = roomTargetTenant == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onRoomTargetTenantChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Bathroom Facilities:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Shared CR", "En-suite Bath").forEach { opt ->
                                val isSelected = roomBathFacility == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onRoomBathFacilityChange(opt) },
                                    label = { Text(opt, fontSize = 11.5.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Cooking Rules:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Light electric only (rice cooker/kettle)", "No cooking", "LPG cooking allowed").forEach { opt ->
                                val isSelected = roomCookingRules == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onRoomCookingRulesChange(opt) },
                                    label = { Text(opt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Visitor Policy:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Daytime visitors only", "Strictly no visitors", "Visitors allowed").forEach { opt ->
                                val isSelected = roomVisitorPolicy == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onRoomVisitorPolicyChange(opt) },
                                    label = { Text(opt, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Inclusions (Included in Rent):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("WiFi", "Water", "Electricity").forEach { incl ->
                                val isChecked = roomInclusions.contains(incl)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = { onToggleRoomInclusion(incl) },
                                    label = { Text(incl, fontSize = 11.5.sp) },
                                    leadingIcon = if (isChecked) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp)) }
                                    } else null
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Room Furnishing Provided:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Double-deck", "Single bed", "Cabinets / Lockers", "Aircon", "Electric Fan").forEach { furn ->
                                val isChecked = roomFurnishing.contains(furn)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = { onToggleRoomFurnishing(furn) },
                                    label = { Text(furn, fontSize = 11.sp) },
                                    leadingIcon = if (isChecked) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp)) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
