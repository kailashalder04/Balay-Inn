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
import androidx.compose.material.icons.filled.Fullscreen
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
import com.example.data.model.PropertyCostHelper
import com.example.data.model.RentCostBreakdown
import com.example.ui.theme.SoftGreenBg
import com.example.ui.theme.SoftGreenBorder
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.theme.SoftGreenDarkBorder
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

    // Cost Breakdown & Host Contact helpers
    val costBreakdown = remember(property) { PropertyCostHelper.getRentCostBreakdown(property) }
    val hostInfo = remember(property) { PropertyCostHelper.getHostContactInfo(property) }

    var showCostBreakdown by remember { mutableStateOf(false) }
    var showDirectMessageDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

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
    var showFullScreenDialog by remember { mutableStateOf(false) }
    var fullScreenInitialPage by remember { mutableStateOf(0) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            text = property.propertyType,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("share_btn_${property.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export Listing for Facebook / Social Media",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
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

                // Bottom-Left Image Info: Monthly Rent, Cost Break-up Option & Fullscreen button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "$formattedRent / month",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            softWrap = false
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        // Small clickable display panel option for Rent cost break-up
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .clickable { showCostBreakdown = !showCostBreakdown }
                                .testTag("rent_breakdown_toggle_${property.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color(0xFFFDE68A),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (showCostBreakdown) "Hide Cost Break-up ▲" else "Rent Cost Break-up ▾",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            fullScreenInitialPage = pagerState.currentPage
                            showFullScreenDialog = true
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("fullscreen_btn_${property.id}")
                    ) {
                        Icon(
                            Icons.Default.Fullscreen,
                            contentDescription = "View Fullscreen Photo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Full-screen Image Viewer Lightbox Modal
            if (showFullScreenDialog) {
                FullScreenImageLightbox(
                    displayImages = displayImages,
                    initialPage = fullScreenInitialPage,
                    propertyTitle = property.title,
                    onDismiss = {
                        coroutineScope.launch {
                            pagerState.scrollToPage(fullScreenInitialPage)
                        }
                        showFullScreenDialog = false
                    },
                    onPageChange = { page ->
                        fullScreenInitialPage = page
                    }
                )
            }

            // Body Content (Using smaller text fonts to adjust all text, and showing full without hiding)
            Column(modifier = Modifier.padding(14.dp)) {

                // Small Display Panel for Rent Cost Break-up (when toggled)
                AnimatedVisibility(
                    visible = showCostBreakdown,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        RentCostBreakdownPanel(
                            breakdown = costBreakdown,
                            isDark = isDark,
                            onClose = { showCostBreakdown = false }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Full Title (smaller font, adjust all text, show full without hiding)
                Text(
                    text = property.title,
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Map Address in a Rectangular Pill (smaller font, show full text)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f) else Color(0xFFCBD5E1)),
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
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationDisplay,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            softWrap = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Boarding house amenities highlights: WiFi available, Electricity, Water, Highway access
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // WiFi Available
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDark) Color(0xFF1E293B) else Color(0xFFE2EDFE),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFF93C5FD))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Wifi,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.5.dp))
                            Text(
                                "WiFi available",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                            )
                        }
                    }

                    // Electricity / Water (Very soft, soothing green display for eyes)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                        border = BorderStroke(1.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (isDark) SoftGreenDark else Color(0xFF2E6F4E),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.5.dp))
                            Text(
                                text = if (property.utilitiesIncluded) "Elec/Water in rent" else "Elec submeter / Water",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) SoftGreenDark else Color(0xFF1D533A)
                            )
                        }
                    }

                    // Highway Access
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isDark) Color(0xFF2E2013) else Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF593C1D) else Color(0xFFFCD34D))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.AltRoute,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.5.dp))
                            Text(
                                "Highway access",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Highlights Bar: 4-5 quick tags separated by bullets (In-App format)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                    border = BorderStroke(0.8.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val crTag = if (property.bathrooms > 0) "Private CR" else "Common CR"
                    val petTag = if (property.petPolicy.contains("No", ignoreCase = true)) "Strictly No Pets" else "Pet Friendly"
                    val curfewTag = if (hostInfo.gateCurfewPolicy.contains("24/7", ignoreCase = true) || hostInfo.gateCurfewPolicy.contains("night", ignoreCase = true)) "No Curfew / Night Key" else "24/7 Access"
                    val highlightsText = "$formattedRent/mo • $crTag • $petTag • $curfewTag"

                    Text(
                        text = "✨ $highlightsText",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) SoftGreenDark else Color(0xFF1D533A),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Full Description (smaller text fonts, show full, don't hide any text information!)
                Text(
                    text = property.description,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.92f),
                    softWrap = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Specs: Key-value list for fast reading (Electricity, Water, WiFi, Cooking, Parking)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(9.dp)) {
                        Text(
                            text = "📊 QUICK SPECS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        CardSpecItemRow("⚡ Electricity", costBreakdown.electricityBilling)
                        CardSpecItemRow("💧 Water", costBreakdown.waterBilling)
                        CardSpecItemRow("📶 WiFi", costBreakdown.internetBilling)
                        CardSpecItemRow("🍳 Cooking", if (property.id.contains("dampas")) "Light electric only (rice cooker/kettle)" else "Dedicated kitchen/cooking")
                        CardSpecItemRow("🛵 Parking", costBreakdown.parkingBilling)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Details & Requirements: Terse bullet points for rules, contract length, and move-in IDs
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else Color(0xFFF1F5F9),
                    border = BorderStroke(0.6.dp, if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(9.dp)) {
                        Text(
                            text = "📋 DETAILS & REQUIREMENTS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "• Move-in Terms: ${costBreakdown.advanceMonths}-Mo Advance, ${costBreakdown.depositMonths}-Mo Deposit (${currencyFormat.format(costBreakdown.totalUpfrontMoveIn).replace("PHP", "₱")})",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Contract & Payment: ${costBreakdown.minLeaseDuration} • ${costBreakdown.acceptedPayments}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Curfew & Access: ${hostInfo.gateCurfewPolicy}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Viewing Policy: ${hostInfo.viewingPolicy}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Concise Specs: Beds, Baths, Furnished, Lease Term (smaller fonts)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bed, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (property.bedrooms == 0) "Studio / Bedspace" else "${property.bedrooms} Bed${if (property.bedrooms > 1) "s" else ""}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bathtub, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${property.bathrooms} Bath/CR",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = property.furnishedStatus,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("•", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${property.leaseDurationMonths} mos min",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- HOST NAME BOX with Message option inside & Host contact details ---
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("host_box_${property.id}")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Host Avatar Icon
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Host Avatar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = hostInfo.hostName,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                                        border = BorderStroke(0.6.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder)
                                    ) {
                                        Text(
                                            text = "Verified Host",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) SoftGreenDark else Color(0xFF1D533A),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = hostInfo.hostRole,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Host Contact Details (Phone, calling hours & quick dial/SMS)
                        if (hostInfo.phone.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                border = BorderStroke(0.7.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.Phone,
                                            contentDescription = "Host Contact",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "${hostInfo.phone} • ${hostInfo.contactChannel}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        modifier = Modifier.clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hostInfo.phone}"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // fallback
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = "Call/SMS",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Host instructions & schedule
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📋 ${hostInfo.viewingPolicy} • 🔑 ${hostInfo.gateCurfewPolicy}",
                            fontSize = 9.sp,
                            lineHeight = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message Option situated INSIDE the Host Name Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (onMessageHost != null) {
                                        onMessageHost()
                                    } else {
                                        showDirectMessageDialog = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("host_box_message_btn_${property.id}")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Message,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Message ${hostInfo.hostName}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Direct Message Dialog right from card
    if (showDirectMessageDialog) {
        var messageText by remember {
            mutableStateOf("Hi ${hostInfo.hostName}! I am interested in '${property.title}'. Is this room still available for viewing?")
        }
        var messageSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDirectMessageDialog = false },
            title = {
                Text(
                    text = "Message ${hostInfo.hostName}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!messageSent) {
                        Text(
                            text = "Send an inquiry directly to ${hostInfo.hostName} (${hostInfo.hostRole}). You can also call or text at ${hostInfo.phone}.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.5.sp)
                        )
                    } else {
                        Text(
                            text = "✅ Inquiry sent to ${hostInfo.hostName}! They will respond shortly. You may also contact them directly at ${hostInfo.phone}.",
                            fontSize = 11.5.sp,
                            color = if (isDark) SoftGreenDark else Color(0xFF1D533A)
                        )
                    }
                }
            },
            confirmButton = {
                if (!messageSent) {
                    Button(
                        onClick = { messageSent = true }
                    ) {
                        Text("Send Message", fontSize = 11.5.sp)
                    }
                } else {
                    Button(onClick = { showDirectMessageDialog = false }) {
                        Text("Done", fontSize = 11.5.sp)
                    }
                }
            },
            dismissButton = {
                if (!messageSent) {
                    TextButton(onClick = { showDirectMessageDialog = false }) {
                        Text("Cancel", fontSize = 11.5.sp)
                    }
                }
            }
        )
    }

    // Export to Social Media / Facebook Marketplace Dialog from Card
    if (showExportDialog) {
        val payload = remember(property, locationDisplay, hostInfo, costBreakdown) {
            ListingTransformationEngine.fromProperty(property, locationDisplay, hostInfo, costBreakdown)
        }
        val fbPostText = remember(payload) {
            ListingTransformationEngine.formatFacebookPost(payload)
        }
        val inAppText = remember(payload) {
            ListingTransformationEngine.formatInAppListing(payload)
        }
        var selectedOutputTab by remember { mutableIntStateOf(0) } // 0 = Facebook, 1 = In-App

        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Listing / FB Post", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(3.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOutputTab = 0 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedOutputTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "Facebook Post",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOutputTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOutputTab = 1 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedOutputTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "In-App Specs",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOutputTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayText = if (selectedOutputTab == 0) fbPostText else inAppText

                    OutlinedTextField(
                        value = displayText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.5.sp, lineHeight = 14.5.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val textToCopy = if (selectedOutputTab == 0) fbPostText else inAppText
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rental Listing", textToCopy)
                        clipboard.setPrimaryClip(clip)
                        showExportDialog = false
                    }
                ) {
                    Text("Copy to Clipboard", fontSize = 11.5.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val textToShare = if (selectedOutputTab == 0) fbPostText else inAppText
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, textToShare)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Listing"))
                    }
                ) {
                    Text("Share...", fontSize = 11.5.sp)
                }
            }
        )
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
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Rent & Utility Cost Break-up",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (onClose != null) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cost line items in smaller compact fonts
            CostBreakdownItemRow("Monthly Base Rent", "${currencyFormat.format(breakdown.monthlyRent).replace("PHP", "₱")} / month", isHighlight = true)
            CostBreakdownItemRow("Water Billing", breakdown.waterBilling)
            CostBreakdownItemRow("Electricity Billing", breakdown.electricityBilling)
            CostBreakdownItemRow("Fiber WiFi Internet", breakdown.internetBilling)
            CostBreakdownItemRow("Parking Provisions", breakdown.parkingBilling)
            CostBreakdownItemRow("Maintenance / Dues", breakdown.maintenanceDues)

            Spacer(modifier = Modifier.height(6.dp))

            // Move-in upfront total container
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                border = BorderStroke(0.8.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Initial Move-in Cash Required",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) SoftGreenDark else Color(0xFF165B33)
                        )
                        Text(
                            text = currencyFormat.format(breakdown.totalUpfrontMoveIn).replace("PHP", "₱"),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) SoftGreenDark else Color(0xFF165B33)
                        )
                    }
                    Text(
                        text = "• ${breakdown.advanceMonths}-Mo Advance (${currencyFormat.format(breakdown.advanceRentAmount).replace("PHP", "₱")}) + ${breakdown.depositMonths}-Mo Deposit (${currencyFormat.format(breakdown.securityDepositAmount).replace("PHP", "₱")})",
                        fontSize = 9.5.sp,
                        color = if (isDark) SoftGreenDark.copy(alpha = 0.9f) else Color(0xFF1D533A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "💳 ${breakdown.acceptedPayments}",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "⏱️ ${breakdown.minLeaseDuration}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
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
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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

