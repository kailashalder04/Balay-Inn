package com.example.ui.screens

import com.example.ui.components.BoardingHouseImagePager
import com.example.ui.components.NearbyDestinationSection
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

                        OutlinedButton(
                            onClick = { showChatDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = if (isNarrow) 8.dp else 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("detail_message_btn")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(15.dp))
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

                        Text(
                            text = "$formattedRent / month",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = prop.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Map Address in a Pin Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Utility and Highway Highlights
                    val isDark = isSystemInDarkTheme()
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
                            color = if (isDark) Color(0xFF2563EB).copy(alpha = 0.2f) else Color(0xFFEFF6FF),
                            border = BorderStroke(0.5.dp, if (isDark) Color(0xFF3B82F6).copy(alpha = 0.45f) else Color(0xFF93C5FD))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF93C5FD) else Color(0xFF2563EB),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "WiFi available",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFBFDBFE) else Color(0xFF1D4ED8)
                                )
                            }
                        }

                        // Electricity / Water
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) Color(0xFF16A34A).copy(alpha = 0.2f) else Color(0xFFF0FDF4),
                            border = BorderStroke(0.5.dp, if (isDark) Color(0xFF22C55E).copy(alpha = 0.45f) else Color(0xFF86EFAC))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF86EFAC) else Color(0xFF16A34A),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (prop.utilitiesIncluded) "Elec/Water in rent" else "Elec submeter / Water",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFBBF7D0) else Color(0xFF15803D)
                                )
                            }
                        }

                        // Highway Access
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isDark) Color(0xFFD97706).copy(alpha = 0.2f) else Color(0xFFFFFBEB),
                            border = BorderStroke(0.5.dp, if (isDark) Color(0xFFF59E0B).copy(alpha = 0.45f) else Color(0xFFFDE68A))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.AltRoute,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFFDE68A) else Color(0xFFD97706),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Highway access",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFFEF08A) else Color(0xFFB45309)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Boarding House Main Description (Simple and Focused)
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
                            text = prop.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 3. Room Details
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
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
                        SpecColumn(icon = Icons.Default.Bed, label = "Room", value = if (prop.bedrooms == 0) "Bed Space" else "${prop.bedrooms} Bed${if (prop.bedrooms > 1) "s" else ""}")
                        SpecColumn(icon = Icons.Default.Bathtub, label = "Bathroom", value = "${prop.bathrooms} Bath")
                        SpecColumn(icon = Icons.Default.Home, label = "Furnished", value = prop.furnishedStatus)
                    }
                }
            }

            // 4. Nearby Destination (TRANSIT, ESSENTIALS, UTILITY)
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
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
                                    text = "Host / Caretaker",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
