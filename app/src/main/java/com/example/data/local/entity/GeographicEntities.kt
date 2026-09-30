package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "countries",
    indices = [Index(value = ["code"], unique = true)]
)
data class CountryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(
    tableName = "regions",
    foreignKeys = [
        ForeignKey(
            entity = CountryEntity::class,
            parentColumns = ["id"],
            childColumns = ["country_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["country_id"]),
        Index(value = ["code"], unique = true)
    ]
)
data class RegionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "country_id") val countryId: String,
    val name: String,
    val code: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(
    tableName = "provinces",
    foreignKeys = [
        ForeignKey(
            entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["region_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["region_id"]),
        Index(value = ["code"], unique = true)
    ]
)
data class ProvinceEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "region_id") val regionId: String,
    val name: String,
    val code: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(
    tableName = "cities_municipalities",
    foreignKeys = [
        ForeignKey(
            entity = ProvinceEntity::class,
            parentColumns = ["id"],
            childColumns = ["province_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["province_id"]),
        Index(value = ["code"], unique = true)
    ]
)
data class CityMunicipalityEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "province_id") val provinceId: String,
    val name: String,
    val type: String, // "City" or "Municipality"
    val code: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(
    tableName = "barangays",
    foreignKeys = [
        ForeignKey(
            entity = CityMunicipalityEntity::class,
            parentColumns = ["id"],
            childColumns = ["city_municipality_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["city_municipality_id"]),
        Index(value = ["code"], unique = true)
    ]
)
data class BarangayEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "city_municipality_id") val cityMunicipalityId: String,
    val name: String,
    val code: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)
