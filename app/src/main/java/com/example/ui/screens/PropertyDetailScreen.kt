package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Visibility
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
import com.example.ui.components.VerificationBadgeRow
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

    // Dialog & Lightbox States
    var showViewingDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showLightbox by remember { mutableStateOf(false) }
    var lightboxInitialIndex by remember { mutableIntStateOf(0) }

    if (property == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading property details...")
        }
        return
    }

    val prop = property!!
    val locationDisplay = viewModel.getLocationDisplay(prop)

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

    val heroPagerState = rememberPagerState(pageCount = { displayImages.size })

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val formattedRent = currencyFormat.format(prop.monthlyRent).replace("PHP", "₱")

    val totalMonthly = prop.monthlyRent + prop.associationDues + prop.parkingFees + prop.maintenanceFees
    val advanceRentAmount = prop.monthlyRent * prop.advanceRentMonths
    val securityDepositAmount = prop.monthlyRent * prop.securityDepositMonths
    val totalMoveInCost = advanceRentAmount + securityDepositAmount

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
                                text = "Est. Total: ${currencyFormat.format(totalMonthly).replace("PHP", "₱")}/mo",
                                fontSize = if (isNarrow) 10.sp else 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { showChatDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = if (isNarrow) 8.dp else 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("detail_message_btn")
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Message", maxLines = 1, softWrap = false, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { showViewingDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = if (isNarrow) 8.dp else 14.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("detail_request_viewing_btn")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Request Viewing", maxLines = 1, softWrap = false, fontSize = 12.sp)
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
            // Photos Carousel / Gallery (Full-Width Swipeable Image Carousel)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                ) {
                    HorizontalPager(
                        state = heroPagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                lightboxInitialIndex = heroPagerState.currentPage
                                showLightbox = true
                            }
                            .testTag("detail_hero_carousel")
                    ) { page ->
                        AsyncImage(
                            model = displayImages[page],
                            contentDescription = "${prop.title} photo ${page + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Subtle gradient for buttons & badges
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.25f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )

                    // Navigation Chevrons
                    if (displayImages.size > 1) {
                        if (heroPagerState.currentPage > 0) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        heroPagerState.animateScrollToPage(heroPagerState.currentPage - 1)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Previous Photo",
                                    tint = Color.White
                                )
                            }
                        }

                        if (heroPagerState.currentPage < displayImages.size - 1) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        heroPagerState.animateScrollToPage(heroPagerState.currentPage + 1)
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next Photo",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Tap to expand hint (top-left)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.55f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clickable {
                                lightboxInitialIndex = heroPagerState.currentPage
                                showLightbox = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Fullscreen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Full Screen",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Floating Photo Count Badge (bottom-right: "1 / 5 Photos")
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clickable {
                                lightboxInitialIndex = heroPagerState.currentPage
                                showLightbox = true
                            }
                    ) {
                        Text(
                            text = "${heroPagerState.currentPage + 1} / ${displayImages.size} Photos",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    // Dot indicators centered at bottom
                    if (displayImages.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(displayImages.size) { index ->
                                val isCurrent = heroPagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(width = if (isCurrent) 16.dp else 6.dp, height = 6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (isCurrent) Color.White else Color.White.copy(alpha = 0.45f))
                                )
                            }
                        }
                    }
                }
            }

            // Title, Type, and Location
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

                        Text(
                            text = "Availability: ${prop.availabilityDate}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = prop.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (prop.neighborhood.isNotBlank() || prop.nearbyLandmark.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = listOfNotNull(
                                if (prop.neighborhood.isNotBlank()) "Neighborhood: ${prop.neighborhood}" else null,
                                if (prop.nearbyLandmark.isNotBlank()) "Landmark: ${prop.nearbyLandmark}" else null
                            ).joinToString(" • "),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Key Specs Grid: Beds, Baths, Area, Furnished, Lease
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SpecColumn(icon = Icons.Default.Bed, label = "Bedrooms", value = if (prop.bedrooms == 0) "Studio" else "${prop.bedrooms} Bed")
                        SpecColumn(icon = Icons.Default.Bathtub, label = "Baths", value = "${prop.bathrooms} Bath")
                        SpecColumn(icon = Icons.Default.SquareFoot, label = "Floor Area", value = "${prop.floorAreaSqm.toInt()} sqm")
                        SpecColumn(icon = Icons.Default.Home, label = "Furnished", value = prop.furnishedStatus)
                    }
                }
            }

            // Section 23: Complete Cost Transparency Breakdown
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Cost Transparency Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CostRow(label = "Monthly Base Rent", amount = prop.monthlyRent, isBold = false)
                            CostRow(label = "Association Dues", amount = prop.associationDues, isBold = false)
                            CostRow(label = "Parking Fee", amount = prop.parkingFees, isBold = false)
                            CostRow(label = "Maintenance / Misc", amount = prop.maintenanceFees, isBold = false)

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)))

                            CostRow(label = "Total Estimated Monthly Cost", amount = totalMonthly, isBold = true, isHighlight = true)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Estimated Move-in Cash Required:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            CostRow(label = "Advance Rent (${prop.advanceRentMonths} month)", amount = advanceRentAmount, isBold = false)
                            CostRow(label = "Security Deposit (${prop.securityDepositMonths} months)", amount = securityDepositAmount, isBold = false)
                            CostRow(label = "Total Move-in Payment", amount = totalMoveInCost, isBold = true)
                        }
                    }
                }
            }

            // Section 26: Poster vs Owner Relationship & Permissions
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Listing Representative & Ownership",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prop.posterName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Relationship: ${prop.posterRole}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (prop.isPosterAuthorized) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (prop.isPosterAuthorized) Color(0xFF059669) else Color(0xFFD97706),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (prop.isPosterAuthorized) "Authorized by property owner to advertise" else "Authorization verification pending",
                                        fontSize = 11.sp,
                                        color = if (prop.isPosterAuthorized) Color(0xFF059669) else Color(0xFFD97706),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Layered Verification Section
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Balay-Inn Layered Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    VerificationBadgeRow(property = prop, showDetailsDialogOnClick = true)
                }
            }

            // Description
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "About this Property",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = prop.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            // Section 18: Location Privacy Information
            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Location Privacy & Area",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Privacy Setting: ${prop.locationPrivacy.lowercase().replaceFirstChar { it.uppercase() }} Location",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "To protect tenant and owner security, exact residential street numbers are kept confidential until an in-app viewing request is accepted. Nearby landmarks: ${prop.nearbyLandmark.ifBlank { "Available upon contact" }}.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Request Viewing Dialog
    if (showViewingDialog) {
        var prefDate by remember { mutableStateOf("Tomorrow, 2:00 PM") }
        var prefTime by remember { mutableStateOf("2:00 PM - 3:00 PM") }
        var visitorsCount by remember { mutableIntStateOf(1) }
        var tenantMsg by remember { mutableStateOf("") }
        var sendEmailAlert by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showViewingDialog = false },
            title = { Text("Request Property Viewing") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Select your preferred date & time to visit '${prop.title}':",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                        Text("Number of Visitors:", fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { if (visitorsCount > 1) visitorsCount-- }) { Text("-") }
                            Text("$visitorsCount", fontWeight = FontWeight.Bold)
                            TextButton(onClick = { visitorsCount++ }) { Text("+") }
                        }
                    }

                    OutlinedTextField(
                        value = tenantMsg,
                        onValueChange = { tenantMsg = it },
                        label = { Text("Message to Poster (Optional)") },
                        placeholder = { Text("e.g. Inquiring about water pressure and WiFi...") },
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
                            Text("Dispatches instant notification to host", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        viewModel.requestViewing(
                            propertyId = prop.id,
                            posterId = prop.posterId,
                            date = prefDate,
                            time = prefTime,
                            visitors = visitorsCount,
                            message = tenantMsg,
                            sendEmailAlert = sendEmailAlert,
                            onComplete = {
                                showViewingDialog = false
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Viewing request sent to ${prop.posterName}!" +
                                            (if (sendEmailAlert) " (Email alert dispatched)" else "")
                                    )
                                }
                            }
                        )
                    }
                ) {
                    Text("Submit Request", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showViewingDialog = false }) {
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

    // --- FULL-SCREEN HIGH-RESOLUTION LIGHTBOX MODAL ---
    if (showLightbox) {
        Dialog(
            onDismissRequest = { showLightbox = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            val lightboxPagerState = rememberPagerState(
                initialPage = lightboxInitialIndex,
                pageCount = { displayImages.size }
            )
            val context = LocalContext.current
            var isZoomedIn by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .testTag("fullscreen_lightbox_modal")
            ) {
                // High-Resolution Swipeable Photo Pager with Pinch-to-Zoom
                HorizontalPager(
                    state = lightboxPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    var scale by remember(page) { mutableFloatStateOf(1f) }
                    var offset by remember(page) { mutableStateOf(Offset.Zero) }

                    LaunchedEffect(isZoomedIn) {
                        if (isZoomedIn) {
                            scale = 2.5f
                        } else {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(page) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale = (scale * zoom).coerceIn(1f, 4.5f)
                                    if (scale > 1.05f) {
                                        offset += pan
                                        isZoomedIn = true
                                    } else {
                                        offset = Offset.Zero
                                        isZoomedIn = false
                                    }
                                }
                            }
                            .pointerInput(page) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        if (scale > 1.05f || isZoomedIn) {
                                            scale = 1f
                                            offset = Offset.Zero
                                            isZoomedIn = false
                                        } else {
                                            scale = 2.5f
                                            isZoomedIn = true
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = displayImages[page],
                            contentDescription = "Full-Screen Photo ${page + 1}",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                        )
                    }
                }

                // Top Floating Bar with Index, Zoom Toggle, Share, and Close
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                            )
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${lightboxPagerState.currentPage + 1} of ${displayImages.size}",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Zoom Toggle Button
                        IconButton(
                            onClick = { isZoomedIn = !isZoomedIn },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = if (isZoomedIn) Icons.Default.ZoomOut else Icons.Default.ZoomIn,
                                contentDescription = if (isZoomedIn) "Zoom Out" else "Zoom In",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                val currentUrl = displayImages.getOrNull(lightboxPagerState.currentPage) ?: ""
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, prop.title)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Check out this verified listing on Balay-Inn: ${prop.title} • $currentUrl"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Listing Photo"))
                            },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share Photo",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Close Button
                        IconButton(
                            onClick = { showLightbox = false },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("lightbox_close_btn")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Lightbox",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Left & Right Navigation Chevron Buttons
                if (displayImages.size > 1) {
                    if (lightboxPagerState.currentPage > 0) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    lightboxPagerState.animateScrollToPage(lightboxPagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous photo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    if (lightboxPagerState.currentPage < displayImages.size - 1) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    lightboxPagerState.animateScrollToPage(lightboxPagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next photo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Bottom thumbnails indicator preview strip
                if (displayImages.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(bottom = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        displayImages.forEachIndexed { idx, url ->
                            val isSelected = lightboxPagerState.currentPage == idx
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                border = if (isSelected) BorderStroke(2.dp, Color.White) else null,
                                modifier = Modifier
                                    .size(width = 46.dp, height = 32.dp)
                                    .clickable {
                                        scope.launch { lightboxPagerState.animateScrollToPage(idx) }
                                    }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Thumbnail ${idx + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
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
private fun CostRow(label: String, amount: Double, isBold: Boolean, isHighlight: Boolean = false) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val formatted = if (amount == 0.0) "Included / Free" else currencyFormat.format(amount).replace("PHP", "₱")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 13.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = formatted,
            fontSize = if (isBold) 14.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            softWrap = false
        )
    }
}
