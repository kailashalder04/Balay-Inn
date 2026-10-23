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
import com.example.data.model.getCategorySpecificationItems
import com.example.util.AppSecurityManager
import com.example.ui.components.BoardingHouseImagePager
import com.example.ui.components.NearbyDestinationSection
import com.example.ui.components.RentCostBreakdownPanel
import com.example.ui.components.VerifiedIcon
import com.example.ui.components.isPlatformVerified
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.SoftGreenBg
import com.example.ui.theme.SoftGreenBorder
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.theme.SoftGreenDarkBorder
import com.example.ui.theme.SteelBlue
import com.example.ui.theme.SteelBlueBorder
import com.example.ui.theme.SteelBlueDarkBg
import com.example.ui.theme.SteelBlueDarkBorder
import com.example.ui.theme.SteelBlueLightBg
import com.example.ui.theme.SteelBlueText
import com.example.ui.theme.SteelBlueTextDark
import com.example.ui.theme.PropertyBoxBg
import com.example.ui.theme.PropertyBoxBgDark
import com.example.ui.theme.PropertyBoxBorder
import com.example.ui.theme.PropertyBoxBorderDark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import com.example.ui.components.ComparisonMatrixSheet
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
import com.example.data.model.PhilippineRentalPropertyCategory
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
    onNavigateToProperty: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val propertyFlow = remember(propertyId) { viewModel.repository.getPropertyById(propertyId) }
    val property by propertyFlow.collectAsState(initial = null)
    val isSaved by viewModel.isPropertySaved(propertyId).collectAsState(initial = false)
    val images by remember(propertyId) { viewModel.getImagesForProperty(propertyId) }.collectAsState(initial = emptyList())
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog States
    var showViewingDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showExportPostDialog by remember { mutableStateOf(false) }
    var showComparisonMatrix by remember { mutableStateOf(false) }

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading property details...")
        }
        return
    }

    val prop = property!!
    val locationDisplay = viewModel.getLocationDisplay(prop)

    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()
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
    val standardCategory = remember(prop.propertyType) {
        PhilippineRentalPropertyCategory.fromPropertyType(prop.propertyType)
    }
    val localElectricProvider = remember(prop.provinceId) {
        PhilippineRentalPropertyCategory.getLocalElectricProvider(prop.provinceId)
    }
    var selectedRoomForInquiry by remember { mutableStateOf<RoomType?>(null) }

    // Clean property title without landmark
    val cleanTitle = remember(prop.title) {
        if (prop.title.contains(Regex("(?i)\\s+near\\s+"))) {
            prop.title.split(Regex("(?i)\\s+near\\s+")).first().trim()
        } else {
            prop.title
        }
    }

    // Nearby landmark text placed below images
    val landmarkText = remember(prop.title, prop.nearbyLandmark) {
        if (prop.nearbyLandmark.isNotBlank()) {
            prop.nearbyLandmark
        } else if (prop.title.contains(Regex("(?i)\\s+near\\s+"))) {
            prop.title.split(Regex("(?i)\\s+near\\s+")).getOrNull(1)?.trim() ?: ""
        } else {
            ""
        }
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val formattedRent = currencyFormat.format(prop.monthlyRent).replace("PHP", "₱")

    val comparedProperties by viewModel.comparedProperties.collectAsState()
    val isCompared = comparedProperties.any { it.id == prop.id }

    val isPhoneMaskingEnabled by viewModel.isPhoneMaskingEnabled.collectAsState()
    val revealedPhonePropertyIds by viewModel.revealedPhonePropertyIds.collectAsState()
    val isPhoneRevealed = !isPhoneMaskingEnabled || revealedPhonePropertyIds.contains(prop.id)
    val maskedPhone = AppSecurityManager.maskPhoneNumber(hostInfo.phone, isPhoneRevealed)

    Scaffold(
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
                        Surface(
                            onClick = { showCostBreakdown = true },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) Color(0xFF1B2C3F) else Color(0xFFEDF4FA),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF2E4863) else Color(0xFFB8C9DC)),
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("lower_bar_rent_btn")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "$formattedRent / mo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "View Cost Breakup",
                                            fontSize = if (isNarrow) 9.5.sp else 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
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
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Header Image & Title: Carousel, Property Name, and Location
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    BoardingHouseImagePager(
                        images = displayImages,
                        propertyTitle = cleanTitle,
                        roomType = prop.propertyType,
                        onBack = onBack,
                        onShare = { showExportPostDialog = true },
                        isSaved = isSaved,
                        onToggleSave = {
                            viewModel.toggleSave(prop.id)
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (!isSaved) "Saved to your favorites" else "Removed from favorites"
                                )
                            }
                        },
                        onReport = { showReportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Property Name & Price Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cleanTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$formattedRent / month",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (prop.isPlatformVerified()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verified",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        }

                        // Location & Landmark Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) PropertyBoxBgDark else Color.White,
                            border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("detail_map_pin_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Map Pin",
                                    modifier = Modifier.size(16.dp),
                                    tint = SteelBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (landmarkText.isNotBlank()) "$locationDisplay • Near $landmarkText" else locationDisplay,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) SteelBlueTextDark else SteelBlueText,
                                    lineHeight = 16.sp,
                                    softWrap = true,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Card 1: Description: A text block for the landlord's subjective notes
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) PropertyBoxBgDark else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
                    ) {
                        Text(
                            text = boardingHouse.fullDescription.ifBlank { prop.description },
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B),
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 3. Card 2: Unit Specifications: Group Property Type, Floor/Lot Area, Bed/Bath count, and Furnishing status
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "Unit Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) PropertyBoxBgDark else Color.White,
                            contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
                        ),
                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Row 1: Property Type & Floor/Lot Area
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UnitSpecItem(
                                    icon = Icons.Default.Apartment,
                                    label = "Property Type",
                                    value = prop.propertyType.ifBlank { "Rental Unit" },
                                    modifier = Modifier.weight(1f)
                                )
                                val areaValue = if (prop.floorAreaSqm > 0) {
                                    "${prop.floorAreaSqm.toInt()} sqm"
                                } else {
                                    "Standard"
                                }
                                UnitSpecItem(
                                    icon = Icons.Default.SquareFoot,
                                    label = "Floor / Lot Area",
                                    value = areaValue,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            HorizontalDivider(
                                color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                                thickness = 0.8.dp
                            )

                            // Row 2: Bed / Bath Count & Furnishing Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val bedBathValue = "${prop.bedrooms} Bed${if (prop.bedrooms > 1) "s" else ""} • ${prop.bathrooms} Bath/CR"
                                UnitSpecItem(
                                    icon = Icons.Default.Bed,
                                    label = "Bed / Bath Count",
                                    value = bedBathValue,
                                    modifier = Modifier.weight(1f)
                                )
                                UnitSpecItem(
                                    icon = Icons.Default.Chair,
                                    label = "Furnishing Status",
                                    value = prop.furnishedStatus.ifBlank { "Unfurnished" },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Dynamic category-specific specifications (seamless integration without duplicate standard rows)
                            val catItems = remember(prop) {
                                prop.getCategorySpecificationItems().filter { item ->
                                    !item.label.contains("Type", ignoreCase = true) &&
                                    !item.label.contains("Furnish", ignoreCase = true) &&
                                    !item.label.contains("Bed", ignoreCase = true) &&
                                    !item.label.contains("Area", ignoreCase = true)
                                }
                            }
                            if (catItems.isNotEmpty()) {
                                HorizontalDivider(
                                    color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                                    thickness = 0.8.dp
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    catItems.forEach { item ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isDark) Color(0xFF162536) else Color(0xFFF1F5F9),
                                            border = BorderStroke(0.8.dp, if (isDark) SteelBlue.copy(alpha = 0.5f) else Color(0xFFCBD5E1))
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(item.iconEmoji, fontSize = 11.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${item.label}: ${item.value}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F263B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Card 3: Amenities & Rules: Group Parking, Building Facilities (Pool/Gym), and Pet/Smoking policies
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                    Text(
                        text = "Amenities & Rules",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) PropertyBoxBgDark else Color.White,
                            contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
                        ),
                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Section A: Parking
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Parking",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = "Parking",
                                        tint = SteelBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (prop.parkingAvailable) "Parking Available (Garage / Dedicated Slot)" else "No Dedicated Parking (Street Parking Only)",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                                thickness = 0.8.dp
                            )

                            // Section B: Building Facilities (Pool, Gym, Security, Balcony, etc.)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Building Facilities",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val cleanAmenities = remember(boardingHouse.amenities) {
                                    val list = boardingHouse.amenities.filter { amenity ->
                                        !amenity.contains("Sub-meter", ignoreCase = true) &&
                                        !amenity.contains("Deposit", ignoreCase = true)
                                    }
                                    if (list.isNotEmpty()) list else listOf("WiFi Available", "24/7 Security", "Gated Compound")
                                }
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    cleanAmenities.forEach { amenity ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isDark) PropertyBoxBgDark else Color(0xFFF8FAFC),
                                            border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Text(
                                                    text = amenity,
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isDark) SteelBlueTextDark else SteelBlueText
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = if (isDark) PropertyBoxBorderDark else PropertyBoxBorder,
                                thickness = 0.8.dp
                            )

                            // Section C: Pet & Smoking Policies
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Pet & Smoking Policies",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDark) PropertyBoxBgDark else Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Pets,
                                                contentDescription = "Pet Policy",
                                                tint = SteelBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text("Pet Policy", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = prop.petPolicy.ifBlank { "No Pets Allowed" },
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isDark) PropertyBoxBgDark else Color(0xFFF8FAFC),
                                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.SmokeFree,
                                                contentDescription = "Smoking Policy",
                                                tint = SteelBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text("Smoking Policy", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Text(
                                                    text = "No Smoking Indoors",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (boardingHouse.houseRules.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
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
                        }
                    }
                }
            }

            // 5. Explore Neighborhood as it is
            item {
                NearbyDestinationSection(
                    property = prop,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            // 6. Contact Details as it is
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
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) PropertyBoxBgDark else Color.White,
                            contentColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
                        ),
                        border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
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
                                    val hostNameFontSize = when {
                                        hostInfo.hostName.length > 25 -> 13.5.sp
                                        hostInfo.hostName.length > 18 -> 15.sp
                                        else -> 16.5.sp
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = hostInfo.hostName,
                                            fontSize = hostNameFontSize,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E),
                                            maxLines = 1,
                                            softWrap = false,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isDark) SoftGreenDarkBg else SoftGreenBg,
                                            border = BorderStroke(1.dp, if (isDark) SoftGreenDarkBorder else Color(0xFF16A34A))
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
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                                    )
                                }
                            }

                            // Host Contact Details (Phone, calling channel & quick call button)
                            if (hostInfo.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isDark) Color(0xFF152233) else Color.White,
                                    border = BorderStroke(1.dp, if (isDark) PropertyBoxBorderDark else PropertyBoxBorder),
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
                                                tint = SteelBlue,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = maskedPhone,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F1E2E)
                                                )
                                                Text(
                                                    text = if (!isPhoneRevealed) "🔒 Tap to Reveal Contact" else hostInfo.contactChannel,
                                                    fontSize = 10.5.sp,
                                                    color = if (!isPhoneRevealed) SteelBlue else (if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)),
                                                    modifier = Modifier.clickable { if (!isPhoneRevealed) viewModel.toggleRevealPhone(prop.id) }
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
                                val msgBtnFontSize = if (hostInfo.hostName.length > 20) 11.5.sp else 13.sp
                                Text(
                                    text = "Message ${hostInfo.hostName}",
                                    fontSize = msgBtnFontSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
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
            title = {
                val dialogTitleFontSize = if (prop.posterName.length > 22) 13.5.sp else 16.sp
                Text("Message ${prop.posterName}", fontSize = dialogTitleFontSize, maxLines = 1, softWrap = false)
            },
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
        val marketStandardText = remember(prop, locationDisplay, hostInfo) {
            ListingTransformationEngine.formatMarketplaceListing(prop, locationDisplay, hostInfo)
        }
        val fbPostText = remember(payload) {
            ListingTransformationEngine.formatFacebookPost(payload)
        }
        val inAppText = remember(payload) {
            ListingTransformationEngine.formatInAppListing(payload)
        }
        var selectedOutputTab by remember { mutableIntStateOf(0) } // 0 = Market Standard, 1 = Facebook, 2 = In-App Specs

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
                                .weight(1.1f)
                                .clickable { selectedOutputTab = 0 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedOutputTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "Standard (4 Types)",
                                fontSize = 11.sp,
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
                                text = "Facebook Post",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOutputTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedOutputTab = 2 },
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedOutputTab == 2) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = "In-App Specs",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOutputTab == 2) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayText = when (selectedOutputTab) {
                        0 -> marketStandardText
                        1 -> fbPostText
                        else -> inAppText
                    }

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
                        val textToCopy = when (selectedOutputTab) {
                            0 -> marketStandardText
                            1 -> fbPostText
                            else -> inAppText
                        }
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
                    Text("Share", fontSize = 11.5.sp)
                }
            }
        )
    }

    // Rent & Deposit Cost Breakup Modal Sheet
    if (showCostBreakdown) {
        ModalBottomSheet(
            onDismissRequest = { showCostBreakdown = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rent & Fee Cost Breakup",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = prop.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { showCostBreakdown = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                RentCostBreakdownPanel(
                    breakdown = costBreakdown,
                    isDark = isDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showComparisonMatrix) {
        ComparisonMatrixSheet(
            properties = comparedProperties,
            imagesMap = propertyImagesMap,
            onDismiss = { showComparisonMatrix = false },
            onRemoveProperty = { propertyId -> viewModel.removeFromCompare(propertyId) },
            onClearAll = {
                viewModel.clearCompare()
                showComparisonMatrix = false
            },
            onNavigateToDetail = { id ->
                showComparisonMatrix = false
                if (id != prop.id) {
                    onNavigateToProperty(id)
                }
            }
        )
    }
}

@Composable
private fun UnitSpecItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SteelBlue,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

