package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhonelinkLock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SteelBlue

enum class PolicyTab(val title: String, val icon: ImageVector) {
    PRIVACY("Privacy Policy", Icons.Default.PrivacyTip),
    TERMS("Terms of Service", Icons.Default.Assignment),
    DATA_SAFETY("Data Safety", Icons.Default.Shield)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalPolicyScreen(
    initialTab: PolicyTab = PolicyTab.PRIVACY,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val isDark = isSystemInDarkTheme()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("legal_policy_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Legal & Compliance Center",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Balay-Inn • Philippine RA 10173 & Google Play Policies",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("legal_policy_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                PolicyTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        },
                        modifier = Modifier.testTag("policy_tab_${tab.name.lowercase()}")
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Scrollable Content View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        PolicyTab.PRIVACY -> PrivacyPolicyContent(isDark = isDark)
                        PolicyTab.TERMS -> TermsOfServiceContent(isDark = isDark)
                        PolicyTab.DATA_SAFETY -> DataSafetyContent(isDark = isDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicyContent(isDark: Boolean) {
    ComplianceBadgeCard(
        badgeTitle = "🇵🇭 Philippine RA 10173 & NPC Compliant",
        subtitle = "Official Data Privacy Policy for Balay-Inn Mobile Application",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(16.dp))

    PolicySectionCard(
        number = "1",
        title = "Scope & Legal Framework",
        body = "This Privacy Policy governs the collection, processing, storage, and protection of personal information on Balay-Inn in compliance with the Philippine Data Privacy Act of 2012 (Republic Act No. 10173), its Implementing Rules and Regulations (IRR), and Google Play Developer Program Policies.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "2",
        title = "Personal Data Collected",
        body = "To facilitate verified boarding house matchmaking, we collect:\n\n" +
                "• Identity Credentials: Full name, profile image, and government ID (UMID, Driver's License, PhilID, Passport) for verification.\n" +
                "• Contact Details: Email address and Philippine mobile number (+63).\n" +
                "• Search & Location Data: Province, municipality, university, and radius preferences.\n" +
                "• Property Listings: Photos, pricing, amenities, and house rules.\n" +
                "• In-App Communications: Encrypted messages between tenants and hosts.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "3",
        title = "Your Rights Under Philippine RA 10173",
        body = "As a data subject under Philippine law, you maintain full control:\n\n" +
                "• Right to be Informed: Transparency on all processing activities.\n" +
                "• Right to Access & Rectify: Ability to view and edit profile info.\n" +
                "• Right to Erasure / Account Deletion: You can request complete account and data purge directly in App Settings at any time.\n" +
                "• Right to Data Portability: Export your stored profile and saved properties.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "4",
        title = "Data Protection Officer (DPO) Contact",
        body = "For National Privacy Commission (NPC) inquiries or data privacy requests:\n\n" +
                "Data Protection Officer: dpo@balayinn.ph\n" +
                "Location: Manila, Philippines",
        isDark = isDark
    )
}

@Composable
private fun TermsOfServiceContent(isDark: Boolean) {
    ComplianceBadgeCard(
        badgeTitle = "📜 Platform Terms & Philippine Rental Regulations",
        subtitle = "User Agreement & Host Guidelines for Balay-Inn",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(16.dp))

    PolicySectionCard(
        number = "1",
        title = "Acceptance & Rental Regulations",
        body = "By using Balay-Inn, you agree to these Terms of Service, the Philippine Civil Code provisions on Lease, the Rent Control Act, and DHSUD/DTI boarding house registration standards.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "2",
        title = "Host Listing & Pricing Standards",
        body = "Landlords and Authorized Representatives agree that:\n\n" +
                "• All property listings must display authentic photos, true monthly rent in Philippine Pesos (PHP), and clear amenity inclusions.\n" +
                "• Phantom, duplicate, or deceptive property listings are strictly banned.\n" +
                "• Verification badges require valid government ID and property authorization proof.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "3",
        title = "Tenant Conduct & Inquiries",
        body = "Tenants agree to communicate respectfully with property owners, attend scheduled viewing tours on time, and refrain from fraudulent or spam inquiries.",
        isDark = isDark
    )

    PolicySectionCard(
        number = "4",
        title = "Account Termination & Account Purge",
        body = "Users may terminate their account and request complete data deletion at any time in Profile > Security & Privacy Suite.",
        isDark = isDark
    )
}

@Composable
private fun DataSafetyContent(isDark: Boolean) {
    ComplianceBadgeCard(
        badgeTitle = "🛡️ Google Play Data Safety & Security Architecture",
        subtitle = "Technical Safeguards & Privacy Protections",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(16.dp))

    DataSafetyFeatureCard(
        icon = Icons.Default.VisibilityOff,
        title = "Contact Phone Masking",
        description = "Mobile phone numbers are masked (+63 917 *** *567) on public cards and only revealed when intentionally authorized.",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    DataSafetyFeatureCard(
        icon = Icons.Default.PhonelinkLock,
        title = "Secure Screen Capture Blur (FLAG_SECURE)",
        description = "Enforces Android FLAG_SECURE window protection to block screenshot/screen recording on sensitive government ID & document verification views.",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    DataSafetyFeatureCard(
        icon = Icons.Default.Lock,
        title = "Transit Encryption & Zero Third-Party Selling",
        description = "All user credentials, ID uploads, and chat messages are encrypted in transit via SSL/TLS. Personal data is never sold or shared with external advertising networks.",
        isDark = isDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    DataSafetyFeatureCard(
        icon = Icons.Default.Security,
        title = "Runtime Device Integrity Guard",
        description = "Monitors device security posture and detects root binaries to protect user authentication tokens.",
        isDark = isDark
    )
}

@Composable
private fun ComplianceBadgeCard(
    badgeTitle: String,
    subtitle: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFEDF4FA)
        ),
        border = BorderStroke(1.dp, SteelBlue.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SteelBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badgeTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Text(
                    text = subtitle,
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                )
            }
        }
    }
}

@Composable
private fun PolicySectionCard(
    number: String,
    title: String,
    body: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = SteelBlue.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = number,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SteelBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.5.sp,
                lineHeight = 18.sp,
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
            )
        }
    }
}

@Composable
private fun DataSafetyFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SteelBlue.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SteelBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = if (isDark) Color.White else Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}
