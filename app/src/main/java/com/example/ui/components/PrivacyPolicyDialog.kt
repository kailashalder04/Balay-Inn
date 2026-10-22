package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PrivacyPolicyDialog(
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
                .testTag("privacy_policy_dialog"),
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
                            modifier = Modifier
                                .size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Privacy Policy & Data Protection",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Compliance with Philippine RA 10173 & Google Play Policies",
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

                // Scrollable Policy Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(end = 4.dp)
                ) {
                    PolicySection(
                        title = "1. Introduction & Overview",
                        body = "Balay-Inn is committed to protecting the personal information of tenants, landlords, and authorized representatives in the Philippines. This Privacy Policy governs our data collection, usage, storage, and protection practices in compliance with the Philippine Data Privacy Act of 2012 (Republic Act No. 10173), its Implementing Rules and Regulations (IRR), National Privacy Commission (NPC) circulars, and Google Play Store Developer Policies."
                    )

                    PolicySection(
                        title = "2. Information We Collect",
                        body = "We collect information necessary to facilitate verified boarding house and rental property matchmaking:\n\n" +
                                "• Personal Identity Data: Full name, government identification details (e.g., UMID, Driver's License, Passport, PhilID), profile photo, and role status (Tenant, Landlord, Representative).\n" +
                                "• Contact Details: Email address and Philippine mobile phone number (+63).\n" +
                                "• Location & Search Preferences: Preferred municipality, province, university proximity, and listing radius.\n" +
                                "• Property Listing Information: Rental rates, amenity inclusions, house rules, and verified property photos.\n" +
                                "• Communication & Inquiries: Encrypted messages sent between tenants and property hosts."
                    )

                    PolicySection(
                        title = "3. How We Use Your Data",
                        body = "Your information is processed strictly for legitimate operational purposes:\n\n" +
                                "• Authenticating user accounts via Google Sign-In & Role-Based Access Control (RBAC).\n" +
                                "• Verification of property ownership and representative authorization documents.\n" +
                                "• Protecting user phone numbers through contact masking (+63 917 *** *567).\n" +
                                "• Managing viewing schedule requests and property inquiries.\n" +
                                "• Preventing fraudulent property listings and ensuring platform integrity."
                    )

                    PolicySection(
                        title = "4. Your Rights Under Philippine RA 10173 (Data Privacy Act)",
                        body = "As a data subject under Philippine law, you hold explicit rights regarding your personal information:\n\n" +
                                "• Right to be Informed: You have the right to know how your data is collected and processed.\n" +
                                "• Right to Access: You may view your profile, saved properties, and inquiry logs at any time in the app.\n" +
                                "• Right to Rectification: You may update or correct inaccurate profile details in your Dashboard.\n" +
                                "• Right to Erasure / Blocking: You have the right to suspend, withdraw, or request complete account and data deletion.\n" +
                                "• Right to Data Portability: You may request a copy of your stored personal data."
                    )

                    PolicySection(
                        title = "5. Data Retention & Google Play Account Deletion",
                        body = "We retain personal data only as long as necessary to fulfill rental services or legal requirements. You can request immediate account and data deletion directly within the Balay-Inn app under Settings > Security & Privacy Suite > Request Account Deletion. Upon confirmation, your profile, active listings, saved properties, and inquiry history will be permanently purged from our database."
                    )

                    PolicySection(
                        title = "6. Data Safety & Security Measures",
                        body = "We enforce technical and organizational safeguards including SSL/TLS transit encryption, secure screen capture protection (FLAG_SECURE on identity documents), contact number masking, and restricted database access. We do NOT sell, rent, or trade your personal data to third-party advertisers."
                    )

                    PolicySection(
                        title = "7. Contact Data Protection Officer (DPO)",
                        body = "For privacy inquiries, NPC compliance concerns, or data erasure requests, contact our Data Protection Officer at:\n\nEmail: dpo@balayinn.ph\nLocation: Manila, Philippines"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("I Understand & Accept", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
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
