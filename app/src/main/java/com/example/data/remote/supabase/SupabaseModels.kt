package com.example.data.remote.supabase

import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabasePropertyDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyIdCol: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "short_description") val shortDescription: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "full_description") val fullDescription: String? = null,
    @Json(name = "property_type") val propertyType: String? = null,
    @Json(name = "monthly_rent") val monthlyRent: Double? = null,
    @Json(name = "min_monthly_rent") val minMonthlyRent: Double? = null,
    @Json(name = "max_monthly_rent") val maxMonthlyRent: Double? = null,

    // Direct manual location columns with merged location and coordinates
    @Json(name = "location") val location: String? = null,
    @Json(name = "coordinates") val coordinates: String? = null,
    @Json(name = "province") val province: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "barangay") val barangay: String? = null,
    @Json(name = "neighborhood") val neighborhood: String? = null,
    @Json(name = "nearby_landmark") val nearbyLandmark: String? = null,
    @Json(name = "street_address_private") val streetAddressPrivate: String? = null,
    @Json(name = "location_privacy") val locationPrivacy: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,

    // Specifications & Metrics
    @Json(name = "bedrooms") val bedrooms: Int? = null,
    @Json(name = "bathrooms") val bathrooms: Int? = null,
    @Json(name = "floor_area_sqm") val floorAreaSqm: Double? = null,
    @Json(name = "total_capacity") val totalCapacity: Int? = null,
    @Json(name = "total_available_beds") val totalAvailableBeds: Int? = null,
    @Json(name = "furnished_status") val furnishedStatus: String? = null,
    @Json(name = "parking_available") val parkingAvailable: Boolean? = null,
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "availability_date") val availabilityDate: String? = null,
    @Json(name = "lease_duration_months") val leaseDurationMonths: Int? = null,
    @Json(name = "advance_rent_months") val advanceRentMonths: Int? = null,
    @Json(name = "security_deposit_months") val securityDepositMonths: Int? = null,
    @Json(name = "security_deposit") val securityDeposit: Double? = null,
    @Json(name = "association_dues") val associationDues: Double? = null,
    @Json(name = "utilities_included") val utilitiesIncluded: Boolean? = null,
    @Json(name = "internet_included") val internetIncluded: Boolean? = null,
    @Json(name = "maintenance_fees") val maintenanceFees: Double? = null,
    @Json(name = "parking_fees") val parkingFees: Double? = null,
    @Json(name = "other_fees") val otherFees: Double? = null,
    @Json(name = "amenities_json") val amenitiesJson: String? = null,
    @Json(name = "category_specs_json") val categorySpecsJson: String? = null,
    @Json(name = "specifications") val specifications: Any? = null,
    @Json(name = "contact_preferences") val contactPreferences: String? = null,
    @Json(name = "status") val status: String? = null,

    // Ownership & Host details
    @Json(name = "owner_id") val ownerId: String? = null,
    @Json(name = "host_id") val hostId: String? = null,
    @Json(name = "poster_id") val posterId: String? = null,
    @Json(name = "host_name") val hostName: String? = null,
    @Json(name = "poster_name") val posterName: String? = null,
    @Json(name = "host_phone") val hostPhone: String? = null,
    @Json(name = "poster_role") val posterRole: String? = null,
    @Json(name = "is_poster_authorized") val isPosterAuthorized: Boolean? = null,
    @Json(name = "verification_level") val verificationLevel: Int? = null,
    @Json(name = "phone_verified") val phoneVerified: Boolean? = null,
    @Json(name = "email_verified") val emailVerified: Boolean? = null,
    @Json(name = "identity_submitted") val identitySubmitted: Boolean? = null,
    @Json(name = "professional_verified") val professionalVerified: Boolean? = null,
    @Json(name = "property_visited") val propertyVisited: Boolean? = null,
    @Json(name = "property_docs_reviewed") val propertyDocsReviewed: Boolean? = null,
    @Json(name = "views_count") val viewsCount: Int? = null,
    @Json(name = "saves_count") val savesCount: Int? = null,
    @Json(name = "image_urls") val imageUrls: List<String>? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    fun toEntity(): PropertyEntity {
        val prov = province ?: "Cebu"
        val ct = city ?: "Cebu City"
        val bgy = barangay ?: "Lahug"

        val resolvedPropId = propertyIdCol?.trim()?.ifBlank { null } ?: id?.trim()?.ifBlank { null } ?: "prop_${System.currentTimeMillis()}"
        return PropertyEntity(
            id = resolvedPropId,
            title = title ?: shortDescription ?: "Untitled Property",
            description = description ?: fullDescription ?: "",
            propertyType = propertyType ?: "Condominium",
            monthlyRent = monthlyRent ?: minMonthlyRent ?: 0.0,
            countryId = "ctry_ph",
            regionId = "reg_vii",
            provinceId = resolveProvinceId(prov),
            cityMunicipalityId = resolveCityId(ct),
            barangayId = resolveBarangayId(bgy),
            neighborhood = neighborhood ?: "",
            nearbyLandmark = nearbyLandmark ?: "",
            location = location?.takeIf { it.isNotBlank() } ?: listOfNotNull(
                streetAddressPrivate?.takeIf { it.isNotBlank() },
                bgy.takeIf { it.isNotBlank() },
                ct.takeIf { it.isNotBlank() },
                prov.takeIf { it.isNotBlank() }
            ).joinToString(", "),
            coordinates = coordinates?.takeIf { it.isNotBlank() } ?: "${latitude ?: 10.3157}, ${longitude ?: 123.8854}",
            streetAddressPrivate = streetAddressPrivate ?: "",
            locationPrivacy = locationPrivacy ?: "APPROXIMATE",
            latitude = coordinates?.split(",")?.getOrNull(0)?.trim()?.toDoubleOrNull() ?: latitude ?: 10.3157,
            longitude = coordinates?.split(",")?.getOrNull(1)?.trim()?.toDoubleOrNull() ?: longitude ?: 123.8854,
            bedrooms = bedrooms ?: 1,
            bathrooms = bathrooms ?: 1,
            floorAreaSqm = floorAreaSqm ?: 30.0,
            furnishedStatus = furnishedStatus ?: "Furnished",
            parkingAvailable = parkingAvailable ?: false,
            petPolicy = petPolicy ?: "No Pets",
            availabilityDate = availabilityDate ?: "Immediate",
            leaseDurationMonths = leaseDurationMonths ?: 12,
            advanceRentMonths = advanceRentMonths ?: 1,
            securityDepositMonths = securityDepositMonths ?: 2,
            associationDues = associationDues ?: 0.0,
            utilitiesIncluded = utilitiesIncluded ?: false,
            internetIncluded = internetIncluded ?: false,
            maintenanceFees = maintenanceFees ?: 0.0,
            parkingFees = parkingFees ?: 0.0,
            otherFees = otherFees ?: 0.0,
            amenitiesJson = amenitiesJson ?: "[]",
            categorySpecsJson = categorySpecsJson?.ifBlank { null }
                ?: (specifications?.toString()?.takeIf { it.isNotBlank() && it != "null" })
                ?: "{}",
            contactPreferences = contactPreferences ?: "In-App Messaging Only",
            status = status ?: "ACTIVE",
            ownerId = ownerId ?: hostId ?: "user_landlord_01",
            posterId = posterId ?: hostId ?: ownerId ?: "user_landlord_01",
            posterName = posterName ?: hostName ?: "Balay-Inn Host",
            posterRole = posterRole ?: "Property owner",
            isPosterAuthorized = isPosterAuthorized ?: true,
            verificationLevel = verificationLevel ?: 2,
            phoneVerified = phoneVerified ?: true,
            emailVerified = emailVerified ?: true,
            identitySubmitted = identitySubmitted ?: false,
            professionalVerified = professionalVerified ?: false,
            propertyVisited = propertyVisited ?: false,
            propertyDocsReviewed = propertyDocsReviewed ?: false,
            viewsCount = viewsCount ?: 0,
            savesCount = savesCount ?: 0,
            createdAt = TimestampParser.toEpochMillis(createdAt),
            updatedAt = TimestampParser.toEpochMillis(updatedAt)
        )
    }

    fun toEntityOrNull(): PropertyEntity? {
        val propId = propertyIdCol?.trim()?.ifBlank { null } ?: id?.trim()?.ifBlank { null } ?: return null
        return toEntity().copy(id = propId)
    }

    fun toImageEntities(): List<com.example.data.local.entity.PropertyImageEntity> {
        val propId = propertyIdCol?.trim()?.ifBlank { null } ?: id?.trim()?.ifBlank { null } ?: return emptyList()
        val urls = imageUrls?.filter { it.isNotBlank() } ?: emptyList()
        return if (urls.isNotEmpty()) {
            urls.mapIndexed { index, url ->
                com.example.data.local.entity.PropertyImageEntity(
                    id = "img_${propId}_$index",
                    propertyId = propId,
                    imageUrl = url,
                    isPrimary = index == 0,
                    caption = "",
                    orderIndex = index
                )
            }
        } else {
            val fallback = com.example.ui.components.getDefaultPropertyThumbnail(propertyType)
            listOf(
                com.example.data.local.entity.PropertyImageEntity(
                    id = "img_${propId}_fallback",
                    propertyId = propId,
                    imageUrl = fallback,
                    isPrimary = true,
                    caption = "Verified property view",
                    orderIndex = 0
                )
            )
        }
    }

    companion object {
        fun resolveProvinceName(provinceId: String?): String {
            if (provinceId.isNullOrBlank()) return "Cebu"
            return com.example.data.seed.MarketplaceDataSeed.seedProvinces.find { it.id == provinceId }?.name
                ?: provinceId.removePrefix("prov_").replace("_", " ").split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }

        fun resolveCityName(cityId: String?): String {
            if (cityId.isNullOrBlank()) return "Cebu City"
            return com.example.data.seed.MarketplaceDataSeed.seedCities.find { it.id == cityId }?.name
                ?: cityId.removePrefix("city_").removePrefix("bohol_").removePrefix("ley_").replace("_", " ").split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }

        fun resolveBarangayName(barangayId: String?): String {
            if (barangayId.isNullOrBlank()) return "Lahug"
            return com.example.data.seed.MarketplaceDataSeed.seedBarangays.find { it.id == barangayId }?.name
                ?: barangayId.removePrefix("bgy_").replace("_", " ").split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }

        fun resolveProvinceId(provinceName: String?): String {
            if (provinceName.isNullOrBlank()) return "prov_cebu"
            val clean = provinceName.trim().lowercase()
            return com.example.data.seed.MarketplaceDataSeed.seedProvinces.find {
                it.name.lowercase() == clean || it.id.lowercase() == clean || clean.contains(it.name.lowercase())
            }?.id ?: "prov_cebu"
        }

        fun resolveCityId(cityName: String?): String {
            if (cityName.isNullOrBlank()) return "city_cebu"
            val clean = cityName.trim().lowercase()
            return com.example.data.seed.MarketplaceDataSeed.seedCities.find {
                it.name.lowercase() == clean || it.id.lowercase() == clean || clean.contains(it.name.lowercase())
            }?.id ?: "city_cebu"
        }

        fun resolveBarangayId(barangayName: String?): String {
            if (barangayName.isNullOrBlank()) return "bgy_lahug"
            val clean = barangayName.trim().lowercase()
            return com.example.data.seed.MarketplaceDataSeed.seedBarangays.find {
                it.name.lowercase() == clean || it.id.lowercase() == clean || clean.contains(it.name.lowercase())
            }?.id ?: "bgy_lahug"
        }

        fun fromEntity(
            e: PropertyEntity,
            images: List<com.example.data.local.entity.PropertyImageEntity> = emptyList()
        ): SupabasePropertyDto {
            val prov = resolveProvinceName(e.provinceId)
            val ct = resolveCityName(e.cityMunicipalityId)
            val bgy = resolveBarangayName(e.barangayId)

            return SupabasePropertyDto(
                id = e.id,
                title = e.title,
                shortDescription = e.title,
                description = e.description,
                fullDescription = e.description,
                propertyType = e.propertyType,
                monthlyRent = e.monthlyRent,
                minMonthlyRent = e.monthlyRent,
                maxMonthlyRent = e.monthlyRent,
                province = prov,
                city = ct,
                barangay = bgy,
                location = e.location.takeIf { it.isNotBlank() } ?: listOfNotNull(
                    e.streetAddressPrivate.takeIf { it.isNotBlank() },
                    bgy.takeIf { it.isNotBlank() },
                    ct.takeIf { it.isNotBlank() },
                    prov.takeIf { it.isNotBlank() }
                ).joinToString(", "),
                coordinates = if (e.coordinates.isNotBlank()) e.coordinates else "${e.latitude}, ${e.longitude}",
                neighborhood = e.neighborhood,
                nearbyLandmark = e.nearbyLandmark,
                streetAddressPrivate = null,
                locationPrivacy = e.locationPrivacy,
                latitude = null,
                longitude = null,
                bedrooms = e.bedrooms,
                bathrooms = e.bathrooms,
                floorAreaSqm = e.floorAreaSqm,
                totalCapacity = maxOf(e.bedrooms * 2, 1),
                totalAvailableBeds = maxOf(e.bedrooms * 2, 1),
                furnishedStatus = e.furnishedStatus,
                parkingAvailable = e.parkingAvailable,
                petPolicy = e.petPolicy,
                availabilityDate = e.availabilityDate,
                leaseDurationMonths = e.leaseDurationMonths,
                advanceRentMonths = e.advanceRentMonths,
                securityDepositMonths = e.securityDepositMonths,
                securityDeposit = e.monthlyRent * e.securityDepositMonths,
                associationDues = e.associationDues,
                utilitiesIncluded = e.utilitiesIncluded,
                internetIncluded = e.internetIncluded,
                maintenanceFees = e.maintenanceFees,
                parkingFees = e.parkingFees,
                otherFees = e.otherFees,
                amenitiesJson = e.amenitiesJson,
                categorySpecsJson = e.categorySpecsJson,
                contactPreferences = e.contactPreferences,
                status = e.status,
                ownerId = e.ownerId,
                hostId = e.ownerId,
                posterId = e.posterId,
                hostName = e.posterName,
                posterName = e.posterName,
                hostPhone = "",
                posterRole = e.posterRole,
                isPosterAuthorized = e.isPosterAuthorized,
                verificationLevel = e.verificationLevel,
                phoneVerified = e.phoneVerified,
                emailVerified = e.emailVerified,
                identitySubmitted = e.identitySubmitted,
                professionalVerified = e.professionalVerified,
                propertyVisited = e.propertyVisited,
                propertyDocsReviewed = e.propertyDocsReviewed,
                viewsCount = e.viewsCount,
                savesCount = e.savesCount,
                imageUrls = images.map { it.imageUrl }.takeIf { it.isNotEmpty() },
                createdAt = e.createdAt,
                updatedAt = e.updatedAt
            )
        }
    }
}

object UuidHelper {
    private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    fun toValidUuidOrNull(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val clean = raw.trim()
        if (UUID_REGEX.matches(clean)) return clean
        return java.util.UUID.nameUUIDFromBytes(clean.toByteArray(Charsets.UTF_8)).toString()
    }

    fun formatSqlDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) {
            return java.time.LocalDate.now().plusDays(1).toString()
        }
        val clean = dateStr.trim()
        val isoMatch = Regex("^\\d{4}-\\d{2}-\\d{2}").find(clean)
        if (isoMatch != null) return isoMatch.value

        if (clean.contains("tomorrow", ignoreCase = true)) {
            return java.time.LocalDate.now().plusDays(1).toString()
        }
        if (clean.contains("today", ignoreCase = true)) {
            return java.time.LocalDate.now().toString()
        }
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy", java.util.Locale.US)
            java.time.LocalDate.parse(clean, formatter).toString()
        } catch (_: Exception) {
            try {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("M/d/yyyy", java.util.Locale.US)
                java.time.LocalDate.parse(clean, formatter).toString()
            } catch (_: Exception) {
                java.time.LocalDate.now().plusDays(1).toString()
            }
        }
    }

    fun formatSqlTime(timeStr: String?): String {
        if (timeStr.isNullOrBlank()) return "14:00:00"
        val clean = timeStr.trim().uppercase(java.util.Locale.US)
        val isoTime = Regex("^(\\d{2}):(\\d{2})(:(\\d{2}))?").find(clean)
        if (isoTime != null) {
            val h = isoTime.groupValues[1]
            val m = isoTime.groupValues[2]
            val s = isoTime.groupValues[4].ifBlank { "00" }
            return "$h:$m:$s"
        }
        val match = Regex("(\\d{1,2})(:(\\d{2}))?\\s*(AM|PM)?").find(clean)
        if (match != null) {
            var hour = match.groupValues[1].toIntOrNull() ?: 14
            val min = match.groupValues[3].toIntOrNull() ?: 0
            val ampm = match.groupValues[4]
            if (ampm == "PM" && hour < 12) hour += 12
            if (ampm == "AM" && hour == 12) hour = 0
            return String.format(java.util.Locale.US, "%02d:%02d:00", hour, min)
        }
        return "14:00:00"
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseViewingDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String? = null,
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "renter_id") val renterId: String? = null,
    @Json(name = "landlord_id") val landlordId: String? = null,
    @Json(name = "tenant_id") val tenantId: String? = null,
    @Json(name = "poster_id") val posterId: String? = null,
    @Json(name = "scheduled_date") val scheduledDate: String? = null,
    @Json(name = "scheduled_time") val scheduledTime: String? = null,
    @Json(name = "preferred_date") val preferredDate: String? = null,
    @Json(name = "preferred_time") val preferredTime: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "notes") val notes: String? = null,
    @Json(name = "tenant_message") val tenantMessage: String? = null,
    @Json(name = "poster_response") val posterResponse: String? = null,
    @Json(name = "recipient_email") val recipientEmail: String? = null,
    @Json(name = "number_of_visitors") val numberOfVisitors: Int? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    fun toEntity(): ViewingRequestEntity = ViewingRequestEntity(
        id = id?.ifBlank { null } ?: "view_${System.currentTimeMillis()}",
        propertyId = propertyId ?: "",
        tenantId = userId ?: renterId ?: tenantId ?: "usr_tenant_1",
        posterId = landlordId ?: posterId ?: "usr_landlord_1",
        preferredDate = scheduledDate ?: preferredDate ?: "Upcoming",
        preferredTime = scheduledTime ?: preferredTime ?: "Flexible",
        numberOfVisitors = numberOfVisitors ?: 1,
        tenantMessage = notes ?: tenantMessage ?: "",
        posterResponse = posterResponse ?: "",
        status = when (status?.lowercase()) {
            "pending", "requested" -> "REQUESTED"
            "confirmed", "accepted" -> "CONFIRMED"
            "rescheduled" -> "RESCHEDULED"
            "cancelled", "declined" -> "CANCELLED"
            "completed" -> "COMPLETED"
            else -> status?.uppercase() ?: "REQUESTED"
        },
        createdAt = TimestampParser.toEpochMillis(createdAt),
        updatedAt = TimestampParser.toEpochMillis(updatedAt)
    )

    fun toEntityOrNull(): ViewingRequestEntity? {
        val pid = propertyId?.trim() ?: return null
        if (pid.isBlank()) return null
        return toEntity().copy(propertyId = pid)
    }

    fun toInquiryDto(): SupabaseInquiryDto = SupabaseInquiryDto(
        id = id,
        propertyId = propertyId ?: "",
        tenantId = userId ?: renterId ?: tenantId ?: "usr_tenant_1",
        tenantName = "Tenant",
        tenantPhone = null,
        tenantEmail = null,
        inquiryType = "viewing_request",
        preferredDate = scheduledDate ?: preferredDate,
        preferredTime = scheduledTime ?: preferredTime,
        message = notes ?: tenantMessage ?: "",
        status = when (status?.lowercase()) {
            "confirmed", "accepted" -> "accepted"
            "cancelled", "declined" -> "cancelled"
            "rescheduled" -> "rescheduled"
            else -> "pending"
        },
        hostResponse = posterResponse,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(
            e: ViewingRequestEntity,
            recipientEmail: String? = null,
            landlordId: String? = null
        ): SupabaseViewingDto {
            val normalizedId = UuidHelper.toValidUuidOrNull(e.id) ?: java.util.UUID.randomUUID().toString()
            val normalizedPropertyId = UuidHelper.toValidUuidOrNull(e.propertyId)
            val normalizedUserId = UuidHelper.toValidUuidOrNull(e.tenantId)
            val normalizedLandlordId = UuidHelper.toValidUuidOrNull(landlordId ?: e.posterId)

            val sqlDate = UuidHelper.formatSqlDate(e.preferredDate)
            val sqlTime = UuidHelper.formatSqlTime(e.preferredTime)

            val sqlStatus = when (e.status.uppercase()) {
                "CONFIRMED" -> "confirmed"
                "CANCELLED" -> "cancelled"
                "RESCHEDULED" -> "rescheduled"
                "COMPLETED" -> "completed"
                else -> "pending"
            }

            val sqlNotes = e.tenantMessage.ifBlank { e.posterResponse }.ifBlank { "Viewing request submitted" }

            return SupabaseViewingDto(
                id = normalizedId,
                propertyId = normalizedPropertyId,
                userId = normalizedUserId,
                renterId = normalizedUserId,
                landlordId = normalizedLandlordId,
                tenantId = e.tenantId,
                posterId = landlordId ?: e.posterId,
                scheduledDate = sqlDate,
                scheduledTime = sqlTime,
                preferredDate = e.preferredDate,
                preferredTime = e.preferredTime,
                status = sqlStatus,
                notes = sqlNotes,
                tenantMessage = e.tenantMessage,
                posterResponse = e.posterResponse,
                recipientEmail = if (recipientEmail.isNullOrBlank()) null else recipientEmail.trim(),
                numberOfVisitors = e.numberOfVisitors,
                createdAt = TimestampParser.toEpochMillis(e.createdAt),
                updatedAt = TimestampParser.toEpochMillis(e.updatedAt)
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseMessageDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "conversation_id") val conversationId: String? = null,
    @Json(name = "sender_id") val senderId: String? = null,
    @Json(name = "receiver_id") val receiverId: String? = null,
    @Json(name = "property_id") val propertyId: String? = null,
    @Json(name = "sender_name") val senderName: String? = null,
    @Json(name = "message_text") val messageText: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "content") val content: String? = null,
    @Json(name = "timestamp") val timestamp: Any? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "is_read") val isRead: Boolean? = null
) {
    fun toEntity(): MessageEntity {
        val text = content ?: messageText ?: message ?: ""
        val s = senderId ?: ""
        val r = receiverId ?: ""
        val p = propertyId ?: ""
        val convId = conversationId?.ifBlank { null }
            ?: if (p.isNotBlank() && (s.isNotBlank() || r.isNotBlank())) {
                val userA = minOf(s, r)
                val userB = maxOf(s, r)
                "conv_${p}_${userA}_$userB"
            } else if (s.isNotBlank() && r.isNotBlank()) {
                "conv_${minOf(s, r)}_${maxOf(s, r)}"
            } else {
                "conv_${s.ifBlank { "general" }}"
            }

        return MessageEntity(
            id = id?.ifBlank { null } ?: "msg_${System.currentTimeMillis()}",
            conversationId = convId,
            senderId = s,
            senderName = senderName ?: "User",
            messageText = text,
            timestamp = TimestampParser.toEpochMillis(timestamp ?: createdAt),
            isRead = isRead ?: false
        )
    }

    fun toEntityOrNull(): MessageEntity? {
        val text = (content ?: messageText ?: message)?.trim() ?: return null
        if (text.isBlank()) return null
        return toEntity()
    }

    companion object {
        fun fromEntity(e: MessageEntity): SupabaseMessageDto = SupabaseMessageDto(
            id = e.id,
            conversationId = e.conversationId,
            senderId = e.senderId,
            senderName = e.senderName,
            messageText = e.messageText,
            message = null,
            content = e.messageText,
            timestamp = e.timestamp,
            isRead = e.isRead
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseConversationDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String? = null,
    @Json(name = "property_title") val propertyTitle: String? = null,
    @Json(name = "tenant_id") val tenantId: String? = null,
    @Json(name = "tenant_name") val tenantName: String? = null,
    @Json(name = "poster_id") val posterId: String? = null,
    @Json(name = "poster_name") val posterName: String? = null,
    @Json(name = "last_message_text") val lastMessageText: String? = null,
    @Json(name = "last_message_time") val lastMessageTime: Any? = null,
    @Json(name = "unread_for_tenant") val unreadForTenant: Int? = 0,
    @Json(name = "unread_for_poster") val unreadForPoster: Int? = 0,
    @Json(name = "created_at") val createdAt: Any? = null
) {
    fun toEntity(): ConversationEntity = ConversationEntity(
        id = id?.ifBlank { null } ?: "conv_${System.currentTimeMillis()}",
        propertyId = propertyId ?: "",
        propertyTitle = propertyTitle ?: "Rental Property",
        tenantId = tenantId ?: "usr_tenant_1",
        tenantName = tenantName ?: "Tenant",
        posterId = posterId ?: "usr_landlord_1",
        posterName = posterName ?: "Host",
        lastMessageText = lastMessageText ?: "",
        lastMessageTime = TimestampParser.toEpochMillis(lastMessageTime ?: createdAt),
        unreadForTenant = unreadForTenant ?: 0,
        unreadForPoster = unreadForPoster ?: 0
    )

    companion object {
        fun fromEntity(e: ConversationEntity): SupabaseConversationDto = SupabaseConversationDto(
            id = e.id,
            propertyId = e.propertyId,
            propertyTitle = e.propertyTitle,
            tenantId = e.tenantId,
            tenantName = e.tenantName,
            posterId = e.posterId,
            posterName = e.posterName,
            lastMessageText = e.lastMessageText,
            lastMessageTime = e.lastMessageTime,
            unreadForTenant = e.unreadForTenant,
            unreadForPoster = e.unreadForPoster
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseNotificationDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "property_id") val propertyId: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "category") val category: String? = "SYSTEM",
    @Json(name = "is_read") val isRead: Boolean? = false,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    fun toEntity(): NotificationEntity = NotificationEntity(
        id = id?.ifBlank { null } ?: "notif_${System.currentTimeMillis()}",
        userId = userId ?: "",
        title = title ?: "Notification",
        message = message ?: "",
        category = category ?: "SYSTEM",
        relatedPropertyId = propertyId,
        isRead = isRead ?: false,
        createdAt = TimestampParser.toEpochMillis(createdAt)
    )

    companion object {
        fun fromEntity(e: NotificationEntity): SupabaseNotificationDto = SupabaseNotificationDto(
            id = e.id,
            userId = e.userId,
            propertyId = e.relatedPropertyId,
            title = e.title,
            message = e.message,
            category = e.category,
            isRead = e.isRead,
            createdAt = e.createdAt
        )
    }
}

@JsonClass(generateAdapter = true)
data class EdgeNotificationPayload(
    @Json(name = "action") val action: String = "SEND_NOTIFICATION",
    @Json(name = "type") val type: String = "VIEWING_REQUEST",
    @Json(name = "recipientEmail") val recipientEmail: String,
    @Json(name = "propertyTitle") val propertyTitle: String,
    @Json(name = "tenantName") val tenantName: String = "",
    @Json(name = "date") val date: String = "",
    @Json(name = "time") val time: String = "",
    @Json(name = "message") val message: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "notes") val notes: String = ""
)

@JsonClass(generateAdapter = true)
data class EdgeFunctionResponse(
    @Json(name = "ok") val ok: Boolean = false,
    @Json(name = "error") val error: String? = null
)

// ==========================================
// Supabase GoTrue Authentication DTOs
// ==========================================

@JsonClass(generateAdapter = true)
data class SupabaseSignUpRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "data") val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseSignInRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, Any?>? = null,
    @Json(name = "created_at") val createdAt: Any? = null
) {
    fun toUserEntity(): com.example.data.local.entity.UserEntity {
        val meta = userMetadata ?: emptyMap()
        val fullName = meta["full_name"]?.toString()?.ifBlank { null }
            ?: email?.substringBefore('@')
            ?: "Balay-Inn User"
        val rawRole = meta["role"]?.toString()?.ifBlank { null } ?: "TENANT"
        val role = when (rawRole.uppercase().trim()) {
            "HOST", "LANDLORD", "PROPERTY_OWNER", "OWNER" -> "PROPERTY_OWNER"
            "REPRESENTATIVE", "AUTHORIZED_REPRESENTATIVE", "BROKER" -> "REPRESENTATIVE"
            "ADMIN" -> "ADMIN"
            else -> "TENANT"
        }
        val userPhone = meta["phone"]?.toString()?.ifBlank { null } ?: this.phone ?: ""
        val relationship = meta["relationship_to_property"]?.toString()?.ifBlank { null }
            ?: if (role == "PROPERTY_OWNER") "Property owner" else "Tenant"

        return com.example.data.local.entity.UserEntity(
            id = id,
            fullName = fullName,
            email = email ?: "",
            phone = userPhone,
            role = role,
            relationshipToProperty = relationship,
            isAuthorizedByOwner = true,
            verificationLevel = if (role == "PROPERTY_OWNER") 2 else 1,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = role == "PROPERTY_OWNER",
            professionalVerified = role == "REAL_ESTATE_BROKER"
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseSessionResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: SupabaseAuthUser? = null,
    @Json(name = "id") val rootId: String? = null,
    @Json(name = "email") val rootEmail: String? = null,
    @Json(name = "phone") val rootPhone: String? = null,
    @Json(name = "user_metadata") val rootUserMetadata: Map<String, Any?>? = null,
    @Json(name = "created_at") val rootCreatedAt: Any? = null
) {
    fun resolveUser(): SupabaseAuthUser? {
        if (user != null) return user
        val uid = rootId?.trim()?.ifBlank { null }
        if (uid != null) {
            return SupabaseAuthUser(
                id = uid,
                email = rootEmail,
                phone = rootPhone,
                userMetadata = rootUserMetadata,
                createdAt = rootCreatedAt
            )
        }
        return null
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseAuthError(
    @Json(name = "error") val error: String? = null,
    @Json(name = "error_description") val errorDescription: String? = null,
    @Json(name = "msg") val msg: String? = null,
    @Json(name = "message") val message: String? = null
) {
    fun readableMessage(): String {
        return errorDescription?.ifBlank { null }
            ?: message?.ifBlank { null }
            ?: msg?.ifBlank { null }
            ?: error
            ?: "Authentication failed"
    }
}

// ==========================================
// Extended Backend Schema DTOs (PostgreSQL Alignments)
// ==========================================

object SavedListingsHelper {
    fun parseSavedListingIds(raw: Any?): MutableList<Map<String, String>> {
        val list = mutableListOf<Map<String, String>>()
        if (raw is List<*>) {
            for (item in raw) {
                when (item) {
                    is Map<*, *> -> {
                        val pid = item["property_id"]?.toString() ?: item["id"]?.toString() ?: ""
                        val title = item["title"]?.toString() ?: item["name"]?.toString() ?: "Rental Listing"
                        if (pid.isNotBlank()) {
                            list.add(mapOf("property_id" to pid, "title" to title))
                        }
                    }
                    is String -> {
                        if (item.isNotBlank()) {
                            list.add(mapOf("property_id" to item, "title" to "Rental Listing"))
                        }
                    }
                }
            }
        }
        return list
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseUserProfileDto(
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "id") val id: String? = null,
    @Json(name = "full_name") val fullName: String = "",
    @Json(name = "email") val email: String? = null,
    @Json(name = "phone_no") val phoneNo: String? = null,
    @Json(name = "role") val role: String = "renter",
    @Json(name = "avatar") val avatar: String? = "",
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "privacy_preferences") val privacyPreferences: Map<String, Any?>? = null,
    @Json(name = "log_at") val logAt: String? = null,
    @Json(name = "saved_listing_ids") val savedListingIds: List<Any>? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    val profileId: String get() = userId?.ifBlank { null } ?: id ?: ""
    val name: String get() = fullName
    val phone: String? get() = phoneNo
    val isPhoneVerified: Boolean get() = isVerified
    val isHost: Boolean get() = role.equals("host", ignoreCase = true)

    fun getSavedListings(): List<Map<String, String>> =
        SavedListingsHelper.parseSavedListingIds(savedListingIds)
}

@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String,
    @Json(name = "contact_no") val contactNo: String? = "",
    @Json(name = "email_ad") val emailAd: String,
    @Json(name = "role") val role: String = "renter"
)

@JsonClass(generateAdapter = true)
data class SupabaseInquiryDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "tenant_id") val tenantId: String,
    @Json(name = "tenant_name") val tenantName: String,
    @Json(name = "tenant_phone") val tenantPhone: String? = null,
    @Json(name = "tenant_email") val tenantEmail: String? = null,
    @Json(name = "inquiry_type") val inquiryType: String? = "viewing_request",
    @Json(name = "preferred_date") val preferredDate: String? = null,
    @Json(name = "preferred_time") val preferredTime: String? = null,
    @Json(name = "message") val message: String = "",
    @Json(name = "status") val status: String? = "pending",
    @Json(name = "host_response") val hostResponse: String? = null,
    @Json(name = "created_at") val createdAt: Any? = null
) {
    fun toViewingEntityOrNull(posterIdFallback: String = "usr_landlord_1"): ViewingRequestEntity? {
        val pid = propertyId.trim().ifBlank { return null }
        return ViewingRequestEntity(
            id = id?.ifBlank { null } ?: "view_${System.currentTimeMillis()}",
            propertyId = pid,
            tenantId = tenantId.ifBlank { "usr_tenant_1" },
            posterId = posterIdFallback,
            preferredDate = preferredDate ?: "Upcoming",
            preferredTime = preferredTime ?: "Flexible",
            numberOfVisitors = 1,
            tenantMessage = message,
            posterResponse = hostResponse ?: "",
            status = when (status?.lowercase()) {
                "accepted" -> "CONFIRMED"
                "rescheduled" -> "RESCHEDULED"
                "declined", "cancelled" -> "CANCELLED"
                else -> "REQUESTED"
            },
            createdAt = TimestampParser.toEpochMillis(createdAt),
            updatedAt = TimestampParser.toEpochMillis(createdAt)
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseRoomTypeDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "name") val name: String,
    @Json(name = "bed_count") val bedCount: Int = 1,
    @Json(name = "price_per_month") val pricePerMonth: Double = 0.0,
    @Json(name = "available_beds") val availableBeds: Int = 1,
    @Json(name = "total_beds") val totalBeds: Int = 1,
    @Json(name = "deposit_amount") val depositAmount: Double = 0.0,
    @Json(name = "advance_rent_amount") val advanceRentAmount: Double = 0.0,
    @Json(name = "has_aircon") val hasAircon: Boolean = false,
    @Json(name = "has_private_bath") val hasPrivateBath: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SupabaseListingPhotoDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "photo_url") val photoUrl: String,
    @Json(name = "is_primary") val isPrimary: Boolean = false,
    @Json(name = "caption") val caption: String? = "",
    @Json(name = "order_index") val orderIndex: Int = 0
)

@JsonClass(generateAdapter = true)
data class SupabaseBoardingHouseSpecDto(
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "pricing_model") val pricingModel: String = "Per Person/Bed",
    @Json(name = "monthly_rent") val monthlyRent: Double = 0.0,
    @Json(name = "room_capacity") val roomCapacity: String = "4-bed bunk",
    @Json(name = "total_capacity") val totalCapacity: Int = 4,
    @Json(name = "total_available_beds") val totalAvailableBeds: Int = 2,
    @Json(name = "gender_policy") val genderPolicy: String = "Mixed",
    @Json(name = "bathroom_type") val bathroomType: String = "Shared/Communal",
    @Json(name = "utilities_setup") val utilitiesSetup: List<String>? = null,
    @Json(name = "floor_area_sqm") val floorAreaSqm: Double = 18.0,
    @Json(name = "furnishing") val furnishing: String = "Fully-Furnished",
    @Json(name = "curfew_rule") val curfewRule: String? = null,
    @Json(name = "visitors_rule") val visitorsRule: String? = null,
    @Json(name = "cooking_rule") val cookingRule: String? = null,
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "lease_term_minimum") val leaseTermMinimum: String? = "6 months"
)

@JsonClass(generateAdapter = true)
data class SupabaseApartmentSpecDto(
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "monthly_rent") val monthlyRent: Double = 0.0,
    @Json(name = "total_floor_area_sqm") val totalFloorAreaSqm: Double = 35.0,
    @Json(name = "bedrooms") val bedrooms: Int = 1,
    @Json(name = "bathrooms") val bathrooms: Int = 1,
    @Json(name = "lease_term_minimum") val leaseTermMinimum: String = "1 year",
    @Json(name = "furnishing") val furnishing: String = "Semi-Furnished",
    @Json(name = "kitchen_type") val kitchenType: String = "Indoor/Integral",
    @Json(name = "utility_billing") val utilityBilling: String = "Own Meter",
    @Json(name = "parking") val parking: String? = null,
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "curfew_rule") val curfewRule: String? = null,
    @Json(name = "amenities") val amenities: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseCondominiumSpecDto(
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "monthly_rent") val monthlyRent: Double = 0.0,
    @Json(name = "association_dues_status") val associationDuesStatus: String = "Inclusive in rent",
    @Json(name = "association_dues_amount") val associationDuesAmount: Double = 0.0,
    @Json(name = "unit_layout") val unitLayout: String = "Studio",
    @Json(name = "total_floor_area_sqm") val totalFloorAreaSqm: Double = 28.0,
    @Json(name = "floor_level") val floorLevel: String = "8th Floor",
    @Json(name = "parking") val parking: String? = null,
    @Json(name = "amenities") val amenities: List<String>? = null,
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "furnishing") val furnishing: String = "Fully-Furnished",
    @Json(name = "lease_term_minimum") val leaseTermMinimum: String = "1 year",
    @Json(name = "visitors_rule") val visitorsRule: String? = null,
    @Json(name = "kitchen_type") val kitchenType: String? = "Equipped Kitchenette"
)

@JsonClass(generateAdapter = true)
data class SupabaseStudioSpecDto(
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "monthly_rent") val monthlyRent: Double = 0.0,
    @Json(name = "total_floor_area_sqm") val totalFloorAreaSqm: Double = 22.0,
    @Json(name = "is_open_plan") val isOpenPlan: Boolean = true,
    @Json(name = "kitchenette_type") val kitchenetteType: String = "Built-in Induction & Exhaust",
    @Json(name = "bathroom_type") val bathroomType: String = "Private Bathroom with Water Heater",
    @Json(name = "submetering_water") val submeteringWater: Boolean = true,
    @Json(name = "submetering_electric") val submeteringElectric: Boolean = true,
    @Json(name = "max_occupancy") val maxOccupancy: Int = 2,
    @Json(name = "balcony_available") val balconyAvailable: Boolean = false,
    @Json(name = "furnishing") val furnishing: String = "Fully-Furnished",
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "lease_term_minimum") val leaseTermMinimum: String = "6 months"
)

@JsonClass(generateAdapter = true)
data class SupabaseTownhouseSpecDto(
    @Json(name = "property_id") val propertyId: String,
    @Json(name = "monthly_rent") val monthlyRent: Double = 0.0,
    @Json(name = "lot_area_sqm") val lotAreaSqm: Double = 120.0,
    @Json(name = "floor_area_sqm") val floorAreaSqm: Double = 85.0,
    @Json(name = "stories") val stories: Int = 2,
    @Json(name = "bedrooms") val bedrooms: Int = 3,
    @Json(name = "bathrooms") val bathrooms: Int = 2,
    @Json(name = "garage_capacity") val garageCapacity: Int = 2,
    @Json(name = "has_maids_room") val hasMaidsRoom: Boolean = false,
    @Json(name = "is_gated_subdivision") val isGatedSubdivision: Boolean = true,
    @Json(name = "subdivision_name") val subdivisionName: String? = "",
    @Json(name = "furnishing") val furnishing: String = "Semi-Furnished",
    @Json(name = "pet_policy") val petPolicy: String = "Pet-Friendly",
    @Json(name = "association_dues_status") val associationDuesStatus: String = "Inclusive in rent",
    @Json(name = "lease_term_minimum") val leaseTermMinimum: String = "1 year",
    @Json(name = "kitchen_type") val kitchenType: String = "Indoor Kitchen + Dirty Kitchen"
)



