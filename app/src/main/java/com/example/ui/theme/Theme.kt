package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TealDarkPrimary,
    onPrimary = TealPrimaryDark,
    primaryContainer = TealPrimary,
    onPrimaryContainer = Color.White,
    secondary = AmberSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF452600),
    onSecondaryContainer = AmberContainer,
    tertiary = SageDark,
    onTertiary = Color(0xFF0F241A),
    tertiaryContainer = SageDarkContainer,
    onTertiaryContainer = OnSageDarkContainer,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF2E3E53),
    outlineVariant = Color(0xFF1E2D44)
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = OnAmberContainer,
    tertiary = ForestSage,
    onTertiary = Color.White,
    tertiaryContainer = SageContainer,
    onTertiaryContainer = OnSageContainer,
    background = SurfaceBackground,
    surface = SurfaceCard,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEBF2F1),
    onSurfaceVariant = TextSecondary,
    outline = DividerBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded Balay-Inn palette consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Material3 ColorScheme extensions for Muted Sage and Soft Forest Green highlights.
 * Reduces eye strain across both Light and Dark modes.
 */
val androidx.compose.material3.ColorScheme.sage: Color
    get() = if (this.surface == DarkSurface) SageDark else ForestSage

val androidx.compose.material3.ColorScheme.sageContainer: Color
    get() = if (this.surface == DarkSurface) SageDarkContainer else SageContainer

val androidx.compose.material3.ColorScheme.onSageContainer: Color
    get() = if (this.surface == DarkSurface) OnSageDarkContainer else OnSageContainer

val androidx.compose.material3.ColorScheme.sageBorder: Color
    get() = if (this.surface == DarkSurface) SageDarkBorder else SageBorder

