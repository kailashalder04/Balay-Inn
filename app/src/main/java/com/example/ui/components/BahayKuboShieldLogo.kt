package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Modern Balay-Inn App Logo Emblem displaying the 3D orange and slate geometric house icon.
 */
@Composable
fun BahayKuboShieldLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    badgeCornerRadius: Dp = 32.dp,
    showGradientBackground: Boolean = true
) {
    AppLogo(
        modifier = modifier,
        size = size,
        badgeCornerRadius = badgeCornerRadius,
        showShadow = showGradientBackground
    )
}

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    badgeCornerRadius: Dp = 32.dp,
    showShadow: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .then(if (showShadow) Modifier.shadow(8.dp, RoundedCornerShape(badgeCornerRadius)) else Modifier)
            .clip(RoundedCornerShape(badgeCornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Balay-Inn Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
