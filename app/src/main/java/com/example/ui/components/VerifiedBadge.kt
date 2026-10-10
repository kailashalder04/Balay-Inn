package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity

// Vibrant, accessible green colors for verification badges and checkmarks
val VerifiedGreen = Color(0xFF16A34A)
val VerifiedGreenBg = Color(0xFFDCFCE7)
val VerifiedGreenDarkBg = Color(0xFF14532D)

/**
 * Platform verification criteria helper logic.
 * A property qualifies as verified if the host contact channels (phone and email)
 * have been verified (Basic verification), or if the listing has verified documents/visit (verificationLevel >= 2).
 */
fun isPropertyVerified(property: PropertyEntity): Boolean {
    return (property.phoneVerified && property.emailVerified) || property.verificationLevel >= 2
}

/**
 * Extension helper on PropertyEntity for idiomatic checking.
 */
fun PropertyEntity.isPlatformVerified(): Boolean {
    return isPropertyVerified(this)
}

/**
 * Clean inline green checkmark icon component to display next to property titles.
 * Meets accessibility criteria with a clear contentDescription and testTag.
 * Static indicator with no click interaction.
 */
@Composable
fun VerifiedIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color = VerifiedGreen,
    contentDescription: String = "Verified Listing"
) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(size)
            .testTag("verified_icon")
    )
}

/**
 * Elegant 'Verified' pill overlay badge for property images and cards.
 * Combines the green checkmark with a readable 'Verified' badge label.
 * Static indicator with no click interaction.
 */
@Composable
fun VerifiedPillBadge(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    text: String = "Verified"
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Color(0xFF162536).copy(alpha = 0.92f) else Color.White.copy(alpha = 0.95f),
        border = BorderStroke(
            0.8.dp,
            if (isDark) VerifiedGreen.copy(alpha = 0.6f) else VerifiedGreen.copy(alpha = 0.5f)
        ),
        modifier = modifier.testTag("verified_pill_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Checkmark",
                tint = VerifiedGreen,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.5.dp))
            Text(
                text = text,
                color = if (isDark) Color(0xFF86EFAC) else Color(0xFF15803D),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
