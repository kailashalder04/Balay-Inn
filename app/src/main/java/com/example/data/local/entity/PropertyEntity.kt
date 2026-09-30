package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "properties",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["id"],
            childColumns = ["country_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ProvinceEntity::class,
            parentColumns = ["id"],
            childColumns = ["province_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CityMunicipalityEntity::class,
            parentColumns = ["id"],
            childColumns = ["city_municipality_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BarangayEntity::class,
            parentColumns = ["id"],
            childColumns = ["barangay_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["country_id"]),
        Index(value = ["region_id"]),
        Index(value = ["province_id"]),
        Index(value = ["city_municipality_id"]),
        Index(value = ["barangay_id"]),
        Index(value = ["monthly_rent"]),
        Index(value = ["property_type"]),
        Index(value = ["status"])
    ]
)
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    @ColumnInfo(name = "property_type") val propertyType: String,
    @ColumnInfo(name = "monthly_rent") val monthlyRent: Double,

    // Hierarchical Location Foreign Keys
    @ColumnInfo(name = "country_id") val countryId: String,
    @ColumnInfo(name = "region_id") val regionId: String,
    @ColumnInfo(name = "province_id") val provinceId: String,
    @ColumnInfo(name = "city_municipality_id") val cityMunicipalityId: String,
    @ColumnInfo(name = "barangay_id") val barangayId: String,

    val neighborhood: String = "",
    @ColumnInfo(name = "nearby_landmark") val nearbyLandmark: String = "",
    @ColumnInfo(name = "street_address_private") val streetAddressPrivate: String = "",
    @ColumnInfo(name = "location_privacy") val locationPrivacy: String = "APPROXIMATE", // EXACT, APPROXIMATE, HIDDEN

    val latitude: Double,
    val longitude: Double,

    val bedrooms: Int,
    val bathrooms: Int,
    @ColumnInfo(name = "floor_area_sqm") val floorAreaSqm: Double,
    @ColumnInfo(name = "furnished_status") val furnishedStatus: String, // Furnished, Semi-Furnished, Unfurnished
    @ColumnInfo(name = "parking_available") val parkingAvailable: Boolean,
    @ColumnInfo(name = "pet_policy") val petPolicy: String, // Pets Allowed, Small Pets Only, No Pets
    @ColumnInfo(name = "availability_date") val availabilityDate: String,
    @ColumnInfo(name = "lease_duration_months") val leaseDurationMonths: Int = 12,
    @ColumnInfo(name = "advance_rent_months") val advanceRentMonths: Int = 1,
    @ColumnInfo(name = "security_deposit_months") val securityDepositMonths: Int = 2,

    // Additional Recurring & Upfront Fees (Cost Transparency)
    @ColumnInfo(name = "association_dues") val associationDues: Double = 0.0,
    @ColumnInfo(name = "utilities_included") val utilitiesIncluded: Boolean = false,
    @ColumnInfo(name = "internet_included") val internetIncluded: Boolean = false,
    @ColumnInfo(name = "maintenance_fees") val maintenanceFees: Double = 0.0,
    @ColumnInfo(name = "parking_fees") val parkingFees: Double = 0.0,
    @ColumnInfo(name = "other_fees") val otherFees: Double = 0.0,

    @ColumnInfo(name = "amenities_json") val amenitiesJson: String = "[]",
    @ColumnInfo(name = "contact_preferences") val contactPreferences: String = "In-App Messaging Only",
    val status: String = "ACTIVE", // ACTIVE, PAUSED, RENTED, UNDER_REVIEW

    // Poster & Ownership Relation (Rule: Poster != Owner is supported)
    @ColumnInfo(name = "owner_id") val ownerId: String,
    @ColumnInfo(name = "poster_id") val posterId: String,
    @ColumnInfo(name = "poster_name") val posterName: String,
    @ColumnInfo(name = "poster_role") val posterRole: String, // Property owner, Family member, Caretaker, Property manager, Licensed broker, etc.
    @ColumnInfo(name = "is_poster_authorized") val isPosterAuthorized: Boolean = true,

    // Verification Badges Level 0-8
    @ColumnInfo(name = "verification_level") val verificationLevel: Int = 2,
    @ColumnInfo(name = "phone_verified") val phoneVerified: Boolean = true,
    @ColumnInfo(name = "email_verified") val emailVerified: Boolean = true,
    @ColumnInfo(name = "identity_submitted") val identitySubmitted: Boolean = false,
    @ColumnInfo(name = "professional_verified") val professionalVerified: Boolean = false,
    @ColumnInfo(name = "property_visited") val propertyVisited: Boolean = false,
    @ColumnInfo(name = "property_docs_reviewed") val propertyDocsReviewed: Boolean = false,

    @ColumnInfo(name = "views_count") val viewsCount: Int = 0,
    @ColumnInfo(name = "saves_count") val savesCount: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "property_images",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["property_id"])]
)
data class PropertyImageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "property_id") val propertyId: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "is_primary") val isPrimary: Boolean = false,
    val caption: String = "",
    @ColumnInfo(name = "order_index") val orderIndex: Int = 0
)
