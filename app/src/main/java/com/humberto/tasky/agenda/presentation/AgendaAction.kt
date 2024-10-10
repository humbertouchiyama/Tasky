package com.humberto.tasky.agenda.presentation

import java.time.LocalDate

sealed interface AgendaAction {
    data object OnNewEventClick: AgendaAction
    data object OnNewTaskClick: AgendaAction
    data object OnNewReminderClick: AgendaAction
    data object OnLogoutClick: AgendaAction
    data class OnSelectedDate(
        val selectedDate: LocalDate
    ): AgendaAction
}