package com.humberto.tasky.core.database

import androidx.room.TypeConverter
import com.humberto.tasky.core.database.entity.LocalAttendee
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromAttendeeList(attendees: List<LocalAttendee>?): String? {
        return attendees?.let { json.encodeToString(it) }
    }

    @TypeConverter
    fun toAttendeeList(data: String?): List<LocalAttendee>? {
        return data?.let { json.decodeFromString(it) }
    }

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}