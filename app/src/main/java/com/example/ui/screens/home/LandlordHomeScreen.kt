package com.example.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeBlue
import com.example.ui.theme.BadgeBlueBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.theme.SteelBlueDarkBg
import com.example.ui.components.BalayInnLogoHeader
import com.example.ui.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import com.example.ui.components.NotificationDetailDialog
import java.util.Locale

@Composable
fun LandlordHomeScreen(
    viewModel: MarketplaceViewModel,
    currentUser: UserEntity,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onNavigateToChat: (String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark by viewModel.isDarkMode.collectAsState()
    val managedProperties by viewModel.managedProperties.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val userViewings by viewModel.userViewings.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val activeListings = managedProperties.filter { it.status == "ACTIVE" }
    val pendingListings = managedProperties.filter { it.status == "PENDING" }
    val rentedListings = managedProperties.filter { it.status == "RENTED" }

    val totalViews = managedProperties.sumOf { it.viewsCount }
    val totalSaves = managedProperties.sumOf { it.savesCount }
    val pendingViewings = userViewings.filter { it.status == "REQUESTED" }
    val unreadHostNotifs = notifications.count { !it.isRead }
    val unreadMessagesCount = conversations.sumOf { it.unreadForPoster }
    val totalHostAlerts = pendingViewings.size + unreadHostNotifs + unreadMessagesCount

    var showVerificationWarningDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var showNotificationsSheet by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedNotificationDetail by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<NotificationEntity?>(null) }
    var propertyToEdit by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<PropertyEntity?>(null) }

    fun handlePostPropertyClick() {
        if (!currentUser.isHostVerified) {
            showVerificationWarningDialog = true
        } else {
            onNavigateToPostProperty()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Host Dashboard Header: Notification Icon instead of Theme Icon
        BalayInnLogoHeader(
            isDark = isDark,
            showThemeToggle = false,
            onNotificationsClick = { showNotificationsSheet = true },
            notificationsCount = totalHostAlerts
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("landlord_home_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Business Overview Header Card
            item {
                Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("landlord_business_overview"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                text = "PROPERTY BUSINESS OVERVIEW",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Button(
                            onClick = { handlePostPropertyClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("landlord_post_property_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post Property", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Welcome back,",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    val landlordNameFontSize = when {
                        currentUser.fullName.length > 30 -> 14.5.sp
                        currentUser.fullName.length > 24 -> 16.5.sp
                        currentUser.fullName.length > 18 -> 18.5.sp
                        else -> 22.sp
                    }

                    Text(
                        text = currentUser.fullName,
                        fontSize = landlordNameFontSize,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Property Owner • Level ${currentUser.verificationLevel} Verified Host",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )



                    Spacer(modifier = Modifier.height(14.dp))

                    // Listing status breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusKpiCard(
                            label = "Active",
                            count = "${activeListings.size}",
                            color = if (isDark) SoftGreenDark else BadgeGreen,
                            bg = if (isDark) SoftGreenDarkBg else BadgeGreenBg,
                            modifier = Modifier.weight(1f)
                        )
                        StatusKpiCard(
                            label = "Pending",
                            count = "${pendingListings.size}",
                            color = if (isDark) Color(0xFFFBBF24) else BadgeAmber,
                            bg = if (isDark) Color(0xFF451A03) else BadgeAmberBg,
                            modifier = Modifier.weight(1f)
                        )
                        StatusKpiCard(
                            label = "Rented",
                            count = "${rentedListings.size}",
                            color = if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB),
                            bg = if (isDark) Color(0xFF1E3A8A) else Color(0xFFEFF6FF),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Listing Performance Metrics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Listing Performance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Text(
                            text = "30-Day Platform Insights",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PerformanceMetric(
                            icon = Icons.Default.Visibility,
                            value = "$totalViews",
                            label = "Total Views",
                            modifier = Modifier.weight(1f)
                        )
                        PerformanceMetric(
                            icon = Icons.Default.Bookmark,
                            value = "$totalSaves",
                            label = "Tenant Saves",
                            modifier = Modifier.weight(1f)
                        )
                        PerformanceMetric(
                            icon = Icons.Default.Chat,
                            value = "${conversations.size}",
                            label = "Inquiries",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Pending Viewing Requests (High Priority)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Viewing Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                val isDark = isSystemInDarkTheme()
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (pendingViewings.isNotEmpty()) {
                        if (isDark) Color(0xFFD97706).copy(alpha = 0.22f) else Color(0xFFFEF3C7)
                    } else {
                        if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = "${pendingViewings.size} pending confirmation",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pendingViewings.isNotEmpty()) {
                            if (isDark) Color(0xFFFBBF24) else BadgeAmber
                        } else {
                            if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        if (pendingViewings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BadgeGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "All viewing requests have been handled. No pending appointments.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(pendingViewings.take(3), key = { "landlord_view_${it.id}" }) { req ->
                val prop = managedProperties.find { it.id == req.propertyId } ?: properties.find { it.id == req.propertyId }
                LandlordViewingActionCard(
                    viewing = req,
                    propertyTitle = prop?.title ?: "Rental Listing",
                    onAccept = { viewModel.updateViewingStatus(req, "CONFIRMED", "Confirmed by owner.") },
                    onDecline = { viewModel.updateViewingStatus(req, "CANCELLED", "Declined by owner.") }
                )
            }
        }

        // 4. Recent Inquiries (Tenant Leads)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent Inquiries & Leads",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                Text("${conversations.size} total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
            }
        }

        if (conversations.isEmpty()) {
            item {
                Text(
                    text = "No inquiries received yet. Verified listings with photos attract tenants faster.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(conversations.take(3), key = { "conv_lead_${it.id}" }) { conv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = conv.tenantName.take(1),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            val tenantItemFontSize = when {
                                conv.tenantName.length > 25 -> 11.5.sp
                                conv.tenantName.length > 18 -> 12.5.sp
                                else -> 13.5.sp
                            }
                            Text(
                                text = conv.tenantName,
                                fontWeight = FontWeight.Bold,
                                fontSize = tenantItemFontSize,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = conv.propertyTitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = conv.lastMessageText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 5. Your Listings & Quick Listing Management Actions
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your Listings (${managedProperties.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
                TextButton(onClick = { handlePostPropertyClick() }) {
                    Text("+ Add Another", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
        }

        items(managedProperties, key = { "landlord_prop_${it.id}" }) { prop ->
            LandlordPropertyManagementCard(
                property = prop,
                locationDisplay = viewModel.getLocationDisplay(prop),
                onEdit = { propertyToEdit = prop },
                onToggleStatus = {
                    val nextStatus = if (prop.status == "ACTIVE") "RENTED" else "ACTIVE"
                    viewModel.updatePropertyListingStatus(prop.id, nextStatus)
                },
                onClick = { onNavigateToPropertyDetail(prop.id) }
            )
        }

        // 6. Verification Status & Trust Compliance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToVerificationCenter() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ownership Verification: Level ${currentUser.verificationLevel}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = "Submit Land Title / Tax Declaration to earn the verified owner seal.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                    TextButton(onClick = onNavigateToVerificationCenter) {
                        Text("Manage", fontSize = 11.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}

    if (showVerificationWarningDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showVerificationWarningDialog = false },
            icon = {
                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            },
            title = {
                Text("Verification Required", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Sorry, verification is needed before adding property. Please complete your identity and host verification in your Profile Settings to start listing properties.",
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showVerificationWarningDialog = false
                        onNavigateToVerificationCenter()
                    }
                ) {
                    Text("Verify in Profile")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showVerificationWarningDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showNotificationsSheet) {
        HostNotificationsDialog(
            conversations = conversations,
            pendingViewings = pendingViewings,
            notifications = notifications,
            totalViews = totalViews,
            totalSaves = totalSaves,
            onDismiss = { showNotificationsSheet = false },
            onNotificationClick = { notif ->
                viewModel.markNotificationRead(notif.id)
                selectedNotificationDetail = notif
            },
            onNavigateToChat = { convId ->
                showNotificationsSheet = false
                onNavigateToChat(convId)
            }
        )
    }

    selectedNotificationDetail?.let { notif ->
        NotificationDetailDialog(
            notification = notif,
            onDismiss = { selectedNotificationDetail = null },
            onNavigateToProperty = { propId ->
                selectedNotificationDetail = null
                onNavigateToPropertyDetail(propId)
            },
            onNavigateToChat = { convId ->
                selectedNotificationDetail = null
                onNavigateToChat(convId)
            }
        )
    }

    propertyToEdit?.let { prop ->
        EditPropertyDialog(
            property = prop,
            onDismiss = { propertyToEdit = null },
            onSave = { updated ->
                viewModel.updatePropertyDetails(
                    property = updated,
                    onSuccess = { propertyToEdit = null }
                )
            }
        )
    }
}

@Composable
private fun StatusKpiCard(
    label: String,
    count: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bg,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = color, maxLines = 1, softWrap = false)
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = color, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
private fun PerformanceMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, softWrap = false)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
        }
    }
}

@Composable
private fun LandlordViewingActionCard(
    viewing: ViewingRequestEntity,
    propertyTitle: String = "Rental Listing",
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = propertyTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = "${viewing.numberOfVisitors} Visitor(s)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BadgeAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Visit: ${viewing.preferredDate} (${viewing.preferredTime})",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
            }

            if (viewing.tenantMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${viewing.tenantMessage}\"",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirm Appointment", fontSize = 11.sp, maxLines = 1, softWrap = false)
                }

                OutlinedButton(
                    onClick = onDecline,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Decline", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                }
            }
        }
    }
}

@Composable
private fun LandlordPropertyManagementCard(
    property: PropertyEntity,
    locationDisplay: String,
    onEdit: () -> Unit,
    onToggleStatus: () -> Unit,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val isActive = property.status == "ACTIVE"
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) {
                        if (isDark) SoftGreenDarkBg else BadgeGreenBg
                    } else {
                        if (isDark) Color(0xFF2563EB).copy(alpha = 0.22f) else Color(0xFFEFF6FF)
                    }
                ) {
                    Text(
                        text = property.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) {
                            if (isDark) SoftGreenDark else BadgeGreen
                        } else {
                            if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Text(
                    text = "${currencyFormat.format(property.monthlyRent).replace("PHP", "₱")} / month",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = property.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = locationDisplay,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Views: ${property.viewsCount} • Saves: ${property.savesCount}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("landlord_edit_prop_${property.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Property",
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Edit",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleStatus,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isActive) "Mark Rented" else "Mark Active",
                            fontSize = 10.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditPropertyDialog(
    property: PropertyEntity,
    onDismiss: () -> Unit,
    onSave: (PropertyEntity) -> Unit
) {
    var title by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.title) }
    var rentText by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.monthlyRent.toInt().toString()) }
    var selectedType by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.propertyType) }
    var location by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.location) }
    var bedroomsText by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.bedrooms.toString()) }
    var bathroomsText by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.bathrooms.toString()) }
    var floorAreaText by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.floorAreaSqm.toInt().toString()) }
    var description by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.description) }
    var furnishedStatus by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.furnishedStatus) }
    var petPolicy by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.petPolicy) }
    var status by androidx.compose.runtime.remember(property) { androidx.compose.runtime.mutableStateOf(property.status) }
    var errorMessage by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    val propertyTypes = listOf("Boarding House", "Apartment", "Condominium", "Studio", "Townhouse", "Single Family House")
    val furnishedOptions = listOf("Furnished", "Semi-Furnished", "Unfurnished")
    val petPolicies = listOf("Pets Allowed", "Small Pets Only", "No Pets")
    val statusOptions = listOf("ACTIVE", "RENTED", "PAUSED")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Property Listing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMessage = null },
                    label = { Text("Listing Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prop_title_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rentText,
                        onValueChange = { rentText = it.filter { ch -> ch.isDigit() }; errorMessage = null },
                        label = { Text("Rent (₱/mo) *") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("edit_prop_rent_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = floorAreaText,
                        onValueChange = { floorAreaText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Area (sqm)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Text("Property Type", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    propertyTypes.forEach { pType ->
                        FilterChip(
                            selected = selectedType == pType,
                            onClick = { selectedType = pType },
                            label = { Text(pType, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (Barangay, City, Province)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prop_location_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bedroomsText,
                        onValueChange = { bedroomsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Bedrooms") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = bathroomsText,
                        onValueChange = { bathroomsText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Bathrooms") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Text("Furnishing", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    furnishedOptions.forEach { f ->
                        FilterChip(
                            selected = furnishedStatus == f,
                            onClick = { furnishedStatus = f },
                            label = { Text(f, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Pet Policy", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    petPolicies.forEach { p ->
                        FilterChip(
                            selected = petPolicy == p,
                            onClick = { petPolicy = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Listing Status", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth().testTag("edit_prop_desc_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please provide a listing title."
                        return@Button
                    }
                    val rent = rentText.toDoubleOrNull() ?: 0.0
                    if (rent <= 0) {
                        errorMessage = "Monthly rent must be greater than zero."
                        return@Button
                    }
                    val updated = property.copy(
                        title = title.trim(),
                        monthlyRent = rent,
                        propertyType = selectedType,
                        location = location.trim(),
                        bedrooms = bedroomsText.toIntOrNull() ?: property.bedrooms,
                        bathrooms = bathroomsText.toIntOrNull() ?: property.bathrooms,
                        floorAreaSqm = floorAreaText.toDoubleOrNull() ?: property.floorAreaSqm,
                        description = description.trim(),
                        furnishedStatus = furnishedStatus,
                        petPolicy = petPolicy,
                        status = status
                    )
                    onSave(updated)
                },
                modifier = Modifier.testTag("save_edited_property_btn")
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun HostNotificationsDialog(
    conversations: List<com.example.data.local.entity.ConversationEntity>,
    pendingViewings: List<ViewingRequestEntity>,
    notifications: List<NotificationEntity>,
    totalViews: Int,
    totalSaves: Int,
    onDismiss: () -> Unit,
    onNotificationClick: (NotificationEntity) -> Unit = {},
    onNavigateToChat: (String?) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Host Notifications & Alerts", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Live Notifications from Tenants & System
                if (notifications.isNotEmpty()) {
                    Text("Live Alerts (${notifications.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    notifications.take(8).forEach { notif ->
                        val categoryColor = when (notif.category) {
                            "VIEWING" -> Color(0xFFD97706)
                            "MESSAGE" -> MaterialTheme.colorScheme.primary
                            "VERIFICATION" -> Color(0xFF16A34A)
                            "STATUS" -> Color(0xFFDC2626)
                            "SAVED" -> Color(0xFFE11D48)
                            "SYSTEM" -> Color(0xFF4F46E5)
                            else -> MaterialTheme.colorScheme.secondary
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = categoryColor.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNotificationClick(notif) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = categoryColor
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = categoryColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = notif.category,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = categoryColor,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = notif.message,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // 2. Pending Viewings Alert
                if (pendingViewings.isNotEmpty()) {
                    Text("Pending Viewing Requests (${pendingViewings.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    pendingViewings.forEach { req ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, BadgeAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Viewing: ${req.preferredDate} @ ${req.preferredTime}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = BadgeAmber
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = BadgeAmber.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Awaiting Host Action",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BadgeAmber,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                if (req.tenantMessage.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "\"${req.tenantMessage}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Active Inquiries / Conversations
                val activeConvs = conversations.filter { it.lastMessageText.isNotBlank() }
                if (activeConvs.isNotEmpty()) {
                    Text("Recent Tenant Messages (${activeConvs.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    activeConvs.take(5).forEach { conv ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToChat(conv.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "From: ${conv.tenantName} (Re: ${conv.propertyTitle})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "\"${conv.lastMessageText}\"",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // 4. Listing Performance Notification
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "30-Day Listing Performance",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your properties gained $totalViews views and $totalSaves tenant saves this month.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
