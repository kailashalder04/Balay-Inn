package com.example.data.remote.gmail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class GmailNotificationService(private val context: Context) {

    private val httpClient = OkHttpClient()

    /**
     * Formats an RFC 2822 / RFC 822 MIME message and encodes in Base64URL.
     */
    private fun createBase64UrlEmail(
        to: String,
        from: String,
        subject: String,
        bodyText: String
    ): String {
        val rawMessage = buildString {
            append("To: $to\r\n")
            append("From: $from\r\n")
            append("Subject: =?utf-8?B?${Base64.encodeToString(subject.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)}?=\r\n")
            append("MIME-Version: 1.0\r\n")
            append("Content-Type: text/plain; charset=utf-8\r\n")
            append("Content-Transfer-Encoding: 7bit\r\n\r\n")
            append(bodyText)
        }

        return Base64.encodeToString(
            rawMessage.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }

    /**
     * Sends an email notification via Google Gmail REST API using OAuth token.
     */
    suspend fun sendEmailViaGmailApi(
        oauthToken: String,
        toEmail: String,
        fromEmail: String = "me",
        subject: String,
        bodyText: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val encodedMessage = createBase64UrlEmail(toEmail, fromEmail, subject, bodyText)
            val jsonPayload = JSONObject().apply {
                put("raw", encodedMessage)
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url("https://gmail.googleapis.com/gmail/v1/users/me/messages/send")
                .header("Authorization", "Bearer $oauthToken")
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d("GmailService", "Gmail notification successfully sent: $responseBody")
                Result.success(responseBody)
            } else {
                val errorMsg = "Gmail API Error ${response.code}: ${response.message}"
                Log.w("GmailService", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("GmailService", "Failed to send Gmail notification: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Dispatches notification for a new viewing request.
     */
    fun sendViewingRequestNotification(
        landlordEmail: String,
        tenantName: String,
        propertyTitle: String,
        preferredDate: String,
        preferredTime: String,
        tenantMessage: String,
        oauthToken: String? = null
    ) {
        val subject = "🏠 New Viewing Request: $propertyTitle"
        val body = """
            Hello Host,

            $tenantName has requested an in-person rental viewing for your property:
            Listing: $propertyTitle
            Preferred Date: $preferredDate
            Preferred Time: $preferredTime

            Tenant Note:
            "${tenantMessage.ifBlank { "Looking forward to viewing the property!" }}"

            You can confirm, reschedule, or communicate directly via the Balay-Inn app.

            Best regards,
            Balay-Inn Rental Marketplace
        """.trimIndent()

        // Fallback / Intent trigger to launch Gmail app or email client
        launchEmailIntent(landlordEmail, subject, body)
    }

    /**
     * Dispatches notification when a viewing request status is updated.
     */
    fun sendViewingStatusNotification(
        recipientEmail: String,
        propertyTitle: String,
        newStatus: String,
        date: String,
        time: String,
        notes: String = ""
    ) {
        val subject = "📅 Viewing Status Update ($newStatus): $propertyTitle"
        val body = """
            Hello,

            The scheduled rental viewing for "$propertyTitle" has been updated to: $newStatus.
            Scheduled Date: $date
            Scheduled Time: $time
            ${if (notes.isNotBlank()) "Host Notes: $notes" else ""}

            Check the Balay-Inn app to view updated scheduling details.

            Best regards,
            Balay-Inn Rental Marketplace
        """.trimIndent()

        launchEmailIntent(recipientEmail, subject, body)
    }

    private fun launchEmailIntent(to: String, subject: String, body: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(to.ifBlank { "host@balayinn.ph" }))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.w("GmailService", "No email app found to handle mailto intent: ${e.message}")
        }
    }
}
