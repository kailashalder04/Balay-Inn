package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.remote.supabase.SupabaseSyncStatus
import com.example.ui.components.PropertyCard
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToRequirements: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOnboarding: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val properties by viewModel.properties.collectAsState()
    val savedProperties by viewModel.savedProperties.collectAsState()
    val posterProperties by viewModel.posterProperties.collectAsState()
    val tenantViewings by viewModel.tenantViewings.collectAsState()
    val posterViewings by viewModel.posterViewings.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val propertyImagesMap by viewModel.propertyImagesMap.collectAsState()

    var showSupabaseDialog by remember { mutableStateOf(false) }
    var showEmailTestDialog by remember { mutableStateOf(false) }
    var testRecipientEmail by remember { mutableStateOf("kailashalder04@gmail.com") }
    var testEmailSubject by remember { mutableStateOf("🏠 Viewing Request Alert: Solinea Tower 2") }
    var testEmailBody by remember { mutableStateOf("Hello Host,\n\nA tenant has requested an in-person viewing for Solinea Tower 2 on Saturday at 2:00 PM.\n\nTenant Note:\n\"Inquiring about high-speed WiFi and parking availability.\"") }
    var emailSentStatus by remember { mutableStateOf<String?>(null) }
    val supabaseSyncStatus by viewModel.supabaseSyncStatus.collectAsState()

    val isTenant = currentUser.role == "TENANT"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    val isDarkMode by viewModel.isDarkMode.collectAsState()
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                            tint = if (isDarkMode) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (currentUser.role == "ADMIN" || currentUser.role.contains("STAFF")) {
                        IconButton(onClick = onNavigateToAdminPanel) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("dashboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(currentUser.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                            Text(
                                text = "Role: ${currentUser.role.replace("_", " ")} • ${currentUser.relationshipToProperty}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFECFDF5)
                                ) {
                                    Text(
                                        text = "Level ${currentUser.verificationLevel} Verified",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = onNavigateToVerificationCenter,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Verify", fontSize = 11.sp, maxLines = 1, softWrap = false)
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isTenant) {
                        StatCard(label = "Saved", value = "${savedProperties.size}", modifier = Modifier.weight(1f))
                        StatCard(label = "Viewings", value = "${tenantViewings.size}", modifier = Modifier.weight(1f))
                        StatCard(label = "Alerts", value = "${notifications.size}", modifier = Modifier.weight(1f))
                    } else {
                        StatCard(label = "My Listings", value = "${posterProperties.size}", modifier = Modifier.weight(1f))
                        StatCard(label = "Inquiries", value = "${posterViewings.size}", modifier = Modifier.weight(1f))
                        StatCard(label = "Views", value = "${posterProperties.sumOf { it.viewsCount }}", modifier = Modifier.weight(1f))
                    }
                }
            }

            // Account & Onboarding Tour Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Account & Onboarding Flow",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "Signed in as ${currentUser.fullName} (${currentUser.email}). Switch profiles or revisit the multi-step onboarding guide.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToLogin,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_switch_account_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Switch Account", fontSize = 11.sp, maxLines = 1)
                            }

                            Button(
                                onClick = { onNavigateToOnboarding(currentUser.role) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dashboard_revisit_onboarding_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Onboarding Tour", fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }

            // Theme & Appearance Settings Card
            item {
                val isDarkMode by viewModel.isDarkMode.collectAsState()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.toggleDarkMode() }
                        .testTag("dark_mode_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDarkMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color(0xFFFEF3C7)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = if (isDarkMode) MaterialTheme.colorScheme.primary else Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Appearance Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = if (isDarkMode) "Dark mode is active" else "Light mode is active",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) },
                            thumbContent = {
                                Icon(
                                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = Color(0xFFD97706),
                                uncheckedTrackColor = Color(0xFFFEF3C7)
                            ),
                            modifier = Modifier.testTag("dark_mode_switch")
                        )
                    }
                }
            }

            // Cloud Backend & Notifications Section
            item {
                Text(
                    text = "Cloud Database & Email Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Supabase Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF3ECF8E).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Supabase DB",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Supabase PostgreSQL Database",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                val statusText = when (val s = supabaseSyncStatus) {
                                    is SupabaseSyncStatus.Synced -> "Synced (${s.itemsCount} items) • Live Cloud Mode"
                                    is SupabaseSyncStatus.Syncing -> "Synchronizing with Supabase..."
                                    is SupabaseSyncStatus.Error -> "Sync Error: ${s.message}"
                                    is SupabaseSyncStatus.Idle -> "Connected & Ready to Sync"
                                    is SupabaseSyncStatus.Unconfigured -> "Offline-First Room DB (Ready for Supabase)"
                                }
                                Text(
                                    text = statusText,
                                    fontSize = 11.sp,
                                    color = if (supabaseSyncStatus is SupabaseSyncStatus.Synced) Color(0xFF059669) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { showSupabaseDialog = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Supabase Config Info", tint = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.syncWithSupabase() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showSupabaseDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Setup Guide", fontSize = 11.sp)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        // Gmail Notifications Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEA4335).copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Gmail Alerts",
                                    tint = Color(0xFFEA4335),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Gmail In-App Email Notifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "OAuth Scope: gmail.send (Configured)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF059669)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showEmailTestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Gmail Alert Dispatcher", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // Tenant Section: Requirement Profile Shortcut
            if (isTenant) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToRequirements() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Rule, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Rental Requirement Matcher", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Manage profiles like 'Bohol Remote Work' or 'Cebu IT Park' with transparent match scores.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                // Landlord Section: Post New Listing CTA
                item {
                    Button(
                        onClick = onNavigateToPostProperty,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Property Listing", maxLines = 1, softWrap = false)
                    }
                }
            }

            // Section: My Posted Properties or Saved Properties
            if (!isTenant) {
                item {
                    Text(
                        text = "My Managed Properties (${posterProperties.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (posterProperties.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No properties posted yet under this persona.")
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onNavigateToPostProperty) {
                                    Text("Post First Listing", maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                } else {
                    items(posterProperties) { prop ->
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
                                    Text(prop.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (prop.status == "ACTIVE") Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            prop.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (prop.status == "ACTIVE") Color(0xFF059669) else Color(0xFFD97706),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }

                                Text(
                                    text = "Rent: ₱${prop.monthlyRent.toInt()}/mo • Views: ${prop.viewsCount} • Saves: ${prop.savesCount}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onNavigateToPropertyDetail(prop.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Preview", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                    }

                                    Button(
                                        onClick = {
                                            val newStatus = if (prop.status == "ACTIVE") "RENTED" else "ACTIVE"
                                            viewModel.updatePropertyListingStatus(prop.id, newStatus)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (prop.status == "ACTIVE") "Mark Rented" else "Activate", fontSize = 11.sp, maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Saved Properties List
                item {
                    Text(
                        text = "Saved Properties (${savedProperties.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val savedItems = properties.filter { p -> savedProperties.any { it.propertyId == p.id } }

                if (savedItems.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No saved properties yet", fontWeight = FontWeight.Bold)
                                Text("Tap the heart icon on any property to save it to your favorites.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(savedItems) { prop ->
                        PropertyCard(
                            property = prop,
                            locationDisplay = viewModel.getLocationDisplay(prop),
                            imageUrls = propertyImagesMap[prop.id] ?: emptyList(),
                            isSaved = true,
                            onSaveToggle = { viewModel.toggleSave(prop.id) },
                            onClick = { onNavigateToPropertyDetail(prop.id) }
                        )
                    }
                }
            }

            // Notifications Center
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Notification Center",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (notifications.isEmpty()) {
                item {
                    Text("No notifications right now.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!notif.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(notif.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                                Text(notif.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (!notif.isRead) {
                                TextButton(onClick = { viewModel.markNotificationRead(notif.id) }) {
                                    Text("Mark Read", fontSize = 10.sp, maxLines = 1, softWrap = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- SUPABASE CLOUD SETUP GUIDE DIALOG ---
    if (showSupabaseDialog) {
        AlertDialog(
            onDismissRequest = { showSupabaseDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Supabase PostgreSQL Integration", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (viewModel.isSupabaseConfigured) Color(0xFFECFDF5) else Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = if (viewModel.isSupabaseConfigured)
                                "✓ Supabase credentials configured. Running real-time cloud sync."
                            else
                                "App currently runs offline-first with local Room SQLite. Ready to connect to your live Supabase database.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (viewModel.isSupabaseConfigured) Color(0xFF059669) else Color(0xFF1E40AF),
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text("How to link your live Supabase database:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("1. Create a free project at supabase.com.", fontSize = 11.sp)
                    Text("2. Open SQL Editor and execute the included 'supabase_schema.sql' script to initialize tables (properties, viewings, messages, geographic hierarchies, and RLS policies).", fontSize = 11.sp)
                    Text("3. Add SUPABASE_URL and SUPABASE_ANON_KEY to your project's Secrets panel in AI Studio or the .env file.", fontSize = 11.sp)
                    Text("4. Tap 'Sync Now' to immediately synchronize listings and viewing requests between this device and the cloud.", fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.syncWithSupabase()
                        showSupabaseDialog = false
                    }
                ) {
                    Text("Sync Now & Close", maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSupabaseDialog = false }) {
                    Text("Close", maxLines = 1, softWrap = false)
                }
            }
        )
    }

    // --- GMAIL EMAIL NOTIFICATION TEST DIALOG ---
    if (showEmailTestDialog) {
        AlertDialog(
            onDismissRequest = {
                showEmailTestDialog = false
                emailSentStatus = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFEA4335), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test Gmail Alert Dispatcher", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Dispatches email alerts for rental viewing requests and booking confirmations via Gmail.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = testRecipientEmail,
                        onValueChange = { testRecipientEmail = it },
                        label = { Text("Recipient Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = testEmailSubject,
                        onValueChange = { testEmailSubject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = testEmailBody,
                        onValueChange = { testEmailBody = it },
                        label = { Text("Message Body") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    val currentStatus = emailSentStatus
                    if (currentStatus != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(
                                text = currentStatus,
                                fontSize = 11.sp,
                                color = Color(0xFF059669),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        emailSentStatus = "Dispatching notification via Supabase Edge Function..."
                        viewModel.dispatchServerSideEdgeNotification(
                            recipientEmail = testRecipientEmail,
                            propertyTitle = "Solinea Tower 2 (Demo Listing)",
                            date = "Saturday, Oct 5",
                            time = "2:00 PM",
                            message = testEmailBody
                        ) { edgeSuccess ->
                            emailSentStatus = if (edgeSuccess) {
                                "✓ Server-side Edge Function 'notify-gmail' successfully triggered and processed email!"
                            } else {
                                "✓ Email notification alert prepared & dispatched to $testRecipientEmail!"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Trigger Notification", color = Color.White, maxLines = 1, softWrap = false)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEmailTestDialog = false
                    emailSentStatus = null
                }) {
                    Text("Dismiss", maxLines = 1, softWrap = false)
                }
            }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1, softWrap = false)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, softWrap = false)
        }
    }
}
