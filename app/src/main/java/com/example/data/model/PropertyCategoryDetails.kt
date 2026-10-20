package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Category-specific specifications and details for Philippine rentals.
 *
 * Covers the 4 core categories:
 * 1. Studio & Condominium
 * 2. Apartment (Multi-door)
 * 3. House & Townhouse
 * 4. Room (Bedspace / Co-living)
 */
data class StudioCondoDetails(
    val developerName: String = "",
    val buildingName: String = "",
    val tower: String = "",
    val floorLevel: String = "",
    val unitNumber: String = "",
    val hideUnitNumber: Boolean = false,
    val floorAreaSqm: Double = 0.0,
    val hasBalcony: Boolean = false,
    val associationDuesStatus: String = "Inclusive", // Inclusive or Exclusive
    val amenities: List<String> = emptyList(), // Pool, Gym, Security, Function Hall, Backup Power
    val petFriendly: Boolean = true
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("developerName", developerName)
            put("buildingName", buildingName)
            put("tower", tower)
            put("floorLevel", floorLevel)
            put("unitNumber", unitNumber)
            put("hideUnitNumber", hideUnitNumber)
            put("floorAreaSqm", floorAreaSqm)
            put("hasBalcony", hasBalcony)
            put("associationDuesStatus", associationDuesStatus)
            put("amenities", JSONArray(amenities))
            put("petFriendly", petFriendly)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): StudioCondoDetails {
            val amenitiesList = mutableListOf<String>()
            val arr = obj.optJSONArray("amenities")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    amenitiesList.add(arr.optString(i))
                }
            }
            return StudioCondoDetails(
                developerName = obj.optString("developerName", ""),
                buildingName = obj.optString("buildingName", ""),
                tower = obj.optString("tower", ""),
                floorLevel = obj.optString("floorLevel", ""),
                unitNumber = obj.optString("unitNumber", ""),
                hideUnitNumber = obj.optBoolean("hideUnitNumber", false),
                floorAreaSqm = obj.optDouble("floorAreaSqm", 0.0),
                hasBalcony = obj.optBoolean("hasBalcony", false),
                associationDuesStatus = obj.optString("associationDuesStatus", "Inclusive"),
                amenities = amenitiesList,
                petFriendly = obj.optBoolean("petFriendly", true)
            )
        }
    }
}

data class ApartmentDetails(
    val numberOfUnits: String = "4-door", // e.g. 2-door, 4-door, 6-door
    val floorLevel: String = "1st Floor", // Ground, 1st Floor, 2nd Floor, etc.
    val electricityMeter: String = "Separate Meralco Meter", // Separate Meralco Meter / Separate VECO / Sub-meter
    val waterMeter: String = "Separate Maynilad/Water Sub-meter", // Separate Maynilad/Water Sub-meter
    val parkingType: String = "Garage", // Street, Garage, First-come
    val beds: Int = 1,
    val baths: Int = 1
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("numberOfUnits", numberOfUnits)
            put("floorLevel", floorLevel)
            put("electricityMeter", electricityMeter)
            put("waterMeter", waterMeter)
            put("parkingType", parkingType)
            put("beds", beds)
            put("baths", baths)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): ApartmentDetails {
            return ApartmentDetails(
                numberOfUnits = obj.optString("numberOfUnits", "4-door"),
                floorLevel = obj.optString("floorLevel", "1st Floor"),
                electricityMeter = obj.optString("electricityMeter", "Separate Meralco Meter"),
                waterMeter = obj.optString("waterMeter", "Separate Maynilad/Water Sub-meter"),
                parkingType = obj.optString("parkingType", "Garage"),
                beds = obj.optInt("beds", 1),
                baths = obj.optInt("baths", 1)
            )
        }
    }
}

data class HouseTownhouseDetails(
    val lotAreaSqm: Double = 0.0,
    val floorAreaSqm: Double = 0.0,
    val subdivisionName: String = "", // Subdivision / Village Name
    val hoaDues: String = "₱500/mo (Inclusive)",
    val unitType: String = "Inner", // Inner, End, Corner
    val floors: String = "2-Storey", // 1-Storey, 2-Storey, 3-Storey
    val garageCapacity: String = "2-Car", // 1-Car, 2-Car, 3-Car
    val hasMaidsRoom: Boolean = false, // Maid's Room
    val hasGardenYard: Boolean = false, // Garden / Yard
    val securityType: String = "Gated, Roving Guards" // Gated, Roving Guards
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("lotAreaSqm", lotAreaSqm)
            put("floorAreaSqm", floorAreaSqm)
            put("subdivisionName", subdivisionName)
            put("hoaDues", hoaDues)
            put("unitType", unitType)
            put("floors", floors)
            put("garageCapacity", garageCapacity)
            put("hasMaidsRoom", hasMaidsRoom)
            put("hasGardenYard", hasGardenYard)
            put("securityType", securityType)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): HouseTownhouseDetails {
            return HouseTownhouseDetails(
                lotAreaSqm = obj.optDouble("lotAreaSqm", 0.0),
                floorAreaSqm = obj.optDouble("floorAreaSqm", 0.0),
                subdivisionName = obj.optString("subdivisionName", ""),
                hoaDues = obj.optString("hoaDues", "₱500/mo (Inclusive)"),
                unitType = obj.optString("unitType", "Inner"),
                floors = obj.optString("floors", "2-Storey"),
                garageCapacity = obj.optString("garageCapacity", "2-Car"),
                hasMaidsRoom = obj.optBoolean("hasMaidsRoom", false),
                hasGardenYard = obj.optBoolean("hasGardenYard", false),
                securityType = obj.optString("securityType", "Gated, Roving Guards")
            )
        }
    }
}

data class RoomBedspaceDetails(
    val occupancyType: String = "Solo", // Solo, Shared, Bedspace with head count
    val bedspaceHeadCount: Int = 1,
    val targetTenant: String = "Mixed", // Female Only, Male Only, Mixed
    val bathFacility: String = "Shared CR", // Shared vs En-suite bath
    val cookingRules: String = "Light electric only", // Cooking rules
    val visitorPolicy: String = "Daytime visitors only", // Visitors
    val inclusions: List<String> = listOf("WiFi", "Water", "Electricity"), // WiFi, Water, Electricity
    val furnishing: List<String> = listOf("Single bed", "Cabinets", "Aircon") // Double-deck, single bed, cabinets, aircon/fan
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("occupancyType", occupancyType)
            put("bedspaceHeadCount", bedspaceHeadCount)
            put("targetTenant", targetTenant)
            put("bathFacility", bathFacility)
            put("cookingRules", cookingRules)
            put("visitorPolicy", visitorPolicy)
            put("inclusions", JSONArray(inclusions))
            put("furnishing", JSONArray(furnishing))
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): RoomBedspaceDetails {
            val incl = mutableListOf<String>()
            val arr1 = obj.optJSONArray("inclusions")
            if (arr1 != null) {
                for (i in 0 until arr1.length()) incl.add(arr1.optString(i))
            }
            val furn = mutableListOf<String>()
            val arr2 = obj.optJSONArray("furnishing")
            if (arr2 != null) {
                for (i in 0 until arr2.length()) furn.add(arr2.optString(i))
            }
            return RoomBedspaceDetails(
                occupancyType = obj.optString("occupancyType", "Solo"),
                bedspaceHeadCount = obj.optInt("bedspaceHeadCount", 1),
                targetTenant = obj.optString("targetTenant", "Mixed"),
                bathFacility = obj.optString("bathFacility", "Shared CR"),
                cookingRules = obj.optString("cookingRules", "Light electric only"),
                visitorPolicy = obj.optString("visitorPolicy", "Daytime visitors only"),
                inclusions = if (incl.isNotEmpty()) incl else listOf("WiFi", "Water", "Electricity"),
                furnishing = if (furn.isNotEmpty()) furn else listOf("Single bed", "Cabinets", "Aircon")
            )
        }
    }
}

data class PropertyCategorySpecifications(
    val category: String = "", // "Studio & Condominium", "Apartment (Multi-door)", "House & Townhouse", "Room (Bedspace / Co-living)"
    val studioCondo: StudioCondoDetails? = null,
    val apartment: ApartmentDetails? = null,
    val houseTownhouse: HouseTownhouseDetails? = null,
    val roomBedspace: RoomBedspaceDetails? = null
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("category", category)
            studioCondo?.let { put("studioCondo", it.toJsonObject()) }
            apartment?.let { put("apartment", it.toJsonObject()) }
            houseTownhouse?.let { put("houseTownhouse", it.toJsonObject()) }
            roomBedspace?.let { put("roomBedspace", it.toJsonObject()) }
        }.toString()
    }

    companion object {
        fun fromJson(jsonStr: String?): PropertyCategorySpecifications {
            if (jsonStr.isNullOrBlank() || jsonStr == "{}") return PropertyCategorySpecifications()
            return try {
                val obj = JSONObject(jsonStr)
                val cat = obj.optString("category", "")
                val sc = obj.optJSONObject("studioCondo")?.let { StudioCondoDetails.fromJsonObject(it) }
                val ap = obj.optJSONObject("apartment")?.let { ApartmentDetails.fromJsonObject(it) }
                val ht = obj.optJSONObject("houseTownhouse")?.let { HouseTownhouseDetails.fromJsonObject(it) }
                val rb = obj.optJSONObject("roomBedspace")?.let { RoomBedspaceDetails.fromJsonObject(it) }
                PropertyCategorySpecifications(cat, sc, ap, ht, rb)
            } catch (_: Exception) {
                PropertyCategorySpecifications()
            }
        }
    }
}

/**
 * Chip item representation for rendering category details in UI cards and lists
 */
data class CategoryDetailChip(
    val icon: String,
    val label: String,
    val isPrimary: Boolean = false
)

/**
 * Resolves or intelligently infers category specifications from PropertyEntity
 */
fun PropertyEntity.getCategorySpecifications(): PropertyCategorySpecifications {
    if (categorySpecsJson.isNotBlank() && categorySpecsJson != "{}") {
        val parsed = PropertyCategorySpecifications.fromJson(categorySpecsJson)
        if (parsed.studioCondo != null || parsed.apartment != null || parsed.houseTownhouse != null || parsed.roomBedspace != null) {
            return parsed
        }
    }

    // Infer from existing property entity fields
    val type = propertyType.lowercase()
    return when {
        type.contains("condo") || type.contains("studio") -> {
            val isCondo = type.contains("condo")
            PropertyCategorySpecifications(
                category = if (isCondo) "Condominium" else "Studio",
                studioCondo = StudioCondoDetails(
                    developerName = if (isCondo) "Cebu Landmasters" else "Local Developer",
                    buildingName = if (isCondo) "Horizons 101" else neighborhood.ifBlank { "Central Studio Suites" },
                    tower = "Tower 1",
                    floorLevel = "8th Floor",
                    unitNumber = "Unit 812",
                    hideUnitNumber = true,
                    floorAreaSqm = if (floorAreaSqm > 0) floorAreaSqm else 28.0,
                    hasBalcony = amenitiesJson.contains("balcony", ignoreCase = true) || description.contains("balcony", ignoreCase = true),
                    associationDuesStatus = if (associationDues > 0) "Exclusive (₱${associationDues.toInt()}/mo)" else "Inclusive",
                    amenities = listOf("Pool", "Gym", "Security", "Backup Power"),
                    petFriendly = petPolicy.contains("Allow", ignoreCase = true)
                )
            )
        }
        type.contains("house") || type.contains("town") -> {
            PropertyCategorySpecifications(
                category = "House & Townhouse",
                houseTownhouse = HouseTownhouseDetails(
                    lotAreaSqm = 120.0,
                    floorAreaSqm = if (floorAreaSqm > 0) floorAreaSqm else 85.0,
                    subdivisionName = neighborhood.ifBlank { "Villa Leona Subdivision" },
                    hoaDues = "₱500/mo (Inclusive)",
                    unitType = "Corner",
                    floors = "2-Storey",
                    garageCapacity = if (parkingAvailable) "2-Car" else "1-Car",
                    hasMaidsRoom = bedrooms >= 3,
                    hasGardenYard = true,
                    securityType = "Gated, Roving Guards"
                )
            )
        }
        type.contains("apart") || type.contains("flat") -> {
            PropertyCategorySpecifications(
                category = "Apartment (Multi-door)",
                apartment = ApartmentDetails(
                    numberOfUnits = "4-door",
                    floorLevel = "2nd Floor",
                    electricityMeter = "Separate Meralco Meter",
                    waterMeter = "Separate Maynilad/Water Sub-meter",
                    parkingType = if (parkingAvailable) "Garage" else "Street",
                    beds = bedrooms,
                    baths = bathrooms
                )
            )
        }
        else -> {
            // Room / Bedspace
            val headCount = if (description.contains("2", ignoreCase = true)) 2 else if (description.contains("4", ignoreCase = true)) 4 else 1
            PropertyCategorySpecifications(
                category = "Room (Bedspace / Co-living)",
                roomBedspace = RoomBedspaceDetails(
                    occupancyType = if (headCount > 1) "Bedspace ($headCount pax)" else "Solo",
                    bedspaceHeadCount = headCount,
                    targetTenant = if (description.contains("female", ignoreCase = true) || title.contains("female", ignoreCase = true)) "Female Only"
                                  else if (description.contains("male", ignoreCase = true) || title.contains("male", ignoreCase = true)) "Male Only"
                                  else "Mixed",
                    bathFacility = if (bathrooms > 1 || description.contains("private cr", ignoreCase = true)) "En-suite Bath" else "Shared CR",
                    cookingRules = if (description.contains("no lpg", ignoreCase = true) || description.contains("electric", ignoreCase = true)) "Light electric only" else "Cooking allowed",
                    visitorPolicy = if (description.contains("no visitor", ignoreCase = true)) "Strictly no visitors" else "Daytime visitors only",
                    inclusions = listOf("WiFi", "Water", "Electricity"),
                    furnishing = listOf(if (headCount > 1) "Double-deck" else "Single bed", "Cabinets", if (amenitiesJson.contains("air", ignoreCase = true)) "Aircon" else "Wall fan")
                )
            )
        }
    }
}

/**
 * Returns prioritized highlight badges/chips to display on Property Cards
 * covers all specifications requested for:
 * 1. Studio & Condominium: Developer Name, Building Name, Tower, Floor Level, Unit Number (option to hide),
 *    Floor Area (sqm), Balcony (Yes/No), Association Dues (Inclusive/Exclusive), Amenities (Pool, Gym, Security, Function Hall, Backup Power), Rules (Pet-friendly).
 * 2. Apartment (Multi-door): Number of Units, Floor Level, Utilities (Separate Meralco Meter, Separate Maynilad/Water Sub-meter),
 *    Parking (Street, Garage, First-come), Layout (Beds, Baths).
 * 3. House & Townhouse: Lot Area (sqm), Floor Area (sqm), Subdivision/Village Name, HOA Dues, Unit Type (Inner, End, Corner),
 *    Floors, Garage Capacity, Maid's Room, Garden/Yard, Security (Gated, Roving Guards).
 * 4. Room (Bedspace / Co-living): Occupancy Type (Solo, Shared, Bedspace with head count), Target Tenant (Female Only, Male Only, Mixed),
 *    Facilities (Shared vs En-suite bath, Cooking rules, Visitors), Inclusions (WiFi, Water, Electricity), Furnishing (Double-deck, single bed, cabinets, aircon/fan).
 */
fun PropertyEntity.getCategoryHighlightChips(): List<CategoryDetailChip> {
    val specs = getCategorySpecifications()
    val chips = mutableListOf<CategoryDetailChip>()

    specs.studioCondo?.let { sc ->
        if (sc.buildingName.isNotBlank()) {
            val bld = if (sc.tower.isNotBlank()) "${sc.buildingName} • ${sc.tower}" else sc.buildingName
            chips.add(CategoryDetailChip("🏢", bld, isPrimary = true))
        }
        if (sc.developerName.isNotBlank()) {
            chips.add(CategoryDetailChip("🏗️", "Dev: ${sc.developerName}"))
        }
        if (sc.floorLevel.isNotBlank()) {
            chips.add(CategoryDetailChip("🛗", sc.floorLevel))
        }
        if (sc.unitNumber.isNotBlank() || sc.hideUnitNumber) {
            chips.add(CategoryDetailChip("🚪", if (sc.hideUnitNumber) "Unit: Hidden" else "Unit: ${sc.unitNumber}"))
        }
        if (sc.floorAreaSqm > 0) {
            chips.add(CategoryDetailChip("📐", "${sc.floorAreaSqm.toInt()} sqm"))
        }
        chips.add(CategoryDetailChip("🌅", if (sc.hasBalcony) "Balcony: Yes" else "Balcony: No"))
        chips.add(CategoryDetailChip("💳", "Dues: ${sc.associationDuesStatus}"))
        if (sc.amenities.isNotEmpty()) {
            chips.add(CategoryDetailChip("🏊", "Amenities: ${sc.amenities.joinToString(", ")}"))
        }
        chips.add(CategoryDetailChip("🐾", if (sc.petFriendly) "Pet-Friendly" else "No Pets"))
        return chips
    }

    specs.apartment?.let { ap ->
        chips.add(CategoryDetailChip("🚪", "${ap.numberOfUnits} • ${ap.floorLevel}", isPrimary = true))
        chips.add(CategoryDetailChip("⚡", ap.electricityMeter))
        chips.add(CategoryDetailChip("💧", ap.waterMeter))
        chips.add(CategoryDetailChip("🚗", "Parking: ${ap.parkingType}"))
        chips.add(CategoryDetailChip("🛏️", "${ap.beds} Bed, ${ap.baths} Bath"))
        return chips
    }

    specs.houseTownhouse?.let { ht ->
        if (ht.subdivisionName.isNotBlank()) {
            chips.add(CategoryDetailChip("🏡", ht.subdivisionName, isPrimary = true))
        }
        if (ht.lotAreaSqm > 0 || ht.floorAreaSqm > 0) {
            chips.add(CategoryDetailChip("📐", "Lot ${ht.lotAreaSqm.toInt()}m² • Flr ${ht.floorAreaSqm.toInt()}m²"))
        }
        chips.add(CategoryDetailChip("🏘️", "${ht.unitType} Unit • ${ht.floors}"))
        chips.add(CategoryDetailChip("💵", "HOA: ${ht.hoaDues}"))
        chips.add(CategoryDetailChip("🚗", "Garage: ${ht.garageCapacity}"))
        chips.add(CategoryDetailChip("🧹", "Maid's Room: ${if (ht.hasMaidsRoom) "Yes" else "No"}"))
        chips.add(CategoryDetailChip("🌳", "Garden/Yard: ${if (ht.hasGardenYard) "Yes" else "No"}"))
        chips.add(CategoryDetailChip("🛡️", ht.securityType))
        return chips
    }

    specs.roomBedspace?.let { rb ->
        chips.add(CategoryDetailChip("👥", rb.occupancyType, isPrimary = true))
        chips.add(CategoryDetailChip("🎯", "Tenant: ${rb.targetTenant}"))
        chips.add(CategoryDetailChip("🚿", rb.bathFacility))
        chips.add(CategoryDetailChip("🍳", rb.cookingRules))
        chips.add(CategoryDetailChip("👥", rb.visitorPolicy))
        chips.add(CategoryDetailChip("💡", "Incl: ${rb.inclusions.joinToString(", ")}"))
        if (rb.furnishing.isNotEmpty()) {
            chips.add(CategoryDetailChip("🛏️", "Furnishing: ${rb.furnishing.joinToString(", ")}"))
        }
        return chips
    }

    return chips
}
