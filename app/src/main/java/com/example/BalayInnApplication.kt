package com.example

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Custom Application class that equips Coil with a 128 MB disk cache and 25% RAM memory cache
 * for ultra-smooth map tile streaming and high-speed rental property photo rendering.
 */
class BalayInnApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(cacheDir, "image_cache"))
                    .maxSizeBytes(128L * 1024 * 1024)
                    .build()
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val originalRequest = chain.request()
                        val originalUrl = originalRequest.url.toString()
                        // Ensure image CDNs (e.g. Unsplash) deliver universally supported JPEG instead of AVIF/HEIC
                        // which triggers HeifDecoderImpl: getSize: not supported! errors on Android
                        val safeUrl = if (originalUrl.contains("auto=format")) {
                            originalUrl.replace("auto=format", "fm=jpg")
                        } else {
                            originalUrl
                        }
                        val request = originalRequest.newBuilder()
                            .url(safeUrl)
                            .header("User-Agent", "BalayInn/1.0 (Android; Rental Marketplace PH; contact: support@balayinn.ph)")
                            .header("Accept", "image/jpeg,image/png,image/webp;q=0.9,*/*;q=0.8")
                            .build()
                        chain.proceed(request)
                    }
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }
}
