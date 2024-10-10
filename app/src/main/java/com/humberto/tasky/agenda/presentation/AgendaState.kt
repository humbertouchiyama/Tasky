package com.humberto.tasky.agenda.presentation

import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import java.time.LocalDate

data class AgendaState(
    val selectedDateIsToday: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val upperCaseMonth: String = LocalDate.now().displayUpperCaseMonth(),
)
