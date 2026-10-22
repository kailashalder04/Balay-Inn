package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun TermsOfServiceDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .testTag("terms_of_service_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Platform Rules & Philippine Rental Guidelines",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(end = 4.dp)
                ) {
                    TermsSection(
                        title = "1. Acceptance of Terms",
                        body = "By accessing or using the Balay-Inn mobile application, you agree to comply with these Terms of Service, applicable Philippine rental laws (e.g. Civil Code on Lease, Rent Control Act guidelines, DHSUD/DTI regulations), and Google Play Developer Program Policies."
                    )

                    TermsSection(
                        title = "2. User Roles & Account Eligibility",
                        body = "Users may register as Tenants, Landlords (Property Owners), or Authorized Representatives:\n\n" +
                                "• Tenants must be at least 18 years of age or possess parent/guardian authorization for student boarding house arrangements.\n" +
                                "• Landlords & Representatives must provide true, accurate property details and legitimate verification documents when requested."
                    )

                    TermsSection(
                        title = "3. Property Listing & Hosting Guidelines",
                        body = "Landlords and Representatives agree that:\n\n" +
                                "• Property listings must portray actual rates, true bed/room counts, and real amenities.\n" +
                                "• Fraudulent, phantom, or duplicate property listings are strictly prohibited and subject to immediate administrative ban.\n" +
                                "• Rental property pricing must display clear monthly rent in Philippine Pesos (PHP) without undisclosed mandatory fees."
                    )

                    TermsSection(
                        title = "4. Tenant Conduct & Inquiries",
                        body = "Tenants agree to conduct respectful communications with hosts, attend scheduled viewing tours promptly, and refrain from spamming or submitting fraudulent inquiry claims."
                    )

                    TermsSection(
                        title = "5. Account Termination & Data Erasure",
                        body = "Balay-Inn reserves the right to suspend accounts violating community standards. Users maintain the right to delete their account at any time via Settings > Security & Privacy Suite."
                    )

                    TermsSection(
                        title = "6. Limitation of Liability",
                        body = "Balay-Inn serves as a verified property matching and communication platform. Rental lease contracts signed between landlords and tenants remain independent legal agreements under Philippine Lease Laws."
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Accept Terms", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun TermsSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
        )
    }
}
