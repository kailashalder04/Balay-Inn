package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity

data class RentCostBreakdown(
    val monthlyRent: Double,
    val advanceMonths: Int,
    val depositMonths: Int,
    val advanceRentAmount: Double,
    val securityDepositAmount: Double,
    val totalUpfrontMoveIn: Double,
    val waterBilling: String,
    val electricityBilling: String,
    val internetBilling: String,
    val parkingBilling: String,
    val maintenanceDues: String,
    val acceptedPayments: String,
    val minLeaseDuration: String
)

data class HostContactInfo(
    val hostName: String,
    val hostRole: String,
    val phone: String,
    val contactChannel: String = "Calls & SMS",
    val viewingPolicy: String = "Bring 1 valid ID to view",
    val gateCurfewPolicy: String = "Gate locked at 10 PM (night-shift key available)",
    val isVerified: Boolean = true
)

object PropertyCostHelper {

    fun getRentCostBreakdown(property: PropertyEntity): RentCostBreakdown {
        val advanceMonths = if (property.advanceRentMonths > 0) property.advanceRentMonths else 1
        val depositMonths = if (property.securityDepositMonths > 0) property.securityDepositMonths else 1
        val advanceAmount = property.monthlyRent * advanceMonths
        val depositAmount = property.monthlyRent * depositMonths
        val totalUpfront = advanceAmount + depositAmount

        val isDampas = property.id.contains("dampas", ignoreCase = true) ||
            property.posterName.contains("Mark", ignoreCase = true) ||
            property.title.contains("Dampas", ignoreCase = true)

        val water = when {
            isDampas -> "₱200 / head / mo (fixed rate)"
            property.utilitiesIncluded -> "Included in monthly rent (₱0)"
            else -> "Sub-meter / ₱150 - ₱200 / head est."
        }

        val elec = when {
            isDampas -> "Individual sub-meter (BOHECO rates)"
            property.utilitiesIncluded -> "Included in monthly rent"
            property.provinceId.contains("cebu", ignoreCase = true) -> "Individual sub-meter (VECO rates)"
            property.provinceId.contains("bacolod", ignoreCase = true) -> "Individual sub-meter (CENECO rates)"
            else -> "Individual sub-meter (BOHECO rates)"
        }

        val wifi = when {
            isDampas || property.internetIncluded -> "Free shared fiber WiFi in compound (₱0)"
            else -> "Optional fiber WiFi add-on"
        }

        val parking = when {
            isDampas -> "Motorcycle parking inside gated yard (Included)"
            property.parkingAvailable -> if (property.parkingFees > 0) "₱${property.parkingFees.toInt()} / mo" else "Motorcycle & scooter parking (Free)"
            else -> "Street parking / No dedicated vehicle slot"
        }

        val maintenance = if (property.associationDues > 0 || property.maintenanceFees > 0) {
            "₱${(property.associationDues + property.maintenanceFees).toInt()} / mo"
        } else {
            "No maintenance / association dues (₱0)"
        }

        val payment = if (isDampas) "Cash, GCash accepted" else "Cash, GCash, Maya, Bank Transfer"
        val duration = if (isDampas) "Minimum 6 months stay" else "${property.leaseDurationMonths} months minimum stay"

        return RentCostBreakdown(
            monthlyRent = property.monthlyRent,
            advanceMonths = advanceMonths,
            depositMonths = depositMonths,
            advanceRentAmount = advanceAmount,
            securityDepositAmount = depositAmount,
            totalUpfrontMoveIn = totalUpfront,
            waterBilling = water,
            electricityBilling = elec,
            internetBilling = wifi,
            parkingBilling = parking,
            maintenanceDues = maintenance,
            acceptedPayments = payment,
            minLeaseDuration = duration
        )
    }

    fun getHostContactInfo(property: PropertyEntity, user: UserEntity? = null): HostContactInfo {
        val isDampas = property.id.contains("dampas", ignoreCase = true) ||
            property.posterName.contains("Mark", ignoreCase = true) ||
            property.title.contains("Dampas", ignoreCase = true)

        val phone = when {
            !user?.phone.isNullOrBlank() -> user!!.phone
            isDampas -> "0908-555-7890"
            property.posterName.contains("Santos", ignoreCase = true) -> "+63 920 123 7890"
            property.posterName.contains("Flores", ignoreCase = true) -> "+63 939 678 1234"
            property.posterName.contains("Villanueva", ignoreCase = true) -> "+63 917 888 9911"
            property.posterName.contains("Yap", ignoreCase = true) -> "+63 918 901 2345"
            else -> "0908-555-7890"
        }

        val viewing = if (isDampas) {
            "Bring 1 valid ID to view • Call/Text Kuya Mark to book site visit"
        } else {
            "Bring 1 valid government/student ID for site inspection"
        }

        val curfew = if (isDampas) {
            "Gate locked at 10 PM (night-shift key provided) • No sleepover guests"
        } else {
            "24/7 compound access • Dedicated gate key"
        }

        return HostContactInfo(
            hostName = if (isDampas) "Kuya Mark" else property.posterName.ifBlank { "Kuya Mark" },
            hostRole = if (isDampas) "Caretaker (Authorized)" else property.posterRole.ifBlank { "Host / Caretaker" },
            phone = phone,
            contactChannel = if (isDampas) "Calls & texts only" else "Calls, SMS & In-app Chat",
            viewingPolicy = viewing,
            gateCurfewPolicy = curfew,
            isVerified = property.isPosterAuthorized
        )
    }
}
