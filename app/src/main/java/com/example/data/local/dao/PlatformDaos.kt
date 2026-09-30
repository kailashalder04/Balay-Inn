package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.ConversationEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ReportEntity
import com.example.data.local.entity.SavedSearchEntity
import com.example.data.local.entity.TenantRequirementEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.VerificationDocumentEntity
import com.example.data.local.entity.ViewingRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserByIdSync(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface ViewingDao {
    @Query("SELECT * FROM viewing_requests WHERE tenant_id = :tenantId ORDER BY created_at DESC")
    fun getViewingsForTenant(tenantId: String): Flow<List<ViewingRequestEntity>>

    @Query("SELECT * FROM viewing_requests WHERE poster_id = :posterId ORDER BY created_at DESC")
    fun getViewingsForPoster(posterId: String): Flow<List<ViewingRequestEntity>>

    @Query("SELECT * FROM viewing_requests WHERE property_id IN (SELECT id FROM properties WHERE owner_id = :ownerId) ORDER BY created_at DESC")
    fun getViewingsForOwner(ownerId: String): Flow<List<ViewingRequestEntity>>

    @Query("SELECT * FROM viewing_requests ORDER BY created_at DESC")
    fun getAllViewings(): Flow<List<ViewingRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViewingRequest(request: ViewingRequestEntity)

    @Update
    suspend fun updateViewingRequest(request: ViewingRequestEntity)
}

@Dao
interface CommunicationDao {
    @Query("SELECT * FROM conversations WHERE tenant_id = :userId OR poster_id = :userId ORDER BY last_message_time DESC")
    fun getConversationsForUser(userId: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    fun getConversationById(conversationId: String): Flow<ConversationEntity?>

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)
}

@Dao
interface PlatformDao {
    // Notifications
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)

    // Saved Searches
    @Query("SELECT * FROM saved_searches WHERE user_id = :userId ORDER BY created_at DESC")
    fun getSavedSearches(userId: String): Flow<List<SavedSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedSearch(search: SavedSearchEntity)

    @Query("DELETE FROM saved_searches WHERE id = :id")
    suspend fun deleteSavedSearch(id: String)

    // Tenant Requirements
    @Query("SELECT * FROM tenant_requirements WHERE user_id = :userId ORDER BY created_at DESC")
    fun getRequirementsForUser(userId: String): Flow<List<TenantRequirementEntity>>

    @Query("SELECT * FROM tenant_requirements ORDER BY created_at DESC")
    fun getAllRequirements(): Flow<List<TenantRequirementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(req: TenantRequirementEntity)

    @Query("DELETE FROM tenant_requirements WHERE id = :id")
    suspend fun deleteRequirement(id: String)

    // Reports & Moderation
    @Query("SELECT * FROM reports ORDER BY created_at DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Update
    suspend fun updateReport(report: ReportEntity)

    // Verification Documents
    @Query("SELECT * FROM verification_documents WHERE user_id = :userId ORDER BY submitted_at DESC")
    fun getDocumentsForUser(userId: String): Flow<List<VerificationDocumentEntity>>

    @Query("SELECT * FROM verification_documents ORDER BY submitted_at DESC")
    fun getAllDocuments(): Flow<List<VerificationDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: VerificationDocumentEntity)

    @Update
    suspend fun updateDocument(doc: VerificationDocumentEntity)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
