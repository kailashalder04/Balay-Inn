package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ViewingRequestEntity
import com.example.ui.components.PropertyCard
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewingRequestsScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val tenantViewings by viewModel.tenantViewings.collectAsState()
    val posterViewings by viewModel.posterViewings.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Viewings, 1 = Saved, 2 = Host Requests

    val savedItems = properties.filter { p -> savedProperties.any { it.propertyId == p.id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viewings & Saved", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("viewings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Viewings (${tenantViewings.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Saved (${savedItems.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "Requests (${posterViewings.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 70.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Tenant Viewings
                        if (tenantViewings.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.EventNote, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("No scheduled viewings yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Browse rentals and schedule an in-person or virtual viewing tour with the property host.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(tenantViewings, key = { it.id }) { viewing ->
                                ViewingCard(
                                    viewing = viewing,
                                    isPoster = false,
                                    onAccept = {},
                                    onCancel = {
                                        viewModel.updateViewingStatus(viewing, "CANCELLED", "Cancelled by user.", sendEmailAlert = true)
                                    },
                                    onSendEmailAlert = {
                                        viewModel.dispatchViewingStatusNotificationEmail(
                                            recipientEmail = "host@balayinn.ph",
                                            propertyTitle = "Balay-Inn Viewing Appointment",
                                            status = viewing.status,
                                            date = viewing.preferredDate,
                                            time = viewing.preferredTime,
                                            notes = viewing.posterResponse.ifBlank { "Appointment reminder update." }
                                        )
                                    },
                                    onViewProperty = { onNavigateToPropertyDetail(viewing.propertyId) }
                                )
                            }
                        }
                    }
                    1 -> {
                        // Saved Properties (Favorites)
                        if (savedItems.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("No saved properties yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "Tap the heart icon on any listing in Explore or Home to bookmark it here for your upcoming visits.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(savedItems, key = { it.id }) { prop ->
                                PropertyCard(
                                    property = prop,
                                    locationDisplay = viewModel.getBarangayProvinceDisplay(prop),
                                    imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                                    isSaved = true,
                                    onSaveToggle = { viewModel.toggleSave(prop.id) },
                                    onClick = { onNavigateToPropertyDetail(prop.id) }
                                )
                            }
                        }
                    }
                    2 -> {
                        // Received Requests (Host View)
                        if (posterViewings.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 40.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("No received viewing requests", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "When prospective tenants request to tour your listed spaces or boarding houses, requests will appear here for you to confirm.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(posterViewings, key = { it.id }) { viewing ->
                                ViewingCard(
                                    viewing = viewing,
                                    isPoster = true,
                                    onAccept = {
                                        viewModel.updateViewingStatus(viewing, "CONFIRMED", "Confirmed by host.", sendEmailAlert = true)
                                    },
                                    onCancel = {
                                        viewModel.updateViewingStatus(viewing, "CANCELLED", "Cancelled by host.", sendEmailAlert = true)
                                    },
                                    onSendEmailAlert = {
                                        viewModel.dispatchViewingStatusNotificationEmail(
                                            recipientEmail = "tenant@balayinn.ph",
                                            propertyTitle = "Balay-Inn Viewing Appointment",
                                            status = viewing.status,
                                            date = viewing.preferredDate,
                                            time = viewing.preferredTime,
                                            notes = viewing.posterResponse.ifBlank { "Appointment reminder update." }
                                        )
                                    },
                                    onViewProperty = { onNavigateToPropertyDetail(viewing.propertyId) }
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
fun ViewingCard(
    viewing: ViewingRequestEntity,
    isPoster: Boolean,
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    onSendEmailAlert: () -> Unit,
    onViewProperty: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (viewing.status) {
                        "CONFIRMED" -> if (isDark) SoftGreenDarkBg else BadgeGreenBg
                        "REQUESTED" -> if (isDark) BadgeAmber.copy(alpha = 0.22f) else BadgeAmberBg
                        else -> if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = viewing.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (viewing.status) {
                            "CONFIRMED" -> if (isDark) SoftGreenDark else BadgeGreen
                            "REQUESTED" -> if (isDark) Color(0xFFFBBF24) else BadgeAmber
                            else -> if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Text(
                    text = "${viewing.numberOfVisitors} visitor(s)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = viewing.preferredDate, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "(${viewing.preferredTime})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }

            if (viewing.tenantMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${viewing.tenantMessage}\"",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            if (viewing.posterResponse.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Poster note: ${viewing.posterResponse}",
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewProperty,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Listing", fontSize = 11.5.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }

                if (isPoster && viewing.status == "REQUESTED") {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept", fontSize = 11.5.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }

                if (viewing.status != "CANCELLED" && viewing.status != "COMPLETED") {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Cancel", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.error, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }

                OutlinedButton(
                    onClick = onSendEmailAlert,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = "Send Email Alert", modifier = Modifier.size(16.dp), tint = Color(0xFFEA4335))
                }
            }
        }
    }
}
