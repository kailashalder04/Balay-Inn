package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object LocationCoordinatesHelper {

    /**
     * Resolves automatic geographic coordinate pin (Latitude, Longitude)
     * based on the selected Philippine Region VII & Region VIII locations.
     */
    fun resolveCoordinates(provinceId: String, cityId: String): Pair<Double, Double> {
        val cid = cityId.lowercase()
        return when {
            // Cebu
            cid.contains("cebu_city") -> 10.3157 to 123.8854
            cid.contains("mandaue") -> 10.3396 to 123.9485
            cid.contains("lapu") -> 10.3103 to 123.9494
            cid.contains("talisay") && !cid.contains("bacolod") -> 10.2447 to 123.8494
            cid.contains("consolacion") -> 10.3800 to 123.9600
            cid.contains("liloan") -> 10.4000 to 123.9800

            // Bohol
            cid.contains("tagbilaran") -> 9.6444 to 123.8556
            cid.contains("panglao") -> 9.5800 to 123.7714
            cid.contains("dauis") -> 9.6267 to 123.8647
            cid.contains("baclayon") -> 9.6222 to 123.9031
            cid.contains("tubigon") -> 9.9525 to 123.9625
            cid.contains("jagna") -> 9.6522 to 124.3683
            cid.contains("carmen") -> 9.8242 to 124.1956
            cid.contains("ubay") -> 10.0558 to 124.4742
            cid.contains("anda") -> 9.7431 to 124.5767
            cid.contains("loboc") -> 9.6381 to 124.0322

            // Negros Oriental
            cid.contains("dumaguete") -> 9.3068 to 123.3054
            cid.contains("sibulan") -> 9.3583 to 123.2833
            cid.contains("valencia") && !cid.contains("bohol") -> 9.2833 to 123.2500
            cid.contains("bais") -> 9.5911 to 123.1228
            cid.contains("tanjay") -> 9.5167 to 123.1500
            cid.contains("bayawan") -> 9.3667 to 122.8000
            cid.contains("guihulngan") -> 10.1167 to 123.2667

            // Siquijor
            cid.contains("siq_siquijor") || cid == "siquijor" -> 9.2141 to 123.5156
            cid.contains("san_juan") && cid.contains("siq") -> 9.1611 to 123.4917
            cid.contains("larena") -> 9.2500 to 123.5833
            cid.contains("lazi") -> 9.1283 to 123.6300

            // Leyte
            cid.contains("tacloban") -> 11.2444 to 125.0039
            cid.contains("ormoc") -> 11.0050 to 124.6075
            cid.contains("palo") -> 11.1583 to 124.9917
            cid.contains("baybay") -> 10.6764 to 124.7989
            cid.contains("tanauan") -> 11.1167 to 125.0167
            cid.contains("carigara") -> 11.3000 to 124.6833

            // Southern Leyte
            cid.contains("maasin") -> 10.1335 to 124.9667
            cid.contains("sogod") -> 10.3861 to 124.9819
            cid.contains("macrohon") -> 10.0500 to 124.9333
            cid.contains("bontoc") -> 10.3583 to 124.9667

            // Samar
            cid.contains("catbalogan") -> 11.7753 to 124.8860
            cid.contains("calbayog") -> 12.0667 to 124.5958
            cid.contains("basey") -> 11.2833 to 125.0667

            // Eastern Samar
            cid.contains("borongan") -> 11.6080 to 125.4382
            cid.contains("guiuan") -> 11.0333 to 125.7250
            cid.contains("dolores") -> 12.0333 to 125.4833
            cid.contains("balangiga") -> 11.1167 to 125.3833

            // Northern Samar
            cid.contains("catarman") -> 12.5042 to 124.6367
            cid.contains("laoang") -> 12.5667 to 125.0167
            cid.contains("allen") -> 12.5000 to 124.2833

            // Biliran
            cid.contains("naval") -> 11.5833 to 124.4667
            cid.contains("biliran") -> 11.4667 to 124.4833
            cid.contains("caibiran") -> 11.5667 to 124.5833

            // Bacolod
            cid.contains("bacolod") -> 10.6765 to 122.9509

            else -> {
                // Province level fallback
                when (provinceId) {
                    "prov_cebu" -> 10.3157 to 123.8854
                    "prov_bohol" -> 9.6444 to 123.8556
                    "prov_negros_oriental" -> 9.3068 to 123.3054
                    "prov_siquijor" -> 9.2141 to 123.5156
                    "prov_leyte" -> 11.2444 to 125.0039
                    "prov_southern_leyte" -> 10.1335 to 124.9667
                    "prov_samar" -> 11.7753 to 124.8860
                    "prov_eastern_samar" -> 11.6080 to 125.4382
                    "prov_northern_samar" -> 12.5042 to 124.6367
                    "prov_biliran" -> 11.5833 to 124.4667
                    "prov_bacolod" -> 10.6765 to 122.9509
                    else -> 10.3157 to 123.8854
                }
            }
        }
    }

    /**
     * Suggested landmark dropdown items based on the city/province context.
     */
    fun getSuggestedLandmarks(cityId: String, provinceId: String): List<String> {
        val cid = cityId.lowercase()
        val specific = when {
            cid.contains("cebu_city") -> listOf(
                "Near Cebu IT Park / Waterfront Hotel",
                "Near Ayala Center Cebu / CBP",
                "Near SM City Cebu / North Bus",
                "Near University of San Carlos (USC Talamban)",
                "Near Cebu Doctors University / Chong Hua",
                "Near Fuente Osmeña Circle",
                "Near Colon Street / UV / USJ-R"
            )
            cid.contains("mandaue") -> listOf(
                "Near Parkmall / Mandaue City Hall",
                "Near J Centre Mall / AS Fortuna",
                "Near Subangdaku Commercial Area",
                "Near Mandaue Reclamation Area"
            )
            cid.contains("lapu") -> listOf(
                "Near Mactan-Cebu International Airport (MCIA)",
                "Near MEPZ 1 / MEPZ 2 Economic Zone",
                "Near Gaisano Mactan / Marina Mall",
                "Near Maribago Beach Resort Row"
            )
            cid.contains("tagbilaran") -> listOf(
                "Near Island City Mall (ICM)",
                "Near Holy Name University (HNU Dampas/Main)",
                "Near Bohol Provincial Capitol / Cogon Market",
                "Near University of Bohol (UB Main)",
                "Near Tagbilaran Seaport / BQ Mall"
            )
            cid.contains("panglao") -> listOf(
                "Near Alona Beach Commercial Strip",
                "Near Panglao Airport (Bohol-Panglao Intl)",
                "Near Tawala Highway Junction",
                "Near Dumaluan / South Palms Beach"
            )
            cid.contains("dumaguete") -> listOf(
                "Near Silliman University Main Campus",
                "Near Robinsons Place Dumaguete",
                "Near Rizal Boulevard / Dumaguete Port",
                "Near Foundation University / Negros Prov Capitol"
            )
            cid.contains("tacloban") -> listOf(
                "Near Robinsons Place Tacloban (Marasbaras)",
                "Near Leyte Normal University / UP Tacloban",
                "Near Eastern Visayas Regional Medical Center",
                "Near Tacloban Downtown & Bus Terminal"
            )
            cid.contains("ormoc") -> listOf(
                "Near SM Center Ormoc / Robinsons",
                "Near Ormoc City Port / Public Market",
                "Near Western Leyte College"
            )
            cid.contains("maasin") -> listOf(
                "Near Maasin City Port / Cathedral",
                "Near Gaisano Capital Maasin",
                "Near Southern Leyte State University"
            )
            cid.contains("catbalogan") -> listOf(
                "Near Samar State University (SSU)",
                "Near Catbalogan Town Plaza / Pier",
                "Near Samar Provincial Hospital"
            )
            else -> emptyList()
        }

        val general = listOf(
            "Near Public Market / Town Plaza",
            "Near Town Hall / Municipal Hall",
            "Near National Highway / Bus Terminal",
            "Near General Hospital / Health Center",
            "Near Public High School / Elementary",
            "Near Catholic Parish Church / Chapel",
            "Others (Specify Custom Landmark below)..."
        )

        return (specific + general).distinct()
    }

    /**
     * Launches the native Google Maps app (or web fallback) with a pinned marker.
     */
    fun openInGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String) {
        val encodedLabel = Uri.encode(label.ifBlank { "Property Location" })
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        try {
            context.startActivity(mapIntent)
        } catch (_: Exception) {
            // Fallback to web browser Google Maps
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {}
        }
    }
}
