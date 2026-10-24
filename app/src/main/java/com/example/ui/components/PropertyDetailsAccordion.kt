package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Balcony
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.BoardingHouseListing
import com.example.data.model.getCategorySpecifications
import com.example.data.model.normalizeFurnishing
import com.example.ui.theme.PropertyBoxBgDark
import com.example.ui.theme.PropertyBoxBorder
import com.example.ui.theme.PropertyBoxBorderDark
import com.example.ui.theme.SteelBlue

/**
 * Normalized Property Types for category determination
 */
enum class PropertyTypeCategory {
    BOARDING_HOUSE,
    STUDIO,
    CONDO,
    APARTMENT,
    TOWNHOUSE;

    companion object {
        fun from(typeString: String): PropertyTypeCategory {
            val lower = typeString.lowercase().trim()
            return when {
                lower.contains("boarding") || lower.contains("bed space") || lower.contains("dorm") || lower.contains("room") -> BOARDING_HOUSE
                lower.contains("studio") -> STUDIO
                lower.contains("condo") -> CONDO
                lower.contains("townhouse") || lower.contains("town home") || lower.contains("duplex") -> TOWNHOUSE
                else -> APARTMENT
            }
        }
    }
}

/**
 * Hyper-condensed data point inside the accordion's 2-column grid
 * label: Sub-heading (kept for Unit Specs, omitted for Amenities & Facilities and Rules & Lease Terms)
 * value: Detail description (max 3 words for Amenities & Rules, fully visible with no hidden text)
 */
data class AccordionGridItem(
    val icon: ImageVector,
    val label: String? = null,
    val value: String
)

/**
 * Category Model to be displayed as an Accordion
 */
data class PropertyDetailCategory(
    val id: String,
    val title: String,
    val icon: ImageVector? = null,
    val subtitle: String? = null,
    val items: List<AccordionGridItem>
)

/**
 * Reusable Expandable Accordion component with clean title and no taglines
 */
@Composable
fun ExpandableCategoryAccordion(
    title: String,
    icon: ImageVector? = null,
    subtitle: String? = null,
    initiallyExpanded: Boolean = true,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    testTagSuffix: String = "",
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(initiallyExpanded) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "chevron_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("accordion_card_$testTagSuffix"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) PropertyBoxBgDark else Color.White,
            contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
        ),
        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Clickable to expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .testTag("accordion_header_$testTagSuffix"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDark) Color(0xFF22384E) else Color(0xFFEDF4FA)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = SteelBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color(0xFF1F3144) else Color(0xFFF1F5F9)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }
            }

            // Expandable Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(250)),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                        thickness = 0.8.dp
                    )
                    Box(modifier = Modifier.padding(16.dp)) {
                        content()
                    }
                }
            }
        }
    }
}

/**
 * Neat 2-Column Grid presenting hyper-condensed data points with simple, unique icons
 */
@Composable
fun AccordionTwoColumnGrid(
    items: List<AccordionGridItem>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Break into pairs for 2-column grid
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Column 1
                GridItemCell(
                    item = rowItems[0],
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                // Column 2 (or spacer if odd count)
                if (rowItems.size > 1) {
                    GridItemCell(
                        item = rowItems[1],
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Individual cell in the 2-column grid.
 * When label is null/blank (Amenities & Facilities, Rules & Lease Terms),
 * no sub-heading is rendered, and the 1-3 word detail is fully shown without truncation.
 */
@Composable
private fun GridItemCell(
    item: AccordionGridItem,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF142436) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isDark) Color(0xFF1B3249) else Color(0xFFEDF4FA)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = SteelBlue,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (!item.label.isNullOrBlank()) {
                    Text(
                        text = item.label,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.3.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
                Text(
                    text = item.value,
                    fontSize = if (item.label.isNullOrBlank()) 12.5.sp else 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E),
                    lineHeight = 16.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Clip,
                    softWrap = true
                )
            }
        }
    }
}

/**
 * Main dynamic presenter Composable that displays the Three Universal Headings
 * with NO taglines, furnishing strictly restricted to Unfurnished, Semi-Furnished, Fully-Furnished,
 * NO duplicate icons, and NO sub-headings in Amenities & Facilities and Rules & Lease Terms (max 3 words, all words visible).
 */
@Composable
fun PropertyDetailsDynamicAccordion(
    property: PropertyEntity,
    boardingHouse: BoardingHouseListing,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val propertyTypeCategory = remember(property.propertyType) {
        PropertyTypeCategory.from(property.propertyType)
    }

    // 1. Universal Headings: Unit Specifications, Amenities & Facilities, Rules & Lease Terms
    val categories: List<PropertyDetailCategory> = remember(property, boardingHouse, propertyTypeCategory) {
        val specs = property.getCategorySpecifications()

        // ---------------------------------------------------------------------
        // HEADING 1: Unit Specifications
        // (Floor area, bed/bath count, room type, layouts, furnishing level)
        // ---------------------------------------------------------------------
        val unitSpecsItems = mutableListOf<AccordionGridItem>()

        // 1. Floor Area
        val floorAreaDisplay = when {
            property.floorAreaSqm > 0 -> "${property.floorAreaSqm.toInt()} sqm"
            specs.apartment?.totalFloorAreaSqm != null -> "${specs.apartment.totalFloorAreaSqm.toInt()} sqm"
            specs.condominium?.totalFloorAreaSqm != null -> "${specs.condominium.totalFloorAreaSqm.toInt()} sqm"
            specs.townhouse != null -> "${specs.townhouse.floorAreaSqm.toInt()} sqm"
            specs.boardingHouse?.floorAreaSqm != null -> "${specs.boardingHouse.floorAreaSqm.toInt()} sqm"
            propertyTypeCategory == PropertyTypeCategory.STUDIO -> "24 sqm"
            else -> "Standard Area"
        }
        unitSpecsItems.add(
            AccordionGridItem(
                icon = Icons.Default.SquareFoot,
                label = "FLOOR AREA",
                value = floorAreaDisplay
            )
        )

        // 2. Bed / Bath Count
        val bedBathDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.BOARDING_HOUSE -> {
                if (boardingHouse.totalAvailableBeds > 0) {
                    "${boardingHouse.totalAvailableBeds} Beds • ${if (property.bathrooms > 1) "Private Bath" else "Shared Bath"}"
                } else {
                    "${specs.boardingHouse?.roomCapacity ?: "Solo/Bunk"} • ${if (property.bathrooms > 1) "Private Bath" else "Shared Bath"}"
                }
            }
            PropertyTypeCategory.STUDIO -> {
                "Studio Suite • ${property.bathrooms.coerceAtLeast(1)} Bath/CR"
            }
            PropertyTypeCategory.TOWNHOUSE -> {
                "${property.bedrooms} Bed • ${property.bathrooms} Bath"
            }
            else -> {
                "${if (property.bedrooms == 0) "Studio" else "${property.bedrooms} Bed${if (property.bedrooms > 1) "s" else ""}"} • ${property.bathrooms} Bath/CR"
            }
        }
        unitSpecsItems.add(
            AccordionGridItem(
                icon = Icons.Default.Bed,
                label = "BED & BATH",
                value = bedBathDisplay
            )
        )

        // 3. Room Type
        unitSpecsItems.add(
            AccordionGridItem(
                icon = Icons.Default.MeetingRoom,
                label = "ROOM TYPE",
                value = property.propertyType.ifBlank { "Rental Space" }
            )
        )

        // 4. Layout
        val layoutDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.STUDIO -> specs.condominium?.unitLayout ?: "Open Studio Plan"
            PropertyTypeCategory.CONDO -> specs.condominium?.unitLayout?.let { "$it Suite Layout" } ?: if (property.bedrooms == 0) "Studio Unit Layout" else "${property.bedrooms}BR Condo Layout"
            PropertyTypeCategory.TOWNHOUSE -> "${specs.townhouse?.stories ?: 2}-Storey Townhome"
            PropertyTypeCategory.BOARDING_HOUSE -> specs.boardingHouse?.roomCapacity ?: "Single / Shared Layout"
            else -> "${property.bedrooms}BR Apartment Layout"
        }
        unitSpecsItems.add(
            AccordionGridItem(
                icon = Icons.Default.Layers,
                label = "LAYOUT",
                value = layoutDisplay
            )
        )

        // 5. Furnishing Level - Strictly Unfurnished, Semi-Furnished, or Fully-Furnished
        val rawFurnishing = property.furnishedStatus.ifBlank {
            specs.apartment?.furnishing
                ?: specs.condominium?.furnishing
                ?: specs.townhouse?.furnishing
                ?: specs.boardingHouse?.furnishing
                ?: "Fully-Furnished"
        }
        val furnishingDisplay = normalizeFurnishing(rawFurnishing)
        unitSpecsItems.add(
            AccordionGridItem(
                icon = Icons.Default.Chair,
                label = "FURNISHING",
                value = furnishingDisplay
            )
        )

        // Additional Unit Specs from user posts (Unique Icons)
        specs.condominium?.floorLevel?.let { fl ->
            if (fl.isNotBlank()) {
                unitSpecsItems.add(
                    AccordionGridItem(
                        icon = Icons.Default.Elevator,
                        label = "FLOOR LEVEL",
                        value = fl
                    )
                )
            }
        }
        specs.townhouse?.let { th ->
            unitSpecsItems.add(
                AccordionGridItem(
                    icon = Icons.Default.Landscape,
                    label = "LOT AREA",
                    value = "${th.lotAreaSqm.toInt()} sqm Lot"
                )
            )
            if (th.hasMaidsRoom) {
                unitSpecsItems.add(
                    AccordionGridItem(
                        icon = Icons.Default.Deck,
                        label = "MAID'S ROOM",
                        value = "Service Quarters Included"
                    )
                )
            }
        }
        specs.boardingHouse?.let { bh ->
            if (bh.pricingModel.isNotBlank()) {
                unitSpecsItems.add(
                    AccordionGridItem(
                        icon = Icons.Default.Payments,
                        label = "PRICING MODEL",
                        value = bh.pricingModel
                    )
                )
            }
            if (bh.tenantPreference.isNotBlank()) {
                unitSpecsItems.add(
                    AccordionGridItem(
                        icon = Icons.Default.Group,
                        label = "TENANTS",
                        value = "${bh.tenantPreference} Setup"
                    )
                )
            }
        }

        // ---------------------------------------------------------------------
        // HEADING 2: Amenities & Facilities
        // (Sub-headings removed: max 3 words for complete understanding, all words visible)
        // ---------------------------------------------------------------------
        val amenitiesFacilitiesItems = mutableListOf<AccordionGridItem>()

        // 1. WiFi & Internet (<= 3 words, no sub-heading)
        val hasWifi = property.internetIncluded ||
                property.amenitiesJson.contains("wifi", ignoreCase = true) ||
                property.amenitiesJson.contains("fiber", ignoreCase = true) ||
                boardingHouse.amenities.any { it.contains("wifi", ignoreCase = true) } ||
                specs.boardingHouse?.utilities?.any { it.contains("wifi", ignoreCase = true) } == true
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.Wifi,
                label = null,
                value = if (hasWifi) "Free Fast WiFi" else "WiFi Ready Unit"
            )
        )

        // 2. Vehicle Parking (<= 3 words, no sub-heading)
        val parkingDisplay = if (property.parkingAvailable) {
            when {
                specs.townhouse != null -> "${specs.townhouse.garageCapacity} Car Garage"
                specs.condominium != null -> "Dedicated Parking Slot"
                propertyTypeCategory == PropertyTypeCategory.BOARDING_HOUSE -> "Motorcycle Parking Slot"
                else -> "Covered Parking Slot"
            }
        } else {
            "Street Parking Only"
        }
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.DirectionsCar,
                label = null,
                value = parkingDisplay
            )
        )

        // 3. Gym & Pool / Leisure Facilities (<= 3 words, no sub-heading)
        val hasPoolOrGym = specs.condominium?.amenities?.any { it.contains("pool", true) || it.contains("gym", true) } == true ||
                property.amenitiesJson.contains("pool", ignoreCase = true) ||
                property.amenitiesJson.contains("gym", ignoreCase = true)
        val facilitiesDisplay = when {
            hasPoolOrGym -> "Pool Gym Access"
            specs.townhouse != null -> "Private Yard Area"
            propertyTypeCategory == PropertyTypeCategory.BOARDING_HOUSE -> "Study Lounge Area"
            else -> "Community Facilities Access"
        }
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.FitnessCenter,
                label = null,
                value = facilitiesDisplay
            )
        )

        // 4. Shared Kitchen Access / Cooking (<= 3 words, no sub-heading)
        val kitchenDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.BOARDING_HOUSE -> "Light Cooking Allowed"
            PropertyTypeCategory.APARTMENT -> "Indoor Kitchen Access"
            PropertyTypeCategory.TOWNHOUSE -> "Indoor Dirty Kitchen"
            PropertyTypeCategory.CONDO, PropertyTypeCategory.STUDIO -> "Equipped Kitchenette Unit"
            else -> "Full Kitchen Access"
        }
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.Kitchen,
                label = null,
                value = kitchenDisplay
            )
        )

        // 5. Security (<= 3 words, no sub-heading)
        val securityDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.CONDO, PropertyTypeCategory.STUDIO -> "24/7 Security Guards"
            PropertyTypeCategory.TOWNHOUSE -> "Guarded Gated Village"
            PropertyTypeCategory.BOARDING_HOUSE -> "CCTV Gated Compound"
            else -> "24/7 Perimeter Security"
        }
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.Security,
                label = null,
                value = securityDisplay
            )
        )

        // 6. Utilities: Electricity Setup (<= 3 words, no sub-heading)
        val electricDisplay = if (property.utilitiesIncluded) "Electricity Bill Included" else "Own Electric Meter"
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.Bolt,
                label = null,
                value = electricDisplay
            )
        )

        // 7. Utilities: Water Setup (<= 3 words, no sub-heading)
        val waterDisplay = if (property.utilitiesIncluded) "Water Bill Included" else "Sub-metered Water Line"
        amenitiesFacilitiesItems.add(
            AccordionGridItem(
                icon = Icons.Default.WaterDrop,
                label = null,
                value = waterDisplay
            )
        )

        // 8. Air Conditioning (<= 3 words, no sub-heading)
        if (property.amenitiesJson.contains("aircon", ignoreCase = true) || property.amenitiesJson.contains("air condition", ignoreCase = true)) {
            amenitiesFacilitiesItems.add(
                AccordionGridItem(
                    icon = Icons.Default.AcUnit,
                    label = null,
                    value = "Air Conditioned Unit"
                )
            )
        }

        // 9. Balcony (<= 3 words, no sub-heading)
        if (property.amenitiesJson.contains("balcony", ignoreCase = true) || specs.condominium?.amenities?.contains("Balcony") == true) {
            amenitiesFacilitiesItems.add(
                AccordionGridItem(
                    icon = Icons.Default.Balcony,
                    label = null,
                    value = "Private Balcony Deck"
                )
            )
        }

        // 10. Laundry (<= 3 words, no sub-heading)
        if (property.amenitiesJson.contains("laundry", ignoreCase = true) || property.amenitiesJson.contains("wash", ignoreCase = true)) {
            amenitiesFacilitiesItems.add(
                AccordionGridItem(
                    icon = Icons.Default.LocalLaundryService,
                    label = null,
                    value = "Dedicated Laundry Area"
                )
            )
        }

        // ---------------------------------------------------------------------
        // HEADING 3: Rules & Lease Terms
        // (Sub-headings removed: max 3 words for complete understanding, all words visible)
        // ---------------------------------------------------------------------
        val rulesLeaseItems = mutableListOf<AccordionGridItem>()

        // 1. Pet Policies (<= 3 words, no sub-heading)
        val petPolicyDisplay = when {
            property.petPolicy.contains("Allow", ignoreCase = true) || specs.condominium?.petPolicy?.contains("Friendly", ignoreCase = true) == true -> "Small Pets Allowed"
            else -> "No Pets Allowed"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.Pets,
                label = null,
                value = petPolicyDisplay
            )
        )

        // 2. Curfews (<= 3 words, no sub-heading)
        val curfewDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.BOARDING_HOUSE -> "10PM Gate Lock"
            else -> "No Curfew Hours"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.Schedule,
                label = null,
                value = curfewDisplay
            )
        )

        // 3. Visitor Restrictions (<= 3 words, no sub-heading)
        val visitorsDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.BOARDING_HOUSE -> "Lobby Visitors Only"
            PropertyTypeCategory.CONDO -> "Guest Registration Required"
            PropertyTypeCategory.TOWNHOUSE -> "Quiet Hours Apply"
            else -> "Daytime Visitors Only"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.PersonSearch,
                label = null,
                value = visitorsDisplay
            )
        )

        // 4. HOA / Association Dues (<= 3 words, no sub-heading)
        val duesDisplay = when {
            property.associationDues > 0 -> "₱${property.associationDues.toInt()} HOA Dues"
            specs.condominium?.associationDues?.contains("Exclusive", ignoreCase = true) == true -> "Exclusive HOA Dues"
            else -> "Inclusive HOA Dues"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.ReceiptLong,
                label = null,
                value = duesDisplay
            )
        )

        // 5. Minimum Lease Requirements (<= 3 words, no sub-heading)
        val leaseDuration = if (property.leaseDurationMonths > 0) property.leaseDurationMonths else 12
        val leaseDisplay = when (leaseDuration) {
            12 -> "1 Year Minimum"
            6 -> "6 Months Minimum"
            else -> "$leaseDuration Months Minimum"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.CalendarMonth,
                label = null,
                value = leaseDisplay
            )
        )

        // 6. Payment Terms (<= 3 words, no sub-heading)
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = null,
                value = "Standard Advance Deposit"
            )
        )

        // 7. Smoking Policy (<= 3 words, no sub-heading)
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.SmokeFree,
                label = null,
                value = "Strictly No Smoking"
            )
        )

        // 8. Tenancy Policies (<= 3 words, no sub-heading)
        val policyDisplay = when (propertyTypeCategory) {
            PropertyTypeCategory.CONDO -> "Move-in Permit Required"
            PropertyTypeCategory.TOWNHOUSE -> "Strictly Residential Only"
            else -> "No Subletting Allowed"
        }
        rulesLeaseItems.add(
            AccordionGridItem(
                icon = Icons.Default.Assignment,
                label = null,
                value = policyDisplay
            )
        )

        // Return the Three Universal Categories - NO numbers and NO icons on headings
        listOf(
            PropertyDetailCategory(
                id = "unit_specs",
                title = "Unit Specifications",
                icon = null,
                subtitle = null,
                items = unitSpecsItems
            ),
            PropertyDetailCategory(
                id = "amenities_facilities",
                title = "Amenities & Facilities",
                icon = null,
                subtitle = null,
                items = amenitiesFacilitiesItems
            ),
            PropertyDetailCategory(
                id = "rules_lease_terms",
                title = "Rules & Lease Terms",
                icon = null,
                subtitle = null,
                items = rulesLeaseItems
            )
        )
    }

    // Render accordions with clean, modern spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEachIndexed { index, category ->
            ExpandableCategoryAccordion(
                title = category.title,
                icon = category.icon,
                subtitle = category.subtitle,
                initiallyExpanded = (index == 0), // First category expanded by default
                isDark = isDark,
                testTagSuffix = category.id
            ) {
                AccordionTwoColumnGrid(
                    items = category.items,
                    isDark = isDark
                )
            }
        }
    }
}
