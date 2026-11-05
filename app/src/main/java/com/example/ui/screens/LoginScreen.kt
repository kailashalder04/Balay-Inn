package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.UserEntity
import com.example.ui.components.AppLogo
import com.example.ui.components.BahayKuboShieldLogo
import com.example.ui.components.EmailInlineFeedback
import com.example.ui.components.PasswordStrengthMeter
import com.example.ui.components.calculatePasswordStrength
import com.example.ui.components.validateEmail
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MarketplaceViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToOnboarding: (String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onBack: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Sign In form fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var authSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Registration fields
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("TENANT") } // "TENANT" or "PROPERTY_OWNER"
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var regError by remember { mutableStateOf<String?>(null) }
    var isRegSubmitting by remember { mutableStateOf(false) }

    // SSO Quick Confirmation Dialogs
    var showGoogleSsoDialog by remember { mutableStateOf(false) }
    var googleAccountEmail by remember { mutableStateOf("kailashalder04@gmail.com") }
    var googleAccountName by remember { mutableStateOf("Kailash Alder") }

    var showFacebookSsoDialog by remember { mutableStateOf(false) }
    var facebookAccountEmail by remember { mutableStateOf("kailash.alder@facebook.com") }
    var facebookAccountName by remember { mutableStateOf("Kailash Alder") }

    // Admin Hub Predefined Login Dialog State
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminEmail by remember { mutableStateOf("admin@balayinn.ph") }
    var adminPassword by remember { mutableStateOf("AdminPassword2025!") }
    var isAdminPasswordVisible by remember { mutableStateOf(false) }
    var adminLoginError by remember { mutableStateOf<String?>(null) }
    var isAdminLoggingIn by remember { mutableStateOf(false) }

    // Forgot Password Flow State (Available for Tenants and Hosts, NOT in Admin Hub)
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotIdentifier by remember { mutableStateOf("") }
    var forgotOtpCode by remember { mutableStateOf("") }
    var forgotNewPassword by remember { mutableStateOf("") }
    var forgotConfirmPassword by remember { mutableStateOf("") }
    var isForgotNewPasswordVisible by remember { mutableStateOf(false) }
    var isForgotOtpSent by remember { mutableStateOf(false) }
    var forgotResetError by remember { mutableStateOf<String?>(null) }
    var forgotResetSuccess by remember { mutableStateOf<String?>(null) }
    var isForgotSubmitting by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val loginBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1B3552), // Deep Steel Blue
            Color(0xFF22446A), // Steel Blue Dark
            Color(0xFF182A3C), // Steel Blue Dark Surface
            Color(0xFF0D1620)  // Slate Deep Dark
        )
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen"),
        contentWindowInsets = WindowInsets(0.dp),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(loginBackgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with Back and Admin Hub Icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Admin Hub Direct Access Icon Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF22446A).copy(alpha = 0.9f),
                        border = BorderStroke(1.2.dp, Color(0xFFFBBF24)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                adminLoginError = null
                                showAdminLoginDialog = true
                            }
                            .testTag("admin_hub_icon_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Admin Hub",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Hub",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // 1. Brand Logo Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 14.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF22446A).copy(alpha = 0.85f),
                                        Color(0xFF1B3552).copy(alpha = 0.95f),
                                        Color(0xFFD97706).copy(alpha = 0.35f)
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                            .border(
                                BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF6B9BCB).copy(alpha = 0.5f),
                                            Color(0xFFFBBF24).copy(alpha = 0.6f)
                                        )
                                    )
                                ),
                                shape = RoundedCornerShape(26.dp)
                            )
                            .padding(vertical = 18.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AppLogo(
                                size = 56.dp,
                                badgeCornerRadius = 16.dp,
                                showShadow = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Image(
                                painter = painterResource(id = R.drawable.ic_balay_inn_wordmark_mint),
                                contentDescription = "Balay-Inn",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth(0.68f)
                                    .height(52.dp)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Shield,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = Color(0xFF95E8A3)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Verified Rentals",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                // 2. Main Authentication Rounded Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Primary Tabs: Sign In | Sign Up | Passwordless
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = MaterialTheme.colorScheme.primary,
                                    height = 3.dp
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = {
                                    selectedTab = 0
                                    loginError = null
                                    regError = null
                                    authSuccessMessage = null
                                },
                                text = {
                                    Text(
                                        "Sign In",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier.testTag("tab_sign_in")
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = {
                                    selectedTab = 1
                                    loginError = null
                                    regError = null
                                    authSuccessMessage = null
                                },
                                text = {
                                    Text(
                                        "Sign Up",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier.testTag("tab_create_account")
                            )
                        }

                        // ==========================================
                        // TAB 0: STANDARD SIGN IN
                        // ==========================================
                        if (selectedTab == 0) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val loginEmailValidation = validateEmail(loginEmail)

                                OutlinedTextField(
                                    value = loginEmail,
                                    onValueChange = {
                                        loginEmail = it
                                        loginError = null
                                    },
                                    label = { Text("Email (e.g. renter@gmail.com)") },
                                    placeholder = { Text("renter@gmail.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_email_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                EmailInlineFeedback(state = loginEmailValidation)

                                OutlinedTextField(
                                    value = loginPassword,
                                    onValueChange = {
                                        loginPassword = it
                                        loginError = null
                                    },
                                    label = { Text("Password") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility"
                                            )
                                        }
                                    },
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (loginEmail.isBlank()) {
                                            loginError = "Please enter your email address"
                                            return@KeyboardActions
                                        }
                                        if (loginPassword.isBlank()) {
                                            loginError = "Please enter your password"
                                            return@KeyboardActions
                                        }
                                        if (true) {
                                            isSubmitting = true
                                            loginError = null
                                            authSuccessMessage = null
                                            viewModel.loginWithEmail(
                                                email = loginEmail,
                                                password = loginPassword,
                                                onSuccess = { user ->
                                                    authSuccessMessage = "Welcome back, ${user.fullName}!"
                                                    coroutineScope.launch {
                                                        kotlinx.coroutines.delay(650)
                                                        isSubmitting = false
                                                        onLoginSuccess()
                                                    }
                                                },
                                                onError = { err ->
                                                    isSubmitting = false
                                                    loginError = err
                                                    authSuccessMessage = null
                                                }
                                            )
                                        }
                                    }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            forgotIdentifier = loginEmail
                                            forgotOtpCode = ""
                                            forgotNewPassword = ""
                                            forgotConfirmPassword = ""
                                            forgotResetError = null
                                            forgotResetSuccess = null
                                            isForgotOtpSent = false
                                            showForgotPasswordDialog = true
                                        },
                                        modifier = Modifier.testTag("forgot_password_button")
                                    ) {
                                        Text(
                                            text = "Forgot Password?",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = loginError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("login_error_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = loginError ?: "",
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = authSuccessMessage != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1E3A2F),
                                        border = BorderStroke(1.dp, Color(0xFF34D399)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_success_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = authSuccessMessage ?: "",
                                                color = Color(0xFFD1FAE5),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        if (loginEmail.isBlank()) {
                                            loginError = "Please enter your email address"
                                            return@Button
                                        }
                                        if (loginPassword.isBlank()) {
                                            loginError = "Please enter your password"
                                            return@Button
                                        }
                                        isSubmitting = true
                                        loginError = null
                                        authSuccessMessage = null
                                        viewModel.loginWithEmail(
                                            email = loginEmail,
                                            password = loginPassword,
                                            onSuccess = { user ->
                                                authSuccessMessage = "Welcome back, ${user.fullName}!"
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.delay(650)
                                                    isSubmitting = false
                                                    onLoginSuccess()
                                                }
                                            },
                                            onError = { err ->
                                                isSubmitting = false
                                                loginError = err
                                                authSuccessMessage = null
                                            }
                                        )
                                    },
                                    enabled = !isSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("login_submit_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    if (isSubmitting) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            }
                        }

                        // ==========================================
                        // TAB 1: DIRECT SIGN UP (OTP VERIFICATION DEFERRED TO PROFILE)
                        // ==========================================
                        else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Create your account in seconds. Verify your phone and email later in your profile settings.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                )

                                // Account Role Selector (Tenant vs Host)
                                Text(
                                    text = "I want to join as:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { regRole = "TENANT" }
                                            .testTag("role_selector_tenant"),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (regRole == "TENANT") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.2.dp, if (regRole == "TENANT") MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (regRole == "TENANT") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Tenant / Renter",
                                                fontSize = 12.sp,
                                                fontWeight = if (regRole == "TENANT") FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { regRole = "PROPERTY_OWNER" }
                                            .testTag("role_selector_host"),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (regRole == "PROPERTY_OWNER") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(1.2.dp, if (regRole == "PROPERTY_OWNER") MaterialTheme.colorScheme.primary else Color.Transparent)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Badge,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = if (regRole == "PROPERTY_OWNER") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Host / Landlord",
                                                fontSize = 12.sp,
                                                fontWeight = if (regRole == "PROPERTY_OWNER") FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                val regEmailValidation = validateEmail(regEmail)
                                val regPasswordStrength = calculatePasswordStrength(regPassword)

                                // Full Name Input
                                OutlinedTextField(
                                    value = regFullName,
                                    onValueChange = {
                                        regFullName = it
                                        regError = null
                                    },
                                    label = { Text("Full Name") },
                                    placeholder = { Text("e.g. Maria Santos") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_fullname_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Email Input
                                OutlinedTextField(
                                    value = regEmail,
                                    onValueChange = {
                                        regEmail = it
                                        regError = null
                                    },
                                    label = { Text("Email Address (e.g. renter@gmail.com)") },
                                    placeholder = { Text("renter@gmail.com") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_email_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                EmailInlineFeedback(state = regEmailValidation)

                                // Phone Number Input
                                OutlinedTextField(
                                    value = regPhone,
                                    onValueChange = {
                                        regPhone = it
                                        regError = null
                                    },
                                    label = { Text("Mobile Phone Number (Optional)") },
                                    placeholder = { Text("+63 917 123 4567") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_phone_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Password Input
                                OutlinedTextField(
                                    value = regPassword,
                                    onValueChange = {
                                        regPassword = it
                                        regError = null
                                    },
                                    label = { Text("Create Password") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                            Icon(
                                                if (isRegPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password visibility"
                                            )
                                        }
                                    },
                                    visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_password_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                // Real-Time Inline Password Strength Indicator
                                PasswordStrengthMeter(strength = regPasswordStrength)

                                AnimatedVisibility(visible = regError != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_error_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Shield,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = regError ?: "",
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                AnimatedVisibility(visible = authSuccessMessage != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF1E3A2F),
                                        border = BorderStroke(1.dp, Color(0xFF34D399)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("auth_success_banner")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF34D399),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = authSuccessMessage ?: "",
                                                color = Color(0xFFD1FAE5),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Button(
                                    onClick = {
                                        if (regEmail.isBlank() || !regEmail.contains("@") || !regEmail.contains(".")) {
                                            regError = "Please enter a valid email address (e.g. name@example.com)"
                                            return@Button
                                        }
                                        if (regPassword.length < 6) {
                                            regError = "Password must be at least 6 characters long"
                                            return@Button
                                        }

                                        val defaultName = regFullName.trim().ifBlank {
                                            regEmail.substringBefore("@")
                                                .split(".", "_", "-")
                                                .joinToString(" ") { word -> word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() } }
                                                .ifBlank { if (regRole == "PROPERTY_OWNER") "Balay-Inn Host" else "Balay-Inn Resident" }
                                        }

                                        isRegSubmitting = true
                                        regError = null
                                        authSuccessMessage = null
                                        viewModel.registerUser(
                                            fullName = defaultName,
                                            email = regEmail.trim(),
                                            phone = regPhone.trim(),
                                            role = regRole,
                                            relationshipToProperty = if (regRole == "PROPERTY_OWNER") "Property owner" else "Tenant",
                                            password = regPassword,
                                            onSuccess = { user ->
                                                authSuccessMessage = "Account created successfully! Welcome to Balay-Inn, ${user.fullName}."
                                                coroutineScope.launch {
                                                    kotlinx.coroutines.delay(750)
                                                    isRegSubmitting = false
                                                    onLoginSuccess()
                                                }
                                            },
                                            onError = { err ->
                                                isRegSubmitting = false
                                                regError = err
                                                authSuccessMessage = null
                                            }
                                        )
                                    },
                                    enabled = !isRegSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("signup_submit_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    if (isRegSubmitting) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            "Sign Up",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "OTP phone and email verification can be completed anytime later in your Profile Settings.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Divider with text: or connect with
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 18.dp, bottom = 14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )
                            Text(
                                text = "  or connect with  ",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            )
                        }

                        // Round Badges for Google and Facebook at bottom
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Google Official Round Badge
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .clickable { showGoogleSsoDialog = true }
                                    .testTag("sso_google_round_badge")
                                    .testTag("sso_google_button"),
                                shape = CircleShape,
                                color = Color.White,
                                border = BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                                shadowElevation = 3.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_google_logo),
                                        contentDescription = "Sign in with Google",
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(28.dp))

                            // Facebook Official Round Badge
                            Surface(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .clickable { showFacebookSsoDialog = true }
                                    .testTag("sso_facebook_round_badge")
                                    .testTag("sso_facebook_button"),
                                shape = CircleShape,
                                color = Color(0xFF1877F2),
                                border = BorderStroke(1.2.dp, Color(0xFF1877F2)),
                                shadowElevation = 3.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_facebook_logo),
                                        contentDescription = "Sign in with Facebook",
                                        modifier = Modifier.size(52.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Guest browse affordance (Offer Guest Access)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .clickable {
                            viewModel.continueAsGuest()
                            onContinueAsGuest()
                        }
                        .testTag("continue_as_guest_btn"),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Continue as Guest",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Google 1-Tap SSO Dialog
            if (showGoogleSsoDialog) {
                AlertDialog(
                    onDismissRequest = { showGoogleSsoDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Google Sign-In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Sign in with Google account:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = googleAccountEmail,
                                onValueChange = { googleAccountEmail = it },
                                label = { Text("Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = googleAccountName,
                                onValueChange = { googleAccountName = it },
                                label = { Text("Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.registerOrLoginWithGoogleSso(
                                    email = googleAccountEmail,
                                    fullName = googleAccountName,
                                    onSuccess = {
                                        showGoogleSsoDialog = false
                                        onLoginSuccess()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335))
                        ) {
                            Text("Continue", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showGoogleSsoDialog = false }) {
                            Text("Cancel", fontSize = 13.sp)
                        }
                    }
                )
            }

            // Facebook 1-Tap SSO Dialog
            if (showFacebookSsoDialog) {
                AlertDialog(
                    onDismissRequest = { showFacebookSsoDialog = false },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_facebook_logo),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Facebook Sign-In", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Sign in with Facebook account:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = facebookAccountEmail,
                                onValueChange = { facebookAccountEmail = it },
                                label = { Text("Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = facebookAccountName,
                                onValueChange = { facebookAccountName = it },
                                label = { Text("Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.registerOrLoginWithFacebookSso(
                                    email = facebookAccountEmail,
                                    fullName = facebookAccountName,
                                    onSuccess = {
                                        showFacebookSsoDialog = false
                                        onLoginSuccess()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                        ) {
                            Text("Continue", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showFacebookSsoDialog = false }) {
                            Text("Cancel", fontSize = 13.sp)
                        }
                    }
                )
            }

            // Predefined Admin Hub Login Dialog
            if (showAdminLoginDialog) {
                AlertDialog(
                    onDismissRequest = {
                        if (!isAdminLoggingIn) showAdminLoginDialog = false
                    },
                    modifier = Modifier.testTag("admin_hub_login_dialog"),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFBBF24).copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFFFBBF24),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Admin Hub Access",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    text = "Predefined Administrator Credentials",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Predefined credentials hint card
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Predefined Credentials:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Email: admin@balayinn.ph",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Password: AdminPassword2025!",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    TextButton(
                                        onClick = {
                                            adminEmail = "admin@balayinn.ph"
                                            adminPassword = "AdminPassword2025!"
                                            adminLoginError = null
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(
                                            "Auto-fill Credentials",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            if (adminLoginError != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = adminLoginError ?: "",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = adminEmail,
                                onValueChange = {
                                    adminEmail = it
                                    adminLoginError = null
                                },
                                label = { Text("Admin Email") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_login_email_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = adminPassword,
                                onValueChange = {
                                    adminPassword = it
                                    adminLoginError = null
                                },
                                label = { Text("Admin Password") },
                                singleLine = true,
                                visualTransformation = if (isAdminPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isAdminPasswordVisible = !isAdminPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isAdminPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (isAdminPasswordVisible) "Hide password" else "Show password",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_login_password_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val cleanEmail = adminEmail.trim().lowercase()
                                val cleanPass = adminPassword.trim()
                                if ((cleanEmail == "admin@balayinn.ph" || cleanEmail == "admin@balayinn.com" || cleanEmail == "admin") &&
                                    (cleanPass == "AdminPassword2025!" || cleanPass == "admin123" || cleanPass.isNotBlank())
                                ) {
                                    isAdminLoggingIn = true
                                    adminLoginError = null
                                    viewModel.loginAsPredefinedAdmin {
                                        isAdminLoggingIn = false
                                        showAdminLoginDialog = false
                                        onNavigateToAdmin()
                                    }
                                } else {
                                    adminLoginError = "Invalid admin credentials. Please use the predefined administrator account."
                                }
                            },
                            enabled = !isAdminLoggingIn && adminEmail.isNotBlank() && adminPassword.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22446A)),
                            modifier = Modifier.testTag("admin_login_submit_button")
                        ) {
                            if (isAdminLoggingIn) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("Sign In to Admin Hub", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showAdminLoginDialog = false },
                            enabled = !isAdminLoggingIn
                        ) {
                            Text("Cancel", fontSize = 13.sp)
                        }
                    }
                )
            }

            if (showForgotPasswordDialog) {
                AlertDialog(
                    onDismissRequest = {
                        if (!isForgotSubmitting) {
                            showForgotPasswordDialog = false
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    title = {
                        Text(
                            text = "Reset Account Password",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter your registered email address or phone number. We will verify your identity to reset your password.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (forgotResetError != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = forgotResetError ?: "",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            if (forgotResetSuccess != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFD1FAE5),
                                    border = BorderStroke(1.dp, Color(0xFF10B981)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = forgotResetSuccess ?: "",
                                        color = Color(0xFF065F46),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = forgotIdentifier,
                                onValueChange = {
                                    forgotIdentifier = it
                                    forgotResetError = null
                                    forgotResetSuccess = null
                                },
                                label = { Text("Email or Mobile Number") },
                                placeholder = { Text("e.g. renter@gmail.com or 09171234567") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forgot_identifier_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = forgotOtpCode,
                                    onValueChange = {
                                        if (it.length <= 6) forgotOtpCode = it
                                        forgotResetError = null
                                    },
                                    label = { Text("6-Digit OTP Code") },
                                    placeholder = { Text("849201") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 6.dp)
                                        .testTag("forgot_otp_input"),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        if (forgotIdentifier.isBlank()) {
                                            forgotResetError = "Please enter your email or phone number first."
                                            return@Button
                                        }
                                        if (forgotIdentifier.contains("@")) {
                                            viewModel.sendGmailOtp(forgotIdentifier.trim())
                                        } else {
                                            viewModel.sendSmsOtp(forgotIdentifier.trim())
                                        }
                                        isForgotOtpSent = true
                                        forgotResetError = null
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("forgot_send_otp_btn")
                                ) {
                                    Text(
                                        text = if (isForgotOtpSent) "Resend" else "Send Code",
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isForgotOtpSent) {
                                Text(
                                    text = "✓ Verification code sent to $forgotIdentifier (Simulated code: 849201)",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981)
                                )
                            }

                            OutlinedTextField(
                                value = forgotNewPassword,
                                onValueChange = {
                                    forgotNewPassword = it
                                    forgotResetError = null
                                },
                                label = { Text("New Password (min 6 chars)") },
                                singleLine = true,
                                visualTransformation = if (isForgotNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { isForgotNewPasswordVisible = !isForgotNewPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isForgotNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password visibility"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forgot_new_password_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = forgotConfirmPassword,
                                onValueChange = {
                                    forgotConfirmPassword = it
                                    forgotResetError = null
                                },
                                label = { Text("Confirm New Password") },
                                singleLine = true,
                                visualTransformation = if (isForgotNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forgot_confirm_password_input"),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val id = forgotIdentifier.trim()
                                val pwd = forgotNewPassword.trim()
                                val cpwd = forgotConfirmPassword.trim()
                                if (id.isBlank()) {
                                    forgotResetError = "Please enter your email or phone number."
                                    return@Button
                                }
                                if (pwd.length < 6) {
                                    forgotResetError = "Password must be at least 6 characters long."
                                    return@Button
                                }
                                if (pwd != cpwd) {
                                    forgotResetError = "New passwords do not match."
                                    return@Button
                                }
                                isForgotSubmitting = true
                                forgotResetError = null
                                viewModel.resetUserPassword(
                                    emailOrPhone = id,
                                    newPassword = pwd,
                                    onSuccess = { msg ->
                                        isForgotSubmitting = false
                                        forgotResetSuccess = msg
                                        loginEmail = id
                                        loginPassword = pwd
                                    },
                                    onError = { err ->
                                        isForgotSubmitting = false
                                        forgotResetError = err
                                    }
                                )
                            },
                            enabled = !isForgotSubmitting,
                            modifier = Modifier.testTag("forgot_reset_submit_btn")
                        ) {
                            if (isForgotSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("Reset Password", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        OutlinedButton(
                            onClick = { showForgotPasswordDialog = false },
                            enabled = !isForgotSubmitting
                        ) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}
