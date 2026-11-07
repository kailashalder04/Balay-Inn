package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationDetailDialog(
    notification: NotificationEntity,
    property: PropertyEntity? = null,
    onDismiss: () -> Unit,
    onNavigateToProperty: ((String) -> Unit)? = null,
    onNavigateToChat: ((String?) -> Unit)? = null,
    isDark: Boolean = false
) {
    val (catColor, catIcon) = remember(notification.category) {
        when (notification.category.uppercase()) {
            "VIEWING", "VIEWING_REQUEST", "VIEWING_CANCELLED", "VIEWING_RESPONSE" -> 
                Color(0xFFD97706) to Icons.Default.CalendarMonth
            "MESSAGE" -> 
                Color(0xFF2563EB) to Icons.Default.Chat
            "VERIFICATION" -> 
                Color(0xFF16A34A) to Icons.Default.Verified
            "PROPERTY_FULL", "STATUS", "SAVED", "PROPERTY_UNAVAILABLE" -> 
                Color(0xFFDC2626) to Icons.Default.Warning
            "APP_UPDATE" -> 
                Color(0xFF7C3AED) to Icons.Default.RocketLaunch
            "PRICE_DROP" -> 
                Color(0xFFEA580C) to Icons.AutoMirrored.Filled.TrendingDown
            else -> 
                Color(0xFF4F46E5) to Icons.Default.Notifications
        }
    }

    val formattedDate = remember(notification.createdAt) {
        val sdf = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
        sdf.format(Date(notification.createdAt))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_detail_dialog"),
        shape = RoundedCornerShape(24.dp),
        containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = catColor.copy(alpha = 0.15f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = catIcon,
                                contentDescription = null,
                                tint = catColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = catColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = notification.category.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = catColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = formattedDate,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )

                // Read Status Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Marked as read",
                        fontSize = 11.sp,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Message Body Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                    ),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.message,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // Related Property Preview (if available)
                if (property != null) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = property.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "₱${property.monthlyRent.toInt()}/mo • ${property.propertyType}",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (onNavigateToProperty != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        onDismiss()
                                        onNavigateToProperty(property.id)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onNavigateToChat != null && (notification.category in listOf("MESSAGE", "VIEWING", "VIEWING_REQUEST", "VIEWING_RESPONSE") || notification.relatedPropertyId != null)) {
                    Button(
                        onClick = {
                            onDismiss()
                            onNavigateToChat(null)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Chat", fontSize = 12.sp)
                    }
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Close", fontSize = 12.sp)
                }
            }
        }
    )
}
