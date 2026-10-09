package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.UserEntity
import java.text.NumberFormat
import java.util.Locale

/**
 * Aligning with user's Boarding House & User TypeScript specification:
 * - UserRole = 'guest' | 'renter' | 'host' | 'admin'
 * - UserProfile
 * - ListingStatus
 * - VerificationChecks
 * - PrivateVerificationData
 * - RoomType
 * - BoardingHouseListing
 * - Inquiry
 * - ListingReport
 * - AuditLog
 * - FilterOptions
 * - Coordinates
 */

enum class AppUserRole(val value: String, val displayName: String) {
    GUEST("guest", "Guest"),
    RENTER("renter", "Renter / Tenant"),
    HOST("host", "Host / Owner"),
    ADMIN("admin", "Admin");

    companion object {
        fun fromString(role: String?): AppUserRole {
            if (role == null) return RENTER
            return when (role.lowercase().trim()) {
                "guest" -> GUEST
                "renter", "tenant" -> RENTER
                "host", "owner", "landlord", "authorized_manager", "caretaker" -> HOST
                "admin", "staff" -> ADMIN
                else -> RENTER
            }
        }
    }
}

data class PrivacyPreferences(
    val sharePhoneWithHost: Boolean = true,
    val allowLocationTracking: Boolean = true
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val isPhoneVerified: Boolean = true,
    val avatar: String? = null,
    val characterId: String? = null,
    val role: AppUserRole = AppUserRole.RENTER,
    val isHost: Boolean = false,
    val savedListingIds: List<String> = emptyList(),
    val createdAt: String = "2024-01-15T08:00:00Z",
    val privacyPreferences: PrivacyPreferences = PrivacyPreferences()
) {
    companion object {
        fun fromUserEntity(entity: UserEntity, savedIds: List<String> = emptyList()): UserProfile {
            val role = when {
                entity.role.contains("ADMIN", ignoreCase = true) -> AppUserRole.ADMIN
                entity.role.contains("OWNER", ignoreCase = true) ||
                    entity.role.contains("LANDLORD", ignoreCase = true) ||
                    entity.role.contains("CARETAKER", ignoreCase = true) ||
                    entity.role.contains("MANAGER", ignoreCase = true) -> AppUserRole.HOST
                else -> AppUserRole.RENTER
            }
            return UserProfile(
                id = entity.id,
                name = entity.fullName,
                email = entity.email,
                phone = entity.phone,
                isPhoneVerified = entity.phoneVerified,
                avatar = null,
                characterId = null,
                role = role,
                isHost = role == AppUserRole.HOST || role == AppUserRole.ADMIN,
                savedListingIds = savedIds,
                createdAt = "2024-01-15T08:00:00Z",
                privacyPreferences = PrivacyPreferences(
                    sharePhoneWithHost = true,
                    allowLocationTracking = true
                )
            )
        }
    }
}

enum class ListingStatus(val value: String, val label: String) {
    DRAFT("draft", "Draft"),
    UNVERIFIED("unverified", "Unverified"),
    PENDING_VERIFICATION("pending_verification", "Pending Verification"),
    APPROVED("approved", "Approved"),
    NEEDS_CHANGES("needs_changes", "Needs Changes"),
    SUSPENDED("suspended", "Suspended"),
    ARCHIVED("archived", "Archived");

    companion object {
        fun fromString(status: String?): ListingStatus {
            if (status == null) return APPROVED
            return when (status.lowercase().trim()) {
                "draft" -> DRAFT
                "unverified" -> UNVERIFIED
                "pending_verification", "under_review" -> PENDING_VERIFICATION
                "approved", "active" -> APPROVED
                "needs_changes" -> NEEDS_CHANGES
                "suspended", "paused" -> SUSPENDED
                "archived", "rented" -> ARCHIVED
                else -> APPROVED
            }
        }
    }
}

data class VerificationChecks(
    val hostIdentity: Boolean = true,
    val hostIdentityType: String? = "Philippine National ID (PhilSys)",
    val authorizationToList: Boolean = true,
    val authorizationRole: String? = "owner", // 'owner' | 'authorized_manager'
    val propertyLocation: Boolean = true,
    val listingDetails: Boolean = true,
    val lastVerifiedAt: String? = "2024-09-15T10:30:00Z",
    val verifiedBy: String? = "Balay-Inn Compliance Team"
)

data class PrivateVerificationData(
    val exactAddress: String,
    val hostFullName: String,
    val hostPhone: String,
    val idDocumentName: String,
    val idDocumentType: String,
    val authorizationDocName: String,
    val isOwner: Boolean = true,
    val managerAuthorizationProof: String? = null,
    val recentPropertyPhotoName: String = "property_facade_photo.jpg",
    val notesToAdmin: String? = null
)

enum class GenderRule(val value: String, val displayName: String, val badge: String) {
    ALL("all", "All Genders / Mixed", "Mixed / All"),
    FEMALE_ONLY("female_only", "Female Only", "Female Only"),
    MALE_ONLY("male_only", "Male Only", "Male Only");

    companion object {
        fun fromString(value: String?): GenderRule {
            if (value == null) return ALL
            return when (value.lowercase().trim()) {
                "female_only", "female", "women_only", "ladies_only" -> FEMALE_ONLY
                "male_only", "male", "men_only" -> MALE_ONLY
                else -> ALL
            }
        }
    }
}

data class RoomType(
    val id: String,
    val name: String, // e.g. "2-Person Aircon Room", "Solo Bedspace Room", "4-Bed Fan Dorm"
    val genderRule: GenderRule = GenderRule.ALL,
    val totalBeds: Int,
    val occupiedBeds: Int,
    val availableBeds: Int = (totalBeds - occupiedBeds).coerceAtLeast(0),
    val monthlyRent: Double, // in PHP ₱
    val securityDeposit: Double = monthlyRent,
    val advanceRentMonths: Int = 1,
    val utilityDetails: String = "Water included, electric sub-meter",
    val hasAircon: Boolean = false,
    val hasPrivateBathroom: Boolean = false
) {
    val occupancyDisplay: String
        get() = "$availableBeds of $totalBeds beds available"

    val isAvailable: Boolean
        get() = availableBeds > 0
}

data class LivingSpecifications(
    val roomDescription: String? = null,
    val wifiDetails: String? = null,
    val waterDetails: String? = null,
    val electricityDetails: String? = null,
    val roadProximity: String? = null,
    val curfewRule: String? = null,
    val visitorsRule: String? = null,
    val cookingRule: String? = null,
    val laundryRule: String? = null,
    val smokingRule: String? = null,
    val petsRule: String? = null,
    val quietHoursRule: String? = null
)

data class ListingFlags(
    val repeatedAddress: Boolean = false,
    val unusuallyLowPrice: Boolean = false,
    val frequentChanges: Boolean = false,
    val needsHumanReview: Boolean = false,
    val flagReason: String? = null
)

data class BoardingHouseListing(
    val id: String,
    val title: String,
    val shortDescription: String,
    val fullDescription: String,
    val status: ListingStatus = ListingStatus.APPROVED,
    val isIllustrativeDemo: Boolean = false,
    val hostId: String,
    val hostName: String,
    val hostPhone: String,
    val hostJoinedDate: String = "Joined March 2023",
    val hostResponseTime: String = "Within 1 hour",

    // Location
    val barangay: String, // e.g. "Pajo"
    val city: String, // "Lapu-Lapu City"
    val province: String, // "Cebu"
    val nearbyLandmark: String, // e.g. "3-min walk to Lapu-Lapu City Hall & Gaisano Mactan"
    val latitude: Double,
    val longitude: Double,

    // Private verification data
    val privateVerification: PrivateVerificationData? = null,

    // Pricing & Capacities
    val minMonthlyRent: Double,
    val maxMonthlyRent: Double,
    val totalCapacity: Int,
    val totalAvailableBeds: Int,
    val lastAvailabilityUpdate: String = "Updated today",

    // Features
    val amenities: List<String> = emptyList(),
    val houseRules: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val rooms: List<RoomType> = emptyList(),

    // Detailed Specifications & Living Rules
    val specifications: LivingSpecifications? = null,

    // Verification & Trust
    val verifications: VerificationChecks = VerificationChecks(),
    val adminChangeRequestNote: String? = null,

    // Flags
    val flags: ListingFlags? = null,

    val createdAt: String = "2024-01-15T08:00:00Z",
    val updatedAt: String = "2024-10-01T08:00:00Z"
) {
    val formattedRentRange: String
        get() {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
                maximumFractionDigits = 0
            }
            return if (minMonthlyRent == maxMonthlyRent) {
                "${currencyFormat.format(minMonthlyRent).replace("PHP", "₱")} / mo"
            } else {
                "${currencyFormat.format(minMonthlyRent).replace("PHP", "₱")} - ${currencyFormat.format(maxMonthlyRent).replace("PHP", "₱")} / mo"
            }
        }

    val primaryGenderRule: GenderRule
        get() {
            val rules = rooms.map { it.genderRule }.distinct()
            return when {
                rules.size == 1 -> rules.first()
                rules.contains(GenderRule.FEMALE_ONLY) && !rules.contains(GenderRule.MALE_ONLY) -> GenderRule.FEMALE_ONLY
                rules.contains(GenderRule.MALE_ONLY) && !rules.contains(GenderRule.FEMALE_ONLY) -> GenderRule.MALE_ONLY
                else -> GenderRule.ALL
            }
        }
}

enum class InquiryType(val value: String, val label: String) {
    INQUIRY("inquiry", "General Inquiry"),
    SCHEDULE_VIEWING("schedule_viewing", "Schedule Viewing"),
    APPLY("apply", "Apply for Bedspace")
}

enum class InquiryStatus(val value: String, val label: String) {
    PENDING("pending", "Pending Host Confirmation"),
    VIEWING_SCHEDULED("viewing_scheduled", "Viewing Scheduled"),
    REPLIED("replied", "Replied"),
    DECLINED("declined", "Declined")
}

data class Inquiry(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val listingAddress: String,
    val renterId: String,
    val renterName: String,
    val renterEmail: String,
    val renterPhone: String,
    val hostId: String,
    val type: InquiryType = InquiryType.INQUIRY,
    val targetMoveInDate: String? = null,
    val preferredViewingDate: String? = null,
    val preferredViewingTime: String? = null,
    val message: String,
    val status: InquiryStatus = InquiryStatus.PENDING,
    val hostReply: String? = null,
    val createdAt: String = "2024-10-05T14:30:00Z"
)

enum class ReportReason(val value: String, val label: String) {
    FAKE_PROPERTY("fake_property", "Fake Property / Non-existent Address"),
    MISLEADING_PRICE("misleading_price", "Misleading Price / Hidden Charges"),
    UNAVAILABLE_ROOM("unavailable_room", "No Available Rooms / False Vacancy"),
    IMPERSONATION("impersonation", "Impersonating Owner or Caretaker"),
    PAYMENT_PRESSURE("payment_pressure", "Suspicious Advance Payment Pressure"),
    OTHER("other", "Other Policy Violation")
}

enum class ReportStatus(val value: String, val label: String) {
    PENDING_REVIEW("pending_review", "Pending Review"),
    ACTION_TAKEN("action_taken", "Action Taken"),
    DISMISSED("dismissed", "Dismissed")
}

data class ListingReport(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val hostId: String,
    val reporterId: String,
    val reporterName: String? = null,
    val reason: ReportReason = ReportReason.FAKE_PROPERTY,
    val details: String,
    val status: ReportStatus = ReportStatus.PENDING_REVIEW,
    val adminDecisionNote: String? = null,
    val createdAt: String = "2024-10-05T16:00:00Z",
    val resolvedAt: String? = null
)

data class AuditLog(
    val id: String,
    val timestamp: String = "2024-10-05T16:00:00Z",
    val actorId: String,
    val actorName: String,
    val actorRole: AppUserRole,
    val action: String,
    val targetId: String,
    val targetType: String, // 'listing' | 'verification' | 'report' | 'user'
    val details: String
)

data class BoardingHouseFilterOptions(
    val searchQuery: String = "",
    val barangay: String = "",
    val maxPrice: Double = 30000.0,
    val minAvailableBeds: Int = 0,
    val roomCapacity: Int = 0,
    val genderRule: GenderRule = GenderRule.ALL,
    val amenities: List<String> = emptyList(),
    val maxDistanceKm: Double = 25.0,
    val sortBy: String = "highest_available" // 'nearest' | 'lowest_price' | 'recently_updated' | 'highest_available'
)

data class Coordinates(
    val lat: Double,
    val lng: Double
)

/**
 * Extension function to transform a Room database PropertyEntity into a rich
 * BoardingHouseListing aligned with the Boarding House & User specification.
 */
fun PropertyEntity.toBoardingHouseListing(
    hostPhone: String = "",
    photosList: List<String> = emptyList()
): BoardingHouseListing {
    val isDampas = this.id.contains("dampas", ignoreCase = true)
    val isPanglao = this.id.contains("boh_01", ignoreCase = true) || this.id.contains("panglao", ignoreCase = true)
    val isCebu = this.id.contains("cebu", ignoreCase = true) || this.cityMunicipalityId.contains("cebu", ignoreCase = true)
    val isBacolod = this.id.contains("bacolod", ignoreCase = true) || this.cityMunicipalityId.contains("bacolod", ignoreCase = true)

    val provName = when {
        isCebu -> "Cebu"
        isBacolod -> "Negros Occidental"
        else -> "Bohol"
    }

    val cityName = when {
        isCebu -> "Cebu City"
        isBacolod -> "Bacolod City"
        isPanglao -> "Panglao"
        else -> "Tagbilaran City"
    }

    val bgyName = when {
        isDampas -> "Dampas"
        isPanglao -> "Tawala"
        isCebu -> "Lahug"
        isBacolod -> "Villamonte"
        this.neighborhood.isNotBlank() -> this.neighborhood.split(",").firstOrNull()?.trim() ?: "Central"
        else -> "Poblacion"
    }

    val resolvedPhone = if (hostPhone.isNotBlank()) hostPhone else "0908-555-7890"

    // Dynamic Room types based on boarding house configuration
    val roomTypes = when {
        isDampas -> listOf(
            RoomType(
                id = "room_${this.id}_1",
                name = "Solo Private Room with Attached CR",
                genderRule = GenderRule.ALL,
                totalBeds = 2,
                occupiedBeds = 1,
                availableBeds = 1,
                monthlyRent = this.monthlyRent,
                securityDeposit = this.monthlyRent,
                advanceRentMonths = this.advanceRentMonths,
                utilityDetails = "Fixed ₱200/mo water, BOHECO electric sub-meter",
                hasAircon = false,
                hasPrivateBathroom = true
            ),
            RoomType(
                id = "room_${this.id}_2",
                name = "2-Bed Fan Bedspace (Twin Sharing)",
                genderRule = GenderRule.MALE_ONLY,
                totalBeds = 2,
                occupiedBeds = 2,
                availableBeds = 0,
                monthlyRent = 3200.0,
                securityDeposit = 3200.0,
                advanceRentMonths = 1,
                utilityDetails = "Fixed ₱200/mo water, shared sub-meter",
                hasAircon = false,
                hasPrivateBathroom = false
            )
        )
        this.bedrooms == 0 || this.propertyType.contains("Studio", ignoreCase = true) || this.propertyType.contains("Room", ignoreCase = true) -> listOf(
            RoomType(
                id = "room_${this.id}_1",
                name = if (this.monthlyRent >= 5000) "Deluxe Aircon Studio Room" else "Solo Fan Room",
                genderRule = GenderRule.ALL,
                totalBeds = 2,
                occupiedBeds = 1,
                availableBeds = 1,
                monthlyRent = this.monthlyRent,
                securityDeposit = this.monthlyRent,
                advanceRentMonths = this.advanceRentMonths,
                utilityDetails = if (this.utilitiesIncluded) "Water & Electricity included in rent" else "Water included, digital electric sub-meter",
                hasAircon = this.monthlyRent >= 5000,
                hasPrivateBathroom = this.bathrooms > 0
            ),
            RoomType(
                id = "room_${this.id}_2",
                name = "Bedspace Option (Shared Room)",
                genderRule = if (this.title.contains("Ladies", ignoreCase = true)) GenderRule.FEMALE_ONLY else GenderRule.ALL,
                totalBeds = 4,
                occupiedBeds = 2,
                availableBeds = 2,
                monthlyRent = (this.monthlyRent * 0.55).coerceAtLeast(2500.0),
                securityDeposit = (this.monthlyRent * 0.55).coerceAtLeast(2500.0),
                advanceRentMonths = 1,
                utilityDetails = "Equal share sub-meter, free WiFi",
                hasAircon = false,
                hasPrivateBathroom = false
            )
        )
        else -> listOf(
            RoomType(
                id = "room_${this.id}_1",
                name = "Master Bedroom with Private CR",
                genderRule = GenderRule.ALL,
                totalBeds = 2,
                occupiedBeds = 1,
                availableBeds = 1,
                monthlyRent = this.monthlyRent,
                securityDeposit = this.monthlyRent,
                advanceRentMonths = this.advanceRentMonths,
                utilityDetails = "Sub-metered utilities, high-speed WiFi",
                hasAircon = true,
                hasPrivateBathroom = true
            ),
            RoomType(
                id = "room_${this.id}_2",
                name = "Common Bedroom (Fan/Aircon ready)",
                genderRule = GenderRule.ALL,
                totalBeds = 2,
                occupiedBeds = 0,
                availableBeds = 2,
                monthlyRent = (this.monthlyRent * 0.75).coerceAtLeast(3500.0),
                securityDeposit = (this.monthlyRent * 0.75).coerceAtLeast(3500.0),
                advanceRentMonths = 1,
                utilityDetails = "Shared common CR, sub-metered power",
                hasAircon = false,
                hasPrivateBathroom = false
            )
        )
    }

    val totalCap = roomTypes.sumOf { it.totalBeds }
    val totalAvail = roomTypes.sumOf { it.availableBeds }
    val minRent = roomTypes.minOfOrNull { it.monthlyRent } ?: this.monthlyRent
    val maxRent = roomTypes.maxOfOrNull { it.monthlyRent } ?: this.monthlyRent

    val cat = PhilippineRentalPropertyCategory.fromPropertyType(this.propertyType)
    val localDU = PhilippineRentalPropertyCategory.getLocalElectricProvider(this.provinceId)

    val livingSpecs = LivingSpecifications(
        roomDescription = if (isDampas) {
            "Single bed with pull-out mattress, study table with chair, wall fan, screen windows, and attached private CR."
        } else {
            "Comfortable bedframe with foam mattress, personal cabinet with padlock loop, study desk, and good ventilation."
        },
        wifiDetails = if (this.internetIncluded) "Free Shared High-Speed Fiber WiFi (200 Mbps) compound-wide" else cat.internetDefault,
        waterDetails = if (isDampas) "Fixed ₱200 per head/month (continuous deep-well supply + overhead tank)" else cat.waterDefault,
        electricityDetails = if (this.utilitiesIncluded) "Electricity included in monthly rent (fair use policy)" else cat.electricityDefault.replace("VECO/MERALCO/CENECO/BOHECO", localDU),
        roadProximity = if (this.nearbyLandmark.isNotBlank()) "Walking distance to ${this.nearbyLandmark}, 1-ride transit to central district" else "Along accessible paved road, 2 mins to highway",
        curfewRule = if (isDampas) "Gate locked at 10 PM. Tenants receive personal gate key for night shifts / BPO" else cat.curfewDefault,
        visitorsRule = cat.visitorsDefault,
        cookingRule = if (isDampas) "Light electric cooking allowed (rice cooker, electric kettle). No gas/LPG stoves" else cat.cookingDefault,
        laundryRule = "Designated handwash laundry sink & roof deck drying lines. Laundromat within 2-min walk",
        smokingRule = "Strictly non-smoking inside all rooms, bathrooms, and interior corridors",
        petsRule = cat.petPolicyDefault,
        quietHoursRule = "Quiet study & rest hours strictly observed between 10:00 PM and 6:00 AM"
    )

    val houseRulesList = listOfNotNull(
        cat.curfewDefault,
        cat.securityDefault,
        cat.visitorsDefault,
        cat.cookingDefault,
        cat.petPolicyDefault,
        cat.parkingDefault,
        "Quiet hours: 10:00 PM - 6:00 AM",
        "Bring 1 valid Philippine Government / School ID upon move-in"
    ).distinct()

    val amenitiesList = listOf(
        if (this.bathrooms > 0) "Attached Private CR" else "Shared Common CR",
        if (this.internetIncluded) "Free Shared Fiber WiFi" else "WiFi Ready",
        if (this.parkingAvailable) "Motorcycle Parking Space" else "Street Parking",
        if (this.furnishedStatus == "Furnished") "Furnished (Bed & Desk)" else "Semi-Furnished",
        "Individual Utility Sub-meter",
        "Gated Secure Compound",
        "CCTV in Common Areas"
    )

    val verifChecks = VerificationChecks(
        hostIdentity = this.phoneVerified && this.emailVerified,
        hostIdentityType = if (this.identitySubmitted) "Philippine National ID (PhilSys)" else "Government ID Verified",
        authorizationToList = this.isPosterAuthorized,
        authorizationRole = if (this.posterRole.contains("Owner", ignoreCase = true)) "owner" else "authorized_manager",
        propertyLocation = true,
        listingDetails = true,
        lastVerifiedAt = "2024-09-20",
        verifiedBy = "Balay-Inn Compliance"
    )

    val cleanDescription = if (isDampas) {
        "Comfortable boarding house space situated in the peaceful locality of Dampas, Tagbilaran City, Bohol. Strategically located within walking distance to Holy Name University (HNU) and Island City Mall (ICM), with direct paved access to public transit and major city thoroughfares."
    } else {
        val loc = "${bgyName}, ${cityName}"
        val landmarkDetail = if (this.nearbyLandmark.isNotBlank()) "Conveniently located near ${this.nearbyLandmark}." else "Situated along a peaceful, accessible residential road."
        val transitDetail = "Direct access to local transit routes and main circumferential roads, making commuting quick and effortless."
        "Spacious residential living space in $loc. $landmarkDetail $transitDetail"
    }

    return BoardingHouseListing(
        id = this.id,
        title = this.title,
        shortDescription = cleanDescription,
        fullDescription = cleanDescription,
        status = ListingStatus.fromString(this.status),
        isIllustrativeDemo = false,
        hostId = this.posterId,
        hostName = this.posterName,
        hostPhone = resolvedPhone,
        hostJoinedDate = "Joined March 2023",
        hostResponseTime = "Within 1 hour",
        barangay = bgyName,
        city = cityName,
        province = provName,
        nearbyLandmark = if (this.nearbyLandmark.isNotBlank()) this.nearbyLandmark else "$bgyName, $cityName",
        latitude = this.latitude,
        longitude = this.longitude,
        privateVerification = PrivateVerificationData(
            exactAddress = this.streetAddressPrivate.ifBlank { "${this.neighborhood}, $bgyName, $cityName" },
            hostFullName = this.posterName,
            hostPhone = resolvedPhone,
            idDocumentName = "philsys_id_${this.posterId}.pdf",
            idDocumentType = "Philippine National ID",
            authorizationDocName = "notarized_authorization_${this.id}.pdf",
            isOwner = this.posterRole.contains("Owner", ignoreCase = true),
            recentPropertyPhotoName = "property_facade_verified.jpg"
        ),
        minMonthlyRent = minRent,
        maxMonthlyRent = maxRent,
        totalCapacity = totalCap,
        totalAvailableBeds = totalAvail,
        lastAvailabilityUpdate = "Updated today",
        amenities = amenitiesList,
        houseRules = houseRulesList,
        photos = photosList,
        rooms = roomTypes,
        specifications = livingSpecs,
        verifications = verifChecks,
        createdAt = "2024-01-15T08:00:00Z",
        updatedAt = "2024-10-01T08:00:00Z"
    )
}

/**
 * Generates a clean, concise description strictly covering property space, locality,
 * nearby destinations, and transit details, omitting room counts, prices, contact, and tenure.
 */
fun PropertyEntity.getCleanLocalityDescription(): String {
    val bgy = this.neighborhood.ifBlank { "accessible neighbourhood" }
    val landmarkPart = if (this.nearbyLandmark.isNotBlank()) "Conveniently near ${this.nearbyLandmark}." else "Located in a safe and peaceful community."
    val transitPart = "Easy access to major roads and local transit."
    return "Pleasant property space in $bgy. $landmarkPart $transitPart"
}
