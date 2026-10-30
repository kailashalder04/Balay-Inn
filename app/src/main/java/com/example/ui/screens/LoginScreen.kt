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
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Sign Up
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Sign In form fields
    var loginEmail by remember { mutableStateOf("joshua.alcantara@gmail.com") }
    var loginPassword by remember { mutableStateOf("password123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Registration with OTP Verification fields (Email or Phone)
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("+63 917 123 4567") }
    var regPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var regVerificationChannel by remember { mutableStateOf("EMAIL") } // "EMAIL" or "PHONE"
    var regStep by remember { mutableIntStateOf(0) } // 0 = Credentials, 1 = OTP Verification
    var regGeneratedOtp by remember { mutableStateOf("842915") }
    var regEnteredOtp by remember { mutableStateOf("") }
    var regSentTarget by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf<String?>(null) }
    var isRegSubmitting by remember { mutableStateOf(false) }

    // SSO Quick Confirmation Dialogs
    var showGoogleSsoDialog by remember { mutableStateOf(false) }
    var googleAccountEmail by remember { mutableStateOf("kailashalder04@gmail.com") }
    var googleAccountName by remember { mutableStateOf("Kailash Alder") }

    var showFacebookSsoDialog by remember { mutableStateOf(false) }
    var facebookAccountEmail by remember { mutableStateOf("kailash.alder@facebook.com") }
    var facebookAccountName by remember { mutableStateOf("Kailash Alder") }

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
                // Top Navigation Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
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
                                    regStep = 0
                                    regError = null
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
                                    regStep = 0
                                    regError = null
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
                                    label = { Text("Email") },
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
                                        if (loginEmail.isNotBlank()) {
                                            isSubmitting = true
                                            viewModel.loginWithEmail(
                                                email = loginEmail,
                                                password = loginPassword,
                                                onSuccess = {
                                                    isSubmitting = false
                                                    onLoginSuccess()
                                                },
                                                onError = { err ->
                                                    isSubmitting = false
                                                    loginError = err
                                                }
                                            )
                                        }
                                    }),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input"),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                AnimatedVisibility(visible = loginError != null) {
                                    Text(
                                        text = loginError ?: "",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (loginEmail.isBlank()) {
                                            loginError = "Enter your email"
                                            return@Button
                                        }
                                        isSubmitting = true
                                        viewModel.loginWithEmail(
                                            email = loginEmail,
                                            password = loginPassword,
                                            onSuccess = {
                                                isSubmitting = false
                                                onLoginSuccess()
                                            },
                                            onError = { err ->
                                                isSubmitting = false
                                                loginError = err
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

                                Spacer(modifier = Modifier.height(4.dp))

                                // Quick Demo Personas
                                Text(
                                    text = "Demo Profiles",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                val demoProfiles = listOf(
                                    Triple("usr_tenant_1", "Joshua Alcantara", "Tenant"),
                                    Triple("usr_owner_1", "Donato Yap", "Landlord"),
                                    Triple("usr_broker_marco", "Marco Villanueva", "Broker"),
                                    Triple("usr_caretaker_mark", "Kuya Mark", "Caretaker")
                                )

                                demoProfiles.forEach { (userId, name, desc) ->
                                    val matchingUser = allUsers.find { it.id == userId }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                matchingUser?.let { user ->
                                                    viewModel.switchUser(user)
                                                    onLoginSuccess()
                                                }
                                            }
                                            .testTag("demo_profile_${userId}"),
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = name,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = desc,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1
                                                )
                                            }
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Switch to user",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                                                // ==========================================
                        // TAB 1: SIGN UP WITH OTP VERIFICATION (EMAIL OR PHONE)
                        // ==========================================
                        else {
                            if (regStep == 0) {
                                // STEP 0: Credentials & Verification Channel Selection
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Register with instant OTP verification via email or mobile.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 2.dp)
                                    )

                                    val regEmailValidation = validateEmail(regEmail)
                                    val regPasswordStrength = calculatePasswordStrength(regPassword)

                                    // Email Input
                                    OutlinedTextField(
                                        value = regEmail,
                                        onValueChange = {
                                            regEmail = it
                                            regError = null
                                        },
                                        label = { Text("Email Address") },
                                        placeholder = { Text("name@example.com") },
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
                                        label = { Text("Mobile Phone Number") },
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

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // OTP Delivery Choice Toggle
                                    Text(
                                        text = "Send verification OTP code to:",
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
                                                .clickable { regVerificationChannel = "EMAIL" },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (regVerificationChannel == "EMAIL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(1.2.dp, if (regVerificationChannel == "EMAIL") MaterialTheme.colorScheme.primary else Color.Transparent)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.Email,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (regVerificationChannel == "EMAIL") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "Email OTP",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (regVerificationChannel == "EMAIL") FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { regVerificationChannel = "PHONE" },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (regVerificationChannel == "PHONE") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = BorderStroke(1.2.dp, if (regVerificationChannel == "PHONE") MaterialTheme.colorScheme.primary else Color.Transparent)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.PhoneIphone,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (regVerificationChannel == "PHONE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    "SMS OTP",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (regVerificationChannel == "PHONE") FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(visible = regError != null) {
                                        Text(
                                            text = regError ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (regEmail.isBlank() || !regEmail.contains("@") || !regEmail.contains(".")) {
                                                regError = "Please enter a valid email address"
                                                return@Button
                                            }
                                            if (regVerificationChannel == "PHONE" && (regPhone.isBlank() || regPhone.length < 7)) {
                                                regError = "Please enter a valid phone number for SMS OTP"
                                                return@Button
                                            }
                                            if (regPassword.length < 6) {
                                                regError = "Password must be at least 6 characters"
                                                return@Button
                                            }

                                            // Generate 6-digit OTP
                                            val randomOtp = (100000..999999).random().toString()
                                            regGeneratedOtp = randomOtp
                                            regSentTarget = if (regVerificationChannel == "EMAIL") regEmail.trim() else regPhone.trim()
                                            regEnteredOtp = ""
                                            regError = null
                                            regStep = 1
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("signup_submit_btn"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(
                                            "Send OTP Verification Code",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.5.sp
                                        )
                                    }
                                }
                            } else {
                                // STEP 1: Enter 6-digit OTP Verification
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (regVerificationChannel == "EMAIL") Icons.Default.MarkEmailRead else Icons.Default.Sms,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Verify Your Account",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Enter the 6-digit code sent to:",
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = regSentTarget,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Demo OTP Banner with 1-click autofill for testing
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFFEF3C7),
                                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Default.Key,
                                                    contentDescription = null,
                                                    tint = Color(0xFFD97706),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Code: $regGeneratedOtp",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF92400E)
                                                )
                                            }
                                            TextButton(
                                                onClick = { regEnteredOtp = regGeneratedOtp },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Auto-fill",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = regEnteredOtp,
                                        onValueChange = { input ->
                                            if (input.length <= 6) {
                                                regEnteredOtp = input.filter { it.isDigit() }
                                                regError = null
                                            }
                                        },
                                        label = { Text("6-Digit OTP") },
                                        placeholder = { Text("------") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("reg_otp_input")
                                            .testTag("otp_code_input"),
                                        shape = RoundedCornerShape(12.dp)
                                    )

                                    AnimatedVisibility(visible = regError != null) {
                                        Text(
                                            text = regError ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (regEnteredOtp.trim().length != 6) {
                                                regError = "Please enter the complete 6-digit OTP"
                                                return@Button
                                            }
                                            if (regEnteredOtp.trim() != regGeneratedOtp) {
                                                regError = "Invalid verification code. Please check and try again."
                                                return@Button
                                            }
                                            isRegSubmitting = true
                                            viewModel.registerWithOtpVerification(
                                                email = regEmail,
                                                phone = regPhone,
                                                password = regPassword,
                                                verifiedVia = regVerificationChannel,
                                                onSuccess = { newUser ->
                                                    isRegSubmitting = false
                                                    onLoginSuccess()
                                                },
                                                onError = { err ->
                                                    isRegSubmitting = false
                                                    regError = err
                                                }
                                            )
                                        },
                                        enabled = !isRegSubmitting,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("verify_otp_btn"),
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
                                                "Verify & Create Account",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                val randomOtp = (100000..999999).random().toString()
                                                regGeneratedOtp = randomOtp
                                                regEnteredOtp = ""
                                                regError = "New code sent to $regSentTarget"
                                            }
                                        ) {
                                            Text("Resend Code", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        TextButton(
                                            onClick = {
                                                regStep = 0
                                                regError = null
                                            }
                                        ) {
                                            Text("Edit Details", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                                        }
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
        }
    }
}
