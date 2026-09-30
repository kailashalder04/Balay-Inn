package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tenant_requirements",
    indices = [Index(value = ["user_id"])]
)
data class TenantRequirementEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "profile_name") val profileName: String, // e.g. "Work Studio near IT Park", "Family House in Panglao"
    @ColumnInfo(name = "preferred_province_id") val preferredProvinceId: String = "",
    @ColumnInfo(name = "preferred_city_id") val preferredCityId: String = "",
    @ColumnInfo(name = "preferred_location_name") val preferredLocationName: String = "",
    @ColumnInfo(name = "min_budget") val minBudget: Double = 0.0,
    @ColumnInfo(name = "max_budget") val maxBudget: Double = 25000.0,
    @ColumnInfo(name = "property_types_json") val propertyTypesJson: String = "[\"Apartment\",\"Studio\",\"Condominium\"]",
    @ColumnInfo(name = "min_bedrooms") val minBedrooms: Int = 1,
    @ColumnInfo(name = "min_bathrooms") val minBathrooms: Int = 1,
    @ColumnInfo(name = "furnished_required") val furnishedRequired: Boolean = true,
    @ColumnInfo(name = "parking_required") val parkingRequired: Boolean = false,
    @ColumnInfo(name = "pet_friendly_required") val petFriendlyRequired: Boolean = false,
    @ColumnInfo(name = "move_in_date") val moveInDate: String = "",
    val notes: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "saved_properties",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id", "property_id"], unique = true),
        Index(value = ["property_id"])
    ]
)
data class SavedPropertyEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "property_id") val propertyId: String,
    @ColumnInfo(name = "saved_at") val savedAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(
    tableName = "saved_searches",
    indices = [Index(value = ["user_id"])]
)
data class SavedSearchEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "search_name") val searchName: String,
    @ColumnInfo(name = "query_json") val queryJson: String,
    val frequency: String = "Daily", // Instant, Daily, Weekly, Off
    @ColumnInfo(name = "matches_count") val matchesCount: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["is_read"])
    ]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val title: String,
    val message: String,
    val category: String, // MATCH, PRICE_DROP, VIEWING, MESSAGE, VERIFICATION, SYSTEM
    @ColumnInfo(name = "related_property_id") val relatedPropertyId: String? = null,
    @ColumnInfo(name = "is_read") val isRead: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reports",
    indices = [
        Index(value = ["target_id"]),
        Index(value = ["status"])
    ]
)
data class ReportEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "reporter_id") val reporterId: String,
    @ColumnInfo(name = "target_type") val targetType: String, // PROPERTY or USER
    @ColumnInfo(name = "target_id") val targetId: String,
    val reason: String, // FAKE_PROPERTY, FAKE_OWNER, SUSPICIOUS_PAYMENT, MISLEADING_PHOTOS, WRONG_LOCATION, ALREADY_RENTED, HARASSMENT, OTHER
    val details: String,
    val status: String = "PENDING", // PENDING, REVIEWED, DISMISSED, ACTIONED
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "verification_documents",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["status"])
    ]
)
data class VerificationDocumentEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "property_id") val propertyId: String? = null,
    @ColumnInfo(name = "document_type") val documentType: String, // GOVERNMENT_ID, PRC_ID, AUTHORIZATION_LETTER, TITLE_TAX_DEC, UTILITY_BILL
    val status: String = "SUBMITTED", // SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED
    @ColumnInfo(name = "file_name") val fileName: String,
    @ColumnInfo(name = "is_private_encrypted") val isPrivateEncrypted: Boolean = true,
    @ColumnInfo(name = "reviewer_notes") val reviewerNotes: String = "",
    @ColumnInfo(name = "submitted_at") val submittedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "reviewed_at") val reviewedAt: Long? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "admin_user_id") val adminUserId: String,
    @ColumnInfo(name = "action_type") val actionType: String,
    @ColumnInfo(name = "entity_type") val entityType: String,
    @ColumnInfo(name = "entity_id") val entityId: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
