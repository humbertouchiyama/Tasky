package com.humberto.tasky.core.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


fun LocalDate.toStartOfDayUtc(): ZonedDateTime {
    return this.atStartOfDay(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
}

fun LocalDate.toEndOfDayUtc(): ZonedDateTime {
    return this.atTime(LocalTime.MAX)
        .atZone(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
}

fun Long.toZonedDateTime(zoneId: String): ZonedDateTime {
    val instant = Instant.ofEpochMilli(this)
    return ZonedDateTime.ofInstant(instant, ZoneId.of(zoneId))
}

fun LocalDate.toFormatted(): String {
    val dateInLocalZone = this
        .atStartOfDay(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd yyyy")
        .format(dateInLocalZone)
}

fun LocalTime.toFormatted(): String {
    return DateTimeFormatter
        .ofPattern("HH:mm")
        .format(this)
}