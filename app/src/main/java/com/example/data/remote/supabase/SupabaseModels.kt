package com.example.data.remote.supabase

import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.ViewingRequestEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabasePropertyDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "property_type") val propertyType: String? = null,
    @Json(name = "monthly_rent") val monthlyRent: Double? = null,
    @Json(name = "country_id") val countryId: String? = null,
    @Json(name = "region_id") val regionId: String? = null,
    @Json(name = "province_id") val provinceId: String? = null,
    @Json(name = "city_municipality_id") val cityMunicipalityId: String? = null,
    @Json(name = "barangay_id") val barangayId: String? = null,
    @Json(name = "neighborhood") val neighborhood: String? = null,
    @Json(name = "nearby_landmark") val nearbyLandmark: String? = null,
    @Json(name = "street_address_private") val streetAddressPrivate: String? = null,
    @Json(name = "location_privacy") val locationPrivacy: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "bedrooms") val bedrooms: Int? = null,
    @Json(name = "bathrooms") val bathrooms: Int? = null,
    @Json(name = "floor_area_sqm") val floorAreaSqm: Double? = null,
    @Json(name = "furnished_status") val furnishedStatus: String? = null,
    @Json(name = "parking_available") val parkingAvailable: Boolean? = null,
    @Json(name = "pet_policy") val petPolicy: String? = null,
    @Json(name = "availability_date") val availabilityDate: String? = null,
    @Json(name = "lease_duration_months") val leaseDurationMonths: Int? = null,
    @Json(name = "advance_rent_months") val advanceRentMonths: Int? = null,
    @Json(name = "security_deposit_months") val securityDepositMonths: Int? = null,
    @Json(name = "association_dues") val associationDues: Double? = null,
    @Json(name = "utilities_included") val utilitiesIncluded: Boolean? = null,
    @Json(name = "internet_included") val internetIncluded: Boolean? = null,
    @Json(name = "maintenance_fees") val maintenanceFees: Double? = null,
    @Json(name = "parking_fees") val parkingFees: Double? = null,
    @Json(name = "other_fees") val otherFees: Double? = null,
    @Json(name = "amenities_json") val amenitiesJson: String? = null,
    @Json(name = "contact_preferences") val contactPreferences: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "owner_id") val ownerId: String? = null,
    @Json(name = "poster_id") val posterId: String? = null,
    @Json(name = "poster_name") val posterName: String? = null,
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
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    fun toEntity(): PropertyEntity = PropertyEntity(
        id = id?.ifBlank { null } ?: "prop_${System.currentTimeMillis()}",
        title = title ?: "Untitled Property",
        description = description ?: "",
        propertyType = propertyType ?: "Condominium",
        monthlyRent = monthlyRent ?: 0.0,
        countryId = countryId ?: "ph",
        regionId = regionId ?: "reg_07",
        provinceId = provinceId ?: "prov_cebu",
        cityMunicipalityId = cityMunicipalityId ?: "city_cebu",
        barangayId = barangayId ?: "bgy_lahug",
        neighborhood = neighborhood ?: "",
        nearbyLandmark = nearbyLandmark ?: "",
        streetAddressPrivate = streetAddressPrivate ?: "",
        locationPrivacy = locationPrivacy ?: "APPROXIMATE",
        latitude = latitude ?: 10.3157,
        longitude = longitude ?: 123.8854,
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
        contactPreferences = contactPreferences ?: "In-App Messaging Only",
        status = status ?: "ACTIVE",
        ownerId = ownerId ?: "user_landlord_01",
        posterId = posterId ?: "user_landlord_01",
        posterName = posterName ?: "Balay-Inn Host",
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

    fun toEntityOrNull(): PropertyEntity? {
        val propId = id?.trim()?.ifBlank { null } ?: return null
        return toEntity().copy(id = propId)
    }

    companion object {
        fun fromEntity(e: PropertyEntity): SupabasePropertyDto = SupabasePropertyDto(
            id = e.id,
            title = e.title,
            description = e.description,
            propertyType = e.propertyType,
            monthlyRent = e.monthlyRent,
            countryId = e.countryId,
            regionId = e.regionId,
            provinceId = e.provinceId,
            cityMunicipalityId = e.cityMunicipalityId,
            barangayId = e.barangayId,
            neighborhood = e.neighborhood,
            nearbyLandmark = e.nearbyLandmark,
            streetAddressPrivate = e.streetAddressPrivate,
            locationPrivacy = e.locationPrivacy,
            latitude = e.latitude,
            longitude = e.longitude,
            bedrooms = e.bedrooms,
            bathrooms = e.bathrooms,
            floorAreaSqm = e.floorAreaSqm,
            furnishedStatus = e.furnishedStatus,
            parkingAvailable = e.parkingAvailable,
            petPolicy = e.petPolicy,
            availabilityDate = e.availabilityDate,
            leaseDurationMonths = e.leaseDurationMonths,
            advanceRentMonths = e.advanceRentMonths,
            securityDepositMonths = e.securityDepositMonths,
            associationDues = e.associationDues,
            utilitiesIncluded = e.utilitiesIncluded,
            internetIncluded = e.internetIncluded,
            maintenanceFees = e.maintenanceFees,
            parkingFees = e.parkingFees,
            otherFees = e.otherFees,
            amenitiesJson = e.amenitiesJson,
            contactPreferences = e.contactPreferences,
            status = e.status,
            ownerId = e.ownerId,
            posterId = e.posterId,
            posterName = e.posterName,
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
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseViewingDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "property_id") val propertyId: String? = null,
    @Json(name = "tenant_id") val tenantId: String? = null,
    @Json(name = "poster_id") val posterId: String? = null,
    @Json(name = "preferred_date") val preferredDate: String? = null,
    @Json(name = "preferred_time") val preferredTime: String? = null,
    @Json(name = "number_of_visitors") val numberOfVisitors: Int? = null,
    @Json(name = "tenant_message") val tenantMessage: String? = null,
    @Json(name = "poster_response") val posterResponse: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "updated_at") val updatedAt: Any? = null
) {
    fun toEntity(): ViewingRequestEntity = ViewingRequestEntity(
        id = id?.ifBlank { null } ?: "view_${System.currentTimeMillis()}",
        propertyId = propertyId ?: "",
        tenantId = tenantId ?: "usr_tenant_1",
        posterId = posterId ?: "usr_landlord_1",
        preferredDate = preferredDate ?: "Upcoming",
        preferredTime = preferredTime ?: "Flexible",
        numberOfVisitors = numberOfVisitors ?: 1,
        tenantMessage = tenantMessage ?: "",
        posterResponse = posterResponse ?: "",
        status = status ?: "REQUESTED",
        createdAt = TimestampParser.toEpochMillis(createdAt),
        updatedAt = TimestampParser.toEpochMillis(updatedAt)
    )

    fun toEntityOrNull(): ViewingRequestEntity? {
        val pid = propertyId?.trim() ?: return null
        if (pid.isBlank()) return null
        return ViewingRequestEntity(
            id = id?.ifBlank { null } ?: "view_${System.currentTimeMillis()}",
            propertyId = pid,
            tenantId = tenantId?.ifBlank { "usr_tenant_1" } ?: "usr_tenant_1",
            posterId = posterId?.ifBlank { "usr_landlord_1" } ?: "usr_landlord_1",
            preferredDate = preferredDate?.ifBlank { "Upcoming" } ?: "Upcoming",
            preferredTime = preferredTime?.ifBlank { "Flexible" } ?: "Flexible",
            numberOfVisitors = numberOfVisitors ?: 1,
            tenantMessage = tenantMessage ?: "",
            posterResponse = posterResponse ?: "",
            status = status?.ifBlank { "REQUESTED" } ?: "REQUESTED",
            createdAt = TimestampParser.toEpochMillis(createdAt),
            updatedAt = TimestampParser.toEpochMillis(updatedAt)
        )
    }

    companion object {
        fun fromEntity(e: ViewingRequestEntity): SupabaseViewingDto = SupabaseViewingDto(
            id = e.id,
            propertyId = e.propertyId,
            tenantId = e.tenantId,
            posterId = e.posterId,
            preferredDate = e.preferredDate,
            preferredTime = e.preferredTime,
            numberOfVisitors = e.numberOfVisitors,
            tenantMessage = e.tenantMessage,
            posterResponse = e.posterResponse,
            status = e.status,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}

@JsonClass(generateAdapter = true)
data class SupabaseMessageDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "conversation_id") val conversationId: String? = null,
    @Json(name = "sender_id") val senderId: String? = null,
    @Json(name = "sender_name") val senderName: String? = null,
    @Json(name = "message_text") val messageText: String? = null,
    @Json(name = "timestamp") val timestamp: Any? = null,
    @Json(name = "created_at") val createdAt: Any? = null,
    @Json(name = "is_read") val isRead: Boolean? = null
) {
    fun toEntity(): MessageEntity = MessageEntity(
        id = id?.ifBlank { null } ?: "msg_${System.currentTimeMillis()}",
        conversationId = conversationId ?: "",
        senderId = senderId ?: "",
        senderName = senderName ?: "User",
        messageText = messageText ?: "",
        timestamp = TimestampParser.toEpochMillis(timestamp ?: createdAt),
        isRead = isRead ?: false
    )

    fun toEntityOrNull(): MessageEntity? {
        val cid = conversationId?.trim() ?: return null
        if (cid.isBlank()) return null
        return MessageEntity(
            id = id?.ifBlank { null } ?: "msg_${System.currentTimeMillis()}",
            conversationId = cid,
            senderId = senderId ?: "",
            senderName = senderName ?: "User",
            messageText = messageText ?: "",
            timestamp = TimestampParser.toEpochMillis(timestamp ?: createdAt),
            isRead = isRead ?: false
        )
    }

    companion object {
        fun fromEntity(e: MessageEntity): SupabaseMessageDto = SupabaseMessageDto(
            id = e.id,
            conversationId = e.conversationId,
            senderId = e.senderId,
            senderName = e.senderName,
            messageText = e.messageText,
            timestamp = e.timestamp,
            isRead = e.isRead
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
        val role = meta["role"]?.toString()?.ifBlank { null } ?: "TENANT"
        val userPhone = meta["phone"]?.toString()?.ifBlank { null } ?: this.phone ?: ""
        val relationship = meta["relationship_to_property"]?.toString()?.ifBlank { null } ?: "Property owner"

        return com.example.data.local.entity.UserEntity(
            id = id,
            fullName = fullName,
            email = email ?: "",
            phone = userPhone,
            role = role,
            relationshipToProperty = relationship,
            isAuthorizedByOwner = true,
            verificationLevel = 1,
            phoneVerified = true,
            emailVerified = true,
            identitySubmitted = false,
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
    @Json(name = "user") val user: SupabaseAuthUser? = null
)

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


