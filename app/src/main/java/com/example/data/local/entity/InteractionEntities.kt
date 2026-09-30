package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "viewing_requests",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["property_id"]),
        Index(value = ["tenant_id"]),
        Index(value = ["status"])
    ]
)
data class ViewingRequestEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "property_id") val propertyId: String,
    @ColumnInfo(name = "tenant_id") val tenantId: String,
    @ColumnInfo(name = "poster_id") val posterId: String,
    @ColumnInfo(name = "preferred_date") val preferredDate: String,
    @ColumnInfo(name = "preferred_time") val preferredTime: String,
    @ColumnInfo(name = "number_of_visitors") val numberOfVisitors: Int = 1,
    @ColumnInfo(name = "tenant_message") val tenantMessage: String = "",
    @ColumnInfo(name = "poster_response") val posterResponse: String = "",
    val status: String = "REQUESTED", // REQUESTED, CONFIRMED, RESCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "conversations",
    indices = [
        Index(value = ["property_id"]),
        Index(value = ["tenant_id"]),
        Index(value = ["poster_id"])
    ]
)
data class ConversationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "property_id") val propertyId: String,
    @ColumnInfo(name = "property_title") val propertyTitle: String,
    @ColumnInfo(name = "tenant_id") val tenantId: String,
    @ColumnInfo(name = "tenant_name") val tenantName: String,
    @ColumnInfo(name = "poster_id") val posterId: String,
    @ColumnInfo(name = "poster_name") val posterName: String,
    @ColumnInfo(name = "last_message_text") val lastMessageText: String,
    @ColumnInfo(name = "last_message_time") val lastMessageTime: Long,
    @ColumnInfo(name = "unread_for_tenant") val unreadForTenant: Int = 0,
    @ColumnInfo(name = "unread_for_poster") val unreadForPoster: Int = 0
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["conversation_id"])]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "sender_name") val senderName: String,
    @ColumnInfo(name = "message_text") val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_read") val isRead: Boolean = false
)
