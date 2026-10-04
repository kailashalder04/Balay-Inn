package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Three primary tabs for Explore Neighbourhood / Nearby Destinations:
 * 1. TRANSIT - Nearby public transport depot, buses, railways, ferry ports, airports
 * 2. ESSENTIALS - Schools, colleges, universities, hospitals, and medical centers
 * 3. UTILITY - Shopping malls, stores, movie theaters, and pharmacy stores
 */
enum class DestinationGroup(
    val title: String,
    val subtitle: String
) {
    TRANSIT(
        title = "TRANSIT",
        subtitle = "Nearby public transport depot"
    ),
    ESSENTIALS(
        title = "ESSENTIALS",
        subtitle = "School, colleges and hospitals"
    ),
    UTILITY(
        title = "UTILITY",
        subtitle = "Shopping malls, stores, movie theaters, pharmacy stores"
    )
}

enum class LandmarkCategory(val title: String, val iconEmoji: String) {
    ALL("All", "🌐"),
    UNIVERSITY("Schools & Universities", "🎓"),
    WORK_BPO("BPO & Workplaces", "💼"),
    SHOPPING_MALL("Malls & Markets", "🛒"),
    HOSPITAL("Hospitals & Clinics", "🏥"),
    TRANSIT("Transit & Terminals", "🚌")
}

data class CommuteDestination(
    val id: String,
    val name: String,
    val group: DestinationGroup,
    val subcategory: String,
    val distanceMeters: Int,
    val walkingTimeMinutes: Int,
    val transitRouteName: String, // Public transit route name
    val transitTimeMinutes: Int,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: LandmarkCategory = LandmarkCategory.ALL,
    val transitFarePhp: Int = 0,
    val driveTimeMinutes: Int = 0,
    val stepByStepDirections: String = ""
) {
    val formattedDistance: String
        get() = String.format(java.util.Locale.US, "%.1f km", distanceMeters / 1000.0)

    val timeToReachMinutes: Int
        get() = if (walkingTimeMinutes < 5) walkingTimeMinutes else transitTimeMinutes

    // Walking icon for < 5 mins, transport icon for >= 5 mins
    val isWalking: Boolean
        get() = timeToReachMinutes < 5

    val timeToReachText: String
        get() = if (isWalking) "$walkingTimeMinutes min walk" else "$timeToReachMinutes mins"

    // Formatted distance and time for the right-hand column: e.g. "7.1 km|20 mins"
    val formattedDistanceAndTime: String
        get() = "$formattedDistance|${timeToReachMinutes} mins"

    // Walking time shown only if not more than 10 mins
    val walkingTimeFormatted: String?
        get() = if (walkingTimeMinutes <= 10) "$walkingTimeMinutes min walk" else null
}

object CommuteGuideProvider {

    private data class RawLandmark(
        val id: String,
        val name: String,
        val group: DestinationGroup,
        val subcategory: String,
        val lat: Double,
        val lng: Double,
        val transitRoute: String,
        val category: LandmarkCategory
    )

    // Master Reference Landmarks categorized into TRANSIT, ESSENTIALS, and UTILITY with subcategories
    private val masterLandmarks = listOf(
        // ==========================================
        // BACOLOD CITY & NEGROS OCCIDENTAL
        // ==========================================
        // 1. TRANSIT
        RawLandmark("bcd_tr_north", "Bacolod North Bus Terminal", DestinationGroup.TRANSIT, "Buses", 10.7022, 122.9621, "Banago-Bata or Ceres Liner Feeder", LandmarkCategory.TRANSIT),
        RawLandmark("bcd_tr_south", "Bacolod South Bus Terminal", DestinationGroup.TRANSIT, "Buses", 10.6592, 122.9502, "Libertad-Lopez Jaena Jeepney", LandmarkCategory.TRANSIT),
        RawLandmark("bcd_tr_central", "Central Market Jeepney Depot", DestinationGroup.TRANSIT, "Jeepney & Transit Depots", 10.6685, 122.9490, "Central Market Loop Jeepney", LandmarkCategory.TRANSIT),
        RawLandmark("bcd_tr_bredco", "Banago Wharf & BREDCO Sea Port", DestinationGroup.TRANSIT, "Ports & Ferries", 10.6860, 122.9370, "Banago-Libertad Jeepney", LandmarkCategory.TRANSIT),
        RawLandmark("bcd_tr_airport", "Bacolod-Silay Airport Shuttle", DestinationGroup.TRANSIT, "Airports & Shuttles", 10.6780, 122.9520, "Silay Shuttle Van / Modern PUV", LandmarkCategory.TRANSIT),

        // 2. ESSENTIALS
        RawLandmark("bcd_es_usls", "University of St. La Salle (USLS)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.6789, 122.9618, "Shopping-La Salle or Bata Jeepney", LandmarkCategory.UNIVERSITY),
        RawLandmark("bcd_es_riverside", "Riverside College", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.6811, 122.9555, "Bata-Libertad or Shopping Jeep", LandmarkCategory.UNIVERSITY),
        RawLandmark("bcd_es_csab", "Colegio San Agustin - Bacolod (CSA-B)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.6833, 122.9567, "Mandalagan-Libertad Jeep", LandmarkCategory.UNIVERSITY),
        RawLandmark("bcd_es_stiwnu", "STI West Negros University", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.6710, 122.9580, "Burgos-Hilado Jeepney", LandmarkCategory.UNIVERSITY),
        RawLandmark("bcd_es_clmmrh", "Corazon Locsin Regional Hospital", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 10.6720, 122.9530, "Lacson-Burgos Jeepney", LandmarkCategory.HOSPITAL),
        RawLandmark("bcd_es_torre", "Dr. Pablo Torre Memorial Hospital", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 10.6815, 122.9560, "BS Aquino Drive Jeepney", LandmarkCategory.HOSPITAL),
        RawLandmark("bcd_es_bcnhs", "Bacolod City National High School", DestinationGroup.ESSENTIALS, "Schools", 10.6595, 122.9585, "Taculing-Libertad Jeepney", LandmarkCategory.UNIVERSITY),

        // 3. UTILITY
        RawLandmark("bcd_ut_ayala", "Ayala Malls Capitol Central", DestinationGroup.UTILITY, "Shopping Malls", 10.6738, 122.9515, "Bata-Libertad or Mandalagan Jeep", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_robinsons", "Robinsons Place Bacolod", DestinationGroup.UTILITY, "Shopping Malls", 10.6908, 122.9565, "Bata-Libertad Jeepney", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_sm", "SM City Bacolod & SM Cinema", DestinationGroup.UTILITY, "Movie Theaters", 10.6698, 122.9437, "Banago-Libertad Jeepney", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_mercury", "Mercury Drug Lacson", DestinationGroup.UTILITY, "Pharmacies", 10.6750, 122.9512, "Mandalagan-Libertad Jeep", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_watsons", "Watsons Capitol Central", DestinationGroup.UTILITY, "Pharmacies", 10.6740, 122.9518, "Lacson-Burgos Jeep", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_lopues", "Lopue's East Supermarket", DestinationGroup.UTILITY, "Supermarkets & Stores", 10.6680, 122.9710, "Homesite-Central Market Jeep", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("bcd_ut_citymall", "CityMall Mandalagan", DestinationGroup.UTILITY, "Supermarkets & Stores", 10.6940, 122.9580, "Bata-Libertad Jeepney", LandmarkCategory.SHOPPING_MALL),

        // ==========================================
        // CEBU CITY & METRO CEBU
        // ==========================================
        // 1. TRANSIT
        RawLandmark("ceb_tr_north_bus", "Cebu North Bus Terminal (SM City)", DestinationGroup.TRANSIT, "Buses", 10.3130, 123.9190, "01K or MyBus Feeder", LandmarkCategory.TRANSIT),
        RawLandmark("ceb_tr_south_bus", "Cebu South Bus Terminal (CSBT)", DestinationGroup.TRANSIT, "Buses", 10.3015, 123.8940, "09C or 10H Jeepney", LandmarkCategory.TRANSIT),
        RawLandmark("ceb_tr_it_park", "Cebu I.T. Park Transport Terminal", DestinationGroup.TRANSIT, "Jeepney & Transit Depots", 10.3305, 123.9070, "17B, 17D, 04L Jeep / Modern PUV", LandmarkCategory.TRANSIT),
        RawLandmark("ceb_tr_ayala_term", "Ayala Center Public Transport Terminal", DestinationGroup.TRANSIT, "Jeepney & Transit Depots", 10.3185, 123.9065, "14D, 13C, or 06H Jeepney", LandmarkCategory.TRANSIT),
        RawLandmark("ceb_tr_pier1", "Cebu Pier 1 Passenger Ferry Terminal", DestinationGroup.TRANSIT, "Ports & Ferries", 10.2930, 123.9050, "12I, 02B or Pier Tricycle", LandmarkCategory.TRANSIT),
        RawLandmark("ceb_tr_mcia", "Mactan-Cebu International Airport (MCIA)", DestinationGroup.TRANSIT, "Airports & Shuttles", 10.3120, 123.9780, "MyBus Express Direct Line", LandmarkCategory.TRANSIT),

        // 2. ESSENTIALS
        RawLandmark("ceb_es_usc_tc", "University of San Carlos (USC Talamban)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.3541, 123.9114, "13C or 62B Jeepney", LandmarkCategory.UNIVERSITY),
        RawLandmark("ceb_es_up_cebu", "University of the Philippines Cebu", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.3204, 123.8967, "04L, 17B, or 17D Jeepney", LandmarkCategory.UNIVERSITY),
        RawLandmark("ceb_es_cnu", "Cebu Normal University (CNU)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.3025, 123.8960, "01K, 06H or Colon Jeep", LandmarkCategory.UNIVERSITY),
        RawLandmark("ceb_es_swu", "Southwestern University PHINMA", DestinationGroup.ESSENTIALS, "Colleges & Universities", 10.3005, 123.8905, "10F or Urgello Jeep", LandmarkCategory.UNIVERSITY),
        RawLandmark("ceb_es_cduh", "Cebu Doctors' University Hospital", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 10.3135, 123.8920, "14D or 17B Jeepney", LandmarkCategory.HOSPITAL),
        RawLandmark("ceb_es_chong_hua", "Chong Hua Hospital (Fuente)", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 10.3117, 123.8912, "06B or 17D Jeepney", LandmarkCategory.HOSPITAL),
        RawLandmark("ceb_es_perpetual", "Perpetual Succour Hospital", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 10.3190, 123.8985, "Gorordo Ave Jeepney", LandmarkCategory.HOSPITAL),

        // 3. UTILITY
        RawLandmark("ceb_ut_ayala", "Ayala Center Cebu", DestinationGroup.UTILITY, "Shopping Malls", 10.3175, 123.9056, "14D, 13C, or 04L Jeepney", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_robinsons_gal", "Robinsons Galleria Cebu", DestinationGroup.UTILITY, "Shopping Malls", 10.3060, 123.9110, "01K or General Maxilom PUV", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_sm_city", "SM City Cebu & IMAX Cinema", DestinationGroup.UTILITY, "Movie Theaters", 10.3121, 123.9184, "01K, 12G, or MyBus", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_sm_seaside", "SM Seaside Cinema", DestinationGroup.UTILITY, "Movie Theaters", 10.2818, 123.8821, "MyBus Seaside Route", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_rose_fuente", "Rose Pharmacy Fuente", DestinationGroup.UTILITY, "Pharmacies", 10.3110, 123.8925, "14D or Fuente Circle Jeep", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_mercury_it", "Mercury Drug IT Park", DestinationGroup.UTILITY, "Pharmacies", 10.3290, 123.9055, "17B or 17D Jeepney", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_sugbo", "Sugbo Mercado Retail Strip", DestinationGroup.UTILITY, "Supermarkets & Stores", 10.3298, 123.9060, "17B or 17D Jeepney", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("ceb_ut_jy_square", "JY Square Supermarket", DestinationGroup.UTILITY, "Supermarkets & Stores", 10.3275, 123.8980, "04L or 17B Jeepney", LandmarkCategory.SHOPPING_MALL),

        // ==========================================
        // BOHOL & TAGBILARAN / PANGLAO
        // ==========================================
        // 1. TRANSIT
        RawLandmark("boh_tr_dao", "Dao Integrated Bus Terminal", DestinationGroup.TRANSIT, "Buses", 9.6644, 123.8741, "Dao-bound Tricycle / Multicab", LandmarkCategory.TRANSIT),
        RawLandmark("boh_tr_cpg_term", "CPG Avenue Tricycle & Jeepney Depot", DestinationGroup.TRANSIT, "Jeepney & Transit Depots", 9.6460, 123.8560, "CPG Avenue Tricycle", LandmarkCategory.TRANSIT),
        RawLandmark("boh_tr_panglao_hub", "Panglao Town Transport Hub", DestinationGroup.TRANSIT, "Jeepney & Transit Depots", 9.5780, 123.7480, "Panglao-Tagbilaran Jeepney Line", LandmarkCategory.TRANSIT),
        RawLandmark("boh_tr_port", "Tagbilaran FastCraft Port", DestinationGroup.TRANSIT, "Ports & Ferries", 9.6542, 123.8486, "Port Tricycle Line", LandmarkCategory.TRANSIT),
        RawLandmark("boh_tr_bpia", "Bohol-Panglao Airport", DestinationGroup.TRANSIT, "Airports & Shuttles", 9.5667, 123.7667, "Southern Star Airport Bus", LandmarkCategory.TRANSIT),

        // 2. ESSENTIALS
        RawLandmark("boh_es_hnu", "Holy Name University (HNU Dampas)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 9.6608, 123.8703, "Tagbilaran Tricycle Line", LandmarkCategory.UNIVERSITY),
        RawLandmark("boh_es_bisu", "Bohol Island State University (BISU)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 9.6450, 123.8542, "CPG Avenue Tricycle", LandmarkCategory.UNIVERSITY),
        RawLandmark("boh_es_ub", "University of Bohol (UB Main)", DestinationGroup.ESSENTIALS, "Colleges & Universities", 9.6435, 123.8530, "City Center Tricycle", LandmarkCategory.UNIVERSITY),
        RawLandmark("boh_es_gallares", "Gov. Celestino Gallares Medical Center", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 9.6480, 123.8520, "Hospital Tricycle Route", LandmarkCategory.HOSPITAL),
        RawLandmark("boh_es_ramiro", "Ramiro Community Hospital", DestinationGroup.ESSENTIALS, "Hospitals & Medical Centers", 9.6470, 123.8550, "Gallares Tricycle Line", LandmarkCategory.HOSPITAL),
        RawLandmark("boh_es_dauis_hs", "Dauis National High School", DestinationGroup.ESSENTIALS, "Schools", 9.6260, 123.8640, "Dauis-Poblacion Tricycle", LandmarkCategory.UNIVERSITY),

        // 3. UTILITY
        RawLandmark("boh_ut_bq_mall", "BQ Mall (Bohol Quality Mall)", DestinationGroup.UTILITY, "Shopping Malls", 9.6440, 123.8555, "CPG Ave Tricycle Line", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_alturas", "Alturas Mall", DestinationGroup.UTILITY, "Shopping Malls", 9.6465, 123.8565, "Tagbilaran Market Tricycle", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_icm", "Island City Mall (ICM) Cinema", DestinationGroup.UTILITY, "Movie Theaters", 9.6644, 123.8741, "Dao-bound Tricycle / Multicab", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_southstar", "South Star Drug CPG", DestinationGroup.UTILITY, "Pharmacies", 9.6455, 123.8545, "CPG Avenue Tricycle", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_mercury_tag", "Mercury Drug Tagbilaran", DestinationGroup.UTILITY, "Pharmacies", 10.6450, 123.8540, "Downtown Tricycle Route", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_alona_strip", "Alona Commercial Stores", DestinationGroup.UTILITY, "Supermarkets & Stores", 9.5492, 123.7731, "Panglao Tourist Multicab", LandmarkCategory.SHOPPING_MALL),
        RawLandmark("boh_ut_7eleven", "7-Eleven Downtown Tagbilaran", DestinationGroup.UTILITY, "Supermarkets & Stores", 9.6445, 123.8550, "Downtown Tricycle Route", LandmarkCategory.SHOPPING_MALL)
    )

    /**
     * Returns destinations grouped by Subcategory for a given DestinationGroup (TRANSIT, ESSENTIALS, UTILITY)
     */
    fun getDestinationsBySubcategory(
        property: PropertyEntity,
        group: DestinationGroup
    ): Map<String, List<CommuteDestination>> {
        val all = getCommuteDestinations(property).filter { it.group == group }
        return all.groupBy { it.subcategory }
    }

    /**
     * Returns places grouped under three primary categories:
     * 1. TRANSIT - Nearby public transport depot
     * 2. ESSENTIALS - School, colleges and hospitals
     * 3. UTILITY - Shopping malls, stores, movie theaters, pharmacy stores
     */
    fun getGroupedDestinations(property: PropertyEntity): Map<DestinationGroup, List<CommuteDestination>> {
        val allDestinations = getCommuteDestinations(property)

        val transitList = allDestinations.filter { it.group == DestinationGroup.TRANSIT }
        val essentialsList = allDestinations.filter { it.group == DestinationGroup.ESSENTIALS }
        val utilityList = allDestinations.filter { it.group == DestinationGroup.UTILITY }

        val map = linkedMapOf<DestinationGroup, List<CommuteDestination>>()
        if (transitList.isNotEmpty()) map[DestinationGroup.TRANSIT] = transitList
        if (essentialsList.isNotEmpty()) map[DestinationGroup.ESSENTIALS] = essentialsList
        if (utilityList.isNotEmpty()) map[DestinationGroup.UTILITY] = utilityList
        return map
    }

    /**
     * Computes genuine distances to master landmarks and returns prioritized,
     * categorized commute destination items for the given property.
     */
    fun getCommuteDestinations(property: PropertyEntity): List<CommuteDestination> {
        val propLat = property.latitude
        val propLng = property.longitude

        val computed = masterLandmarks.mapNotNull { landmark ->
            val dist = calculateDistanceMeters(propLat, propLng, landmark.lat, landmark.lng)
            // Limit to relevant regional landmarks (within 45 km)
            if (dist <= 45_000) {
                // Walking speed: ~4.8 km/h = 80 m/min
                val walkMins = (dist / 80).coerceAtLeast(1)
                // Transit speed: avg 22 km/h in city traffic
                val transitMins = (dist / 360 + 3).coerceAtLeast(3)
                // Drive speed: avg 32 km/h
                val driveMins = (dist / 530 + 2).coerceAtLeast(2)

                CommuteDestination(
                    id = landmark.id,
                    name = landmark.name,
                    group = landmark.group,
                    subcategory = landmark.subcategory,
                    distanceMeters = dist,
                    walkingTimeMinutes = walkMins,
                    transitRouteName = landmark.transitRoute,
                    transitTimeMinutes = transitMins,
                    latitude = landmark.lat,
                    longitude = landmark.lng,
                    category = landmark.category,
                    transitFarePhp = 0,
                    driveTimeMinutes = driveMins
                )
            } else null
        }.sortedBy { it.distanceMeters }

        // If the property specifically has a custom nearbyLandmark entered by landlord,
        // prepend it to the matching group and subcategory!
        if (property.nearbyLandmark.isNotBlank()) {
            val landmarkNameLower = property.nearbyLandmark.lowercase()
            val detectedGroup = when {
                landmarkNameLower.contains("terminal") || landmarkNameLower.contains("depot") ||
                landmarkNameLower.contains("port") || landmarkNameLower.contains("airport") ||
                landmarkNameLower.contains("wharf") || landmarkNameLower.contains("station") ||
                landmarkNameLower.contains("transit") || landmarkNameLower.contains("bus") -> DestinationGroup.TRANSIT

                landmarkNameLower.contains("school") || landmarkNameLower.contains("college") ||
                landmarkNameLower.contains("university") || landmarkNameLower.contains("hospital") ||
                landmarkNameLower.contains("clinic") || landmarkNameLower.contains("medical") -> DestinationGroup.ESSENTIALS

                else -> DestinationGroup.UTILITY
            }

            val detectedSubcat = when (detectedGroup) {
                DestinationGroup.TRANSIT -> {
                    when {
                        landmarkNameLower.contains("port") || landmarkNameLower.contains("ferry") -> "Ports & Ferries"
                        landmarkNameLower.contains("airport") -> "Airports & Shuttles"
                        landmarkNameLower.contains("bus") -> "Buses"
                        else -> "Jeepney & Transit Depots"
                    }
                }
                DestinationGroup.ESSENTIALS -> {
                    when {
                        landmarkNameLower.contains("hospital") || landmarkNameLower.contains("clinic") || landmarkNameLower.contains("medical") -> "Hospitals & Medical Centers"
                        landmarkNameLower.contains("university") || landmarkNameLower.contains("college") -> "Colleges & Universities"
                        else -> "Schools"
                    }
                }
                DestinationGroup.UTILITY -> {
                    when {
                        landmarkNameLower.contains("cinema") || landmarkNameLower.contains("movie") || landmarkNameLower.contains("theater") -> "Movie Theaters"
                        landmarkNameLower.contains("pharmacy") || landmarkNameLower.contains("drug") -> "Pharmacies"
                        landmarkNameLower.contains("mall") -> "Shopping Malls"
                        else -> "Supermarkets & Stores"
                    }
                }
            }

            val customLandmark = CommuteDestination(
                id = "custom_landmark_${property.id}",
                name = property.nearbyLandmark,
                group = detectedGroup,
                subcategory = detectedSubcat,
                distanceMeters = 350,
                walkingTimeMinutes = 4,
                transitRouteName = "Immediate Tricycle / Local PUV",
                transitTimeMinutes = 3,
                latitude = propLat + 0.002,
                longitude = propLng + 0.002,
                category = LandmarkCategory.WORK_BPO
            )
            return listOf(customLandmark) + computed.filter { it.name.lowercase() != property.nearbyLandmark.lowercase() }
        }

        return computed
    }

    /**
     * Provides a short, punchy summary badge for listings/cards
     */
    fun getQuickCommuteBadge(property: PropertyEntity): String {
        if (property.nearbyLandmark.isNotBlank()) {
            return "📍 Near ${property.nearbyLandmark.take(18)}"
        }
        val destinations = getCommuteDestinations(property)
        val nearest = destinations.firstOrNull() ?: return "Convenient Access"

        val shortName = when {
            nearest.name.contains("USLS", ignoreCase = true) -> "USLS"
            nearest.name.contains("Riverside", ignoreCase = true) -> "Riverside College"
            nearest.name.contains("Lacson", ignoreCase = true) -> "Lacson Strip"
            nearest.name.contains("I.T. Park", ignoreCase = true) -> "Cebu IT Park"
            nearest.name.contains("Ayala", ignoreCase = true) -> "Ayala / CBP"
            nearest.name.contains("USC", ignoreCase = true) -> "USC Talamban"
            nearest.name.contains("HNU", ignoreCase = true) -> "HNU Dampas"
            nearest.name.contains("BISU", ignoreCase = true) -> "BISU Main"
            nearest.name.contains("Alona", ignoreCase = true) -> "Alona Beach"
            nearest.name.contains("SM", ignoreCase = true) -> "SM City"
            else -> nearest.name.split(" ").take(2).joinToString(" ")
        }

        return if (nearest.distanceMeters <= 800) {
            "🚶 ${nearest.walkingTimeMinutes}m walk to $shortName"
        } else {
            "1 ride to $shortName"
        }
    }

    /**
     * Standard Haversine distance in meters
     */
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c).toInt()
    }
}
