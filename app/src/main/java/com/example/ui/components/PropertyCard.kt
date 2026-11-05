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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

/**
 * Resolves a reliable, high-quality photograph matching the property category
 * so cards never display blank or broken boxes when properties are loaded.
 */
fun getDefaultPropertyThumbnail(propertyType: String?): String {
    val t = (propertyType ?: "").lowercase()
    return when {
        t.contains("board") || t.contains("bed") || t.contains("dorm") || t == "room" ->
            "https://images.unsplash.com/photo-1555854877-bab0e564b8d5?w=1200&auto=format&fit=crop&q=80"
        t.contains("apart") || t.contains("flat") ->
            "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=1200&auto=format&fit=crop&q=80"
        t.contains("condo") ->
            "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=1200&auto=format&fit=crop&q=80"
        t.contains("studio") ->
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=1200&auto=format&fit=crop&q=80"
        t.contains("town") || (t.contains("house") && !t.contains("board")) ->
            "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=1200&auto=format&fit=crop&q=80"
        else ->
            "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=1200&auto=format&fit=crop&q=80"
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

    val defaultFallback = remember(property.propertyType) {
        getDefaultPropertyThumbnail(property.propertyType)
    }
    val primaryImage = remember(imageUrls, defaultFallback) {
        imageUrls.firstOrNull()?.takeIf { it.isNotBlank() } ?: defaultFallback
    }

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
            // Base background while image loads
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F172A))
            )

            // Full-bleed Property Image covering the WHOLE card
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(primaryImage)
                    .crossfade(true)
                    .build(),
                contentDescription = property.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("property_card_image_${property.id}")
            )

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

                    Surface(
                        onClick = onSaveToggle,
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.55f),
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("property_card_save_${property.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isSaved) "Remove from saved" else "Save property",
                                tint = if (isSaved) Color(0xFFE11D48) else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
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
                        fontWeight = FontWeight.Black,
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
                        fontWeight = FontWeight.ExtraBold,
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

/**
 * Reusable property card component to display listings with:
 * - Image (loaded via Coil AsyncImage with crossfade and placeholder fallback)
 * - Price
 * - Title
 * - Location
 *
 * Can be used anywhere across the app without requiring a PropertyEntity.
 */
@Composable
fun ReusablePropertyCard(
    imageUrl: String?,
    price: String,
    title: String,
    location: String,
    modifier: Modifier = Modifier,
    propertyType: String? = null,
    isSaved: Boolean = false,
    onSaveToggle: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("reusable_property_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Image Section with Coil AsyncImage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                val effectiveImage = imageUrl?.takeIf { it.isNotBlank() }
                    ?: getDefaultPropertyThumbnail(propertyType)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(effectiveImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("property_card_image")
                )

                // Optional Property Type Badge
                if (!propertyType.isNullOrBlank()) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f)
                    ) {
                        Text(
                            text = propertyType,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF95E8A3),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Optional Save/Favorite Button
                if (onSaveToggle != null) {
                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("property_card_save_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isSaved) "Remove from favorites" else "Save to favorites",
                            tint = if (isSaved) Color(0xFFFF5252) else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Info Section: Price, Title, Location
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Price
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("property_card_price")
                )

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("property_card_title")
                )

                // Location with Pin Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("property_card_location")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/**
 * Overload of PropertyCard accepting primitive parameters (imageUrl, price, title, location)
 * for reusable decoupled rendering anywhere in the app.
 */
@Composable
fun PropertyCard(
    imageUrl: String?,
    price: String,
    title: String,
    location: String,
    modifier: Modifier = Modifier,
    propertyType: String? = null,
    isSaved: Boolean = false,
    onSaveToggle: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    ReusablePropertyCard(
        imageUrl = imageUrl,
        price = price,
        title = title,
        location = location,
        modifier = modifier,
        propertyType = propertyType,
        isSaved = isSaved,
        onSaveToggle = onSaveToggle,
        onClick = onClick
    )
}

