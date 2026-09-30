package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.UserRole
import com.example.ui.screens.home.AdminHomeScreen
import com.example.ui.screens.home.LandlordHomeScreen
import com.example.ui.screens.home.RepresentativeHomeScreen
import com.example.ui.screens.home.TenantHomeScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToExplore: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onNavigateToAdminPanel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    var showUserMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // Top Bar with Role Switcher & Branding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Balay-Inn",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "Philippine Rental Marketplace",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Role switcher pill
            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .clickable { showUserMenu = true }
                        .testTag("role_switcher_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${currentUser.canonicalRole.roleBadge}: ${currentUser.fullName.split(" ").first()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                // Modal Role & Persona Switcher Dialog
                if (showUserMenu) {
                    AlertDialog(
                        onDismissRequest = { showUserMenu = false },
                        title = {
                            Text(
                                text = "Switch Persona & Role",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        },
                        text = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Select an authenticated profile to test role-specific features:",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                allUsers.forEach { user ->
                                    val isSelected = user.id == currentUser.id
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                showUserMenu = false
                                                viewModel.switchUser(user)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = user.fullName,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                                    ) {
                                                        Text(
                                                            text = user.canonicalRole.roleBadge,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "${user.role.replace("_", " ")} • ${user.relationshipToProperty}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Active",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showUserMenu = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
            }
        }

        // Dynamic Role-based Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (currentUser.canonicalRole) {
                UserRole.TENANT -> {
                    TenantHomeScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigateToExplore = onNavigateToExplore,
                        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                        onNavigateToVerificationCenter = onNavigateToVerificationCenter
                    )
                }
                UserRole.LANDLORD -> {
                    LandlordHomeScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                        onNavigateToPostProperty = onNavigateToPostProperty,
                        onNavigateToVerificationCenter = onNavigateToVerificationCenter
                    )
                }
                UserRole.REPRESENTATIVE -> {
                    RepresentativeHomeScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                        onNavigateToPostProperty = onNavigateToPostProperty,
                        onNavigateToVerificationCenter = onNavigateToVerificationCenter
                    )
                }
                UserRole.ADMIN -> {
                    AdminHomeScreen(
                        viewModel = viewModel,
                        currentUser = currentUser,
                        onNavigateToAdminPanel = onNavigateToAdminPanel
                    )
                }
            }
        }
    }
}
