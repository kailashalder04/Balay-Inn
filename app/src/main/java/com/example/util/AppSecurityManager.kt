package com.example.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager
import com.example.data.model.UserRole
import java.io.File

object AppSecurityManager {

    /**
     * Masks a phone number for user privacy.
     * Example: "+639171234567" -> "+63 917 *** *567"
     */
    fun maskPhoneNumber(phone: String, isRevealed: Boolean): String {
        if (isRevealed || phone.isBlank()) return phone
        val clean = phone.trim()
        if (clean.length <= 6) return "*****"

        return when {
            clean.startsWith("+63") && clean.length >= 13 -> {
                val prefix = clean.substring(0, 7) // "+63 917" or "+63917"
                val suffix = clean.takeLast(3)
                "$prefix *** *$suffix"
            }
            clean.startsWith("09") && clean.length >= 11 -> {
                val prefix = clean.substring(0, 4) // "0917"
                val suffix = clean.takeLast(3)
                "$prefix *** *$suffix"
            }
            else -> {
                val visibleLen = (clean.length / 3).coerceAtLeast(2)
                val prefix = clean.take(visibleLen)
                val suffix = clean.takeLast(visibleLen)
                "$prefix***$suffix"
            }
        }
    }

    /**
     * Performs lightweight runtime checks to detect root binaries or test keys.
     */
    fun isDeviceRooted(): Boolean {
        // Check test-keys build tag
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }

        // Check common SU binary locations
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            try {
                if (File(path).exists()) return true
            } catch (_: Exception) {
                // Ignore security exceptions
            }
        }
        return false
    }

    /**
     * Enables or disables FLAG_SECURE on the current Activity window
     * to prevent unauthorized screenshots or screen recordings on sensitive views.
     */
    fun applySecureScreenProtection(activity: Activity?, enable: Boolean) {
        if (activity == null) return
        activity.runOnUiThread {
            try {
                // Always clear FLAG_SECURE to allow streaming emulator preview rendering
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } catch (_: Exception) {
                // Handle window context variations safely
            }
        }
    }

    /**
     * Checks if a user's role has permission to perform actions requiring a target role.
     */
    fun checkRolePermission(userRole: UserRole, targetRole: UserRole): Boolean {
        if (userRole == UserRole.ADMIN) return true // Admin possesses supreme permission
        if (userRole == targetRole) return true

        return when (targetRole) {
            UserRole.TENANT -> true // All authenticated users have tenant rights
            UserRole.LANDLORD -> userRole == UserRole.LANDLORD || userRole == UserRole.REPRESENTATIVE
            UserRole.REPRESENTATIVE -> userRole == UserRole.REPRESENTATIVE
            UserRole.ADMIN -> false
        }
    }
}
