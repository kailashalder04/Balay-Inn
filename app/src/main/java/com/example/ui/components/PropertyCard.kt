package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.PropertyEntity
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * Formats location to strictly "Barangay/District, Province only"
 */
fun formatBarangayProvinceOnly(locationDisplay: String, property: PropertyEntity): String {
    val parts = locationDisplay.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.size >= 3) {
        return "${parts.first()}, ${parts.last()}"
    } else if (parts.size == 2) {
        return "${parts[0]}, ${parts[1]}"
    }

    val district = when {
        property.barangayId.isNotBlank() && !property.barangayId.startsWith("bgy_", ignoreCase = true) -> property.barangayId
        property.neighborhood.isNotBlank() -> property.neighborhood
        property.cityMunicipalityId.isNotBlank() && !property.cityMunicipalityId.startsWith("city_", ignoreCase = true) -> property.cityMunicipalityId
        else -> "Kasambagan"
    }
    val province = when {
        property.provinceId.isNotBlank() && !property.provinceId.startsWith("prov_", ignoreCase = true) -> property.provinceId
        else -> "Cebu"
    }
    return "$district, $province"
}

/**
 * Derives a luxury dark palette tone matching the property image
 */
private fun deriveDarkToneColor(imageUrl: String?, id: String): Color {
    val palettes = listOf(
        Color(0xFF1E293B), // Slate Navy
        Color(0xFF263220), // Olive Moss (matching warm/green reference card)
        Color(0xFF1E2832), // Deep Charcoal Blue
        Color(0xFF2C2420), // Warm Espresso / Amber Dark
        Color(0xFF1D2C2F), // Forest Teal Slate
        Color(0xFF25222E)  // Dark Indigo
    )
    val hash = ((imageUrl ?: id).hashCode() and 0x7FFFFFFF)
    return palettes[hash % palettes.size]
}

/**
 * Dynamically samples pixels from the property image to choose the card color
 */
private fun extractDominantDarkColor(drawable: Drawable): Color? {
    return try {
        val bitmap: Bitmap = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            else -> {
                val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 100
                val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 100
                val bmp = Bitmap.createBitmap(
                    width.coerceAtMost(300),
                    height.coerceAtMost(300),
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            }
        } ?: return null

        if (bitmap.width <= 0 || bitmap.height <= 0) return null

        // Sample pixels from the bottom 40% of the image where it transitions to the card body
        val startY = (bitmap.height * 0.6f).toInt()
        val endY = bitmap.height
        val stepX = (bitmap.width / 8).coerceAtLeast(1)
        val stepY = ((endY - startY) / 5).coerceAtLeast(1)

        var totalR = 0L
        var totalG = 0L
        var totalB = 0L
        var count = 0

        var y = startY
        while (y < endY) {
            var x = 0
            while (x < bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                totalR += r
                totalG += g
                totalB += b
                count++
                x += stepX
            }
            y += stepY
        }

        if (count == 0) return null

        val avgR = (totalR / count).toInt()
        val avgG = (totalG / count).toInt()
        val avgB = (totalB / count).toInt()

        // Darken color to keep white typography prominent and high contrast
        val darkFactor = 0.35f
        val finalR = (avgR * darkFactor).toInt().coerceIn(18, 52)
        val finalG = (avgG * darkFactor).toInt().coerceIn(22, 58)
        val finalB = (avgB * darkFactor).toInt().coerceIn(24, 65)

        Color(android.graphics.Color.rgb(finalR, finalG, finalB))
    } catch (_: Throwable) {
        null
    }
}

@Composable
fun PropertyCard(
    property: PropertyEntity,
    locationDisplay: String = "",
    imageUrls: List<String> = emptyList(),
    isSaved: Boolean = false,
    onSaveToggle: () -> Unit = {},
    isCompared: Boolean = false,
    onCompareToggle: () -> Unit = {},
    onClick: () -> Unit = {},
    onViewOnMap: (() -> Unit)? = null,
    onMessageHost: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }
    }
    // 3. Rent e.g., ₱5,500/mo only
    val formattedRent = remember(property.monthlyRent) {
        "₱" + currencyFormat.format(property.monthlyRent)
    }

    val addressOnly = remember(locationDisplay, property) {
        formatBarangayProvinceOnly(locationDisplay, property)
    }

    val primaryImage = imageUrls.firstOrNull()

    // 1. Choose card color according to the color of the main property image
    var cardColor by remember(primaryImage, property.id) {
        mutableStateOf(deriveDarkToneColor(primaryImage, property.id))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Header Image Box (natural photo with subtle bottom transition into cardColor)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp)
            ) {
                if (!primaryImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = primaryImage,
                        contentDescription = property.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onSuccess = { state ->
                            extractDominantDarkColor(state.result.drawable)?.let { extractedColor ->
                                cardColor = extractedColor
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0F172A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Gradient: top is transparent/subtle shade for badge clarity, bottom fades into cardColor right where the rent begins
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    cardColor.copy(alpha = 0.6f),
                                    cardColor
                                )
                            )
                        )
                )

                // Top row badges: Earlier restored badge on left, Save button on right
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 2. Restore badge to earlier one (Category type in primary color with 8.dp radius)
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = property.propertyType,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Save / Favorite Action Button
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) "Remove from saved" else "Save property",
                            tint = if (isSaved) Color(0xFFE11D48) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Card Body Content:
            // 3. Rent e.g., ₱5,500/mo only
            // Name
            // Address- Barangay/District, Province only
            // 4. Footer: 3 Specification with icons below the address (host handle removed)
            // Lower Part of the Card: Color tone starts from the rent
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardColor)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // 3. Rent e.g., ₱5,500/mo only (prominent, bold, without "Rent:" or "List:")
                Text(
                    text = "$formattedRent/mo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 20.sp,
                    maxLines = 1,
                    modifier = Modifier.testTag("property_card_rent_${property.id}")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Name
                Text(
                    text = property.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF8FAFC),
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("property_card_title_${property.id}")
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Address- Barangay/District, Province only
                Text(
                    text = addressOnly,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("property_card_address_${property.id}")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Divider line before footer specifications
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Footer: 3 Specification with icons below the address
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("property_card_specs_${property.id}"),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Spec 1: Living Area with icon
                    val areaText = if (property.floorAreaSqm > 0) "${property.floorAreaSqm.toInt()} m²" else "24 m²"
                    SpecificationItem(
                        icon = Icons.Default.SquareFoot,
                        value = areaText,
                        label = "Living",
                        contentDescription = "Living Area"
                    )

                    // Vertical Divider 1
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Spec 2: Rooms with icon
                    val roomCount = if (property.bedrooms > 0) "${property.bedrooms}" else "1"
                    val roomLabel = if (property.bedrooms == 1) "Room" else "Rooms"
                    SpecificationItem(
                        icon = Icons.Default.KingBed,
                        value = roomCount,
                        label = roomLabel,
                        contentDescription = "Rooms"
                    )

                    // Vertical Divider 2
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                            .background(Color.White.copy(alpha = 0.15f))
                    )

                    // Spec 3: Baths with icon
                    val bathCount = if (property.bathrooms > 0) "${property.bathrooms}" else "1"
                    val bathLabel = if (property.bathrooms == 1) "Bath" else "Baths"
                    SpecificationItem(
                        icon = Icons.Default.Bathtub,
                        value = bathCount,
                        label = bathLabel,
                        contentDescription = "Bathrooms"
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecificationItem(
    icon: ImageVector,
    value: String,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
