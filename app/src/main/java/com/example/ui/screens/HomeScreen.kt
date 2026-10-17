package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.screens.home.AdminHomeScreen
import com.example.ui.screens.home.LandlordHomeScreen
import com.example.ui.screens.home.RepresentativeHomeScreen
import com.example.ui.screens.home.TenantHomeScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToExplore: () -> Unit,
    onNavigateToPropertyDetail: (String) -> Unit,
    onNavigateToPostProperty: () -> Unit,
    onNavigateToVerificationCenter: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: (String?) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val role = UserRole.fromRoleString(currentUser.role)

    when (role) {
        UserRole.TENANT -> {
            TenantHomeScreen(
                viewModel = viewModel,
                currentUser = currentUser,
                onNavigateToExplore = onNavigateToExplore,
                onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                onNavigateToVerificationCenter = onNavigateToVerificationCenter,
                modifier = modifier
            )
        }
        UserRole.LANDLORD -> {
            LandlordHomeScreen(
                viewModel = viewModel,
                currentUser = currentUser,
                onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                onNavigateToPostProperty = onNavigateToPostProperty,
                onNavigateToVerificationCenter = onNavigateToVerificationCenter,
                modifier = modifier
            )
        }
        UserRole.REPRESENTATIVE -> {
            RepresentativeHomeScreen(
                viewModel = viewModel,
                currentUser = currentUser,
                onNavigateToPropertyDetail = onNavigateToPropertyDetail,
                onNavigateToPostProperty = onNavigateToPostProperty,
                onNavigateToVerificationCenter = onNavigateToVerificationCenter,
                modifier = modifier
            )
        }
        UserRole.ADMIN -> {
            AdminHomeScreen(
                viewModel = viewModel,
                currentUser = currentUser,
                onNavigateToAdminPanel = { },
                modifier = modifier
            )
        }
    }
}
