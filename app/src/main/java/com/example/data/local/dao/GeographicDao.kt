package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.CountryEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.RegionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GeographicDao {
    @Query("SELECT * FROM countries WHERE is_active = 1 ORDER BY name ASC")
    fun getActiveCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM regions WHERE country_id = :countryId AND is_active = 1 ORDER BY name ASC")
    fun getRegionsByCountry(countryId: String): Flow<List<RegionEntity>>

    @Query("SELECT * FROM provinces WHERE region_id = :regionId AND is_active = 1 ORDER BY name ASC")
    fun getProvincesByRegion(regionId: String): Flow<List<ProvinceEntity>>

    @Query("SELECT * FROM provinces WHERE is_active = 1 ORDER BY name ASC")
    fun getAllActiveProvinces(): Flow<List<ProvinceEntity>>

    @Query("SELECT * FROM cities_municipalities WHERE province_id = :provinceId AND is_active = 1 ORDER BY name ASC")
    fun getCitiesByProvince(provinceId: String): Flow<List<CityMunicipalityEntity>>

    @Query("SELECT * FROM cities_municipalities WHERE is_active = 1 ORDER BY name ASC")
    fun getAllActiveCities(): Flow<List<CityMunicipalityEntity>>

    @Query("SELECT * FROM barangays WHERE city_municipality_id = :cityMunicipalityId AND is_active = 1 ORDER BY name ASC")
    fun getBarangaysByCity(cityMunicipalityId: String): Flow<List<BarangayEntity>>

    @Query("SELECT * FROM barangays WHERE is_active = 1 ORDER BY name ASC")
    fun getAllActiveBarangays(): Flow<List<BarangayEntity>>

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountryCount(): Int

    @Query("SELECT COUNT(*) FROM cities_municipalities WHERE province_id = 'prov_bohol'")
    suspend fun getBoholMunicipalitiesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountries(countries: List<CountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(regions: List<RegionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvinces(provinces: List<ProvinceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCities(cities: List<CityMunicipalityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarangays(barangays: List<BarangayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvince(province: ProvinceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityMunicipalityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarangay(barangay: BarangayEntity)

    @Update
    suspend fun updateProvince(province: ProvinceEntity)

    @Update
    suspend fun updateCity(city: CityMunicipalityEntity)

    @Update
    suspend fun updateBarangay(barangay: BarangayEntity)
}
