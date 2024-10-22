package com.humberto.tasky.core.domain.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun LocalDate.atTimeToUtc(localTime: LocalTime): ZonedDateTime {
    val zonedDateTime = this.atTime(localTime).atZone(ZoneOffset.systemDefault())
    return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
}

fun LocalDate.toStartOfDayUtc(): ZonedDateTime {
    return this.atStartOfDay(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
}

fun LocalDate.toEndOfDayUtc(): ZonedDateTime {
    return this.atTime(LocalTime.MAX)
        .atZone(ZoneOffset.systemDefault())
        .withZoneSameInstant(ZoneOffset.UTC)
}
