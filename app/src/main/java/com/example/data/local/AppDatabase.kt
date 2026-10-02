package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CommunicationDao
import com.example.data.local.dao.GeographicDao
import com.example.data.local.dao.PlatformDao
import com.example.data.local.dao.PropertyDao
import com.example.data.local.dao.UserDao
import com.example.data.local.dao.ViewingDao
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.BarangayEntity
import com.example.data.local.entity.CityMunicipalityEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.CountryEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PropertyEntity
import com.example.data.local.entity.PropertyImageEntity
import com.example.data.local.entity.ProvinceEntity
import com.example.data.local.entity.RegionEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedPropertyEntity
import com.example.data.local.entity.SavedSearchEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.data.local.entity.ViewingRequestEntity

@Database(
    entities = [
        CountryEntity::class,
        RegionEntity::class,
        ProvinceEntity::class,
        CityMunicipalityEntity::class,
        BarangayEntity::class,
        UserEntity::class,
        PropertyEntity::class,
        PropertyImageEntity::class,
        ViewingRequestEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        TenantRequirementEntity::class,
        SavedPropertyEntity::class,
        SavedSearchEntity::class,
        NotificationEntity::class,
        ReportEntity::class,
        VerificationDocumentEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun geographicDao(): GeographicDao
    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao
    abstract fun viewingDao(): ViewingDao
    abstract fun communicationDao(): CommunicationDao
    abstract fun platformDao(): PlatformDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "balay_inn_marketplace.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
