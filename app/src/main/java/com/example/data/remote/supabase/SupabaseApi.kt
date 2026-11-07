package com.example.data.remote.supabase

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // Properties Endpoints
    @GET("properties?select=*&order=created_at.desc")
    suspend fun getProperties(): Response<List<SupabasePropertyDto>>

    @Headers("Prefer: return=representation")
    @POST("properties")
    suspend fun createProperty(@Body property: SupabasePropertyDto): Response<List<SupabasePropertyDto>>

    @Headers("Prefer: return=representation")
    @PATCH("properties")
    suspend fun updateProperty(
        @Query("id") idFilter: String, // e.g. "eq.prop_123"
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<List<SupabasePropertyDto>>

    // Viewing Requests Endpoints
    @GET("viewing_requests?select=*&order=created_at.desc")
    suspend fun getViewingRequests(): Response<List<SupabaseViewingDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("viewing_requests?on_conflict=id")
    suspend fun upsertViewingRequest(@Body request: SupabaseViewingDto): Response<List<SupabaseViewingDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("viewing_requests?on_conflict=id")
    suspend fun upsertViewingRequestRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @POST("viewing_requests")
    suspend fun createViewingRequest(@Body request: SupabaseViewingDto): Response<List<SupabaseViewingDto>>

    @Headers("Prefer: return=representation")
    @POST("viewing_requests")
    suspend fun postViewingRequestRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @PATCH("viewing_requests")
    suspend fun updateViewingRequest(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<List<SupabaseViewingDto>>

    // Conversations Endpoints
    @GET("conversations?select=*&order=last_message_time.desc")
    suspend fun getConversations(): Response<List<SupabaseConversationDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("conversations?on_conflict=id")
    suspend fun upsertConversation(@Body conversation: SupabaseConversationDto): Response<List<SupabaseConversationDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("conversations?on_conflict=id")
    suspend fun upsertConversationRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @POST("conversations")
    suspend fun postConversationRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    // Messages Endpoints
    @GET("messages?select=*&order=created_at.asc")
    suspend fun getMessages(): Response<List<SupabaseMessageDto>>

    @GET("messages?select=*")
    suspend fun getAllMessages(): Response<List<SupabaseMessageDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("messages?on_conflict=id")
    suspend fun upsertMessage(@Body message: SupabaseMessageDto): Response<List<SupabaseMessageDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("messages?on_conflict=id")
    suspend fun upsertMessageRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @POST("messages")
    suspend fun sendMessage(@Body message: SupabaseMessageDto): Response<List<SupabaseMessageDto>>

    @Headers("Prefer: return=representation")
    @POST("messages")
    suspend fun postMessageRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    // Notifications Endpoints
    @GET("notifications?select=*&order=created_at.desc")
    suspend fun getNotifications(): Response<List<SupabaseNotificationDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("notifications?on_conflict=id")
    suspend fun upsertNotification(@Body notification: SupabaseNotificationDto): Response<List<SupabaseNotificationDto>>

    // Inquiries Endpoints (Supabase Schema Compatible)
    @GET("inquiries?select=*&order=created_at.desc")
    suspend fun getInquiries(): Response<List<SupabaseInquiryDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("inquiries?on_conflict=id")
    suspend fun upsertInquiry(@Body inquiry: SupabaseInquiryDto): Response<List<SupabaseInquiryDto>>

    @Headers("Prefer: return=representation")
    @POST("inquiries")
    suspend fun createInquiry(@Body inquiry: SupabaseInquiryDto): Response<List<SupabaseInquiryDto>>

    // User Profiles Endpoints
    @GET("user_profiles?select=*")
    suspend fun getUserProfile(@Query("id") idFilter: String): Response<List<SupabaseUserProfileDto>>

    @GET("user_profiles?select=*")
    suspend fun getUserProfileByUserId(@Query("user_id") userFilter: String): Response<List<SupabaseUserProfileDto>>

    @GET("user_profiles?select=*")
    suspend fun getUserProfilesByEmail(@Query("email") emailFilter: String): Response<List<SupabaseUserProfileDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("user_profiles?on_conflict=user_id")
    suspend fun upsertUserProfileByUserId(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @POST("user_profiles")
    suspend fun postUserProfileRaw(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("user_profiles?on_conflict=id")
    suspend fun upsertUserProfile(@Body profile: SupabaseUserProfileDto): Response<List<SupabaseUserProfileDto>>

    @Headers("Prefer: return=representation")
    @PATCH("user_profiles")
    suspend fun patchUserProfile(
        @Query("id") idFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ResponseBody>

    @Headers("Prefer: return=representation")
    @PATCH("user_profiles")
    suspend fun patchUserProfileByUserId(
        @Query("user_id") userFilter: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any?>
    ): Response<ResponseBody>

    // Saved Listings Endpoints
    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("saved_listings?on_conflict=id")
    suspend fun postSavedListing(@Body payload: Map<String, @JvmSuppressWildcards Any?>): Response<ResponseBody>

    @DELETE("saved_listings")
    suspend fun deleteSavedListing(
        @Query("user_id") userFilter: String,
        @Query("property_id") propFilter: String
    ): Response<ResponseBody>

    // Primary Users Endpoints
    @GET("users?select=*")
    suspend fun getUsers(@Query("email_ad") emailFilter: String): Response<List<SupabaseUserDto>>

    @Headers("Prefer: resolution=merge-duplicates,return=representation")
    @POST("users?on_conflict=email_ad")
    suspend fun upsertUser(@Body user: SupabaseUserDto): Response<List<SupabaseUserDto>>

    // Room Types Endpoints
    @GET("room_types?select=*")
    suspend fun getRoomTypes(@Query("property_id") propertyIdFilter: String): Response<List<SupabaseRoomTypeDto>>

    // Supabase Edge Function: notify-gmail
    @POST("../functions/v1/notify-gmail")
    suspend fun invokeGmailNotificationFunction(
        @Body payload: EdgeNotificationPayload
    ): Response<EdgeFunctionResponse>
}

