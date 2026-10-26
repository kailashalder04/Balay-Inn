package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BahayKuboShieldLogo

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1E2E))
            .testTag("welcome_screen")
    ) {
        // 1. Attached House Image Background with Gradient Vignette
        Image(
            painter = painterResource(id = R.drawable.welcome_house),
            contentDescription = "Balay-Inn Verified Rental House",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        )

        // 2. Multi-stop Gradient scrim to guarantee high contrast & readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.25f),
                        0.35f to Color.Transparent,
                        0.55f to Color(0xFF0F1E2E).copy(alpha = 0.85f),
                        0.75f to Color(0xFF0B1724).copy(alpha = 0.98f),
                        1.0f to Color(0xFF070F18)
                    )
                )
        )

        // 3. Foreground Content Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Shield / Verified Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFBBF24).copy(alpha = 0.6f), Color(0xFF60A5FA).copy(alpha = 0.6f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Verified Rentals",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.testTag("welcome_skip_btn")
                ) {
                    Text(
                        text = "Skip",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(180.dp))

            // Bottom Content Section: Logo, App Name, 2 Lines Description & Get Started
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bahay Kubo Logo
                BahayKuboShieldLogo(
                    size = 72.dp,
                    badgeCornerRadius = 20.dp,
                    showGradientBackground = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // App Title
                Text(
                    text = "Balay-Inn",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1.2).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Line 1 of Description
                Text(
                    text = "Find verified boarding houses, apartments, and condo rentals across the Philippines.",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Line 2 of Description
                Text(
                    text = "Connect directly with verified owners with transparent pricing and zero hidden fees.",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFFBBF24),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Primary "Get Started" Button
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_get_started_btn"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subtle Guest Entry Link
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.testTag("welcome_browse_guest_btn")
                ) {
                    Text(
                        text = "Browse Marketplace as Guest",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
