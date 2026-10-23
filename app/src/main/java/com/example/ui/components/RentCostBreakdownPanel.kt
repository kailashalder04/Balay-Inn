package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RentCostBreakdown
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RentCostBreakdownPanel(
    breakdown: RentCostBreakdown,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH")).apply {
        maximumFractionDigits = 0
    }

    // Helper to determine whether a service cost is applicable and non-zero/non-free
    fun isApplicableCost(value: String): Boolean {
        val lower = value.lowercase().trim()
        if (lower.isEmpty()) return false
        if (lower.contains("₱0") || lower.contains("php 0") || lower.contains("0.00") || lower.contains("free") || lower.contains("none")) return false
        if (lower.contains("no maintenance") || lower.contains("no dedicated") || lower.contains("not applicable") || lower.contains("n/a")) return false
        if (lower.contains("included in monthly rent") || lower.contains("included in rent") || lower.contains("(included)")) return false
        return true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Transparent Cost & Fee Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CostRow("Monthly Base Rent", currencyFormat.format(breakdown.monthlyRent))
            if (isApplicableCost(breakdown.electricityBilling)) {
                CostRow("Electricity Billing", breakdown.electricityBilling)
            }
            if (isApplicableCost(breakdown.waterBilling)) {
                CostRow("Water Billing", breakdown.waterBilling)
            }
            if (isApplicableCost(breakdown.internetBilling)) {
                CostRow("Internet / WiFi", breakdown.internetBilling)
            }
            if (isApplicableCost(breakdown.parkingBilling)) {
                CostRow("Parking Slot", breakdown.parkingBilling)
            }
            if (isApplicableCost(breakdown.maintenanceDues)) {
                CostRow("Association / Maintenance Dues", breakdown.maintenanceDues)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

            if (breakdown.advanceMonths > 0 && breakdown.advanceRentAmount > 0) {
                CostRow("Advance Rent (${breakdown.advanceMonths} mo)", currencyFormat.format(breakdown.advanceRentAmount))
            }
            if (breakdown.depositMonths > 0 && breakdown.securityDepositAmount > 0) {
                CostRow("Security Deposit (${breakdown.depositMonths} mo)", currencyFormat.format(breakdown.securityDepositAmount))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Move-in Cash Required",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = currencyFormat.format(breakdown.totalUpfrontMoveIn),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Accepted Payments: ${breakdown.acceptedPayments} • ${breakdown.minLeaseDuration}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CostRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
