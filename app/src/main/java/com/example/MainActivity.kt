package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PostPropertyScreen
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.screens.TenantRequirementScreen
import com.example.ui.screens.VerificationCenterScreen
import com.example.ui.screens.ViewingRequestsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MarketplaceViewModel

sealed class AppScreen {
    object Home : AppScreen()
    object Explore : AppScreen()
    object Notifications : AppScreen()
    object Post : AppScreen()
    object Viewings : AppScreen()
    data class Messages(val conversationId: String? = null) : AppScreen()
    object Dashboard : AppScreen()
    data class PropertyDetail(val propertyId: String) : AppScreen()
    object TenantRequirements : AppScreen()
    object VerificationCenter : AppScreen()
    object AdminPanel : AppScreen()
    object Login : AppScreen()
    data class Onboarding(val initialRole: String? = null) : AppScreen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MarketplaceViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                BalayInnApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BalayInnApp(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val backStack = remember { mutableStateListOf<AppScreen>(AppScreen.Home) }
    val currentScreen = backStack.lastOrNull() ?: AppScreen.Home

    fun navigateTo(screen: AppScreen) {
        if (screen is AppScreen.Home || screen is AppScreen.Explore || screen is AppScreen.Viewings ||
            (screen is AppScreen.Messages && screen.conversationId == null) || screen is AppScreen.Dashboard) {
            // Root destination: clear stack and push
            backStack.clear()
            backStack.add(screen)
        } else {
            backStack.add(screen)
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
        } else {
            backStack.clear()
            backStack.add(AppScreen.Home)
        }
    }

    // Handle system back button for sub-screens
    if (backStack.size > 1) {
        BackHandler {
            navigateBack()
        }
    }

    val isRootScreen = currentScreen is AppScreen.Home || currentScreen is AppScreen.Explore ||
        currentScreen is AppScreen.Viewings ||
        (currentScreen is AppScreen.Messages && currentScreen.conversationId == null) ||
        currentScreen is AppScreen.Dashboard

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (isRootScreen) {
                ResponsiveBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { navigateTo(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is AppScreen.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToExplore = { navigateTo(AppScreen.Explore) },
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) },
                        onNavigateToPostProperty = { navigateTo(AppScreen.Post) },
                        onNavigateToVerificationCenter = { navigateTo(AppScreen.VerificationCenter) },
                        onNavigateToLogin = { navigateTo(AppScreen.Login) },
                        onNavigateToOnboarding = { role -> navigateTo(AppScreen.Onboarding(role)) },
                        onNavigateToNotifications = { navigateTo(AppScreen.Notifications) },
                        onNavigateToPreferences = { navigateTo(AppScreen.TenantRequirements) }
                    )
                }
                is AppScreen.Explore -> {
                    ExploreScreen(
                        viewModel = viewModel,
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) },
                        onNavigateToChat = { convId -> navigateTo(AppScreen.Messages(convId)) }
                    )
                }
                is AppScreen.Notifications -> {
                    NotificationsScreen(
                        viewModel = viewModel,
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) },
                        onBack = { navigateBack() }
                    )
                }
                is AppScreen.Post -> {
                    PostPropertyScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onSuccess = { navigateBack() }
                    )
                }
                is AppScreen.Viewings -> {
                    ViewingRequestsScreen(
                        viewModel = viewModel,
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) }
                    )
                }
                is AppScreen.Messages -> {
                    MessagesScreen(
                        viewModel = viewModel,
                        initialConversationId = currentScreen.conversationId,
                        onSelectConversation = { convId -> navigateTo(AppScreen.Messages(convId)) },
                        onBack = { navigateBack() }
                    )
                }
                is AppScreen.Dashboard -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) },
                        onNavigateToPostProperty = { navigateTo(AppScreen.Post) },
                        onNavigateToRequirements = { navigateTo(AppScreen.TenantRequirements) },
                        onNavigateToVerificationCenter = { navigateTo(AppScreen.VerificationCenter) },
                        onNavigateToAdminPanel = { navigateTo(AppScreen.AdminPanel) },
                        onNavigateToLogin = { navigateTo(AppScreen.Login) },
                        onNavigateToOnboarding = { role -> navigateTo(AppScreen.Onboarding(role)) }
                    )
                }
                is AppScreen.PropertyDetail -> {
                    PropertyDetailScreen(
                        propertyId = currentScreen.propertyId,
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onNavigateToChat = { convId -> navigateTo(AppScreen.Messages(convId)) }
                    )
                }
                is AppScreen.TenantRequirements -> {
                    TenantRequirementScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() },
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) }
                    )
                }
                is AppScreen.VerificationCenter -> {
                    VerificationCenterScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }
                is AppScreen.AdminPanel -> {
                    AdminPanelScreen(
                        viewModel = viewModel,
                        onBack = { navigateBack() }
                    )
                }
                is AppScreen.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { navigateTo(AppScreen.Home) },
                        onNavigateToOnboarding = { role -> navigateTo(AppScreen.Onboarding(role)) },
                        onContinueAsGuest = { navigateTo(AppScreen.Home) }
                    )
                }
                is AppScreen.Onboarding -> {
                    OnboardingScreen(
                        viewModel = viewModel,
                        initialRole = currentScreen.initialRole,
                        onFinishOnboarding = { navigateTo(AppScreen.Home) },
                        onNavigateToHome = { navigateTo(AppScreen.Home) },
                        onNavigateToDashboard = { navigateTo(AppScreen.Dashboard) },
                        onNavigateToPropertyDetail = { propId -> navigateTo(AppScreen.PropertyDetail(propId)) }
                    )
                }
            }
        }
    }
}

private data class NavItemData(
    val label: String,
    val icon: ImageVector,
    val screen: AppScreen,
    val tag: String
)

@Composable
fun ResponsiveBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val totalWidth = maxWidth
            // Responsive typography and icon sizing based on available width:
            val (fontSize, iconSize, itemPaddingV) = when {
                totalWidth < 340.dp -> Triple(9.sp, 18.dp, 3.dp)
                totalWidth < 380.dp -> Triple(9.5.sp, 20.dp, 5.dp)
                totalWidth < 430.dp -> Triple(10.5.sp, 21.dp, 6.dp)
                else -> Triple(11.5.sp, 22.dp, 8.dp)
            }

            val navItems = listOf(
                NavItemData("Home", Icons.Default.Home, AppScreen.Home, "nav_home"),
                NavItemData("Explore", Icons.Default.Explore, AppScreen.Explore, "nav_explore"),
                NavItemData("Viewings", Icons.Default.CalendarMonth, AppScreen.Viewings, "nav_viewings"),
                NavItemData("Messages", Icons.Default.Chat, AppScreen.Messages(), "nav_messages"),
                NavItemData("Profile", Icons.Default.Person, AppScreen.Dashboard, "nav_dashboard")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = itemPaddingV)
                    .testTag("main_bottom_nav"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    val isSelected = when (item.screen) {
                        is AppScreen.Home -> currentScreen is AppScreen.Home
                        is AppScreen.Explore -> currentScreen is AppScreen.Explore
                        is AppScreen.Viewings -> currentScreen is AppScreen.Viewings
                        is AppScreen.Messages -> currentScreen is AppScreen.Messages
                        is AppScreen.Dashboard -> currentScreen is AppScreen.Dashboard
                        else -> false
                    }

                    val activeColor = MaterialTheme.colorScheme.primary
                    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigate(item.screen) }
                            .padding(horizontal = 1.dp, vertical = 2.dp)
                            .testTag(item.tag),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Subtle active pill around icon
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent)
                                .padding(horizontal = if (totalWidth < 360.dp) 8.dp else 12.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(iconSize),
                                tint = if (isSelected) activeColor else inactiveColor
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.label,
                            fontSize = fontSize,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) activeColor else inactiveColor,
                            maxLines = 1,
                            softWrap = false,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

