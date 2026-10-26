package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BadgeGreen
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeAmber
import com.example.ui.theme.BadgeAmberBg

/**
 * Real-time Email Validation State
 */
sealed class EmailValidationState {
    object Empty : EmailValidationState()
    data class Invalid(val message: String) : EmailValidationState()
    object Valid : EmailValidationState()
}

fun validateEmail(email: String): EmailValidationState {
    val clean = email.trim()
    if (clean.isBlank()) return EmailValidationState.Empty
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return if (emailRegex.matches(clean)) {
        EmailValidationState.Valid
    } else if (!clean.contains("@")) {
        EmailValidationState.Invalid("Missing @")
    } else if (!clean.substringAfter("@").contains(".")) {
        EmailValidationState.Invalid("Domain missing")
    } else {
        EmailValidationState.Invalid("Invalid email")
    }
}

@Composable
fun EmailInlineFeedback(
    state: EmailValidationState,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = state !is EmailValidationState.Empty, modifier = modifier) {
        when (state) {
            is EmailValidationState.Valid -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Valid email",
                        tint = BadgeGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Valid email",
                        color = BadgeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            is EmailValidationState.Invalid -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Email note",
                        tint = BadgeAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.message,
                        color = BadgeAmber,
                        fontSize = 11.sp
                    )
                }
            }
            is EmailValidationState.Empty -> {}
        }
    }
}

/**
 * Real-time Password Strength Model
 */
data class PasswordStrength(
    val score: Float, // 0f to 1f
    val label: String,
    val color: Color,
    val hasMinLength: Boolean,
    val hasNumber: Boolean,
    val hasMixedCase: Boolean,
    val hasSpecial: Boolean
)

fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isBlank()) {
        return PasswordStrength(0f, "Empty", Color.Gray, false, false, false, false)
    }
    val hasMinLength = password.length >= 8
    val hasNumber = password.any { it.isDigit() }
    val hasMixedCase = password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
    val hasSpecial = password.any { !it.isLetterOrDigit() }

    var points = 0
    if (hasMinLength) points += 35
    if (hasNumber) points += 25
    if (hasMixedCase) points += 25
    if (hasSpecial) points += 15

    val fraction = (points / 100f).coerceIn(0f, 1f)

    val (label, color) = when {
        points < 35 -> "Weak" to Color(0xFFEF4444)
        points < 70 -> "Moderate" to Color(0xFFF59E0B)
        else -> "Strong" to Color(0xFF10B981)
    }

    return PasswordStrength(
        score = fraction,
        label = label,
        color = color,
        hasMinLength = hasMinLength,
        hasNumber = hasNumber,
        hasMixedCase = hasMixedCase,
        hasSpecial = hasSpecial
    )
}

@Composable
fun PasswordStrengthMeter(
    strength: PasswordStrength,
    modifier: Modifier = Modifier
) {
    if (strength.label == "Empty") return

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Strength:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = strength.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = strength.color
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 3-Segment Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val seg1Active = strength.score > 0.05f
            val seg2Active = strength.score >= 0.35f
            val seg3Active = strength.score >= 0.70f

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (seg1Active) strength.color else Color.LightGray.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (seg2Active) strength.color else Color.LightGray.copy(alpha = 0.3f))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (seg3Active) strength.color else Color.LightGray.copy(alpha = 0.3f))
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Checklist pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RequirementChip(label = "8+ chars", met = strength.hasMinLength)
            RequirementChip(label = "Number", met = strength.hasNumber)
            RequirementChip(label = "Mixed case", met = strength.hasMixedCase)
        }
    }
}

@Composable
private fun RequirementChip(label: String, met: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            if (met) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (met) BadgeGreen else Color.Gray,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (met) BadgeGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = if (met) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Clean, Friendly Guest Mode Action Restriction Dialog
 * Prompts user to register or sign in only when strictly needed for data persistence / messaging / contact reveal.
 */
@Composable
fun GuestAuthPromptDialog(
    reason: String,
    onDismiss: () -> Unit,
    onSignInWithEmail: () -> Unit,
    onGoogleSso: () -> Unit,
    onFacebookSso: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Account Required",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = reason,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                // 1-Tap Google SSO Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onGoogleSso()
                            onDismiss()
                        }
                        .testTag("dialog_google_sso_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color(0xFFEA4335).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA4335)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 1-Tap Facebook SSO Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFacebookSso()
                            onDismiss()
                        }
                        .testTag("dialog_facebook_sso_btn"),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, Color(0xFF1877F2).copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1877F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("f", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Facebook",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onSignInWithEmail()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Sign In", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
