package com.example.data.model

import com.example.data.local.entity.PropertyEntity
import java.text.NumberFormat
import java.util.Locale

/**
 * Structured Landlord Form Input Payload
 */
data class LandlordFormPayload(
    val location: LocationPayload,
    val property: PropertyPayload,
    val pricing: PricingPayload,
    val utilities: UtilitiesPayload,
    val amenities: AmenitiesPayload,
    val rules: List<String>,
    val contact: ContactPayload
)

data class LocationPayload(
    val city: String,
    val barangayStreet: String,
    val landmarksTransit: String
)

data class PropertyPayload(
    val type: String,
    val crSetup: String, // e.g. "Attached Private CR", "Common / Shared CR"
    val maxOccupants: String, // e.g. "1-2 occupants"
    val furnishing: String // e.g. "Furnished (Single bed with pull-out, study table, chair, wall fan)"
)

data class PricingPayload(
    val rentPhp: Double,
    val moveInTerms: String, // e.g. "1 Month Advance, 1 Month Deposit"
    val minStay: String, // e.g. "6 months minimum contract"
    val paymentMethods: String // e.g. "Cash or GCash accepted"
)

data class UtilitiesPayload(
    val electricity: String, // e.g. "Individual sub-meter (BOHECO/VECO/CENECO)"
    val water: String, // e.g. "Fixed ₱200 / head / mo"
    val aircon: String, // e.g. "Wall fan included, provision ready" or "Aircon installed"
    val wifi: String // e.g. "Free shared fiber WiFi in compound"
)

data class AmenitiesPayload(
    val cooking: String, // e.g. "Light cooking only (electric appliances: rice cooker, kettle; no gas stove)"
    val parking: String, // e.g. "Motorcycle parking inside gated yard"
    val security: String // e.g. "Gated compound, night-shift gate key, CCTV"
)

data class ContactPayload(
    val personName: String,
    val phoneViber: String,
    val viewingSchedule: String,
    val requirements: String
)

/**
 * Backend Listing Transformation Engine
 */
object ListingTransformationEngine {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH")).apply {
        maximumFractionDigits = 0
    }

    /**
     * Converts a PropertyEntity and its helpers into the structured LandlordFormPayload
     */
    fun fromProperty(
        property: PropertyEntity,
        locationDisplay: String,
        hostInfo: HostContactInfo,
        breakdown: RentCostBreakdown
    ): LandlordFormPayload {
        val isDampas = property.id.contains("dampas", ignoreCase = true) || property.posterName.contains("Mark", ignoreCase = true)
        val isCebu = property.provinceId.contains("cebu", ignoreCase = true)
        val isBacolod = property.provinceId.contains("bacolod", ignoreCase = true)

        val cr = if (property.bathrooms > 0) "Attached Private CR" else "Common / Shared CR"
        val occupants = if (property.bedrooms <= 1) "1-2 occupants" else "Up to 3-4 occupants"

        val provider = when {
            isCebu -> "VECO"
            isBacolod -> "CENECO"
            else -> "BOHECO"
        }

        val rulesList = mutableListOf<String>()
        if (isDampas) {
            rulesList.add("Gate locked at 10 PM (night-shift key provided)")
            rulesList.add("No sleepover visitors")
            rulesList.add("Strictly no pets")
            rulesList.add("Strictly non-smoking inside rooms")
            rulesList.add("Light cooking (electric appliances only, no gas stove)")
        } else {
            rulesList.add(if (property.petPolicy.contains("No", ignoreCase = true)) "Strictly no pets" else "Small pets allowed")
            rulesList.add("24/7 access with gate key")
            rulesList.add("Strictly non-smoking inside rooms")
            rulesList.add("Respectful quiet hours after 10 PM")
        }

        return LandlordFormPayload(
            location = LocationPayload(
                city = when {
                    isCebu -> "Cebu City"
                    isBacolod -> "Bacolod City"
                    else -> "Tagbilaran City, Bohol"
                },
                barangayStreet = if (isDampas) "Brgy. Dampas" else locationDisplay,
                landmarksTransit = if (isDampas) {
                    "Near Holy Name University (HNU) and Island City Mall (ICM) • 1-ride tricycle/jeep route"
                } else if (isCebu) {
                    "Walking distance to IT Park & Salinas Drive • 1-ride to Ayala / Colon"
                } else {
                    "Near Lopue's East & USLS • 1-ride to Downtown Bacolod"
                }
            ),
            property = PropertyPayload(
                type = property.propertyType,
                crSetup = cr,
                maxOccupants = occupants,
                furnishing = if (isDampas) {
                    "Furnished (Single bed with pull-out, study table, chair, wall fan)"
                } else {
                    property.furnishedStatus
                }
            ),
            pricing = PricingPayload(
                rentPhp = property.monthlyRent,
                moveInTerms = "${breakdown.advanceMonths}-Month Advance, ${breakdown.depositMonths}-Month Security Deposit",
                minStay = breakdown.minLeaseDuration,
                paymentMethods = breakdown.acceptedPayments
            ),
            utilities = UtilitiesPayload(
                electricity = breakdown.electricityBilling,
                water = breakdown.waterBilling,
                aircon = if (isDampas) "Wall fan installed (cool, well-ventilated)" else "Provision ready / Installed",
                wifi = breakdown.internetBilling
            ),
            amenities = AmenitiesPayload(
                cooking = if (isDampas) "Light electric cooking only (rice cooker, kettle). No gas stove." else "Dedicated kitchen counter / cooking allowed",
                parking = breakdown.parkingBilling,
                security = "Gated compound with perimeter fence & tenant keys"
            ),
            rules = rulesList,
            contact = ContactPayload(
                personName = hostInfo.hostName,
                phoneViber = hostInfo.phone,
                viewingSchedule = "Daily viewings available • Advance notice appreciated",
                requirements = hostInfo.viewingPolicy
            )
        )
    }

    /**
     * OUTPUT 1: In-App Listing Display
     */
    fun formatInAppListing(payload: LandlordFormPayload): String {
        val formattedRent = currencyFormat.format(payload.pricing.rentPhp).replace("PHP", "₱")
        val highlights = listOf(
            "$formattedRent/mo",
            payload.property.crSetup,
            payload.property.maxOccupants,
            payload.rules.firstOrNull { it.contains("Pet", ignoreCase = true) } ?: "Non-Smoking",
            payload.rules.firstOrNull { it.contains("Gate", ignoreCase = true) || it.contains("Curfew", ignoreCase = true) } ?: "24/7 Access"
        ).joinToString(" • ")

        return buildString {
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("📱 IN-APP LISTING DISPLAY")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("🏷️ ${payload.property.type} in ${payload.location.barangayStreet}, ${payload.location.city}")
            appendLine("✨ $highlights")
            appendLine()
            appendLine("📊 QUICK SPECS:")
            appendLine("  • ⚡ Electricity : ${payload.utilities.electricity}")
            appendLine("  • 💧 Water       : ${payload.utilities.water}")
            appendLine("  • 📶 Fiber WiFi  : ${payload.utilities.wifi}")
            appendLine("  • 🍳 Cooking     : ${payload.amenities.cooking}")
            appendLine("  • 🛵 Parking     : ${payload.amenities.parking}")
            appendLine("  • 🛏️ Setup       : ${payload.property.furnishing}")
            appendLine()
            appendLine("📋 DETAILS & REQUIREMENTS:")
            appendLine("  • Move-in Terms: ${payload.pricing.moveInTerms}")
            appendLine("  • Minimum Stay : ${payload.pricing.minStay}")
            appendLine("  • Payments     : ${payload.pricing.paymentMethods}")
            payload.rules.forEach { rule ->
                appendLine("  • Rule         : $rule")
            }
            appendLine("  • Requirements : ${payload.contact.requirements}")
            appendLine()
            appendLine("📞 DIRECT CONTACT:")
            appendLine("  • Host/Caretaker: ${payload.contact.personName}")
            appendLine("  • Mobile/Viber  : ${payload.contact.phoneViber}")
            appendLine("  • Schedule      : ${payload.contact.viewingSchedule}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        }
    }

    /**
     * OUTPUT 2: Facebook Marketplace / Social Post Format
     */
    fun formatFacebookPost(payload: LandlordFormPayload): String {
        val formattedRent = currencyFormat.format(payload.pricing.rentPhp).replace("PHP", "₱")
        return buildString {
            appendLine("🏠 [FOR RENT] ${payload.property.type.uppercase()} IN ${payload.location.barangayStreet.uppercase()}, ${payload.location.city.uppercase()} • $formattedRent/MO")
            appendLine()
            appendLine("📍 LOCATION & TRANSIT")
            appendLine("• Exact Area: ${payload.location.barangayStreet}, ${payload.location.city}")
            appendLine("• Landmarks & Access: ${payload.location.landmarksTransit}")
            appendLine()
            appendLine("🏠 UNIT FEATURES")
            appendLine("• Unit Type: ${payload.property.type} (${payload.property.maxOccupants})")
            appendLine("• Bathroom Setup: ${payload.property.crSetup}")
            appendLine("• Inclusions: ${payload.property.furnishing}")
            appendLine("• Ventilation: ${payload.utilities.aircon}")
            appendLine()
            appendLine("💰 RATES & FINANCIAL TERMS")
            appendLine("• Monthly Rent: $formattedRent / month")
            appendLine("• Move-in Payment: ${payload.pricing.moveInTerms}")
            appendLine("• Lease Contract: ${payload.pricing.minStay}")
            appendLine("• Payment Modes: ${payload.pricing.paymentMethods}")
            appendLine()
            appendLine("⚡ UTILITIES (VISAYAS LOCAL FOCUS)")
            appendLine("• Electricity: ${payload.utilities.electricity}")
            appendLine("• Water: ${payload.utilities.water}")
            appendLine("• Internet: ${payload.utilities.wifi}")
            appendLine("• Cooking Policy: ${payload.amenities.cooking}")
            appendLine("• Parking Provision: ${payload.amenities.parking}")
            appendLine("• Security: ${payload.amenities.security}")
            appendLine()
            appendLine("📋 HOUSE RULES & POLICIES")
            payload.rules.forEach { rule ->
                appendLine("• $rule")
            }
            appendLine()
            appendLine("📞 VIEWING & CONTACT")
            appendLine("• Contact Person: ${payload.contact.personName}")
            appendLine("• Mobile / Viber: ${payload.contact.phoneViber}")
            appendLine("• Requirements: ${payload.contact.requirements}")
            appendLine("• Schedule: ${payload.contact.viewingSchedule}")
            appendLine()
            appendLine("👉 Units in this location fill quickly! Send a direct message or call/text ${payload.contact.personName} at ${payload.contact.phoneViber} to schedule a site viewing today!")
        }
    }
}
