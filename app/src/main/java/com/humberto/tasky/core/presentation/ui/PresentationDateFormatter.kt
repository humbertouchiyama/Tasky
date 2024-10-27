package com.humberto.tasky.core.presentation.ui

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.displayUpperCaseMonth(): String {
    return this.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
}

fun ZonedDateTime.toFormattedUppercaseDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("dd MMMM yyyy")
        .format(timeInLocalTime)
        .uppercase()
}