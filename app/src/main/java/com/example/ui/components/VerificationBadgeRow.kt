package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeBlue
import com.example.ui.theme.BadgeBlueBg
import com.example.ui.theme.BadgeGray
import com.example.ui.theme.BadgeGrayBg
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgePurple
import com.example.ui.theme.BadgePurpleBg

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerificationBadgeRow(
    property: PropertyEntity,
    modifier: Modifier = Modifier,
    showDetailsDialogOnClick: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .then(if (showDetailsDialogOnClick) Modifier.clickable { showDialog = true } else Modifier),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (property.phoneVerified) {
            BadgeChip(label = "Phone Verified", color = BadgeGreen, bg = BadgeGreenBg, isVerified = true)
        }
        if (property.emailVerified) {
            BadgeChip(label = "Email Verified", color = BadgeGreen, bg = BadgeGreenBg, isVerified = true)
        }
        if (property.identitySubmitted) {
            BadgeChip(label = "Identity Submitted", color = BadgeBlue, bg = BadgeBlueBg, isVerified = true)
        }
        if (property.professionalVerified) {
            BadgeChip(label = "PRC Verified", color = BadgePurple, bg = BadgePurpleBg, isVerified = true)
        }
        if (!property.propertyVisited) {
            BadgeChip(label = "Pending Visit", color = BadgeAmber, bg = BadgeAmberBg, isVerified = false)
        } else {
            BadgeChip(label = "Property Visited", color = BadgeGreen, bg = BadgeGreenBg, isVerified = true)
        }
        if (property.isPosterAuthorized && property.posterRole.contains("Authorized", ignoreCase = true)) {
            BadgeChip(label = "Authorized Representative", color = BadgeBlue, bg = BadgeBlueBg, isVerified = true)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Layered Verification Info", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Balay-Inn conducts transparent verification checks to establish credibility and reduce rental risks:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    VerificationItemRow(title = "Phone & Email", desc = "Verified through SMS OTP and email token.", verified = property.phoneVerified && property.emailVerified)
                    VerificationItemRow(title = "Identity Document", desc = if (property.identitySubmitted) "Government ID submitted to Balay-Inn staff." else "ID not yet submitted.", verified = property.identitySubmitted)
                    VerificationItemRow(title = "Professional / Broker PRC", desc = if (property.professionalVerified) "Real Estate Broker license verified." else "Standard landlord/representative.", verified = property.professionalVerified)
                    VerificationItemRow(title = "Physical Property Visit", desc = if (property.propertyVisited) "Verified by Balay-Inn site staff." else "Pending physical site visit.", verified = property.propertyVisited)

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BadgeAmberBg)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Disclaimer: Verification badges describe information reviewed by Balay-Inn. They do not guarantee ownership, property condition, or transaction safety. Never wire funds before physically inspecting the premises.",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 15.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Understand")
                }
            }
        )
    }
}

@Composable
fun BadgeChip(
    label: String,
    color: Color,
    bg: Color,
    isVerified: Boolean
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 7.dp, vertical = 3.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun VerificationItemRow(
    title: String,
    desc: String,
    verified: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = if (verified) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (verified) BadgeGreen else BadgeAmber,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
