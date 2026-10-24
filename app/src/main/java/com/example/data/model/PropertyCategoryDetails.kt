package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import org.json.JSONArray
import org.json.JSONObject

/**
 * Helper to normalize furnishing values strictly to Unfurnished, Semi-Furnished, or Fully-Furnished
 */
fun normalizeFurnishing(value: String?): String {
    val lower = value?.lowercase()?.trim() ?: ""
    return when {
        lower.contains("semi") -> "Semi-Furnished"
        lower.contains("unfurnish") || lower.contains("bare") -> "Unfurnished"
        else -> "Fully-Furnished"
    }
}

/**
 * 1. Condition: Boarding House Specifications
 */
data class BoardingHouseDetails(
    val pricingModel: String = "Per Person/Bed", // "Per Person/Bed" vs "Per Room"
    val monthlyRent: Double = 0.0,
    val roomCapacity: String = "4-bed bunk", // "Single", "Double bed", "4-bed bunk", "6-bed bunk"
    val tenantPreference: String = "Mixed", // "Male Only", "Female Only", "Mixed"
    val bathroomType: String = "Shared/Communal", // "Private/En-suite" vs "Shared/Communal"
    val utilities: List<String> = listOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included"),
    val floorAreaSqm: Double = 18.0,
    val furnishing: String = "Fully-Furnished",
    val parking: String = "Motorcycle Parking Included",
    val curfewRule: String = "10:00 PM Gate Lock",
    val visitorsRule: String = "Lobby Visitors Only",
    val petPolicy: String = "Strictly No Pets",
    val cookingRule: String = "Light Cooking Allowed",
    val leaseTermMinimum: String = "6 months"
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("pricingModel", pricingModel)
            put("monthlyRent", monthlyRent)
            put("roomCapacity", roomCapacity)
            put("tenantPreference", tenantPreference)
            put("bathroomType", bathroomType)
            put("utilities", JSONArray(utilities))
            put("floorAreaSqm", floorAreaSqm)
            put("furnishing", normalizeFurnishing(furnishing))
            put("parking", parking)
            put("curfewRule", curfewRule)
            put("visitorsRule", visitorsRule)
            put("petPolicy", petPolicy)
            put("cookingRule", cookingRule)
            put("leaseTermMinimum", leaseTermMinimum)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): BoardingHouseDetails {
            val utils = mutableListOf<String>()
            val arr = obj.optJSONArray("utilities")
            if (arr != null) {
                for (i in 0 until arr.length()) utils.add(arr.optString(i))
            }
            return BoardingHouseDetails(
                pricingModel = obj.optString("pricingModel", "Per Person/Bed"),
                monthlyRent = obj.optDouble("monthlyRent", 0.0),
                roomCapacity = obj.optString("roomCapacity", "4-bed bunk"),
                tenantPreference = obj.optString("tenantPreference", "Mixed"),
                bathroomType = obj.optString("bathroomType", "Shared/Communal"),
                utilities = if (utils.isNotEmpty()) utils else listOf("Sub-metered Electricity", "Sub-metered Water", "Free Wi-Fi included"),
                floorAreaSqm = obj.optDouble("floorAreaSqm", 18.0),
                furnishing = normalizeFurnishing(obj.optString("furnishing", "Fully-Furnished")),
                parking = obj.optString("parking", "Motorcycle Parking Included"),
                curfewRule = obj.optString("curfewRule", "10:00 PM Gate Lock"),
                visitorsRule = obj.optString("visitorsRule", "Lobby Visitors Only"),
                petPolicy = obj.optString("petPolicy", "Strictly No Pets"),
                cookingRule = obj.optString("cookingRule", "Light Cooking Allowed"),
                leaseTermMinimum = obj.optString("leaseTermMinimum", "6 months")
            )
        }
    }
}

/**
 * 2. Condition: Apartment Specifications
 */
data class ApartmentDetails(
    val monthlyRent: Double = 0.0,
    val totalFloorAreaSqm: Double = 35.0,
    val bedrooms: Int = 1,
    val bathrooms: Int = 1,
    val leaseTermMinimum: String = "1 year", // "6 months", "1 year"
    val furnishing: String = "Semi-Furnished", // "Unfurnished", "Semi-Furnished", "Fully-Furnished"
    val kitchenType: String = "Indoor/Integral", // "Indoor/Integral" vs "Outdoor/Dirty Kitchen"
    val utilityBilling: String = "Own Meter", // "Own Meter" vs "Sub-metered by Landlord"
    val parking: String = "Covered Garage Slot",
    val petPolicy: String = "Pets Allowed",
    val curfewRule: String = "No Curfew (24/7 Access)",
    val amenities: List<String> = listOf("Free High-Speed Wi-Fi", "Air Conditioning", "Balcony")
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("monthlyRent", monthlyRent)
            put("totalFloorAreaSqm", totalFloorAreaSqm)
            put("bedrooms", bedrooms)
            put("bathrooms", bathrooms)
            put("leaseTermMinimum", leaseTermMinimum)
            put("furnishing", normalizeFurnishing(furnishing))
            put("kitchenType", kitchenType)
            put("utilityBilling", utilityBilling)
            put("parking", parking)
            put("petPolicy", petPolicy)
            put("curfewRule", curfewRule)
            put("amenities", JSONArray(amenities))
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): ApartmentDetails {
            val ams = mutableListOf<String>()
            val arr = obj.optJSONArray("amenities")
            if (arr != null) {
                for (i in 0 until arr.length()) ams.add(arr.optString(i))
            }
            return ApartmentDetails(
                monthlyRent = obj.optDouble("monthlyRent", 0.0),
                totalFloorAreaSqm = obj.optDouble("totalFloorAreaSqm", 35.0),
                bedrooms = obj.optInt("bedrooms", 1),
                bathrooms = obj.optInt("bathrooms", 1),
                leaseTermMinimum = obj.optString("leaseTermMinimum", "1 year"),
                furnishing = normalizeFurnishing(obj.optString("furnishing", "Semi-Furnished")),
                kitchenType = obj.optString("kitchenType", "Indoor/Integral"),
                utilityBilling = obj.optString("utilityBilling", "Own Meter"),
                parking = obj.optString("parking", "Covered Garage Slot"),
                petPolicy = obj.optString("petPolicy", "Pets Allowed"),
                curfewRule = obj.optString("curfewRule", "No Curfew (24/7 Access)"),
                amenities = if (ams.isNotEmpty()) ams else listOf("Free High-Speed Wi-Fi", "Air Conditioning", "Balcony")
            )
        }
    }
}

/**
 * 3. Condition: Condominium & Studio Specifications
 */
data class CondominiumDetails(
    val monthlyRent: Double = 0.0,
    val associationDues: String = "Inclusive in rent", // "Inclusive in rent" vs "Exclusive/Tenant pays separately"
    val unitLayout: String = "Studio", // "Studio", "1BR", "2BR", "Loft", "Bi-level"
    val totalFloorAreaSqm: Double = 28.0,
    val floorLevel: String = "8th Floor", // e.g. "14th floor"
    val parking: String = "Available for extra fee", // "Included in rent", "Available for extra fee", "None"
    val amenities: List<String> = listOf("Pool", "Gym", "24/7 Security", "Balcony"),
    val petPolicy: String = "Pet-Friendly", // "Strictly No Pets" vs "Pet-Friendly"
    val furnishing: String = "Fully-Furnished",
    val leaseTermMinimum: String = "1 year",
    val visitorsRule: String = "24/7 Concierge & Guest Registration",
    val kitchenType: String = "Equipped Kitchenette"
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("monthlyRent", monthlyRent)
            put("associationDues", associationDues)
            put("unitLayout", unitLayout)
            put("totalFloorAreaSqm", totalFloorAreaSqm)
            put("floorLevel", floorLevel)
            put("parking", parking)
            put("amenities", JSONArray(amenities))
            put("petPolicy", petPolicy)
            put("furnishing", normalizeFurnishing(furnishing))
            put("leaseTermMinimum", leaseTermMinimum)
            put("visitorsRule", visitorsRule)
            put("kitchenType", kitchenType)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): CondominiumDetails {
            val ams = mutableListOf<String>()
            val arr = obj.optJSONArray("amenities")
            if (arr != null) {
                for (i in 0 until arr.length()) ams.add(arr.optString(i))
            }
            return CondominiumDetails(
                monthlyRent = obj.optDouble("monthlyRent", 0.0),
                associationDues = obj.optString("associationDues", "Inclusive in rent"),
                unitLayout = obj.optString("unitLayout", "Studio"),
                totalFloorAreaSqm = obj.optDouble("totalFloorAreaSqm", 28.0),
                floorLevel = obj.optString("floorLevel", "8th Floor"),
                parking = obj.optString("parking", "Available for extra fee"),
                amenities = if (ams.isNotEmpty()) ams else listOf("Pool", "Gym", "24/7 Security"),
                petPolicy = obj.optString("petPolicy", "Pet-Friendly"),
                furnishing = normalizeFurnishing(obj.optString("furnishing", "Fully-Furnished")),
                leaseTermMinimum = obj.optString("leaseTermMinimum", "1 year"),
                visitorsRule = obj.optString("visitorsRule", "24/7 Concierge & Guest Registration"),
                kitchenType = obj.optString("kitchenType", "Equipped Kitchenette")
            )
        }
    }
}

/**
 * 4. Condition: Townhouse Specifications
 */
data class TownhouseDetails(
    val monthlyRent: Double = 0.0,
    val lotAreaSqm: Double = 120.0,
    val floorAreaSqm: Double = 85.0,
    val stories: Int = 2,
    val bedrooms: Int = 3,
    val bathrooms: Int = 2,
    val garageCapacity: Int = 2,
    val hasMaidsRoom: Boolean = false, // Maid's Room / Service Quarters Included?
    val isGatedSubdivision: Boolean = true, // Located inside a gated subdivision?
    val furnishing: String = "Semi-Furnished",
    val petPolicy: String = "Pet-Friendly",
    val associationDues: String = "Inclusive in rent",
    val leaseTermMinimum: String = "1 year",
    val kitchenType: String = "Indoor Kitchen + Dirty Kitchen"
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("monthlyRent", monthlyRent)
            put("lotAreaSqm", lotAreaSqm)
            put("floorAreaSqm", floorAreaSqm)
            put("stories", stories)
            put("bedrooms", bedrooms)
            put("bathrooms", bathrooms)
            put("garageCapacity", garageCapacity)
            put("hasMaidsRoom", hasMaidsRoom)
            put("isGatedSubdivision", isGatedSubdivision)
            put("furnishing", normalizeFurnishing(furnishing))
            put("petPolicy", petPolicy)
            put("associationDues", associationDues)
            put("leaseTermMinimum", leaseTermMinimum)
            put("kitchenType", kitchenType)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): TownhouseDetails {
            return TownhouseDetails(
                monthlyRent = obj.optDouble("monthlyRent", 0.0),
                lotAreaSqm = obj.optDouble("lotAreaSqm", 120.0),
                floorAreaSqm = obj.optDouble("floorAreaSqm", 85.0),
                stories = obj.optInt("stories", 2),
                bedrooms = obj.optInt("bedrooms", 3),
                bathrooms = obj.optInt("bathrooms", 2),
                garageCapacity = obj.optInt("garageCapacity", 2),
                hasMaidsRoom = obj.optBoolean("hasMaidsRoom", false),
                isGatedSubdivision = obj.optBoolean("isGatedSubdivision", true),
                furnishing = normalizeFurnishing(obj.optString("furnishing", "Semi-Furnished")),
                petPolicy = obj.optString("petPolicy", "Pet-Friendly"),
                associationDues = obj.optString("associationDues", "Inclusive in rent"),
                leaseTermMinimum = obj.optString("leaseTermMinimum", "1 year"),
                kitchenType = obj.optString("kitchenType", "Indoor Kitchen + Dirty Kitchen")
            )
        }
    }
}

/**
 * Role-Based Verification Data captured in Step 1
 */
data class RoleVerificationDetails(
    val role: String = "Property owner", // "Property owner", "Authorized representative / Caretaker", "Sublessor / Master tenant"
    val isTitleOwnerConfirmed: Boolean = true,
    val ownerIdType: String = "Philippine National ID (PhilSys)",
    val ownerIdNumber: String = "",
    val contactPreference: String = "Direct Phone & In-App Chat",
    val landlordFullName: String = "",
    val representativeRelationship: String = "Caretaker",
    val isAuthorizedByOwner: Boolean = true,
    val authorizationDocType: String = "Written Consent & Authorization Note",
    val representativePhone: String = "",
    val masterLandlordName: String = "",
    val hasSubleasePermission: Boolean = true,
    val masterLeaseExpiry: String = "Within 1 year"
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("role", role)
            put("isTitleOwnerConfirmed", isTitleOwnerConfirmed)
            put("ownerIdType", ownerIdType)
            put("ownerIdNumber", ownerIdNumber)
            put("contactPreference", contactPreference)
            put("landlordFullName", landlordFullName)
            put("representativeRelationship", representativeRelationship)
            put("isAuthorizedByOwner", isAuthorizedByOwner)
            put("authorizationDocType", authorizationDocType)
            put("representativePhone", representativePhone)
            put("masterLandlordName", masterLandlordName)
            put("hasSubleasePermission", hasSubleasePermission)
            put("masterLeaseExpiry", masterLeaseExpiry)
        }
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): RoleVerificationDetails {
            return RoleVerificationDetails(
                role = obj.optString("role", "Property owner"),
                isTitleOwnerConfirmed = obj.optBoolean("isTitleOwnerConfirmed", true),
                ownerIdType = obj.optString("ownerIdType", "Philippine National ID (PhilSys)"),
                ownerIdNumber = obj.optString("ownerIdNumber", ""),
                contactPreference = obj.optString("contactPreference", "Direct Phone & In-App Chat"),
                landlordFullName = obj.optString("landlordFullName", ""),
                representativeRelationship = obj.optString("representativeRelationship", "Caretaker"),
                isAuthorizedByOwner = obj.optBoolean("isAuthorizedByOwner", true),
                authorizationDocType = obj.optString("authorizationDocType", "Written Consent & Authorization Note"),
                representativePhone = obj.optString("representativePhone", ""),
                masterLandlordName = obj.optString("masterLandlordName", ""),
                hasSubleasePermission = obj.optBoolean("hasSubleasePermission", true),
                masterLeaseExpiry = obj.optString("masterLeaseExpiry", "Within 1 year")
            )
        }
    }
}

/**
 * Root specification container serialized into property.categorySpecsJson
 */
data class PropertyCategorySpecifications(
    val category: String = "", // "Boarding House", "Apartment", "Condominium", "Studio", "Townhouse"
    val boardingHouse: BoardingHouseDetails? = null,
    val apartment: ApartmentDetails? = null,
    val condominium: CondominiumDetails? = null,
    val townhouse: TownhouseDetails? = null,
    val roleVerification: RoleVerificationDetails? = null
) {
    fun toJson(): String {
        return JSONObject().apply {
            put("category", category)
            boardingHouse?.let { put("boardingHouse", it.toJsonObject()) }
            apartment?.let { put("apartment", it.toJsonObject()) }
            condominium?.let { put("condominium", it.toJsonObject()) }
            townhouse?.let { put("townhouse", it.toJsonObject()) }
            roleVerification?.let { put("roleVerification", it.toJsonObject()) }
        }.toString()
    }

    companion object {
        fun fromJson(jsonStr: String?): PropertyCategorySpecifications {
            if (jsonStr.isNullOrBlank() || jsonStr == "{}") return PropertyCategorySpecifications()
            return try {
                val obj = JSONObject(jsonStr)
                val cat = obj.optString("category", "")
                val bh = obj.optJSONObject("boardingHouse")?.let { BoardingHouseDetails.fromJsonObject(it) }
                    ?: obj.optJSONObject("roomBedspace")?.let {
                        BoardingHouseDetails(
                            pricingModel = if (it.optString("occupancyType").contains("Bedspace", ignoreCase = true)) "Per Person/Bed" else "Per Room",
                            roomCapacity = "${it.optInt("bedspaceHeadCount", 1)}-bed bunk",
                            tenantPreference = it.optString("targetTenant", "Mixed"),
                            bathroomType = if (it.optString("bathFacility").contains("Private", ignoreCase = true) || it.optString("bathFacility").contains("En-suite", ignoreCase = true)) "Private/En-suite" else "Shared/Communal"
                        )
                    }
                val ap = obj.optJSONObject("apartment")?.let { ApartmentDetails.fromJsonObject(it) }
                val cd = obj.optJSONObject("condominium")?.let { CondominiumDetails.fromJsonObject(it) }
                    ?: obj.optJSONObject("studioCondo")?.let {
                        CondominiumDetails(
                            associationDues = it.optString("associationDuesStatus", "Inclusive in rent"),
                            unitLayout = "Studio",
                            totalFloorAreaSqm = it.optDouble("floorAreaSqm", 28.0),
                            floorLevel = it.optString("floorLevel", "8th Floor"),
                            parking = "Available for extra fee",
                            petPolicy = if (it.optBoolean("petFriendly", true)) "Pet-Friendly" else "Strictly No Pets"
                        )
                    }
                val th = obj.optJSONObject("townhouse")?.let { TownhouseDetails.fromJsonObject(it) }
                    ?: obj.optJSONObject("houseTownhouse")?.let {
                        TownhouseDetails(
                            lotAreaSqm = it.optDouble("lotAreaSqm", 120.0),
                            floorAreaSqm = it.optDouble("floorAreaSqm", 85.0),
                            stories = if (it.optString("floors").contains("3")) 3 else 2,
                            garageCapacity = if (it.optString("garageCapacity").contains("2")) 2 else 1,
                            hasMaidsRoom = it.optBoolean("hasMaidsRoom", false),
                            isGatedSubdivision = it.optString("securityType").contains("Gated", ignoreCase = true)
                        )
                    }
                val rv = obj.optJSONObject("roleVerification")?.let { RoleVerificationDetails.fromJsonObject(it) }
                PropertyCategorySpecifications(cat, bh, ap, cd, th, rv)
            } catch (_: Exception) {
                PropertyCategorySpecifications()
            }
        }
    }
}

/**
 * Item for seamless integration in PropertyDetailScreen's Living Specifications list
 */
data class CategorySpecificationItem(
    val label: String,
    val value: String,
    val iconEmoji: String
)

/**
 * Resolves or intelligently infers category specifications from PropertyEntity
 */
fun PropertyEntity.getCategorySpecifications(): PropertyCategorySpecifications {
    if (categorySpecsJson.isNotBlank() && categorySpecsJson != "{}") {
        val parsed = PropertyCategorySpecifications.fromJson(categorySpecsJson)
        if (parsed.boardingHouse != null || parsed.apartment != null || parsed.condominium != null || parsed.townhouse != null) {
            return parsed
        }
    }

    val type = propertyType.lowercase()
    return when {
        type.contains("board") || type.contains("bed") || type.contains("room") -> {
            val headCount = if (description.contains("4", ignoreCase = true)) 4 else if (description.contains("2", ignoreCase = true)) 2 else 1
            PropertyCategorySpecifications(
                category = "Boarding House",
                boardingHouse = BoardingHouseDetails(
                    pricingModel = if (headCount > 1) "Per Person/Bed" else "Per Room",
                    monthlyRent = monthlyRent,
                    roomCapacity = if (headCount == 4) "4-bed bunk" else if (headCount == 2) "Double bed" else "Single",
                    tenantPreference = if (description.contains("female", ignoreCase = true) || title.contains("female", ignoreCase = true)) "Female Only"
                                       else if (description.contains("male", ignoreCase = true) || title.contains("male", ignoreCase = true)) "Male Only"
                                       else "Mixed",
                    bathroomType = if (bathrooms > 1 || description.contains("private", ignoreCase = true)) "Private/En-suite" else "Shared/Communal",
                    utilities = listOf("Sub-metered Electricity", "Sub-metered Water", if (internetIncluded) "Free Wi-Fi included" else "")
                        .filter { it.isNotBlank() }
                )
            )
        }
        type.contains("condo") || type.contains("studio") -> {
            val isStudio = type.contains("studio")
            PropertyCategorySpecifications(
                category = if (isStudio) "Studio" else "Condominium",
                condominium = CondominiumDetails(
                    monthlyRent = monthlyRent,
                    associationDues = if (associationDues > 0) "Exclusive/Tenant pays separately (₱${associationDues.toInt()}/mo)" else "Inclusive in rent",
                    unitLayout = if (isStudio) "Studio" else if (bedrooms >= 2) "2BR" else "1BR",
                    totalFloorAreaSqm = if (floorAreaSqm > 0) floorAreaSqm else if (isStudio) 24.0 else 32.0,
                    floorLevel = "8th Floor",
                    parking = if (parkingAvailable) "Included in rent" else "Available for extra fee",
                    amenities = listOf("Pool", "Gym", "24/7 Security", if (amenitiesJson.contains("balcony", ignoreCase = true)) "Balcony" else "").filter { it.isNotBlank() },
                    petPolicy = if (petPolicy.contains("Allow", ignoreCase = true)) "Pet-Friendly" else "Strictly No Pets"
                )
            )
        }
        type.contains("town") || type.contains("house") -> {
            PropertyCategorySpecifications(
                category = "Townhouse",
                townhouse = TownhouseDetails(
                    monthlyRent = monthlyRent,
                    lotAreaSqm = 120.0,
                    floorAreaSqm = if (floorAreaSqm > 0) floorAreaSqm else 85.0,
                    stories = 2,
                    bedrooms = if (bedrooms > 0) bedrooms else 3,
                    bathrooms = if (bathrooms > 0) bathrooms else 2,
                    garageCapacity = if (parkingAvailable) 2 else 1,
                    hasMaidsRoom = bedrooms >= 3,
                    isGatedSubdivision = true
                )
            )
        }
        else -> {
            // Apartment
            PropertyCategorySpecifications(
                category = "Apartment",
                apartment = ApartmentDetails(
                    monthlyRent = monthlyRent,
                    totalFloorAreaSqm = if (floorAreaSqm > 0) floorAreaSqm else 36.0,
                    bedrooms = if (bedrooms > 0) bedrooms else 1,
                    bathrooms = if (bathrooms > 0) bathrooms else 1,
                    leaseTermMinimum = "1 year",
                    furnishing = normalizeFurnishing(furnishedStatus),
                    kitchenType = if (description.contains("dirty", ignoreCase = true) || description.contains("outdoor", ignoreCase = true)) "Outdoor/Dirty Kitchen" else "Indoor/Integral",
                    utilityBilling = if (description.contains("own meter", ignoreCase = true) || description.contains("meralco", ignoreCase = true)) "Own Meter" else "Sub-metered by Landlord"
                )
            )
        }
    }
}

/**
 * Returns category specification items for seamless integration into PropertyDetailScreen's Living Specifications list
 */
fun PropertyEntity.getCategorySpecificationItems(): List<CategorySpecificationItem> {
    val specs = getCategorySpecifications()
    val items = mutableListOf<CategorySpecificationItem>()

    specs.boardingHouse?.let { bh ->
        items.add(CategorySpecificationItem("Pricing Model", bh.pricingModel, "🏷️"))
        items.add(CategorySpecificationItem("Room Capacity", bh.roomCapacity, "🛏️"))
        items.add(CategorySpecificationItem("Tenant Preference", bh.tenantPreference, "👥"))
        items.add(CategorySpecificationItem("Bathroom Type", bh.bathroomType, "🚿"))
        if (bh.utilities.isNotEmpty()) {
            items.add(CategorySpecificationItem("Utilities Setup", bh.utilities.joinToString(", "), "⚡"))
        }
        return items
    }

    specs.apartment?.let { ap ->
        items.add(CategorySpecificationItem("Floor Area", "${ap.totalFloorAreaSqm.toInt()} sqm", "📐"))
        items.add(CategorySpecificationItem("Bedrooms & Baths", "${ap.bedrooms} Bed, ${ap.bathrooms} Bath", "🚪"))
        items.add(CategorySpecificationItem("Min. Lease Term", ap.leaseTermMinimum, "📅"))
        items.add(CategorySpecificationItem("Furnishing", ap.furnishing, "🛋️"))
        items.add(CategorySpecificationItem("Kitchen Type", ap.kitchenType, "🍳"))
        items.add(CategorySpecificationItem("Utility Billing", ap.utilityBilling, "⚡"))
        return items
    }

    specs.condominium?.let { cd ->
        items.add(CategorySpecificationItem("Unit Layout", cd.unitLayout, "🏢"))
        items.add(CategorySpecificationItem("Floor Level", cd.floorLevel, "🛗"))
        items.add(CategorySpecificationItem("Floor Area", "${cd.totalFloorAreaSqm.toInt()} sqm", "📐"))
        items.add(CategorySpecificationItem("Association Dues", cd.associationDues, "💳"))
        items.add(CategorySpecificationItem("Parking Setup", cd.parking, "🚗"))
        items.add(CategorySpecificationItem("Pet Policy", cd.petPolicy, "🐾"))
        if (cd.amenities.isNotEmpty()) {
            items.add(CategorySpecificationItem("Condo Amenities", cd.amenities.joinToString(", "), "🏊"))
        }
        return items
    }

    specs.townhouse?.let { th ->
        items.add(CategorySpecificationItem("Lot / Floor Area", "Lot ${th.lotAreaSqm.toInt()}m² • Floor ${th.floorAreaSqm.toInt()}m²", "📐"))
        items.add(CategorySpecificationItem("Layout", "${th.stories}-Storey • ${th.bedrooms} Bed, ${th.bathrooms} Bath", "🏡"))
        items.add(CategorySpecificationItem("Carport / Garage", "${th.garageCapacity} Car(s)", "🚗"))
        items.add(CategorySpecificationItem("Maid's Quarters", if (th.hasMaidsRoom) "Included" else "None", "🧹"))
        items.add(CategorySpecificationItem("Subdivision Security", if (th.isGatedSubdivision) "Gated Subdivision" else "Open Neighborhood", "🛡️"))
        return items
    }

    return items
}
