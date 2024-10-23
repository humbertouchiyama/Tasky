package com.humberto.tasky.core.database.mapper

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Long.toZonedDateTime(zoneId: String): ZonedDateTime {
    val instant = Instant.ofEpochMilli(this)
    return ZonedDateTime.ofInstant(instant, ZoneId.of(zoneId))
}