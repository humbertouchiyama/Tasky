package com.humberto.tasky.agenda.presentation.agenda_list

import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import java.time.LocalDate

data class AgendaState(
    val initials: String = "",
    val selectedDateIsToday: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val upperCaseMonth: String = LocalDate.now().displayUpperCaseMonth(),
    val isLoggingOut: Boolean = false,
    val agendaItems: List<AgendaItemUi> = listOf(),
    val isLoadingAgendaItems: Boolean = false,
)
