package com.example.model

enum class GenderPolicy(val displayName: String) {
    ALL("All Welcome"),
    FEMALE_ONLY("Female Only"),
    MALE_ONLY("Male Only")
}

enum class RoomType(val displayName: String) {
    SINGLE("Single Room"),
    BEDSPACE("Bedspace / Shared"),
    STUDIO("Studio Unit"),
    BOARDING("Classic Boarding")
}

data class LandlordInfo(
    val name: String,
    val phone: String,
    val isVerified: Boolean = true,
    val responseTime: String = "Within 1 hour",
    val memberSince: String = "2023"
)

data class BoardingHouse(
    val id: String,
    val title: String,
    val description: String,
    val roomType: RoomType,
    val monthlyRent: Int, // in USD / local currency
    val deposit: Int,
    val address: String,
    val city: String,
    val proximityText: String, // e.g. "300m to State University Gate 2"
    val rating: Float,
    val reviewCount: Int,
    val genderPolicy: GenderPolicy,
    val availableRooms: Int,
    val totalRooms: Int,
    val curfewText: String, // e.g. "10:30 PM Curfew" or "No Curfew"
    val utilitiesIncluded: List<String>,
    val amenities: List<String>,
    val houseRules: List<String>,
    val landlord: LandlordInfo,
    val isSaved: Boolean = false
)

data class Inquiry(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val tenantName: String,
    val tenantPhone: String,
    val targetDate: String,
    val message: String,
    val status: String = "Pending Reply",
    val submittedAt: String
)

data class ViewingTour(
    val id: String,
    val propertyId: String,
    val propertyTitle: String,
    val date: String,
    val timeSlot: String,
    val contactPhone: String,
    val status: String = "Confirmed"
)
