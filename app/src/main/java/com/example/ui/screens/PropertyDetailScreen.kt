package com.example.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.BoardingHouseListing
import com.example.data.model.GenderRule
import com.example.data.model.InquiryType
import com.example.data.model.LivingSpecifications
import com.example.data.model.ListingTransformationEngine
import com.example.data.model.PropertyCostHelper
import com.example.data.model.RoomType
import com.example.data.model.VerificationChecks
import com.example.data.model.toBoardingHouseListing
import com.example.ui.components.BoardingHouseImagePager
import com.example.ui.components.NearbyDestinationSection
import com.example.ui.components.RentCostBreakdownPanel
import com.example.ui.theme.SoftGreenBg
import com.example.ui.theme.SoftGreenBorder
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.theme.SoftGreenDarkBorder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.PropertyEntity
import com.example.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PropertyDetailScreen(
    propertyId: String,
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val propertyFlow = remember(propertyId) { viewModel.repository.getPropertyById(propertyId) }
    val property by propertyFlow.collectAsState(initial = null)
    val isSaved by viewModel.isPropertySaved(propertyId).collectAsState(initial = false)
    val images by remember(propertyId) { viewModel.getImagesForProperty(propertyId) }.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog States
    var showViewingDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showExportPostDialog by remember { mutableStateOf(false) }

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading property details...")
        }
        return
    }

    val prop = property!!
    val locationDisplay = viewModel.getLocationDisplay(prop)

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val costBreakdown = remember(prop) { PropertyCostHelper.getRentCostBreakdown(prop) }
    val hostInfo = remember(prop) { PropertyCostHelper.getHostContactInfo(prop) }
    var showCostBreakdown by remember { mutableStateOf(false) }

    val defaultFallbackImages = listOf(
        "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=1200&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=1200&auto=format&fit=crop&q=80"
    )

    val displayImages = remember(images) {
        val urls = images.map { it.imageUrl }.filter { it.isNotBlank() }
        if (urls.isNotEmpty()) urls else defaultFallbackImages
    }

    val boardingHouse = remember(prop, hostInfo, displayImages) {
        prop.toBoardingHouseListing(hostPhone = hostInfo.phone, photosList = displayImages)
    }
    var selectedRoomForInquiry by remember { mutableStateOf<RoomType?>(null) }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val formattedRent = currencyFormat.format(prop.monthlyRent).replace("PHP", "₱")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prop.title, maxLines = 1, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showExportPostDialog = true },
                        modifier = Modifier.testTag("detail_share_social_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export Listing for Social Media")
                    }
                    IconButton(
                        onClick = { viewModel.toggleSave(prop.id) },
                        modifier = Modifier.testTag("detail_save_btn")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Flag, contentDescription = "Report Listing", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val isNarrow = maxWidth < 380.dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = if (isNarrow) 10.dp else 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isNarrow) 6.dp else 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$formattedRent / mo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = if (prop.utilitiesIncluded) "Elec & water included" else "Submetered / Water",
                                fontSize = if (isNarrow) 10.sp else 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = { showViewingDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = if (isNarrow) 14.dp else 20.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("detail_request_viewing_btn")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request Viewing", maxLines = 1, softWrap = false, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Photos Carousel / Gallery using BoardingHouseImagePager component
            item {
                BoardingHouseImagePager(
                    images = displayImages,
                    propertyTitle = prop.title,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 1. Title, Type, Rent, Location Pin, and Utility Badges
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = prop.propertyType,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$formattedRent / month",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .clickable { showCostBreakdown = !showCostBreakdown }
                                    .testTag("detail_rent_breakdown_toggle")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (showCostBreakdown) "Hide Cost Break-up ▲" else "Rent Cost Break-up ▾",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Small Display Panel for Rent cost break-up
                    AnimatedVisibility(
                        visible = showCostBreakdown,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            RentCostBreakdownPanel(
                                breakdown = costBreakdown,
                                isDark = isSystemInDarkTheme(),
                                onClose = { showCostBreakdown = false }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = prop.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Boarding House Real-time Bed Availability & Capacity & Gender Rule Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (boardingHouse.totalAvailableBeds > 0) {
                                if (isDark) SoftGreenDarkBg else SoftGreenBg
                            } else {
                                if (isDark) Color(0xFF332020) else Color(0xFFFEE2E2)
                            },
                            border = BorderStroke(
                                0.8.dp,
                                if (boardingHouse.totalAvailableBeds > 0) {
                                    if (isDark) SoftGreenDarkBorder else SoftGreenBorder
                                } else {
                                    if (isDark) Color(0xFF7F1D1D) else Color(0xFFFCA5A5)
                                }
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (boardingHouse.totalAvailableBeds > 0) Color(0xFF16A34A) else Color(0xFFDC2626),
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${boardingHouse.totalAvailableBeds} of ${boardingHouse.totalCapacity} beds available • ${boardingHouse.lastAvailabilityUpdate}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (boardingHouse.totalAvailableBeds > 0) {
                                        if (isDark) SoftGreenDark else Color(0xFF15803D)
                                    } else {
                                        if (isDark) Color(0xFFF87171) else Color(0xFFB91C1C)
                                    }
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = boardingHouse.primaryGenderRule.badge,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Map Address in a Rectangular Pill (clearer, not too bright)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f) else Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f) else Color(0xFFCBD5E1)),
                        modifier = Modifier.testTag("detail_map_pin_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Map Pin",
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = locationDisplay,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Utility and Highway Highlights (clearer, defined, comfortable contrast — not too bright!)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "WiFi available",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFF93C5FD) else Color(0xFF1E3A8A)
                                )
                            }
                        }

                        // Electricity / Water (Very soft, eye-friendly green display)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                            border = BorderStroke(1.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (isDark) SoftGreenDark else Color(0xFF2E6F4E),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (prop.utilitiesIncluded) "Elec/Water in rent" else "Elec submeter / Water",
                                    fontSize = 11.sp,
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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.AltRoute,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Highway access",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFFDE68A) else Color(0xFF78350F)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Boarding House Main Description (Focused strictly on property space, locality, nearby destinations, and transit)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Text(
                            text = boardingHouse.fullDescription,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 3. Room & Bedspace Configurations (Boarding House Spec)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    // Overview Specs bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            SpecColumn(icon = Icons.Default.Bed, label = "Capacity", value = "${boardingHouse.totalCapacity} Beds")
                            SpecColumn(icon = Icons.Default.Bathtub, label = "Bathroom", value = "${prop.bathrooms} Bath/CR")
                            SpecColumn(icon = Icons.Default.Home, label = "Furnished", value = prop.furnishedStatus)
                        }
                    }

                    Text(
                        text = "Room & Bedspace Types (${boardingHouse.rooms.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    boardingHouse.rooms.forEach { room ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else Color(0xFFF8FAFC)
                            ),
                            border = BorderStroke(1.dp, if (isDark) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f) else Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = room.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (room.genderRule == GenderRule.FEMALE_ONLY) {
                                            if (isDark) Color(0xFF3B1E2B) else Color(0xFFFCE7F3)
                                        } else if (room.genderRule == GenderRule.MALE_ONLY) {
                                            if (isDark) Color(0xFF1E293B) else Color(0xFFE0F2FE)
                                        } else {
                                            if (isDark) SoftGreenDarkBg else SoftGreenBg
                                        },
                                        border = BorderStroke(
                                            0.6.dp,
                                            if (room.genderRule == GenderRule.FEMALE_ONLY) {
                                                if (isDark) Color(0xFF831843) else Color(0xFFF472B6)
                                            } else if (room.genderRule == GenderRule.MALE_ONLY) {
                                                if (isDark) Color(0xFF0369A1) else Color(0xFF38BDF8)
                                            } else {
                                                if (isDark) SoftGreenDarkBorder else SoftGreenBorder
                                            }
                                        )
                                    ) {
                                        Text(
                                            text = room.genderRule.badge,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (room.genderRule == GenderRule.FEMALE_ONLY) {
                                                if (isDark) Color(0xFFF472B6) else Color(0xFF9D174D)
                                            } else if (room.genderRule == GenderRule.MALE_ONLY) {
                                                if (isDark) Color(0xFF38BDF8) else Color(0xFF075985)
                                            } else {
                                                if (isDark) SoftGreenDark else Color(0xFF1D533A)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Occupancy progress & Rent rate
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(if (room.availableBeds > 0) Color(0xFF16A34A) else Color(0xFFDC2626), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${room.availableBeds} of ${room.totalBeds} beds available",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (room.availableBeds > 0) MaterialTheme.colorScheme.onSurface else Color(0xFFDC2626)
                                        )
                                    }

                                    Text(
                                        text = "₱${room.monthlyRent.toInt()} / mo",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Room tags (Aircon, Private CR, Utilities)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = if (room.hasAircon) "❄️ Aircon" else "💨 Fan Room",
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = if (room.hasPrivateBathroom) "🚿 Private CR" else "🚻 Shared CR",
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                    ) {
                                        Text(
                                            text = "${room.advanceRentMonths} Mo Adv / ₱${room.securityDeposit.toInt()} Dep",
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "⚡ Utilities: ${room.utilityDetails}",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = {
                                        selectedRoomForInquiry = room
                                        showViewingDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Apply / Inquire for this Room", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Detailed Living Specifications (VECO/BOHECO electricity, water supply, wifi infrastructure, road access)
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Text(
                        text = "Detailed Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            boardingHouse.specifications?.let { spec ->
                                LivingSpecRow(Icons.Default.Bolt, "⚡ Electricity", spec.electricityDetails)
                                LivingSpecRow(Icons.Default.Bathtub, "💧 Water Supply", spec.waterDetails)
                                LivingSpecRow(Icons.Default.Wifi, "📶 WiFi & Speed", spec.wifiDetails)
                                LivingSpecRow(Icons.AutoMirrored.Filled.AltRoute, "🛵 Transit & Road Proximity", spec.roadProximity)
                                LivingSpecRow(Icons.Default.Home, "🍳 Cooking Facility", spec.cookingRule)
                                LivingSpecRow(Icons.Default.Bathtub, "🧺 Laundry & Drying Area", spec.laundryRule)
                            }
                        }
                    }
                }
            }

            // 5. Amenities & House Rules Badges
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "Amenities & Compound Features",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        boardingHouse.amenities.forEach { amenity ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(0.6.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "✓ $amenity",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "House Rules & Policies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        boardingHouse.houseRules.forEach { rule ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("• ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(rule, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            // 6. Nearby Destination (TRANSIT, ESSENTIALS, UTILITY)
            item {
                NearbyDestinationSection(
                    property = prop,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            // 5. Host Info
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Host",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detail_host_name_box"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Host Avatar",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = hostInfo.hostName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                                            border = BorderStroke(0.6.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder)
                                        ) {
                                            Text(
                                                text = "Verified Host",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isDark) SoftGreenDark else Color(0xFF1D533A),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = hostInfo.hostRole,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Host Contact Details (Phone, calling channel & quick call button)
                            if (hostInfo.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = "Host Phone",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = hostInfo.phone,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = hostInfo.contactChannel,
                                                    fontSize = 10.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                try {
                                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hostInfo.phone}"))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // graceful fallback
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.testTag("host_box_call_btn")
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Call/SMS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Message option placed INSIDE Host Name Box
                            Button(
                                onClick = { showChatDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("host_box_message_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Message ${hostInfo.hostName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Inquiry & Viewing / Application Dialog
    if (showViewingDialog) {
        var inquiryType by remember {
            mutableStateOf(if (selectedRoomForInquiry != null) InquiryType.APPLY else InquiryType.SCHEDULE_VIEWING)
        }
        var targetMoveIn by remember { mutableStateOf("Next week (Flexible)") }
        var prefDate by remember { mutableStateOf("Tomorrow, 2:00 PM") }
        var prefTime by remember { mutableStateOf("2:00 PM - 3:00 PM") }
        var visitorsCount by remember { mutableIntStateOf(1) }
        var tenantMsg by remember {
            mutableStateOf(
                if (selectedRoomForInquiry != null) {
                    "Hello! I would like to apply for '${selectedRoomForInquiry!!.name}'. Is this bedspace still open?"
                } else {
                    ""
                }
            )
        }
        var sendEmailAlert by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = {
                showViewingDialog = false
                selectedRoomForInquiry = null
            },
            title = {
                Text(
                    text = when (inquiryType) {
                        InquiryType.APPLY -> "Apply for Bedspace / Room"
                        InquiryType.SCHEDULE_VIEWING -> "Schedule Property Viewing"
                        InquiryType.INQUIRY -> "Inquire with Landlord"
                    }
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Inquiry Type Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        InquiryType.entries.forEach { type ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (inquiryType == type) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (inquiryType == type) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { inquiryType = type }
                            ) {
                                Text(
                                    text = when (type) {
                                        InquiryType.SCHEDULE_VIEWING -> "Viewing"
                                        InquiryType.APPLY -> "Apply"
                                        InquiryType.INQUIRY -> "Inquire"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (inquiryType == type) FontWeight.Bold else FontWeight.Normal,
                                    color = if (inquiryType == type) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    selectedRoomForInquiry?.let { room ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                            border = BorderStroke(0.8.dp, if (isDark) SoftGreenDarkBorder else SoftGreenBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🛏️ Selected Room: ${room.name} (₱${room.monthlyRent.toInt()}/mo)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) SoftGreenDark else Color(0xFF166534)
                                )
                            }
                        }
                    }

                    if (inquiryType == InquiryType.APPLY || inquiryType == InquiryType.INQUIRY) {
                        OutlinedTextField(
                            value = targetMoveIn,
                            onValueChange = { targetMoveIn = it },
                            label = { Text("Target Move-in Date") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    if (inquiryType == InquiryType.SCHEDULE_VIEWING) {
                        OutlinedTextField(
                            value = prefDate,
                            onValueChange = { prefDate = it },
                            label = { Text("Preferred Date") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = prefTime,
                            onValueChange = { prefTime = it },
                            label = { Text("Preferred Time Window") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Number of Occupants / Visitors:", fontSize = 12.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { if (visitorsCount > 1) visitorsCount-- }) { Text("-") }
                                Text("$visitorsCount", fontWeight = FontWeight.Bold)
                                TextButton(onClick = { visitorsCount++ }) { Text("+") }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = tenantMsg,
                        onValueChange = { tenantMsg = it },
                        label = { Text("Message to Landlord / Caretaker") },
                        placeholder = { Text("e.g. Inquiring about water pressure, curfew keys, and parking...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Email Alert via Gmail", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Dispatches instant alert to host", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = sendEmailAlert,
                            onCheckedChange = { sendEmailAlert = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalMsg = buildString {
                            if (selectedRoomForInquiry != null) {
                                append("[Selected Room: ${selectedRoomForInquiry!!.name} - ₱${selectedRoomForInquiry!!.monthlyRent.toInt()}/mo] ")
                            }
                            if (inquiryType == InquiryType.APPLY) {
                                append("[Target Move-in: $targetMoveIn] ")
                            }
                            append(tenantMsg)
                        }

                        viewModel.requestViewing(
                            propertyId = prop.id,
                            posterId = prop.posterId,
                            date = prefDate,
                            time = prefTime,
                            visitors = visitorsCount,
                            message = finalMsg,
                            sendEmailAlert = sendEmailAlert,
                            onComplete = {
                                val actionLabel = when (inquiryType) {
                                    InquiryType.APPLY -> "Application"
                                    InquiryType.SCHEDULE_VIEWING -> "Viewing request"
                                    InquiryType.INQUIRY -> "Inquiry"
                                }
                                showViewingDialog = false
                                selectedRoomForInquiry = null
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "$actionLabel sent to ${prop.posterName}!" +
                                            (if (sendEmailAlert) " (Email alert dispatched)" else "")
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text(
                        text = when (inquiryType) {
                            InquiryType.APPLY -> "Submit Application"
                            InquiryType.SCHEDULE_VIEWING -> "Submit Request"
                            InquiryType.INQUIRY -> "Send Inquiry"
                        },
                        maxLines = 1,
                        softWrap = false
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showViewingDialog = false
                    selectedRoomForInquiry = null
                }) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Direct Message Dialog
    if (showChatDialog) {
        var messageText by remember { mutableStateOf("Hello! I am interested in this listing '${prop.title}'. Is it still available for viewing?") }

        AlertDialog(
            onDismissRequest = { showChatDialog = false },
            title = { Text("Message ${prop.posterName}") },
            text = {
                Column {
                    Text("Send a secure in-app message. Your phone number is not shared publicly.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startConversation(prop, messageText) { convId ->
                            showChatDialog = false
                            onNavigateToChat(convId)
                        }
                    }
                ) {
                    Text("Send Message", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatDialog = false }) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Report Listing Dialog
    if (showReportDialog) {
        var reason by remember { mutableStateOf("FAKE_PROPERTY") }
        var details by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report this Property Listing") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Help keep Balay-Inn safe and transparent. Select reason:", fontSize = 12.sp)

                    val reasons = listOf(
                        "FAKE_PROPERTY" to "Suspicious / Fake property",
                        "FAKE_OWNER" to "Unauthorized poster / fake owner",
                        "MISLEADING_PHOTOS" to "Misleading photos or description",
                        "WRONG_LOCATION" to "Incorrect geographic location",
                        "SUSPICIOUS_PAYMENT" to "Suspicious advance payment pressure",
                        "ALREADY_RENTED" to "Property already rented out"
                    )

                    reasons.forEach { (code, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reason = code }
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = reason == code,
                                onClick = { reason = code }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("Additional Details (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReport("PROPERTY", prop.id, reason, details) {
                            showReportDialog = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Listing reported. Balay-Inn moderation team has been alerted.")
                            }
                        }
                    }
                ) {
                    Text("Submit Report", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // Export to Social Media / Facebook Marketplace Dialog
    if (showExportPostDialog) {
        val payload = remember(prop, locationDisplay, hostInfo, costBreakdown) {
            ListingTransformationEngine.fromProperty(prop, locationDisplay, hostInfo, costBreakdown)
        }
        val fbPostText = remember(payload) {
            ListingTransformationEngine.formatFacebookPost(payload)
        }
        val inAppText = remember(payload) {
            ListingTransformationEngine.formatInAppListing(payload)
        }
        var selectedOutputTab by remember { mutableIntStateOf(0) } // 0 = Facebook, 1 = In-App

        AlertDialog(
            onDismissRequest = { showExportPostDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Listing Transformation Export", fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
                                text = "In-App Card Specs",
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
                            .height(280.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp, lineHeight = 15.sp)
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
                        showExportPostDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Listing copied to clipboard! Ready to paste.")
                        }
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

@Composable
private fun SpecColumn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, softWrap = false)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
    }
}

@Composable
private fun LivingSpecRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    if (value.isNullOrBlank()) return
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                fontSize = 11.5.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

