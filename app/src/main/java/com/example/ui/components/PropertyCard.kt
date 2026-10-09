package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.HostContactInfo
import com.example.data.model.ListingTransformationEngine
import com.example.data.model.PhilippineRentalPropertyCategory
import com.example.data.model.PropertyCostHelper
import com.example.data.model.RentCostBreakdown
import com.example.data.model.getCleanLocalityDescription
import com.example.ui.theme.SoftGreenBg
import com.example.ui.theme.SoftGreenBorder
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.theme.SoftGreenDarkBorder
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.SteelBlueBorder
import com.example.ui.theme.SteelBlueDarkBg
import com.example.ui.theme.SteelBlueLightBg
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PropertyCard(
    property: PropertyEntity,
    locationDisplay: String,
    imageUrls: List<String> = emptyList(),
    primaryImageUrl: String? = null,
    isSaved: Boolean,
    onSaveToggle: () -> Unit,
    onClick: () -> Unit,
    onViewOnMap: (() -> Unit)? = null,
    onMessageHost: (() -> Unit)? = null,
    containerColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
        maximumFractionDigits = 0
    }
    val formattedRent = currencyFormat.format(property.monthlyRent).replace("PHP", "₱")

    // Estimated monthly cost (Rent + Dues + Parking + Internet + Maintenance)
    val totalEstimatedMonthly = property.monthlyRent + property.associationDues + property.parkingFees + property.maintenanceFees
    val formattedTotalCost = currencyFormat.format(totalEstimatedMonthly).replace("PHP", "₱")

    // Realistic default images if none are supplied for the listing
    val defaultFallbackImages = listOf(
        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80"
    )

    val displayImages = remember(imageUrls, primaryImageUrl) {
        when {
            imageUrls.isNotEmpty() -> imageUrls
            !primaryImageUrl.isNullOrBlank() -> listOf(primaryImageUrl)
            else -> defaultFallbackImages
        }
    }

    val pagerState = rememberPagerState(pageCount = { displayImages.size })
    val coroutineScope = rememberCoroutineScope()

    val standardCategory = remember(property.propertyType) {
        PhilippineRentalPropertyCategory.fromPropertyType(property.propertyType)
    }

    val cardBackground = containerColor ?: if (isDark) SteelBlueDarkBg else SteelBlueLightBg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Interactive Swipeable Image Carousel with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Swipeable photo pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) { page ->
                    AsyncImage(
                        model = displayImages[page],
                        contentDescription = "${property.title} photo ${page + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                // Legibility gradient overlay (top & bottom)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // Chevron Arrows (Mobile tap & Desktop hover navigation)
                if (displayImages.size > 1) {
                    if (pagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 6.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (pagerState.currentPage < displayImages.size - 1) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 6.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Top Chips (Property Type & Favorite Heart)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.92f)
                    ) {
                        Text(
                            text = "${standardCategory.targetEmoji} ${standardCategory.shortLabel}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("save_btn_${property.id}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save Property",
                            tint = if (isSaved) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Pagination Dots centered at the bottom of the image area
                if (displayImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(displayImages.size) { index ->
                            val isCurrent = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(width = if (isCurrent) 14.dp else 5.dp, height = 5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isCurrent) Color.White else Color.White.copy(alpha = 0.45f))
                            )
                        }
                    }
                }

                // Bottom-Left Image Info: Monthly Rent
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$formattedRent / month",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Body Content (Using smaller text fonts to adjust all text, and showing full without hiding)
            Column(modifier = Modifier.padding(14.dp)) {

                // Full Title (clear, high contrast on light Steel Blue background)
                Text(
                    text = property.title,
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F263B),
                    softWrap = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Map Address in a Rectangular Pill (clean contrast, sharp text)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF1E2E40) else Color.White.copy(alpha = 0.92f),
                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF2E4660) else SteelBlueBorder.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .clickable { onViewOnMap?.invoke() ?: onClick() }
                        .testTag("property_map_pin_btn_${property.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.5.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Map Pin",
                            modifier = Modifier.size(13.dp),
                            tint = SteelBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationDisplay,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF16324F),
                            softWrap = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Shortened clean description: locality, space, and nearby transit only
                Text(
                    text = property.getCleanLocalityDescription(),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF2C435A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Reusable small display panel for Rent cost break-up
 */
@Composable
fun RentCostBreakdownPanel(
    breakdown: RentCostBreakdown,
    isDark: Boolean,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
        maximumFractionDigits = 0
    }
    // Boxes below property details only have border color #4682B4 and not a fill
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, SteelBlue),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = SteelBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rent & Utility Cost Break-up",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (onClose != null) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cost line items in crisp readable fonts - only applicable details shown
            CostBreakdownItemRow(
                label = "Monthly Base Rent",
                value = "${currencyFormat.format(breakdown.monthlyRent).replace("PHP", "₱")} / month",
                isHighlight = true
            )

            // Only show details which are applicable and not zero or free; omit fixed rate descriptions
            val isWaterApplicable = breakdown.waterBilling.isNotBlank() &&
                !breakdown.waterBilling.contains("not applicable", ignoreCase = true) &&
                !breakdown.waterBilling.contains("n/a", ignoreCase = true) &&
                !breakdown.waterBilling.contains("₱0", ignoreCase = true) &&
                !breakdown.waterBilling.contains("free", ignoreCase = true) &&
                !breakdown.waterBilling.contains("included", ignoreCase = true)
            if (isWaterApplicable) {
                val cleanWater = breakdown.waterBilling
                    .replace("(fixed rate)", "", ignoreCase = true)
                    .replace("fixed rate", "", ignoreCase = true)
                    .trim()
                CostBreakdownItemRow("Water Billing", cleanWater)
            }

            val isElecApplicable = breakdown.electricityBilling.isNotBlank() &&
                !breakdown.electricityBilling.contains("not applicable", ignoreCase = true) &&
                !breakdown.electricityBilling.contains("n/a", ignoreCase = true) &&
                !breakdown.electricityBilling.contains("₱0", ignoreCase = true) &&
                !breakdown.electricityBilling.contains("free", ignoreCase = true) &&
                !breakdown.electricityBilling.contains("included", ignoreCase = true)
            if (isElecApplicable) {
                CostBreakdownItemRow("Electricity Billing", breakdown.electricityBilling)
            }

            val isInternetApplicable = breakdown.internetBilling.isNotBlank() &&
                !breakdown.internetBilling.contains("not applicable", ignoreCase = true) &&
                !breakdown.internetBilling.contains("n/a", ignoreCase = true) &&
                !breakdown.internetBilling.contains("₱0", ignoreCase = true) &&
                !breakdown.internetBilling.contains("free", ignoreCase = true) &&
                !breakdown.internetBilling.contains("included", ignoreCase = true)
            if (isInternetApplicable) {
                CostBreakdownItemRow("WiFi Internet", breakdown.internetBilling)
            }

            val isParkingApplicable = breakdown.parkingBilling.isNotBlank() &&
                !breakdown.parkingBilling.contains("no dedicated", ignoreCase = true) &&
                !breakdown.parkingBilling.contains("not applicable", ignoreCase = true) &&
                !breakdown.parkingBilling.contains("n/a", ignoreCase = true) &&
                !breakdown.parkingBilling.contains("₱0", ignoreCase = true) &&
                !breakdown.parkingBilling.contains("free", ignoreCase = true) &&
                !breakdown.parkingBilling.contains("included", ignoreCase = true)
            if (isParkingApplicable) {
                CostBreakdownItemRow("Parking Provisions", breakdown.parkingBilling)
            }

            val isMaintenanceApplicable = breakdown.maintenanceDues.isNotBlank() &&
                !breakdown.maintenanceDues.contains("no maintenance", ignoreCase = true) &&
                !breakdown.maintenanceDues.contains("₱0", ignoreCase = true) &&
                !breakdown.maintenanceDues.contains("free", ignoreCase = true) &&
                !breakdown.maintenanceDues.contains("not applicable", ignoreCase = true) &&
                !breakdown.maintenanceDues.contains("n/a", ignoreCase = true)
            if (isMaintenanceApplicable) {
                CostBreakdownItemRow("Maintenance / Dues", breakdown.maintenanceDues)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Move-in upfront total container (no fill, steel blue border)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, SteelBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Initial Move-in Cash Required",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SteelBlue
                        )
                        Text(
                            text = currencyFormat.format(breakdown.totalUpfrontMoveIn).replace("PHP", "₱"),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = SteelBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "• ${breakdown.advanceMonths}-Mo Advance (${currencyFormat.format(breakdown.advanceRentAmount).replace("PHP", "₱")}) + ${breakdown.depositMonths}-Mo Deposit (${currencyFormat.format(breakdown.securityDepositAmount).replace("PHP", "₱")})",
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (breakdown.minLeaseDuration.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏱️ ${breakdown.minLeaseDuration}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SteelBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun CostBreakdownItemRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) SteelBlue else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CardSpecItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 9.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 9.5.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

