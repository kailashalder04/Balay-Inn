package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PropertyEntity
import com.example.data.model.PhilippineRentalPropertyCategory

/**
 * Data representation for a category item with count
 */
data class CategoryFilterItem(
    val id: String,
    val name: String,
    val shortLabel: String,
    val emoji: String,
    val count: Int,
    val isSelected: Boolean
)

@Composable
fun CategoryFilterRow(
    selectedCategory: String,
    allProperties: List<PropertyEntity>,
    onSelectCategory: (String) -> Unit,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val totalCount = allProperties.size

    val categories = listOf(
        CategoryFilterItem(
            id = "All",
            name = "All Properties",
            shortLabel = "All Types",
            emoji = "🏠",
            count = totalCount,
            isSelected = selectedCategory == "All" || selectedCategory.isBlank()
        ),
        CategoryFilterItem(
            id = PhilippineRentalPropertyCategory.APARTMENT_FLAT.displayName,
            name = "Apartments & Flats",
            shortLabel = "Apartments",
            emoji = "🏢",
            count = allProperties.count {
                PhilippineRentalPropertyCategory.fromPropertyType(it.propertyType) == PhilippineRentalPropertyCategory.APARTMENT_FLAT
            },
            isSelected = isCategoryMatch(selectedCategory, PhilippineRentalPropertyCategory.APARTMENT_FLAT)
        ),
        CategoryFilterItem(
            id = PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE.displayName,
            name = "Houses & Townhouses",
            shortLabel = "Houses",
            emoji = "🏡",
            count = allProperties.count {
                PhilippineRentalPropertyCategory.fromPropertyType(it.propertyType) == PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE
            },
            isSelected = isCategoryMatch(selectedCategory, PhilippineRentalPropertyCategory.HOUSE_TOWNHOUSE)
        ),
        CategoryFilterItem(
            id = PhilippineRentalPropertyCategory.BED_SPACE.displayName,
            name = "Dormitories & Bedspaces",
            shortLabel = "Dormitories",
            emoji = "🎓",
            count = allProperties.count {
                PhilippineRentalPropertyCategory.fromPropertyType(it.propertyType) == PhilippineRentalPropertyCategory.BED_SPACE
            },
            isSelected = isCategoryMatch(selectedCategory, PhilippineRentalPropertyCategory.BED_SPACE)
        ),
        CategoryFilterItem(
            id = PhilippineRentalPropertyCategory.CONDOMINIUM.displayName,
            name = "Condominiums",
            shortLabel = "Condos",
            emoji = "🏙️",
            count = allProperties.count {
                PhilippineRentalPropertyCategory.fromPropertyType(it.propertyType) == PhilippineRentalPropertyCategory.CONDOMINIUM
            },
            isSelected = isCategoryMatch(selectedCategory, PhilippineRentalPropertyCategory.CONDOMINIUM)
        ),
        CategoryFilterItem(
            id = PhilippineRentalPropertyCategory.STUDIO_UNIT.displayName,
            name = "Studio Units",
            shortLabel = "Studios",
            emoji = "👤",
            count = allProperties.count {
                PhilippineRentalPropertyCategory.fromPropertyType(it.propertyType) == PhilippineRentalPropertyCategory.STUDIO_UNIT
            },
            isSelected = isCategoryMatch(selectedCategory, PhilippineRentalPropertyCategory.STUDIO_UNIT)
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .testTag("category_filter_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            categories.forEach { item ->
                CategoryChip(
                    item = item,
                    onClick = {
                        if (item.isSelected && item.id != "All") {
                            onSelectCategory("All")
                        } else {
                            onSelectCategory(item.id)
                        }
                    },
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    item: CategoryFilterItem,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val backgroundColor = if (item.isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (isDark) {
        Color(0xFF1E293B)
    } else {
        Color(0xFFF1F5F9)
    }

    val contentColor = if (item.isSelected) {
        Color.White
    } else if (isDark) {
        Color(0xFFE2E8F0)
    } else {
        Color(0xFF334155)
    }

    val borderColor = if (item.isSelected) {
        MaterialTheme.colorScheme.primary
    } else if (isDark) {
        Color(0xFF334155)
    } else {
        Color(0xFFE2E8F0)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("category_chip_${item.shortLabel.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.emoji} ${item.shortLabel}",
                fontSize = 12.sp,
                fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Count Badge
            Surface(
                shape = CircleShape,
                color = if (item.isSelected) {
                    Color.White.copy(alpha = 0.25f)
                } else if (isDark) {
                    Color(0xFF334155)
                } else {
                    Color(0xFFCBD5E1)
                }
            ) {
                Text(
                    text = item.count.toString(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp)
                )
            }
        }
    }
}

@Composable
fun SelectedCategoryHeaderCard(
    selectedCategory: String,
    filteredCount: Int,
    onResetCategory: () -> Unit,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (selectedCategory == "All" || selectedCategory.isBlank()) return

    val cat = PhilippineRentalPropertyCategory.fromPropertyType(selectedCategory)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_category_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF6FF)
        ),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFBFDBFE))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(cat.targetEmoji, fontSize = 16.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "$filteredCount properties found",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                TextButton(onClick = onResetCategory) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear category filter",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Show All", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDark) Color(0xFF0F172A) else Color.White,
                border = BorderStroke(0.8.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "👤 Target Renter: ${cat.targetMarket}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⚡ Typical Setup: ${cat.summaryUtilities}",
                        fontSize = 10.5.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun isCategoryMatch(selected: String, target: PhilippineRentalPropertyCategory): Boolean {
    if (selected.isBlank() || selected == "All") return false
    val resolvedSelected = PhilippineRentalPropertyCategory.fromPropertyType(selected)
    return resolvedSelected == target
}
