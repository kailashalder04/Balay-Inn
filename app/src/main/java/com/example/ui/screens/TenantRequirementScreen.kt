package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.repository.PropertyMatchResult
import com.example.ui.components.CascadingLocationSelector
import com.example.ui.components.PropertyCard
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeGreen
import com.example.ui.viewmodel.MarketplaceViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantRequirementScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val requirements by viewModel.requirements.collectAsState()
    val properties by viewModel.properties.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedReqId by remember { mutableStateOf(requirements.firstOrNull()?.id) }

    val activeReq = requirements.find { it.id == selectedReqId } ?: requirements.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("What are you looking for?") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Requirement Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier.testTag("tenant_requirements_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Profile selector chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Requirement Profiles", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showCreateDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Profile", fontSize = 11.sp, maxLines = 1, softWrap = false)
                    }
                }
            }

            items(requirements) { req ->
                val isSelected = req.id == activeReq?.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedReqId = req.id },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.profileName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                "Location: ${req.preferredLocationName} • Max: ₱${req.maxBudget.toInt()}/mo",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Furnished: ${if (req.furnishedRequired) "Yes" else "Any"} • Parking: ${if (req.parkingRequired) "Yes" else "Any"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { viewModel.deleteRequirement(req.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (activeReq != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Transparent Matches for '${activeReq.profileName}'",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Every match explains exactly why it fits your budget, location, and amenity criteria. No black-box AI scores.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val matches = properties.map { prop ->
                    prop to viewModel.calculatePropertyMatch(prop, activeReq)
                }.sortedByDescending { it.second.scorePercentage }

                items(matches) { (property, matchResult) ->
                    MatchPropertyItem(
                        property = property,
                        match = matchResult,
                        locationDisplay = viewModel.getLocationDisplay(property),
                        onClick = { onNavigateToPropertyDetail(property.id) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateRequirementDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false },
            onSave = { req ->
                viewModel.addRequirement(req)
                selectedReqId = req.id
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun MatchPropertyItem(
    property: PropertyEntity,
    match: PropertyMatchResult,
    locationDisplay: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (match.scorePercentage >= 80) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                ) {
                    Text(
                        text = "${match.scorePercentage}% Match",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.scorePercentage >= 80) BadgeGreen else BadgeAmber,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        maxLines = 1,
                        softWrap = false
                    )
                }

                Text(
                    text = "₱${property.monthlyRent.toInt()} / mo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(property.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(locationDisplay, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent breakdown reasons
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                match.positiveReasons.take(3).forEach { reason ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BadgeGreen, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, fontSize = 11.sp, color = Color(0xFF065F46))
                    }
                }
                match.warningReasons.take(2).forEach { reason ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = BadgeAmber, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(reason, fontSize = 11.sp, color = Color(0xFF92400E))
                    }
                }
            }
        }
    }
}

@Composable
fun CreateRequirementDialog(
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit,
    onSave: (TenantRequirementEntity) -> Unit
) {
    val allProvinces by viewModel.allProvinces.collectAsState()
    val allCities by viewModel.allCities.collectAsState()

    var profileName by remember { mutableStateOf("") }
    var selectedProvinceId by remember { mutableStateOf("prov_bohol") }
    var selectedCityId by remember { mutableStateOf("bohol_panglao") }
    var maxBudgetStr by remember { mutableStateOf("25000") }
    var furnishedReq by remember { mutableStateOf(true) }
    var parkingReq by remember { mutableStateOf(false) }
    var petFriendlyReq by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Requirement Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = { profileName = it },
                    label = { Text("Profile Name") },
                    placeholder = { Text("e.g. Panglao Remote Work Base") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxBudgetStr,
                    onValueChange = { maxBudgetStr = it },
                    label = { Text("Max Monthly Rent (₱)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Must be Furnished", fontSize = 12.sp)
                    Switch(checked = furnishedReq, onCheckedChange = { furnishedReq = it })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Dedicated Parking", fontSize = 12.sp)
                    Switch(checked = parkingReq, onCheckedChange = { parkingReq = it })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Pet-Friendly Required", fontSize = 12.sp)
                    Switch(checked = petFriendlyReq, onCheckedChange = { petFriendlyReq = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (profileName.isNotBlank()) {
                        val cityName = allCities.find { it.id == selectedCityId }?.name ?: "Bohol"
                        onSave(
                            TenantRequirementEntity(
                                id = UUID.randomUUID().toString(),
                                userId = viewModel.currentUser.value.id,
                                profileName = profileName,
                                preferredProvinceId = selectedProvinceId,
                                preferredCityId = selectedCityId,
                                preferredLocationName = cityName,
                                maxBudget = maxBudgetStr.toDoubleOrNull() ?: 25000.0,
                                furnishedRequired = furnishedReq,
                                parkingRequired = parkingReq,
                                petFriendlyRequired = petFriendlyReq
                            )
                        )
                    }
                }
            ) {
                Text("Save Profile", maxLines = 1, softWrap = false)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", maxLines = 1, softWrap = false) }
        }
    )
}
