package com.example.data.remote.supabase

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimestampParser {
    fun parse(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty() || trimmed.equals("null", ignoreCase = true)) return null

        // 1. Direct epoch number (millis or seconds)
        trimmed.toLongOrNull()?.let { num ->
            return if (num in 1_000_000_000L..99_999_999_999L) num * 1000L else num
        }
        trimmed.toDoubleOrNull()?.let { num ->
            val l = num.toLong()
            return if (l in 1_000_000_000L..99_999_999_999L) l * 1000L else l
        }

        // 2. java.time (Android API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                return java.time.OffsetDateTime.parse(trimmed).toInstant().toEpochMilli()
            } catch (_: Throwable) {}
            try {
                return java.time.Instant.parse(trimmed).toEpochMilli()
            } catch (_: Throwable) {}
            try {
                return java.time.LocalDateTime.parse(trimmed).toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
            } catch (_: Throwable) {}
            try {
                return java.time.LocalDate.parse(trimmed).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()
            } catch (_: Throwable) {}
        }

        // 3. Fallback SimpleDateFormat with fractional-seconds normalization (API < 26)
        val normalized = if (trimmed.contains('.')) {
            trimmed.replaceFirst("""\.(\d{3})\d+""".toRegex(), ".$1")
        } else {
            trimmed
        }

        val patterns = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val sdf = java.text.SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val d = sdf.parse(normalized)
                if (d != null) return d.time
            } catch (_: Throwable) {}
        }

        return null
    }

    fun toEpochMillis(raw: Any?): Long {
        if (raw == null) return System.currentTimeMillis()
        if (raw is Number) {
            val num = raw.toLong()
            return if (num in 1_000_000_000L..99_999_999_999L) num * 1000L else num
        }
        if (raw is String) {
            return parse(raw) ?: System.currentTimeMillis()
        }
        return System.currentTimeMillis()
    }

    fun formatIso(epochMillis: Long): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.time.Instant.ofEpochMilli(epochMillis).toString()
            } else {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.format(Date(epochMillis))
            }
        } catch (_: Throwable) {
            epochMillis.toString()
        }
    }
}

class FlexibleLongAdapter : JsonAdapter<Long>() {
    override fun fromJson(reader: JsonReader): Long? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> reader.nextNull()
            JsonReader.Token.NUMBER -> {
                try {
                    reader.nextLong()
                } catch (_: Throwable) {
                    reader.nextDouble().toLong()
                }
            }
            JsonReader.Token.STRING -> {
                val str = reader.nextString()
                TimestampParser.parse(str)
            }
            JsonReader.Token.BOOLEAN -> {
                reader.nextBoolean()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Long?) {
        if (value == null) {
            writer.nullValue()
        } else if (value > 946684800000L) { // If timestamp after year 2000, serialize as ISO-8601 for Supabase Postgres
            writer.value(TimestampParser.formatIso(value))
        } else {
            writer.value(value)
        }
    }
}

class FlexibleDateJsonAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): Long? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> reader.nextNull()
            JsonReader.Token.NUMBER -> {
                try {
                    reader.nextLong()
                } catch (_: Throwable) {
                    reader.nextDouble().toLong()
                }
            }
            JsonReader.Token.STRING -> {
                val str = reader.nextString()
                TimestampParser.parse(str)
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: Long?) {
        if (value == null) {
            writer.nullValue()
        } else if (value > 946684800000L) {
            writer.value(TimestampParser.formatIso(value))
        } else {
            writer.value(value)
        }
    }
}

class FlexibleDoubleAdapter : JsonAdapter<Double>() {
    override fun fromJson(reader: JsonReader): Double? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> reader.nextNull()
            JsonReader.Token.NUMBER -> reader.nextDouble()
            JsonReader.Token.STRING -> {
                val str = reader.nextString().trim()
                str.toDoubleOrNull()
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Double?) {
        if (value == null) writer.nullValue() else writer.value(value)
    }
}

class FlexibleIntAdapter : JsonAdapter<Int>() {
    override fun fromJson(reader: JsonReader): Int? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> reader.nextNull()
            JsonReader.Token.NUMBER -> reader.nextInt()
            JsonReader.Token.STRING -> {
                val str = reader.nextString().trim()
                str.toIntOrNull()
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    override fun toJson(writer: JsonWriter, value: Int?) {
        if (value == null) writer.nullValue() else writer.value(value)
    }
}
