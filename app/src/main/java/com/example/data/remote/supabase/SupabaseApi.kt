package com.example.data.remote.supabase

import retrofit2.Response
import retrofit2.http.Body
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
        @Body updates: Map<String, Any?>
    ): Response<List<SupabasePropertyDto>>

    // Viewing Requests Endpoints
    @GET("viewing_requests?select=*&order=created_at.desc")
    suspend fun getViewingRequests(): Response<List<SupabaseViewingDto>>

    @Headers("Prefer: return=representation")
    @POST("viewing_requests")
    suspend fun createViewingRequest(@Body request: SupabaseViewingDto): Response<List<SupabaseViewingDto>>

    @Headers("Prefer: return=representation")
    @PATCH("viewing_requests")
    suspend fun updateViewingRequest(
        @Query("id") idFilter: String,
        @Body updates: Map<String, Any?>
    ): Response<List<SupabaseViewingDto>>

    // Messages Endpoints
    @GET("messages?select=*&order=timestamp.asc")
    suspend fun getMessages(): Response<List<SupabaseMessageDto>>

    @Headers("Prefer: return=representation")
    @POST("messages")
    suspend fun sendMessage(@Body message: SupabaseMessageDto): Response<List<SupabaseMessageDto>>

    // Supabase Edge Function: notify-gmail
    @POST("../functions/v1/notify-gmail")
    suspend fun invokeGmailNotificationFunction(
        @Body payload: EdgeNotificationPayload
    ): Response<EdgeFunctionResponse>
}
