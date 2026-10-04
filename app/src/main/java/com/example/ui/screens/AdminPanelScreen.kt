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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeGreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allReports by viewModel.allReports.collectAsState()
    val allDocuments by viewModel.allDocuments.collectAsState()
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()
    val allBarangays by viewModel.allBarangays.collectAsState()
    val properties by viewModel.properties.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Locations, 1 = Moderation/Reports, 2 = Document Verification, 3 = Analytics

    var showAddLocationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin & Compliance Hub") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("admin_panel_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Locations (${allCities.size} LGUs)", maxLines = 1, softWrap = false) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Reports (${allReports.size})", maxLines = 1, softWrap = false) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Documents (${allDocuments.size})", maxLines = 1, softWrap = false) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Market Analytics", maxLines = 1, softWrap = false) })
            }

            when (selectedTab) {
                0 -> {
                    // Location Administration
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 60.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Administrative Geographic Coverage", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "Database-driven hierarchy. Bohol has 48 complete LGUs (Tagbilaran + 47 municipalities) and Cebu province seeded. Administrators can add any future Philippine province or municipality.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Provinces & LGUs in Central Visayas", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, softWrap = false)
                                Button(
                                    onClick = { showAddLocationDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Location", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                }
                            }
                        }

                        items(allProvinces) { prov ->
                            val provCities = allCities.filter { it.provinceId == prov.id }
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("${prov.name} (${prov.code})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        val isDark = isSystemInDarkTheme()
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (prov.isActive) {
                                                if (isDark) BadgeGreen.copy(alpha = 0.22f) else Color(0xFFECFDF5)
                                            } else {
                                                if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
                                            }
                                        ) {
                                            Text(
                                                if (prov.isActive) "Active" else "Disabled",
                                                color = if (prov.isActive) {
                                                    if (isDark) Color(0xFF34D399) else BadgeGreen
                                                } else {
                                                    if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "${provCities.size} Cities & Municipalities • Complete administrative coverage",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            Text("Bohol 48 Administrative Municipalities & Cities:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        val boholCities = allCities.filter { it.provinceId == "prov_bohol" }
                        items(boholCities) { city ->
                            val cityBgys = allBarangays.filter { it.cityMunicipalityId == city.id }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(city.name, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                    Text("${city.type} • ${cityBgys.size} barangays", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(city.code, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                1 -> {
                    // Reports & Moderation
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allReports) { report ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Reason: ${report.reason.replace("_", " ")}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        val isDark = isSystemInDarkTheme()
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (report.status == "PENDING") {
                                                if (isDark) BadgeAmber.copy(alpha = 0.22f) else Color(0xFFFEF3C7)
                                            } else {
                                                if (isDark) BadgeGreen.copy(alpha = 0.22f) else Color(0xFFECFDF5)
                                            }
                                        ) {
                                            Text(
                                                report.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (report.status == "PENDING") {
                                                    if (isDark) Color(0xFFFBBF24) else BadgeAmber
                                                } else {
                                                    if (isDark) Color(0xFF34D399) else BadgeGreen
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Details: ${report.details.ifBlank { "Flagged for safety review." }}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = { viewModel.updateReport(report, "DISMISSED") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Dismiss", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                        }
                                        Button(
                                            onClick = { viewModel.updateReport(report, "ACTIONED") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Resolve / Action", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Documents Verification Queue
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(allDocuments) { doc ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(doc.documentType.replace("_", " "), fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, softWrap = false)
                                        val isDark = isSystemInDarkTheme()
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (doc.status == "VERIFIED") {
                                                if (isDark) BadgeGreen.copy(alpha = 0.22f) else Color(0xFFECFDF5)
                                            } else {
                                                if (isDark) Color(0xFF2563EB).copy(alpha = 0.22f) else Color(0xFFEFF6FF)
                                            }
                                        ) {
                                            Text(
                                                doc.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (doc.status == "VERIFIED") {
                                                    if (isDark) Color(0xFF34D399) else BadgeGreen
                                                } else {
                                                    if (isDark) Color(0xFF60A5FA) else Color(0xFF2563EB)
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("File: ${doc.fileName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                                    if (doc.reviewerNotes.isNotBlank()) {
                                        Text("Staff Note: ${doc.reviewerNotes}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    if (doc.status != "VERIFIED") {
                                        Button(
                                            onClick = { viewModel.reviewDocument(doc, "VERIFIED", "Verified by compliance officer.") },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Approve & Verify Badge", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Platform Listing Market Insights (Section 37 & 39)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Balay-Inn Platform Listing Data", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Anonymized platform listing and search trends. Not official Philippine government census data.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        item {
                            AnalyticsCard(title = "Bohol Province Typical Rent", value = "₱13,500 – ₱48,000 / month", subtitle = "Based on Panglao, Dauis, Tagbilaran, Carmen listings")
                        }

                        item {
                            AnalyticsCard(title = "Cebu Province Typical Rent", value = "₱4,500 – ₱32,000 / month", subtitle = "Based on Cebu IT Park, Lahug, Guadalupe rooms/condos")
                        }

                        item {
                            AnalyticsCard(title = "Most Searched Municipalities", value = "1. Panglao  2. Cebu City  3. Dauis", subtitle = "High demand for furnished beachfront & IT hub rentals")
                        }

                        item {
                            AnalyticsCard(title = "Average Time to First Inquiry", value = "2.4 Days", subtitle = "Verified listings receive inquiries 3x faster")
                        }
                    }
                }
            }
        }
    }

    if (showAddLocationDialog) {
        var newProvName by remember { mutableStateOf("") }
        var newProvCode by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddLocationDialog = false },
            title = { Text("Add Philippine Province") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Add a new Philippine province to the database. All child municipalities and barangays can subsequently be attached without schema changes.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = newProvName,
                        onValueChange = { newProvName = it },
                        label = { Text("Province Name") },
                        placeholder = { Text("e.g. Negros Oriental") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newProvCode,
                        onValueChange = { newProvCode = it },
                        label = { Text("Province Code") },
                        placeholder = { Text("e.g. NEG") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newProvName.isNotBlank() && newProvCode.isNotBlank()) {
                            viewModel.addProvinceAdmin(newProvName, newProvCode)
                            showAddLocationDialog = false
                        }
                    }
                ) {
                    Text("Add Province", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLocationDialog = false }) { Text("Cancel", maxLines = 1, softWrap = false) }
            }
        )
    }
}

@Composable
private fun AnalyticsCard(title: String, value: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
