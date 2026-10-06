package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeBlue
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.SoftGreenBg
import com.example.ui.theme.SoftGreenDark
import com.example.ui.theme.SoftGreenDarkBg
import com.example.ui.viewmodel.MarketplaceViewModel

data class VerificationLevelItem(
    val levelNumber: Int,
    val title: String,
    val description: String,
    val status: String // VERIFIED, SUBMITTED, NOT_STARTED, UNDER_REVIEW
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCenterScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userDocuments by viewModel.userDocuments.collectAsState()

    var showUploadDialog by remember { mutableStateOf(false) }

    val levels = listOf(
        VerificationLevelItem(0, "Account Created", "Basic account profile registered", "VERIFIED"),
        VerificationLevelItem(1, "Phone Verified", "Philippine mobile number verified via OTP", if (currentUser.phoneVerified) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(2, "Email Verified", "Email confirmation link authenticated", if (currentUser.emailVerified) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(3, "Identity Document", "Government ID (Passport, UMID, Driver's License)", if (currentUser.identitySubmitted) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(4, "Professional Information", "Broker / Salesperson / Representative profile", if (currentUser.role.contains("BROKER") || currentUser.role.contains("SALESPERSON") || currentUser.role.contains("CARETAKER")) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(5, "PRC Information Verified", "PRC Board Exam License authenticated", if (currentUser.professionalVerified) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(6, "Property Authorization", "Notarized authorization letter or SPA from owner", if (currentUser.isAuthorizedByOwner) "VERIFIED" else "NOT_STARTED"),
        VerificationLevelItem(7, "Property Physically Visited", "On-site visit by Balay-Inn local staff", "NOT_STARTED"),
        VerificationLevelItem(8, "Property Documents Reviewed", "Title / Tax Declaration verification review", "NOT_STARTED")
    )

    val verifiedCount = levels.count { it.status == "VERIFIED" }
    val progress = verifiedCount.toFloat() / levels.size.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Layered Verification Center") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("verification_center_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Header Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Your Profile: Level ${currentUser.verificationLevel} Verification",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$verifiedCount of ${levels.size} levels completed",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Section 8: Document Privacy Guarantee
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Strict Privacy & Data Minimization", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Your submitted government IDs, PRC licenses, and authorization letters are strictly private and encrypted. They are never published or made visible to tenants or other users. You can choose to verify later at any time.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Action: Upload Document button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Verification Steps (Level 0 - 8)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showUploadDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Document", fontSize = 11.sp, maxLines = 1, softWrap = false)
                    }
                }
            }

            // Verification Level List
            items(levels) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDark = isSystemInDarkTheme()
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (item.status == "VERIFIED") {
                                        if (isDark) SoftGreenDarkBg else SoftGreenBg
                                    } else {
                                        if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L${item.levelNumber}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (item.status == "VERIFIED") {
                                    if (isDark) SoftGreenDark else BadgeGreen
                                } else {
                                    if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)
                                },
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, softWrap = false)
                            Text(item.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (item.status) {
                                "VERIFIED" -> if (isDark) SoftGreenDarkBg else SoftGreenBg
                                "SUBMITTED" -> if (isDark) BadgeBlue.copy(alpha = 0.22f) else Color(0xFFEFF6FF)
                                else -> if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
                            }
                        ) {
                            Text(
                                text = when (item.status) {
                                    "VERIFIED" -> "✓ Verified"
                                    "SUBMITTED" -> "Under Review"
                                    else -> "Verify Later"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (item.status) {
                                    "VERIFIED" -> if (isDark) SoftGreenDark else BadgeGreen
                                    "SUBMITTED" -> if (isDark) Color(0xFF60A5FA) else BadgeBlue
                                    else -> if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF64748B)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }

    // Submit Document Dialog
    if (showUploadDialog) {
        var docType by remember { mutableStateOf("GOVERNMENT_ID") }
        var fileName by remember { mutableStateOf("ph_national_id_card.pdf") }

        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            title = { Text("Submit Verification Document") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Upload documents privately. Only authorized Balay-Inn compliance staff will review them.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val docTypes = listOf(
                        "GOVERNMENT_ID" to "Government ID (Passport, UMID, DL)",
                        "PRC_ID" to "PRC Real Estate License",
                        "AUTHORIZATION_LETTER" to "Owner Authorization Letter / SPA",
                        "TITLE_TAX_DEC" to "Property Title or Tax Declaration"
                    )

                    docTypes.forEach { (code, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { docType = code }
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = docType == code,
                                onClick = { docType = code }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }

                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("Document File Name / Reference") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitVerificationDoc(docType, fileName) {
                            showUploadDialog = false
                        }
                    }
                ) {
                    Text("Submit for Review", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) { Text("Cancel", maxLines = 1, softWrap = false) }
            }
        )
    }
}
