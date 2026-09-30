package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeBlue
import com.example.ui.theme.BadgeBlueBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LandlordHomeScreen(
    viewModel: MarketplaceViewModel,
    currentUser: UserEntity,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val managedProperties by viewModel.managedProperties.collectAsState()
    val userViewings by viewModel.userViewings.collectAsState()
    val conversations by viewModel.conversations.collectAsState()

    val activeListings = managedProperties.filter { it.status == "ACTIVE" }
    val pendingListings = managedProperties.filter { it.status == "PENDING" }
    val rentedListings = managedProperties.filter { it.status == "RENTED" }

    val totalViews = managedProperties.sumOf { it.viewsCount }
    val totalSaves = managedProperties.sumOf { it.savesCount }
    val pendingViewings = userViewings.filter { it.status == "REQUESTED" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Welcome, ${currentUser.fullName}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Property Owner • Level ${currentUser.verificationLevel} Verified Host",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Button(
                            onClick = onNavigateToPostProperty,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("landlord_post_property_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Post Property", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Listing status breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatusKpiCard(
                            label = "Active",
                            count = "${activeListings.size}",
                            color = BadgeGreen,
                            bg = BadgeGreenBg,
                            modifier = Modifier.weight(1f)
                        )
                        StatusKpiCard(
                            label = "Pending",
                            count = "${pendingListings.size}",
                            color = BadgeAmber,
                            bg = BadgeAmberBg,
                            modifier = Modifier.weight(1f)
                        )
                        StatusKpiCard(
                            label = "Rented",
                            count = "${rentedListings.size}",
                            color = Color(0xFF2563EB),
                            bg = Color(0xFFEFF6FF),
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
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (pendingViewings.isNotEmpty()) Color(0xFFFEF3C7) else Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "${pendingViewings.size} pending confirmation",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pendingViewings.isNotEmpty()) BadgeAmber else Color(0xFF64748B),
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
                LandlordViewingActionCard(
                    viewing = req,
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
                            Text(
                                text = conv.tenantName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
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
                TextButton(onClick = onNavigateToPostProperty) {
                    Text("+ Add Another", fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
        }

        items(managedProperties, key = { "landlord_prop_${it.id}" }) { prop ->
            LandlordPropertyManagementCard(
                property = prop,
                locationDisplay = viewModel.getLocationDisplay(prop),
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
                Text(
                    text = "Visit: ${viewing.preferredDate} (${viewing.preferredTime})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    softWrap = false
                )
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
    onToggleStatus: () -> Unit,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply { maximumFractionDigits = 0 }
    val isActive = property.status == "ACTIVE"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isActive) BadgeGreenBg else Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = property.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) BadgeGreen else Color(0xFF2563EB),
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

            Spacer(modifier = Modifier.height(6.dp))

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

                OutlinedButton(
                    onClick = onToggleStatus,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isActive) "Mark as Rented" else "Mark as Active",
                        fontSize = 10.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
