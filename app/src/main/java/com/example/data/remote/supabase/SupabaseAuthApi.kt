package com.example.data.remote.supabase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SupabaseAuthApi {

    @POST("signup")
    suspend fun signUp(@Body request: SupabaseSignUpRequest): Response<SupabaseSessionResponse>

    @POST("token?grant_type=password")
    suspend fun signInWithPassword(@Body request: SupabaseSignInRequest): Response<SupabaseSessionResponse>

    @POST("logout")
    suspend fun logout(): Response<Unit>

    @GET("user")
    suspend fun getCurrentUser(): Response<SupabaseAuthUser>
}
