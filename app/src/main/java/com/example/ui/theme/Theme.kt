package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7AA5D6), // Vibrant light Steel Blue for dark theme
    onPrimary = Color(0xFF0F1B2B),
    primaryContainer = Color(0xFF29496C),
    onPrimaryContainer = Color(0xFFE1EDF8),
    secondary = AmberLight,
    onSecondary = Color(0xFF0F1B2B),
    secondaryContainer = Color(0xFF52330B),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = EmeraldLight,
    onTertiary = Color(0xFF0F1B2B),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = SlateBackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SlateSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF3D5878),
    outlineVariant = Color(0xFF253B54)
)

private val LightColorScheme = lightColorScheme(
    primary = SteelBluePrimary, // #4579AC
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDF4FA),
    onPrimaryContainer = Color(0xFF18334E),
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = EmeraldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = SlateBackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SlateSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SlateSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFB8C9DC),
    outlineVariant = Color(0xFFDFE7F2)
)

@Composable
fun StayNestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors
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

    val view = LocalView.current
    if (!view.isInEditMode && view.context is Activity) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun RentalFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = StayNestTheme(darkTheme, dynamicColor, content)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = StayNestTheme(darkTheme, dynamicColor, content)
