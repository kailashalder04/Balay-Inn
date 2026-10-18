package com.example.data.model

import com.example.data.local.entity.PropertyEntity

/**
 * Standard Philippine Rental Property Classifications & Market Standards
 *
 * Provides authoritative definitions for the 4 primary Philippine rental categories
 * (plus locality house/townhouse options), including expected amenities, utilities setup,
 * house rules & security, and target renter demographics.
 */
enum class PhilippineRentalPropertyCategory(
    val displayName: String,
    val shortLabel: String,
    val targetMarket: String,
    val targetEmoji: String,
    val expectedAmenities: List<String>,
    val waterDefault: String,
    val waterOptions: List<String>,
    val electricityDefault: String,
    val electricityOptions: List<String>,
    val internetDefault: String,
    val internetOptions: List<String>,
    val curfewDefault: String,
    val curfewOptions: List<String>,
    val securityDefault: String,
    val securityOptions: List<String>,
    val visitorsDefault: String,
    val visitorsOptions: List<String>,
    val cookingDefault: String,
    val cookingOptions: List<String>,
    val petPolicyDefault: String,
    val petPolicyOptions: List<String>,
    val parkingDefault: String,
    val parkingOptions: List<String>,
    val localityNotes: String
) {
    BED_SPACE(
        displayName = "Bed Space / Boarding House",
        shortLabel = "Bed Space / Boarding House",
        targetMarket = "Students, budget BPO workers",
        targetEmoji = "🎓",
        expectedAmenities = listOf(
            "Bunk beds",
            "Individual lockers",
            "Shared/common CR",
            "Common dirty kitchen",
            "Study table & chair",
            "Wall fan",
            "Shared refrigerator"
        ),
        waterDefault = "Flat rate water (per head)",
        waterOptions = listOf("Flat rate water (per head)", "Fixed ₱150 - ₱200/mo per head", "Included in bed rent"),
        electricityDefault = "Sub-metered electricity divided among roommates",
        electricityOptions = listOf(
            "Sub-metered electricity divided among roommates",
            "Sub-meter per room (pro-rated)",
            "Fixed electricity add-on (₱300-₱500/head)"
        ),
        internetDefault = "Shared WiFi",
        internetOptions = listOf("Shared WiFi", "Free shared fiber WiFi in compound", "Viber/Hotspot ready"),
        curfewDefault = "Curfew hours (Gate locked at 10 PM)",
        curfewOptions = listOf("Curfew hours (Gate locked at 10 PM)", "Gate locked at 11 PM (night-shift key for BPO)", "No curfew with personal gate key"),
        securityDefault = "Caretaker on-site",
        securityOptions = listOf("Caretaker on-site", "Caretaker on-site & CCTV", "Gated compound with perimeter lock"),
        visitorsDefault = "Strictly no visitors (No sleepovers)",
        visitorsOptions = listOf("Strictly no visitors", "Daytime visitors in common area only", "No sleepover visitors"),
        cookingDefault = "No LPG cooking (rice cooker/induction only)",
        cookingOptions = listOf("No LPG cooking (rice cooker/induction only)", "Electric appliances only in dirty kitchen", "Light electric cooking only"),
        petPolicyDefault = "Strictly no pets",
        petPolicyOptions = listOf("Strictly no pets", "No pets allowed"),
        parkingDefault = "Motorcycle parking only",
        parkingOptions = listOf("Motorcycle parking only", "Motorcycle & bicycle parking inside gate", "Street parking only"),
        localityNotes = "Common near university belts (HNU, BISU, USC, USLS) and provincial BPO hubs."
    ),

    STUDIO_UNIT(
        displayName = "Studio Unit (Standard/Budget)",
        shortLabel = "Studio Unit",
        targetMarket = "Solo renters, young professionals, couples",
        targetEmoji = "👤",
        expectedAmenities = listOf(
            "Open layout",
            "Private CR",
            "Kitchenette with sink",
            "Window-type AC or fan",
            "Study desk / table",
            "Built-in wardrobe / cabinet"
        ),
        waterDefault = "Own sub-meter for water",
        waterOptions = listOf("Own sub-meter for water", "Fixed ₱200 / head / mo", "Direct water bill connection"),
        electricityDefault = "Own sub-meter for electricity",
        electricityOptions = listOf(
            "Own sub-meter for electricity (VECO/MERALCO/CENECO/BOHECO)",
            "Individual sub-meter",
            "Direct primary electric meter"
        ),
        internetDefault = "Tenant applies for own WiFi",
        internetOptions = listOf("Tenant applies for own WiFi", "Shared compound fiber WiFi (Free)", "Fiber line provision ready"),
        curfewDefault = "24/7 gate key (no curfew)",
        curfewOptions = listOf("24/7 gate key (no curfew)", "Night-shift key provided", "24/7 compound access"),
        securityDefault = "Gated compound with tenant key",
        securityOptions = listOf("Gated compound with tenant key", "CCTV monitored compound", "Keyless digital lock / RFID"),
        visitorsDefault = "Daytime visitors allowed",
        visitorsOptions = listOf("Daytime visitors allowed", "Visitors allowed (no permanent sleepovers)", "Daytime visitors only"),
        cookingDefault = "Light electric cooking / Induction only",
        cookingOptions = listOf("Light electric cooking / Induction only", "No gas stove (electric appliances only)", "Designated cooking nook with exhaust"),
        petPolicyDefault = "Small pets by approval",
        petPolicyOptions = listOf("Small pets by approval", "Strictly no pets", "Cats allowed / Small dogs allowed"),
        parkingDefault = "Street or motorcycle parking only",
        parkingOptions = listOf("Street or motorcycle parking only", "Motorcycle parking inside gated yard", "Designated motorcycle slot"),
        localityNotes = "Ideal for IT Park employees, hospital workers, and independent students."
    ),

    APARTMENT_FLAT(
        displayName = "Apartment / Multi-BR Flat",
        shortLabel = "Apartment / Flat",
        targetMarket = "Small families, groups of friends",
        targetEmoji = "👨‍👩‍👧",
        expectedAmenities = listOf(
            "1 to 3 bedrooms",
            "Separate living (sala) area",
            "Dining area",
            "Dedicated wash/sampayan area",
            "Kitchen counter with cabinets",
            "Dedicated gated car garage"
        ),
        waterDefault = "Individual water pump/tank",
        waterOptions = listOf("Individual water pump/tank", "Direct city water meter (MCWD/Baciwa/City Water)", "Deep-well with overhead tank"),
        electricityDefault = "Independent main utility meters",
        electricityOptions = listOf("Independent main utility meters", "Direct VECO/MERALCO/CENECO meter", "Separate billing meter"),
        internetDefault = "Dedicated fiber internet line ready",
        internetOptions = listOf("Dedicated fiber internet line ready", "Tenant applies for own line (PLDT/Globe/Converge)", "High-speed line installed"),
        curfewDefault = "Independent 24/7 access (no curfew)",
        curfewOptions = listOf("Independent 24/7 access (no curfew)", "Private gate key"),
        securityDefault = "Dedicated perimeter gate & fence",
        securityOptions = listOf("Dedicated perimeter gate & fence", "Private gated compound", "Subdivision outpost security"),
        visitorsDefault = "Visitors welcome",
        visitorsOptions = listOf("Visitors welcome", "Family visitors allowed", "Open visitation"),
        cookingDefault = "LPG gas stoves allowed",
        cookingOptions = listOf("LPG gas stoves allowed", "Gas stove or electric cooking allowed", "Separate dirty kitchen for LPG"),
        petPolicyDefault = "Pet-friendly (larger pets usually okay)",
        petPolicyOptions = listOf("Pet-friendly (larger pets usually okay)", "Small to medium dogs & cats allowed", "Pets allowed with deposit"),
        parkingDefault = "Dedicated gated car garage",
        parkingOptions = listOf("Dedicated gated car garage", "1-car covered garage", "Car & motorcycle garage"),
        localityNotes = "Prime choice for established families in residential subdivisions and suburban neighborhoods."
    ),

    CONDOMINIUM(
        displayName = "Condominium (Mid-rise/High-rise)",
        shortLabel = "Condominium",
        targetMarket = "High-earning professionals, expats",
        targetEmoji = "🏙️",
        expectedAmenities = listOf(
            "Fire sprinklers",
            "Intercom",
            "Lobby concierge",
            "Swimming pool",
            "Gym",
            "Elevators",
            "Balcony / View deck"
        ),
        waterDefault = "Billed via property management or main meters",
        waterOptions = listOf("Billed via property management or main meters", "Billed via property management", "Sub-metered by building admin", "Monthly PMO billing"),
        electricityDefault = "Billed via property management or main meters",
        electricityOptions = listOf("Billed via property management or main meters", "Direct VECO/MERALCO electric billing", "Billed via PMO"),
        internetDefault = "High-speed building fiber provision",
        internetOptions = listOf("High-speed building fiber provision", "Fiber-to-the-home (FTTH) ready", "Tenant applies own provider"),
        curfewDefault = "24/7 access with RFID / Keycard",
        curfewOptions = listOf("24/7 access with RFID / Keycard", "24/7 lobby concierge access"),
        securityDefault = "24/7 roaming guards",
        securityOptions = listOf("24/7 roaming guards", "24/7 roaming guards & strict move-in permits", "Strict move-in permits", "Lobby concierge & 24/7 security"),
        visitorsDefault = "Lobby visitor registration required",
        visitorsOptions = listOf("Lobby visitor registration required", "Visitors registered at front desk", "Guest passes via PMO"),
        cookingDefault = "Strictly induction cooking (NO LPG)",
        cookingOptions = listOf("Strictly induction cooking (NO LPG)", "Electric cooking only (building fire code)", "Induction range hood only"),
        petPolicyDefault = "Strict pet limits (e.g., max 15 lbs)",
        petPolicyOptions = listOf("Strict pet limits (e.g., max 15 lbs)", "1 small pet permitted (PMO registered)", "Strictly no pets (building policy)"),
        parkingDefault = "Basement / podium parking (fee or included)",
        parkingOptions = listOf("Basement / podium parking (fee or included)", "1 slot included", "Separate parking rental available"),
        localityNotes = "Found in business districts like Cebu IT Park, BGC, Ortigas, and coastal resort complexes."
    ),

    HOUSE_TOWNHOUSE(
        displayName = "House / Townhouse",
        shortLabel = "House / Townhouse",
        targetMarket = "Growing families, long-term tenants",
        targetEmoji = "🏡",
        expectedAmenities = listOf(
            "Multi-level floor plan",
            "Private lawn or garden",
            "Covered carport",
            "Service / dirty kitchen",
            "Balcony",
            "Maid's quarters / storage"
        ),
        waterDefault = "Direct city water line + booster pump",
        waterOptions = listOf("Direct city water line + booster pump", "Individual MCWD/Baciwa meter", "Deep well supply"),
        electricityDefault = "Direct primary electric meter",
        electricityOptions = listOf("Direct primary electric meter", "Independent main electric account"),
        internetDefault = "Fiber broadband line installed",
        internetOptions = listOf("Fiber broadband line installed", "Independent fiber connection"),
        curfewDefault = "Full private autonomy (no curfew)",
        curfewOptions = listOf("Full private autonomy (no curfew)", "Private gate key"),
        securityDefault = "Subdivision HOA security gate & roaming guards",
        securityOptions = listOf("Subdivision HOA security gate & roaming guards", "Private perimeter wall & gates"),
        visitorsDefault = "Visitors welcome",
        visitorsOptions = listOf("Visitors welcome", "Free guest access"),
        cookingDefault = "LPG gas stoves allowed",
        cookingOptions = listOf("LPG gas stoves allowed", "Full kitchen & dirty kitchen for gas cooking"),
        petPolicyDefault = "Pet-friendly (dogs & cats welcome)",
        petPolicyOptions = listOf("Pet-friendly (dogs & cats welcome)", "Large dogs permitted in yard"),
        parkingDefault = "Private covered carport (1-2 cars)",
        parkingOptions = listOf("Private covered carport (1-2 cars)", "Gated multi-vehicle driveway"),
        localityNotes = "Standard in exclusive provincial subdivisions and city outskirts."
    );

    val summaryUtilities: String
        get() = "Water: $waterDefault • Electricity: $electricityDefault • WiFi: $internetDefault"

    val summaryRules: String
        get() = "Curfew: $curfewDefault • $securityDefault • $visitorsDefault • $cookingDefault • $petPolicyDefault"

    fun getFormattedMarketListing(
        location: String,
        rentAmount: Double,
        landlordDetails: String
    ): String {
        val rentInt = rentAmount.toInt()
        val amenitiesList = expectedAmenities.take(4).joinToString("\n- ")
        return """
📍 $displayName in $location
💰 Rent: ₱$rentInt / month
👤 Ideal for: $targetMarket

🏠 UNIT SETUP & AMENITIES
- $amenitiesList

⚡ UTILITIES
- Water: $waterDefault
- Electricity: $electricityDefault
- Internet: $internetDefault

📋 HOUSE RULES
- Curfew: $curfewDefault
- Security: $securityDefault
- Visitors: $visitorsDefault
- Cooking: $cookingDefault
- Pets: $petPolicyDefault
- Parking: $parkingDefault

📞 CONTACT
- $landlordDetails
        """.trimIndent()
    }

    companion object {
        val PRIMARY_4_CATEGORIES = listOf(BED_SPACE, STUDIO_UNIT, APARTMENT_FLAT, CONDOMINIUM)
        val ALL_CATEGORIES = values().toList()

        /**
         * Resolves any raw property type string into its canonical PhilippineRentalPropertyCategory
         */
        fun fromPropertyType(type: String): PhilippineRentalPropertyCategory {
            val normalized = type.trim().lowercase()
            return when {
                normalized.contains("board") || normalized.contains("bed") || normalized.contains("dorm") || normalized.contains("space") || normalized == "room" -> BED_SPACE
                normalized.contains("studio") -> STUDIO_UNIT
                normalized.contains("condo") -> CONDOMINIUM
                normalized.contains("apart") || normalized.contains("flat") -> APARTMENT_FLAT
                normalized.contains("house") || normalized.contains("town") -> HOUSE_TOWNHOUSE
                else -> APARTMENT_FLAT
            }
        }

        /**
         * Returns electric cooperative abbreviation for a given province
         */
        fun getLocalElectricProvider(provinceId: String): String {
            val prov = provinceId.lowercase()
            return when {
                prov.contains("cebu") -> "VECO"
                prov.contains("bacolod") || prov.contains("negros") -> "CENECO"
                prov.contains("bohol") -> "BOHECO"
                prov.contains("iloilo") -> "MORE Power"
                prov.contains("davao") -> "Davao Light"
                prov.contains("manila") || prov.contains("rizal") || prov.contains("cavite") || prov.contains("laguna") -> "MERALCO"
                else -> "Local Distribution Utility (DU)"
            }
        }

        /**
         * Evaluates whether a property logically conforms to the selected category,
         * filtering out illogical amenities or conflicts (e.g. Bed Space claiming private pool or LPG).
         */
        fun filterIllogicalAmenities(
            category: PhilippineRentalPropertyCategory,
            amenities: List<String>
        ): List<String> {
            return when (category) {
                BED_SPACE -> amenities.filterNot {
                    it.contains("Private Pool", ignoreCase = true) ||
                    it.contains("LPG", ignoreCase = true) ||
                    it.contains("Car Garage", ignoreCase = true) ||
                    it.contains("Concierge", ignoreCase = true)
                }
                CONDOMINIUM -> amenities.filterNot {
                    it.contains("LPG", ignoreCase = true) ||
                    it.contains("Dirty Kitchen", ignoreCase = true) ||
                    it.contains("Sub-meter divided", ignoreCase = true)
                }
                STUDIO_UNIT -> amenities.filterNot {
                    it.contains("3 bedrooms", ignoreCase = true) ||
                    it.contains("Car Garage", ignoreCase = true)
                }
                else -> amenities
            }
        }
    }
}
