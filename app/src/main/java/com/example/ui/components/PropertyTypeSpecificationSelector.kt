package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhilippineRentalPropertyCategory

/**
 * Dedicated dynamic selection form component for landlords posting rental properties.
 *
 * Dynamically switches and displays:
 * 1. Primary property type tabs (Bed Space, Studio Unit, Apartment / Flat, Condominium, and optional House).
 * 2. Demographic target marker (Students, Solo Renters, Small Families, High-Earning Expats).
 * 3. Specific expected amenities for the chosen property type with easy toggle chips.
 * 4. Specific utility options (Water billing setup, localized Electricity provider, Internet/WiFi).
 * 5. House rules & security protocols (Curfew & gate access, cooking guidelines, security guard tier,
 *    visitor policy, pet limits, and parking provisions).
 * 6. Market standards guidance and locality notes for Philippine rentals.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyTypeSpecificationSelector(
    selectedCategory: PhilippineRentalPropertyCategory,
    onCategorySelected: (PhilippineRentalPropertyCategory) -> Unit,
    // Amenities selection
    selectedAmenities: List<String>,
    onAmenityToggled: (String) -> Unit,
    // Utilities options
    selectedWaterOption: String,
    onWaterOptionSelected: (String) -> Unit,
    selectedElectricityOption: String,
    onElectricityOptionSelected: (String) -> Unit,
    selectedInternetOption: String,
    onInternetOptionSelected: (String) -> Unit,
    // House rules & security
    selectedCurfewOption: String,
    onCurfewOptionSelected: (String) -> Unit,
    selectedSecurityOption: String,
    onSecurityOptionSelected: (String) -> Unit,
    selectedCookingOption: String,
    onCookingOptionSelected: (String) -> Unit,
    selectedVisitorsOption: String,
    onVisitorsOptionSelected: (String) -> Unit,
    selectedPetOption: String,
    onPetOptionSelected: (String) -> Unit,
    selectedParkingOption: String,
    onParkingOptionSelected: (String) -> Unit,
    // Contextual locality
    provinceId: String = "prov_bohol",
    modifier: Modifier = Modifier
) {
    val localDU = PhilippineRentalPropertyCategory.getLocalElectricProvider(provinceId)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("property_type_specification_selector"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with standards pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Property Type & Market Standards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Philippine Rental Market Standard Classification",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "🇵🇭 PH Standards",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Category Selection Carousel / Horizontal Tabs
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Select Property Category:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhilippineRentalPropertyCategory.ALL_CATEGORIES.forEach { category ->
                        val isSelected = selectedCategory == category
                        val categoryBgColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            label = "cat_bg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                            label = "cat_border"
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = categoryBgColor,
                            border = BorderStroke(if (isSelected) 1.8.dp else 1.dp, borderColor),
                            modifier = Modifier
                                .clickable { onCategorySelected(category) }
                                .testTag("select_category_${category.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.targetEmoji,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = category.shortLabel,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = category.targetMarket,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Dynamic Specification Section with Animated Visibility
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header for selected category with Target Demographics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedCategory.targetEmoji,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedCategory.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Target Market Banner
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("👤", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Ideal Target Market:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedCategory.targetMarket,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 1. SPECIFIC EXPECTED AMENITIES
                    SpecificationGroup(
                        title = "Expected Amenities for ${selectedCategory.shortLabel}",
                        subtitle = "Select amenities present in your unit (standard amenities pre-selected):",
                        icon = Icons.Default.CleaningServices
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            selectedCategory.expectedAmenities.forEach { amenity ->
                                val isChecked = selectedAmenities.contains(amenity)
                                FilterChip(
                                    selected = isChecked,
                                    onClick = { onAmenityToggled(amenity) },
                                    label = {
                                        Text(
                                            text = amenity,
                                            fontSize = 11.sp,
                                            fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    },
                                    leadingIcon = if (isChecked) {
                                        {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.testTag("amenity_chip_${amenity.replace(" ", "_").lowercase()}")
                                )
                            }
                        }
                    }

                    // 2. SPECIFIC UTILITIES SETUP
                    SpecificationGroup(
                        title = "Utilities Setup & Billing",
                        subtitle = "Matching Philippine standards for ${selectedCategory.shortLabel}:",
                        icon = Icons.Default.Bolt
                    ) {
                        // Water Option
                        OptionRowSection(
                            label = "Water Setup:",
                            icon = Icons.Default.WaterDrop,
                            options = selectedCategory.waterOptions,
                            selectedOption = selectedWaterOption,
                            onOptionSelected = onWaterOptionSelected,
                            testTagPrefix = "water_opt"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Electricity Option
                        val resolvedElecOptions = selectedCategory.electricityOptions.map {
                            it.replace("VECO/MERALCO/CENECO/BOHECO", localDU)
                        }
                        val currentSelectedElec = if (selectedElectricityOption.contains("VECO/MERALCO/CENECO/BOHECO")) {
                            selectedElectricityOption.replace("VECO/MERALCO/CENECO/BOHECO", localDU)
                        } else {
                            selectedElectricityOption
                        }

                        OptionRowSection(
                            label = "Electricity ($localDU Region):",
                            icon = Icons.Default.Bolt,
                            options = resolvedElecOptions,
                            selectedOption = currentSelectedElec,
                            onOptionSelected = onElectricityOptionSelected,
                            testTagPrefix = "elec_opt"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Internet Option
                        OptionRowSection(
                            label = "Internet / WiFi:",
                            icon = Icons.Default.Wifi,
                            options = selectedCategory.internetOptions,
                            selectedOption = selectedInternetOption,
                            onOptionSelected = onInternetOptionSelected,
                            testTagPrefix = "internet_opt"
                        )
                    }

                    // 3. SPECIFIC HOUSE RULES & SECURITY
                    SpecificationGroup(
                        title = "House Rules & Security Protocols",
                        subtitle = "Standard rules for tenant compliance & expectations:",
                        icon = Icons.Default.Lock
                    ) {
                        // Curfew & Gate Access
                        OptionRowSection(
                            label = "Curfew & Gate Access:",
                            options = selectedCategory.curfewOptions,
                            selectedOption = selectedCurfewOption,
                            onOptionSelected = onCurfewOptionSelected,
                            testTagPrefix = "curfew_opt"
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Cooking Guidelines
                        OptionRowSection(
                            label = "Cooking Rules:",
                            options = selectedCategory.cookingOptions,
                            selectedOption = selectedCookingOption,
                            onOptionSelected = onCookingOptionSelected,
                            testTagPrefix = "cooking_opt"
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Security Setup
                        OptionRowSection(
                            label = "Security Guard & Caretaker:",
                            options = selectedCategory.securityOptions,
                            selectedOption = selectedSecurityOption,
                            onOptionSelected = onSecurityOptionSelected,
                            testTagPrefix = "security_opt"
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Visitors Policy
                        OptionRowSection(
                            label = "Visitors Policy:",
                            options = selectedCategory.visitorsOptions,
                            selectedOption = selectedVisitorsOption,
                            onOptionSelected = onVisitorsOptionSelected,
                            testTagPrefix = "visitors_opt"
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Pet Policy
                        OptionRowSection(
                            label = "Pet Policy:",
                            options = selectedCategory.petPolicyOptions,
                            selectedOption = selectedPetOption,
                            onOptionSelected = onPetOptionSelected,
                            testTagPrefix = "pet_opt"
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Parking Provision
                        OptionRowSection(
                            label = "Parking Provision:",
                            options = selectedCategory.parkingOptions,
                            selectedOption = selectedParkingOption,
                            onOptionSelected = onParkingOptionSelected,
                            testTagPrefix = "parking_opt"
                        )
                    }

                    // Locality Notes & Guidance
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Market Tip: ${selectedCategory.localityNotes}",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecificationGroup(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = subtitle,
            fontSize = 10.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun OptionRowSection(
    label: String,
    icon: ImageVector? = null,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    testTagPrefix: String = "opt"
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedOption == option
                FilterChip(
                    selected = isSelected,
                    onClick = { onOptionSelected(option) },
                    label = {
                        Text(
                            text = option,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("${testTagPrefix}_${option.take(10).replace(" ", "_").lowercase()}")
                )
            }
        }
    }
}
