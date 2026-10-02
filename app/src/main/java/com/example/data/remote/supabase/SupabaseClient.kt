package com.example.data.remote.supabase

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    val url: String get() = try {
        BuildConfig.SUPABASE_URL.trimEnd('/')
    } catch (e: Throwable) {
        ""
    }

    val anonKey: String get() = try {
        BuildConfig.SUPABASE_ANON_KEY.trim()
    } catch (e: Throwable) {
        ""
    }

    val isConfigured: Boolean
        get() = url.isNotBlank() &&
            !url.contains("your-project.supabase.co") &&
            anonKey.isNotBlank() &&
            anonKey != "your-supabase-anon-key-here"

    @Volatile
    var currentAccessToken: String? = null
        private set

    @Volatile
    var currentRefreshToken: String? = null
        private set

    @Volatile
    var currentAuthUser: SupabaseAuthUser? = null
        private set

    val isAuthenticated: Boolean
        get() = !currentAccessToken.isNullOrBlank()

    fun saveSession(accessToken: String?, refreshToken: String?, user: SupabaseAuthUser? = null) {
        currentAccessToken = accessToken
        currentRefreshToken = refreshToken
        if (user != null) {
            currentAuthUser = user
        }
    }

    fun clearSession() {
        currentAccessToken = null
        currentRefreshToken = null
        currentAuthUser = null
    }

    fun parseAuthError(errorBodyString: String?): String {
        if (errorBodyString.isNullOrBlank()) return "Authentication failed"
        return try {
            val adapter = moshi.adapter(SupabaseAuthError::class.java)
            adapter.fromJson(errorBodyString)?.readableMessage() ?: errorBodyString
        } catch (_: Throwable) {
            errorBodyString
        }
    }

    private val moshi: Moshi by lazy {
        val longAdapter = FlexibleLongAdapter()
        val doubleAdapter = FlexibleDoubleAdapter()
        val intAdapter = FlexibleIntAdapter()

        Moshi.Builder()
            .add(FlexibleDateJsonAdapter())
            .add(Long::class.java, longAdapter)
            .add(Long::class.javaObjectType, longAdapter)
            .add(Double::class.java, doubleAdapter)
            .add(Double::class.javaObjectType, doubleAdapter)
            .add(Int::class.java, intAdapter)
            .add(Int::class.javaObjectType, intAdapter)
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = currentAccessToken ?: anonKey
            val requestBuilder = original.newBuilder()
                .header("apikey", anonKey)
                .header("Authorization", "Bearer $token")
                .header("Content-Type", "application/json")
            chain.proceed(requestBuilder.build())
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val api: SupabaseApi? by lazy {
        if (!isConfigured) {
            null
        } else {
            val restBaseUrl = if (url.endsWith("/rest/v1")) "$url/" else "$url/rest/v1/"
            try {
                Retrofit.Builder()
                    .baseUrl(restBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseApi::class.java)
            } catch (e: Throwable) {
                null
            }
        }
    }

    val authApi: SupabaseAuthApi? by lazy {
        if (!isConfigured) {
            null
        } else {
            val authBaseUrl = if (url.endsWith("/auth/v1")) "$url/" else "$url/auth/v1/"
            try {
                Retrofit.Builder()
                    .baseUrl(authBaseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(SupabaseAuthApi::class.java)
            } catch (e: Throwable) {
                null
            }
        }
    }
}
