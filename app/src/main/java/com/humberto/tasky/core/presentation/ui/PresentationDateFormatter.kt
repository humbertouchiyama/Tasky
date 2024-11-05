package com.humberto.tasky.core.presentation.ui

import com.humberto.tasky.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.displayUpperCaseMonth(): String {
    return this.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
}

fun LocalDate.toFormattedDate(): String {
    return this.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
}

fun ZonedDateTime.toFormattedDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("dd MMMM yyyy")
        .format(timeInLocalTime)
}

fun LocalDate.buildHeaderDate(): UiText {
    val today = LocalDate.now()
    return if (this == today) {
        UiText.StringResource(R.string.today)
    } else {
        UiText.DynamicString(this.toFormattedDate())
    }
}