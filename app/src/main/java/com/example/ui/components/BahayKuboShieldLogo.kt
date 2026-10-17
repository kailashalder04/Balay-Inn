package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modern Bahay Kubo (Nipa Hut) & Security Shield Logo for Balay-Inn.
 * Blends traditional Filipino housing heritage with modern trust & verification security.
 */
@Composable
fun BahayKuboShieldLogo(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    badgeCornerRadius: Dp = 28.dp,
    showGradientBackground: Boolean = true
) {
    val backgroundBrush = if (showGradientBackground) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF4579AC), // Steel Blue
                Color(0xFF2E618B), // Deep Slate Blue
                Color(0xFFF59E0B)  // Warm Amber
            )
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(if (showGradientBackground) 8.dp else 0.dp, RoundedCornerShape(badgeCornerRadius))
            .clip(RoundedCornerShape(badgeCornerRadius))
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.72f)) {
            val w = this.size.width
            val h = this.size.height

            // 1. Sleek Security Shield Frame (Outer Frame)
            val shieldPath = Path().apply {
                moveTo(w * 0.5f, h * 0.05f) // Top center tip
                lineTo(w * 0.92f, h * 0.20f) // Top right corner
                lineTo(w * 0.92f, h * 0.52f) // Right vertical edge
                cubicTo(
                    w * 0.92f, h * 0.80f,
                    w * 0.68f, h * 0.95f,
                    w * 0.50f, h * 1.00f  // Bottom shield point
                )
                cubicTo(
                    w * 0.32f, h * 0.95f,
                    w * 0.08f, h * 0.80f,
                    w * 0.08f, h * 0.52f  // Left vertical edge
                )
                lineTo(w * 0.08f, h * 0.20f) // Top left corner
                close()
            }

            // Draw Shield Fill Overlay
            drawPath(
                path = shieldPath,
                color = Color.White.copy(alpha = 0.18f)
            )

            // Draw Shield Border Stroke
            drawPath(
                path = shieldPath,
                color = Color.White,
                style = Stroke(
                    width = w * 0.055f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 2. Bahay Kubo (Nipa Hut) Roof & Structure Inside Shield
            val roofPath = Path().apply {
                // High-peaked steep thatched roof
                moveTo(w * 0.5f, h * 0.22f) // Peak
                lineTo(w * 0.82f, h * 0.48f) // Right overhang eave
                lineTo(w * 0.74f, h * 0.48f) // Eave return right
                lineTo(w * 0.5f, h * 0.28f) // Inner peak
                lineTo(w * 0.26f, h * 0.48f) // Eave return left
                lineTo(w * 0.18f, h * 0.48f) // Left overhang eave
                close()
            }

            // Draw Roof (Warm Amber Gold)
            drawPath(
                path = roofPath,
                color = Color(0xFFFFD166)
            )

            // Roof Ridge Crest
            drawPath(
                path = Path().apply {
                    moveTo(w * 0.5f, h * 0.22f)
                    lineTo(w * 0.82f, h * 0.48f)
                    moveTo(w * 0.5f, h * 0.22f)
                    lineTo(w * 0.18f, h * 0.48f)
                },
                color = Color.White,
                style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
            )

            // 3. Woven Bamboo Wall Body (Sawali / Bamboo Walls)
            val wallPath = Path().apply {
                moveTo(w * 0.28f, h * 0.48f)
                lineTo(w * 0.72f, h * 0.48f)
                lineTo(w * 0.70f, h * 0.72f)
                lineTo(w * 0.30f, h * 0.72f)
                close()
            }

            drawPath(
                path = wallPath,
                color = Color.White.copy(alpha = 0.90f)
            )

            // 4. Central Doorway / Arched Entrance
            val doorPath = Path().apply {
                moveTo(w * 0.43f, h * 0.72f)
                lineTo(w * 0.43f, h * 0.57f)
                cubicTo(
                    w * 0.43f, h * 0.53f,
                    w * 0.57f, h * 0.53f,
                    w * 0.57f, h * 0.57f
                )
                lineTo(w * 0.57f, h * 0.72f)
                close()
            }

            drawPath(
                path = doorPath,
                color = Color(0xFF2E618B)
            )

            // 5. Stilted Wooden Posts (Haligi / House Stilts)
            // Left Stilt Post
            drawLine(
                color = Color(0xFFFFD166),
                start = androidx.compose.ui.geometry.Offset(w * 0.33f, h * 0.72f),
                end = androidx.compose.ui.geometry.Offset(w * 0.33f, h * 0.83f),
                strokeWidth = w * 0.045f,
                cap = StrokeCap.Round
            )

            // Right Stilt Post
            drawLine(
                color = Color(0xFFFFD166),
                start = androidx.compose.ui.geometry.Offset(w * 0.67f, h * 0.72f),
                end = androidx.compose.ui.geometry.Offset(w * 0.67f, h * 0.83f),
                strokeWidth = w * 0.045f,
                cap = StrokeCap.Round
            )

            // Center Stilt Post
            drawLine(
                color = Color(0xFFFFD166),
                start = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.72f),
                end = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.85f),
                strokeWidth = w * 0.045f,
                cap = StrokeCap.Round
            )
        }
    }
}
