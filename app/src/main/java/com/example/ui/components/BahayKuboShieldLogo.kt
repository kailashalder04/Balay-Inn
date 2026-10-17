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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Precision 3D Bahay Kubo & Security Shield Emblem matching Balay-Inn brand graphics.
 * Features dual-tone Steel Blue and Warm Amber metallic shield with multi-tiered nipa hut & ladder steps.
 */
@Composable
fun BahayKuboShieldLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    badgeCornerRadius: Dp = 32.dp,
    showGradientBackground: Boolean = true
) {
    val backgroundBrush = if (showGradientBackground) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF2E618B), // Steel Blue
                Color(0xFF1E3A5F), // Deep Dark Slate Blue
                Color(0xFFF59E0B)  // Warm Amber
            )
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .size(size)
            .shadow(if (showGradientBackground) 10.dp else 0.dp, RoundedCornerShape(badgeCornerRadius))
            .clip(RoundedCornerShape(badgeCornerRadius))
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.82f)) {
            val w = this.size.width
            val h = this.size.height

            // Shield Geometry
            val outerShieldPath = Path().apply {
                moveTo(w * 0.50f, h * 0.02f)
                lineTo(w * 0.94f, h * 0.18f)
                lineTo(w * 0.94f, h * 0.54f)
                cubicTo(
                    w * 0.94f, h * 0.82f,
                    w * 0.68f, h * 0.96f,
                    w * 0.50f, h * 1.00f
                )
                cubicTo(
                    w * 0.32f, h * 0.96f,
                    w * 0.06f, h * 0.82f,
                    w * 0.06f, h * 0.54f
                )
                lineTo(w * 0.06f, h * 0.18f)
                close()
            }

            val innerShieldPath = Path().apply {
                moveTo(w * 0.50f, h * 0.07f)
                lineTo(w * 0.88f, h * 0.21f)
                lineTo(w * 0.88f, h * 0.52f)
                cubicTo(
                    w * 0.88f, h * 0.78f,
                    w * 0.65f, h * 0.91f,
                    w * 0.50f, h * 0.95f
                )
                cubicTo(
                    w * 0.35f, h * 0.91f,
                    w * 0.12f, h * 0.78f,
                    w * 0.12f, h * 0.52f
                )
                lineTo(w * 0.12f, h * 0.21f)
                close()
            }

            // 1. Draw Inner Shield Background with Golden Amber Radial Gradient
            drawPath(
                path = innerShieldPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD166), // Golden Amber Center Glow
                        Color(0xFFF59E0B), // Warm Amber
                        Color(0xFFB45309)  // Deep Amber Bronze Edge
                    ),
                    center = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.4f),
                    radius = w * 0.7f
                )
            )

            // 2. Outer Steel Blue Shield Border
            drawPath(
                path = outerShieldPath,
                color = Color(0xFF2E618B),
                style = Stroke(
                    width = w * 0.08f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 3. Inner Golden Trim Border
            drawPath(
                path = innerShieldPath,
                color = Color(0xFFFFE39F),
                style = Stroke(
                    width = w * 0.035f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // 4. Bahay Kubo Multi-tiered Gabled Roof (Steel Blue with Gold Trim)
            // Upper Roof Tier
            val topRoofPath = Path().apply {
                moveTo(w * 0.50f, h * 0.18f)
                lineTo(w * 0.72f, h * 0.32f)
                lineTo(w * 0.28f, h * 0.32f)
                close()
            }
            drawPath(path = topRoofPath, color = Color(0xFF1E3A5F))
            drawPath(path = topRoofPath, color = Color(0xFFFFD166), style = Stroke(width = w * 0.025f))

            // Main Roof Tier
            val mainRoofPath = Path().apply {
                moveTo(w * 0.50f, h * 0.26f)
                lineTo(w * 0.82f, h * 0.44f)
                lineTo(w * 0.18f, h * 0.44f)
                close()
            }
            drawPath(path = mainRoofPath, color = Color(0xFF2E618B))
            drawPath(path = mainRoofPath, color = Color(0xFFFFD166), style = Stroke(width = w * 0.035f))

            // Center Roof Divider Line
            drawLine(
                color = Color(0xFFFFD166),
                start = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.18f),
                end = androidx.compose.ui.geometry.Offset(w * 0.50f, h * 0.44f),
                strokeWidth = w * 0.025f
            )

            // 5. Wooden House Body
            val houseBodyPath = Path().apply {
                moveTo(w * 0.25f, h * 0.44f)
                lineTo(w * 0.75f, h * 0.44f)
                lineTo(w * 0.72f, h * 0.65f)
                lineTo(w * 0.28f, h * 0.65f)
                close()
            }
            drawPath(path = houseBodyPath, color = Color(0xFFFFD166))
            drawPath(path = houseBodyPath, color = Color(0xFF1E3A5F), style = Stroke(width = w * 0.028f))

            // Doorway
            val doorPath = Path().apply {
                moveTo(w * 0.44f, h * 0.65f)
                lineTo(w * 0.44f, h * 0.51f)
                lineTo(w * 0.56f, h * 0.51f)
                lineTo(w * 0.56f, h * 0.65f)
                close()
            }
            drawPath(path = doorPath, color = Color(0xFF1E3A5F))
            drawCircle(
                color = Color(0xFFFFD166),
                radius = w * 0.015f,
                center = androidx.compose.ui.geometry.Offset(w * 0.53f, h * 0.58f)
            )

            // Side Windows
            drawRect(
                color = Color(0xFF1E3A5F),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.31f, h * 0.48f),
                size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.08f)
            )
            drawRect(
                color = Color(0xFF1E3A5F),
                topLeft = androidx.compose.ui.geometry.Offset(w * 0.61f, h * 0.48f),
                size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.08f)
            )

            // 6. Porch Railing & Horizontal Stilt/Ladder Steps
            // Porch Base Deck
            drawLine(
                color = Color(0xFF1E3A5F),
                start = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.65f),
                end = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.65f),
                strokeWidth = w * 0.045f,
                cap = StrokeCap.Square
            )

            // Vertical Stilt Pillars
            val stiltXPositions = listOf(0.26f, 0.38f, 0.50f, 0.62f, 0.74f)
            stiltXPositions.forEach { xFrac ->
                drawLine(
                    color = Color(0xFF1E3A5F),
                    start = androidx.compose.ui.geometry.Offset(w * xFrac, h * 0.65f),
                    end = androidx.compose.ui.geometry.Offset(w * xFrac, h * 0.88f),
                    strokeWidth = w * 0.028f
                )
            }

            // Horizontal Ladder Steps / Wooden Rungs (3 steps inside lower shield)
            val stepYPositions = listOf(0.71f, 0.77f, 0.83f)
            stepYPositions.forEachIndexed { idx, yFrac ->
                val inset = idx * 0.035f
                drawLine(
                    color = Color(0xFFFFD166),
                    start = androidx.compose.ui.geometry.Offset(w * (0.28f + inset), h * yFrac),
                    end = androidx.compose.ui.geometry.Offset(w * (0.72f - inset), h * yFrac),
                    strokeWidth = w * 0.032f,
                    cap = StrokeCap.Round
                )
                // Black/Navy Outline on Step
                drawLine(
                    color = Color(0xFF1E3A5F),
                    start = androidx.compose.ui.geometry.Offset(w * (0.28f + inset), h * yFrac),
                    end = androidx.compose.ui.geometry.Offset(w * (0.72f - inset), h * yFrac),
                    strokeWidth = w * 0.012f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
