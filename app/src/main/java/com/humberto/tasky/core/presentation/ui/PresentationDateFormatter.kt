package com.humberto.tasky.core.presentation.ui

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.displayUpperCaseMonth(): String {
    return this.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
}