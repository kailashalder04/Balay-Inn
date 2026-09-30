package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
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
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
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

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = As Tenant, 1 = As Landlord/Poster

    val viewingsToDisplay = if (selectedTab == 0) tenantViewings else posterViewings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Viewing Requests") },
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
                    text = { Text("My Inquiries (${tenantViewings.size})", maxLines = 1, softWrap = false) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Received Requests (${posterViewings.size})", maxLines = 1, softWrap = false) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 70.dp)
            ) {
                if (viewingsToDisplay.isEmpty()) {
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
                                Text("No viewing requests yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    if (selectedTab == 0) "Browse listings to request a physical or live property visit." else "You haven't received viewing requests for your posted properties yet.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(viewingsToDisplay, key = { it.id }) { viewing ->
                        ViewingCard(
                            viewing = viewing,
                            isPoster = selectedTab == 1,
                            onAccept = {
                                viewModel.updateViewingStatus(viewing, "CONFIRMED", "Confirmed by poster.")
                            },
                            onCancel = {
                                viewModel.updateViewingStatus(viewing, "CANCELLED", "Cancelled.")
                            },
                            onViewProperty = { onNavigateToPropertyDetail(viewing.propertyId) }
                        )
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (viewing.status) {
                        "CONFIRMED" -> BadgeGreenBg
                        "REQUESTED" -> BadgeAmberBg
                        else -> Color(0xFFF1F5F9)
                    }
                ) {
                    Text(
                        text = viewing.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (viewing.status) {
                            "CONFIRMED" -> BadgeGreen
                            "REQUESTED" -> BadgeAmber
                            else -> Color(0xFF64748B)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Text(
                    text = "${viewing.numberOfVisitors} visitor(s)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = viewing.preferredDate, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "(${viewing.preferredTime})", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (viewing.tenantMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${viewing.tenantMessage}\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            if (viewing.posterResponse.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Poster note: ${viewing.posterResponse}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProperty,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Listing", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }

                if (isPoster && viewing.status == "REQUESTED") {
                    Button(
                        onClick = onAccept,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Accept", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }

                if (viewing.status != "CANCELLED" && viewing.status != "COMPLETED") {
                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancel", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
