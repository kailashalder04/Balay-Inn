package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val currencyFormat = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = 0
        }
    }
    // 1. Rent e.g., ₱5,500/mo only
    val formattedRent = remember(property.monthlyRent) {
        "₱" + currencyFormat.format(property.monthlyRent)
    }

    // 3. Address- Barangay/District, Province only
    val addressOnly = remember(locationDisplay, property) {
        formatBarangayProvinceOnly(locationDisplay, property)
    }

    val primaryImage = imageUrls.firstOrNull()

    Card(
        modifier = modifier
            .height(325.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .testTag("property_card_${property.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-bleed Property Image covering the WHOLE card
            if (!primaryImage.isNullOrEmpty()) {
                AsyncImage(
                    model = primaryImage,
                    contentDescription = property.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
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
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            // Dark gradient overlay to guarantee text legibility over any photo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.45f), // Top protection for badges
                                Color.Transparent,               // Clear middle showing the photo
                                Color.Black.copy(alpha = 0.65f), // Smooth transition
                                Color.Black.copy(alpha = 0.88f)  // Darker base for texts
                            ),
                            startY = 0f,
                            endY = 950f
                        )
                    )
            )

            // Content: Top Badges & Bottom Texts on Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Category badge & Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = property.propertyType,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .size(34.dp)
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

                // Lower Section: Texts directly on card
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Rent e.g., ₱5,500/mo only
                    Text(
                        text = "$formattedRent/mo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 18.5.sp,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.testTag("property_card_rent_${property.id}")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // 2. Name
                    Text(
                        text = property.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("property_card_title_${property.id}")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // 3. Address- Barangay/District, Province only
                    Text(
                        text = addressOnly,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.5.sp,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("property_card_address_${property.id}")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtle divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.22f))
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    // 4. Footer: 3 Specification with icons below the address (single line icon and text)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("property_card_specs_${property.id}"),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Spec 1: Living Area
                        val areaText = if (property.floorAreaSqm > 0) "${property.floorAreaSqm.toInt()} m²" else "24 m²"
                        SpecificationItem(
                            icon = Icons.Default.SquareFoot,
                            text = areaText,
                            contentDescription = "Living Area",
                            modifier = Modifier.weight(1f)
                        )

                        // Vertical Divider 1
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.22f))
                        )

                        // Spec 2: Rooms
                        val roomCount = if (property.bedrooms > 0) "${property.bedrooms}" else "1"
                        val roomLabel = if (property.bedrooms == 1) "Room" else "Rooms"
                        SpecificationItem(
                            icon = Icons.Default.KingBed,
                            text = "$roomCount $roomLabel",
                            contentDescription = "Rooms",
                            modifier = Modifier.weight(1f)
                        )

                        // Vertical Divider 2
                        Box(
                            modifier = Modifier
                                .height(14.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.22f))
                        )

                        // Spec 3: Baths
                        val bathCount = if (property.bathrooms > 0) "${property.bathrooms}" else "1"
                        val bathLabel = if (property.bathrooms == 1) "Bath" else "Baths"
                        SpecificationItem(
                            icon = Icons.Default.Bathtub,
                            text = "$bathCount $bathLabel",
                            contentDescription = "Bathrooms",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecificationItem(
    icon: ImageVector,
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
