package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.PropertyImageEntity
import com.example.data.local.entity.SavedPropertyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties WHERE status = 'ACTIVE' ORDER BY created_at DESC")
    fun getAllActiveProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties ORDER BY created_at DESC")
    fun getAllProperties(): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE owner_id = :ownerId ORDER BY created_at DESC")
    fun getPropertiesByOwner(ownerId: String): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE id = :propertyId")
    fun getPropertyById(propertyId: String): Flow<PropertyEntity?>

    @Query("SELECT * FROM properties WHERE id = :propertyId")
    suspend fun getPropertyByIdSync(propertyId: String): PropertyEntity?

    @Query("SELECT * FROM properties WHERE poster_id = :posterId ORDER BY created_at DESC")
    fun getPropertiesByPoster(posterId: String): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE province_id = :provinceId AND status = 'ACTIVE' ORDER BY created_at DESC")
    fun getPropertiesByProvince(provinceId: String): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE city_municipality_id = :cityId AND status = 'ACTIVE' ORDER BY created_at DESC")
    fun getPropertiesByCity(cityId: String): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties WHERE barangay_id = :barangayId AND status = 'ACTIVE' ORDER BY created_at DESC")
    fun getPropertiesByBarangay(barangayId: String): Flow<List<PropertyEntity>>

    @Query("SELECT COUNT(*) FROM properties")
    suspend fun getPropertyCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<PropertyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Update
    suspend fun updateProperty(property: PropertyEntity)

    @Query("DELETE FROM properties WHERE id = :propertyId")
    suspend fun deleteProperty(propertyId: String)

    @Query("DELETE FROM properties WHERE id NOT IN (:validIds)")
    suspend fun deletePropertiesNotIn(validIds: List<String>)

    @Query("DELETE FROM property_images WHERE property_id NOT IN (:validIds)")
    suspend fun deletePropertyImagesNotIn(validIds: List<String>)

    // Images
    @Query("SELECT * FROM property_images WHERE property_id = :propertyId ORDER BY order_index ASC")
    fun getImagesForProperty(propertyId: String): Flow<List<PropertyImageEntity>>

    @Query("SELECT * FROM property_images ORDER BY order_index ASC")
    fun getAllPropertyImages(): Flow<List<PropertyImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<PropertyImageEntity>)

    // Saved Properties (Favorites)
    @Query("SELECT * FROM saved_properties WHERE user_id = :userId ORDER BY saved_at DESC")
    fun getSavedProperties(userId: String): Flow<List<SavedPropertyEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_properties WHERE user_id = :userId AND property_id = :propertyId)")
    fun isPropertySaved(userId: String, propertyId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedProperty(saved: SavedPropertyEntity)

    @Query("DELETE FROM saved_properties WHERE user_id = :userId AND property_id = :propertyId")
    suspend fun deleteSavedProperty(userId: String, propertyId: String)
}
