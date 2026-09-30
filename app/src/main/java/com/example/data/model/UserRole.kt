package com.example.data.model

enum class UserRole {
    TENANT,
    LANDLORD,
    REPRESENTATIVE,
    ADMIN;

    val displayName: String
        get() = when (this) {
            TENANT -> "Tenant"
            LANDLORD -> "Landlord / Owner"
            REPRESENTATIVE -> "Authorized Representative"
            ADMIN -> "Platform Admin"
        }

    val roleBadge: String
        get() = when (this) {
            TENANT -> "Tenant"
            LANDLORD -> "Property Owner"
            REPRESENTATIVE -> "Authorized Rep"
            ADMIN -> "Compliance Admin"
        }

    val subtitle: String
        get() = when (this) {
            TENANT -> "Rental discovery & tenant tools"
            LANDLORD -> "Property business & listing management"
            REPRESENTATIVE -> "Assigned property administration"
            ADMIN -> "Platform operations & moderation"
        }

    val isLandlord: Boolean get() = this == LANDLORD
    val isRepresentative: Boolean get() = this == REPRESENTATIVE
    val isTenant: Boolean get() = this == TENANT
    val isAdmin: Boolean get() = this == ADMIN
    val canPostProperty: Boolean get() = this == LANDLORD || this == REPRESENTATIVE || this == ADMIN

    companion object {
        fun fromRoleString(role: String?): UserRole {
            if (role == null) return TENANT
            return when (role.uppercase().trim()) {
                "TENANT" -> TENANT
                "LANDLORD", "PROPERTY_OWNER", "OWNER" -> LANDLORD
                "REPRESENTATIVE", "AUTHORIZED_REPRESENTATIVE", "FAMILY_MEMBER",
                "CARETAKER", "PROPERTY_MANAGER", "REAL_ESTATE_BROKER",
                "REAL_ESTATE_SALESPERSON", "BROKER" -> REPRESENTATIVE
                "ADMIN", "STAFF", "VERIFICATION_STAFF" -> ADMIN
                else -> TENANT
            }
        }
    }
}
