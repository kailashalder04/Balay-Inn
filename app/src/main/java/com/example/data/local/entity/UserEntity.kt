package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phone"])
    ]
)
data class UserEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    val email: String,
    val phone: String,
    val role: String, // TENANT, PROPERTY_OWNER, FAMILY_MEMBER, CARETAKER, PROPERTY_MANAGER, REAL_ESTATE_BROKER, REAL_ESTATE_SALESPERSON, ADMIN, VERIFICATION_STAFF
    @ColumnInfo(name = "relationship_to_property") val relationshipToProperty: String = "Property owner",
    @ColumnInfo(name = "is_authorized_by_owner") val isAuthorizedByOwner: Boolean = true,
    @ColumnInfo(name = "preferred_language") val preferredLanguage: String = "English",
    @ColumnInfo(name = "verification_level") val verificationLevel: Int = 1,
    @ColumnInfo(name = "phone_verified") val phoneVerified: Boolean = true,
    @ColumnInfo(name = "email_verified") val emailVerified: Boolean = true,
    @ColumnInfo(name = "identity_submitted") val identitySubmitted: Boolean = false,
    @ColumnInfo(name = "professional_verified") val professionalVerified: Boolean = false,
    @ColumnInfo(name = "prc_number") val prcNumber: String = "",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    val canonicalRole: com.example.data.model.UserRole
        get() = com.example.data.model.UserRole.fromRoleString(role)

    val isGuest: Boolean
        get() = id == "usr_guest" || role.equals("GUEST", ignoreCase = true)

    companion object {
        val GUEST_USER = UserEntity(
            id = "usr_guest",
            fullName = "Guest Explorer",
            email = "guest@balayinn.app",
            phone = "",
            role = "GUEST",
            relationshipToProperty = "Guest Explorer",
            isAuthorizedByOwner = false,
            verificationLevel = 0,
            phoneVerified = false,
            emailVerified = false,
            identitySubmitted = false,
            professionalVerified = false
        )
    }
}
