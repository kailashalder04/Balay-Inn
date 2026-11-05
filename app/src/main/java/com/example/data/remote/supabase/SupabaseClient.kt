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

    const val DEFAULT_SUPABASE_URL = "https://eqmciquwocwyutlxgdje.supabase.co"
    const val DEFAULT_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVxbWNpcXV3b2N3eXV0bHhnZGplIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA2NDE1ODksImV4cCI6MjEwNjIxNzU4OX0.YSSGxEkXXbQcBmwnERjo2_OY_ZQx9seDvzIzAPKVTsI"
    const val DEFAULT_PUBLISHABLE_KEY = "sb_publishable_bMskPNyml5LD8idQoOiozg_LffpG8ib"

    val url: String get() = try {
        val configured = BuildConfig.SUPABASE_URL.trimEnd('/')
        if (configured.isNotBlank() && !configured.contains("your-project.supabase.co")) {
            configured
        } else {
            DEFAULT_SUPABASE_URL
        }
    } catch (e: Throwable) {
        DEFAULT_SUPABASE_URL
    }

    val anonKey: String get() = try {
        val configured = BuildConfig.SUPABASE_ANON_KEY.trim()
        if (configured.isNotBlank() && configured != "your-supabase-anon-key-here") {
            configured
        } else {
            DEFAULT_SUPABASE_ANON_KEY
        }
    } catch (e: Throwable) {
        DEFAULT_SUPABASE_ANON_KEY
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
        currentAccessToken = accessToken?.takeIf { it.isNotBlank() }
        currentRefreshToken = refreshToken?.takeIf { it.isNotBlank() }
        if (user != null) {
            currentAuthUser = user
        }
    }

    fun clearSession() {
        currentAccessToken = null
        currentRefreshToken = null
        currentAuthUser = null
    }

    fun parseAuthError(errorBodyString: String?, isSignUp: Boolean = false): String {
        val raw = if (errorBodyString.isNullOrBlank()) {
            null
        } else {
            try {
                val adapter = moshi.adapter(SupabaseAuthError::class.java)
                adapter.fromJson(errorBodyString)?.readableMessage() ?: errorBodyString
            } catch (_: Throwable) {
                errorBodyString
            }
        }
        return toUserFriendlyErrorMessage(raw, isSignUp = isSignUp)
    }

    fun toUserFriendlyErrorMessage(rawError: String?, isSignUp: Boolean = false): String {
        if (rawError.isNullOrBlank()) {
            return if (isSignUp) "Could not create account. Please check your details and try again."
            else "Could not sign in. Please check your credentials and try again."
        }

        val lower = rawError.lowercase()

        return when {
            lower.contains("already registered") || lower.contains("user already exists") || lower.contains("email address already in use") || lower.contains("unique constraint") ->
                "An account with this email already exists. Please sign in instead."

            lower.contains("invalid login credentials") || lower.contains("invalid_grant") || lower.contains("invalid credentials") || lower.contains("wrong password") ->
                "Incorrect email or password. Please verify your details and try again."

            lower.contains("email not confirmed") || lower.contains("unconfirmed") ->
                if (isSignUp) "Account created! Welcome to Balay-Inn."
                else "Email confirmation is not required. You can sign in directly."

            lower.contains("password should be at least") || lower.contains("password is too short") || lower.contains("weak password") || lower.contains("at least 6") ->
                "Password must be at least 6 characters long."

            lower.contains("invalid email") || lower.contains("valid email") || (lower.contains("format") && lower.contains("email")) ->
                "Please enter a valid email address (e.g. name@example.com)."

            lower.contains("rate limit") || lower.contains("too many requests") || lower.contains("over quota") ->
                "Too many attempts. Please wait a moment before trying again."

            lower.contains("unable to resolve host") || lower.contains("failed to connect") || lower.contains("timeout") || lower.contains("network") || lower.contains("connection refused") ->
                "Unable to connect to the server. Please check your internet connection."

            lower.contains("missing") && lower.contains("email") ->
                "Please enter your email address."

            lower.contains("missing") && lower.contains("password") ->
                "Please enter your password."

            else -> if (isSignUp) {
                "Could not complete account creation. Please check your information and try again."
            } else {
                "Could not sign in. Please check your email and password, then try again."
            }
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
            val token = currentAccessToken?.takeIf { it.isNotBlank() } ?: anonKey
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
