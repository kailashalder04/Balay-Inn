package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.data.model.PropertyCostHelper
import com.example.ui.theme.SteelBlue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonMatrixSheet(
    properties: List<PropertyEntity>,
    onDismiss: () -> Unit,
    onRemoveProperty: (String) -> Unit,
    onClearAll: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    imagesMap: Map<String, List<String>> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH")).apply {
            maximumFractionDigits = 0
        }
    }

    val surfaceBg = if (isDark) Color(0xFF0F1A28) else Color(0xFFF9FBFC)
    val cardBg = if (isDark) Color(0xFF162538) else Color(0xFFFFFFFF)
    val headerCellBg = if (isDark) Color(0xFF1B2C42) else Color(0xFFEEF5FB)
    val rowAltBg = if (isDark) Color(0xFF132032) else Color(0xFFF4F8FC)
    val borderCol = if (isDark) Color(0xFF283F5A) else Color(0xFFD6E3EF)
    val textPrimary = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = surfaceBg,
        dragHandle = null,
        modifier = modifier.testTag("comparison_matrix_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(bottom = 16.dp)
        ) {
            // Header Bar
            Surface(
                color = cardBg,
                shadowElevation = 2.dp,
                border = BorderStroke(0.8.dp, borderCol)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SteelBlue.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.AutoMirrored.Filled.CompareArrows,
                                    contentDescription = null,
                                    tint = SteelBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Side-by-Side Comparison",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Text(
                                text = "${properties.size} ${if (properties.size == 1) "listing" else "listings"} selected (Max 4)",
                                fontSize = 11.5.sp,
                                color = textSecondary
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (properties.isNotEmpty()) {
                            TextButton(
                                onClick = onClearAll,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "Clear All",
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textSecondary)
                        }
                    }
                }
            }

            if (properties.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No properties selected for comparison",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the 'Compare' button on any property card to compare them side-by-side.",
                            fontSize = 12.5.sp,
                            color = textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Scrollable Matrix Grid
                val horizontalScrollState = rememberScrollState()
                val verticalScrollState = rememberScrollState()

                val columnWidth = if (properties.size == 1) 280.dp else 230.dp
                val labelColumnWidth = 140.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        // Left Column: Attribute Labels
                        Column(
                            modifier = Modifier
                                .width(labelColumnWidth)
                                .padding(start = 12.dp, end = 6.dp)
                        ) {
                            // Property cards header spacer (same height as top property header cards)
                            Box(
                                modifier = Modifier
                                    .width(labelColumnWidth)
                                    .height(230.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = headerCellBg,
                                    border = BorderStroke(0.8.dp, borderCol),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "Features & Details",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // Comparison Criteria Labels
                            MatrixSectionHeader("RENT & FINANCIALS", isDark)
                            MatrixLabelRow("Monthly Rent", 42.dp, textSecondary)
                            MatrixLabelRow("Upfront Move-in", 42.dp, textSecondary)
                            MatrixLabelRow("Advance Rent", 38.dp, textSecondary)
                            MatrixLabelRow("Security Deposit", 38.dp, textSecondary)
                            MatrixLabelRow("Association Dues", 38.dp, textSecondary)

                            MatrixSectionHeader("TERMS & AVAILABILITY", isDark)
                            MatrixLabelRow("Lease Duration", 42.dp, textSecondary)
                            MatrixLabelRow("Move-In Date", 42.dp, textSecondary)
                            MatrixLabelRow("Target Market", 38.dp, textSecondary)

                            MatrixSectionHeader("SPACE & CAPACITY", isDark)
                            MatrixLabelRow("Property Category", 42.dp, textSecondary)
                            MatrixLabelRow("Bedrooms / Beds", 38.dp, textSecondary)
                            MatrixLabelRow("Bathrooms / CR", 38.dp, textSecondary)
                            MatrixLabelRow("Floor Area", 38.dp, textSecondary)
                            MatrixLabelRow("Furnishing", 38.dp, textSecondary)

                            MatrixSectionHeader("HOUSE RULES & ACCESS", isDark)
                            MatrixLabelRow("Curfew Hours", 42.dp, textSecondary)
                            MatrixLabelRow("Gender Policy", 38.dp, textSecondary)
                            MatrixLabelRow("Visitors Policy", 38.dp, textSecondary)
                            MatrixLabelRow("Pet Policy", 38.dp, textSecondary)
                            MatrixLabelRow("Parking Available", 38.dp, textSecondary)

                            MatrixSectionHeader("UTILITIES & AMENITIES", isDark)
                            MatrixLabelRow("Water Utility", 38.dp, textSecondary)
                            MatrixLabelRow("Electricity", 38.dp, textSecondary)
                            MatrixLabelRow("Internet / WiFi", 38.dp, textSecondary)
                            MatrixLabelRow("Air Conditioning", 38.dp, textSecondary)
                            MatrixLabelRow("Cooking Allowed", 38.dp, textSecondary)

                            MatrixSectionHeader("LOCATION & CONTACT", isDark)
                            MatrixLabelRow("Locality / City", 42.dp, textSecondary)
                            MatrixLabelRow("Verification Level", 38.dp, textSecondary)
                            MatrixLabelRow("Host Role", 38.dp, textSecondary)
                            MatrixLabelRow("Action", 54.dp, textSecondary)
                        }

                        // Property Columns
                        properties.forEachIndexed { colIndex, property ->
                            val imageUrl = imagesMap[property.id]?.firstOrNull() ?:
                                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&auto=format&fit=crop&q=80"
                            val standardCategory = PhilippineRentalPropertyCategory.fromPropertyType(property.propertyType)
                            val upfrontTotal = PropertyCostHelper.getRentCostBreakdown(property).totalUpfrontMoveIn

                            Column(
                                modifier = Modifier
                                    .width(columnWidth)
                                    .padding(horizontal = 4.dp)
                            ) {
                                // Property Header Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(230.dp)
                                        .padding(bottom = 6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                    border = BorderStroke(1.dp, borderCol)
                                ) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(115.dp)
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = property.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            IconButton(
                                                onClick = { onRemoveProperty(property.id) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(26.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Remove",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            if (property.isPlatformVerified()) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomStart)
                                                        .padding(6.dp)
                                                ) {
                                                    VerifiedPillBadge(isDark = isDark)
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = property.title,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textPrimary,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 15.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${currencyFormat.format(property.monthlyRent).replace("PHP", "₱")} / mo",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = SteelBlue
                                            )
                                            Text(
                                                text = standardCategory.shortLabel,
                                                fontSize = 10.5.sp,
                                                color = textSecondary
                                            )
                                        }
                                    }
                                }

                                // Financials Values
                                MatrixSectionSpacer()
                                MatrixValueRow(
                                    value = "${currencyFormat.format(property.monthlyRent).replace("PHP", "₱")} / mo",
                                    height = 42.dp,
                                    isHighlight = true,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = SteelBlue,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = "${currencyFormat.format(upfrontTotal).replace("PHP", "₱")} total",
                                    height = 42.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textPrimary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = "${property.advanceRentMonths} Month (${currencyFormat.format(property.monthlyRent * property.advanceRentMonths).replace("PHP", "₱")})",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = "${property.securityDepositMonths} Months (${currencyFormat.format(property.monthlyRent * property.securityDepositMonths).replace("PHP", "₱")})",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = if (property.associationDues > 0) "${currencyFormat.format(property.associationDues).replace("PHP", "₱")} / mo" else "None / Included",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )

                                // Terms & Availability Values
                                MatrixSectionSpacer()
                                val leaseDisplay = when {
                                    property.leaseDurationMonths <= 3 -> "Flexible (${property.leaseDurationMonths} mos)"
                                    property.leaseDurationMonths == 6 -> "Semester (6 mos)"
                                    property.leaseDurationMonths == 12 -> "1 Year (12 mos)"
                                    else -> "${property.leaseDurationMonths} Months"
                                }
                                MatrixValueRow(
                                    value = leaseDisplay,
                                    height = 42.dp,
                                    isHighlight = property.leaseDurationMonths <= 6,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (property.leaseDurationMonths <= 6) Color(0xFF16A34A) else textPrimary,
                                    borderCol = borderCol
                                )
                                val moveInDisplay = if (property.availabilityDate.contains("Immediate", ignoreCase = true) || property.availabilityDate.contains("Now", ignoreCase = true)) {
                                    "⚡ Immediate / Ready"
                                } else {
                                    property.availabilityDate
                                }
                                MatrixValueRow(
                                    value = moveInDisplay,
                                    height = 42.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textPrimary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = standardCategory.targetEmoji + " " + standardCategory.shortLabel,
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )

                                // Space & Capacity Values
                                MatrixSectionSpacer()
                                MatrixValueRow(
                                    value = property.propertyType,
                                    height = 42.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textPrimary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = if (property.bedrooms == 0) "Studio / Bedspace" else "${property.bedrooms} Bedroom(s)",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = "${property.bathrooms} CR / Bath",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = if (property.floorAreaSqm > 0) "${property.floorAreaSqm} sqm" else "Approx. standard",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = property.furnishedStatus,
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )

                                // House Rules & Access
                                MatrixSectionSpacer()
                                val curfewDisplay = if (property.description.contains("curfew", ignoreCase = true)) {
                                    val regex = "(\\d{1,2}(?::\\d{2})?\\s*(?:AM|PM|am|pm))".toRegex()
                                    val match = regex.find(property.description)
                                    match?.value ?: "Has Curfew"
                                } else {
                                    "No Curfew (24/7 Access)"
                                }
                                MatrixValueRow(
                                    value = curfewDisplay,
                                    height = 42.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textPrimary,
                                    borderCol = borderCol
                                )
                                val genderPolicy = when {
                                    property.title.contains("Ladies", ignoreCase = true) || property.description.contains("female", ignoreCase = true) -> "Female Only"
                                    property.title.contains("Men", ignoreCase = true) || property.description.contains("male", ignoreCase = true) -> "Male Only"
                                    else -> "All Genders / Mixed"
                                }
                                MatrixValueRow(
                                    value = genderPolicy,
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                val visitorPolicy = if (property.description.contains("no visitors", ignoreCase = true)) {
                                    "No Visitors"
                                } else if (property.description.contains("visitors allowed", ignoreCase = true) || property.description.contains("common area", ignoreCase = true)) {
                                    "Allowed in Lobby"
                                } else {
                                    "Standard Guidelines"
                                }
                                MatrixValueRow(
                                    value = visitorPolicy,
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = property.petPolicy,
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = if (property.parkingAvailable) "Yes (${if (property.parkingFees > 0) "${currencyFormat.format(property.parkingFees).replace("PHP", "₱")}/mo" else "Free"})" else "No Dedicated",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )

                                // Utilities & Amenities
                                MatrixSectionSpacer()
                                val waterIncluded = property.utilitiesIncluded || property.description.contains("water included", ignoreCase = true)
                                MatrixValueRow(
                                    value = if (waterIncluded) "✓ Included in Rent" else "Sub-metered / Billed",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (waterIncluded) Color(0xFF16A34A) else textSecondary,
                                    borderCol = borderCol
                                )
                                val elecIncluded = property.utilitiesIncluded || property.description.contains("electricity included", ignoreCase = true)
                                MatrixValueRow(
                                    value = if (elecIncluded) "✓ Included in Rent" else "Own Sub-meter",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (elecIncluded) Color(0xFF16A34A) else textSecondary,
                                    borderCol = borderCol
                                )
                                val wifiIncluded = property.internetIncluded || property.amenitiesJson.contains("wifi", ignoreCase = true)
                                MatrixValueRow(
                                    value = if (wifiIncluded) "✓ High-speed WiFi" else "Own Provision",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (wifiIncluded) Color(0xFF16A34A) else textSecondary,
                                    borderCol = borderCol
                                )
                                val hasAircon = property.amenitiesJson.contains("aircon", ignoreCase = true) || property.description.contains("air condition", ignoreCase = true)
                                MatrixValueRow(
                                    value = if (hasAircon) "✓ Air-conditioned" else "Fan-ventilated",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (hasAircon) SteelBlue else textSecondary,
                                    borderCol = borderCol
                                )
                                val cookingAllowed = !property.description.contains("no cooking", ignoreCase = true)
                                MatrixValueRow(
                                    value = if (cookingAllowed) "✓ Cooking Allowed" else "No Cooking Allowed",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (cookingAllowed) textSecondary else Color(0xFFDC2626),
                                    borderCol = borderCol
                                )

                                // Location & Action
                                MatrixSectionSpacer()
                                val locality = "${property.neighborhood.ifBlank { property.nearbyLandmark }.ifBlank { "University Belt" }}"
                                MatrixValueRow(
                                    value = locality,
                                    height = 42.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textPrimary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = if (property.isPlatformVerified()) "Level ${property.verificationLevel} (Verified)" else "Unverified",
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = if (property.isPlatformVerified()) Color(0xFF16A34A) else textSecondary,
                                    borderCol = borderCol
                                )
                                MatrixValueRow(
                                    value = property.posterRole.ifBlank { "Property Owner" },
                                    height = 38.dp,
                                    bgColor = if (colIndex % 2 == 1) rowAltBg else cardBg,
                                    textColor = textSecondary,
                                    borderCol = borderCol
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Button(
                                        onClick = {
                                            onDismiss()
                                            onNavigateToDetail(property.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SteelBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("View Details", fontSize = 11.5.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixSectionHeader(title: String, isDark: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(top = 10.dp, bottom = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = title,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDark) Color(0xFF7CB6E4) else SteelBlue,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun MatrixSectionSpacer() {
    Spacer(modifier = Modifier.height(30.dp))
}

@Composable
private fun MatrixLabelRow(
    label: String,
    height: androidx.compose.ui.unit.Dp,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun MatrixValueRow(
    value: String,
    height: androidx.compose.ui.unit.Dp,
    bgColor: Color,
    textColor: Color,
    borderCol: Color,
    isHighlight: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(0.6.dp, borderCol.copy(alpha = 0.7f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                fontSize = if (isHighlight) 12.sp else 11.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 14.sp
            )
        }
    }
}
