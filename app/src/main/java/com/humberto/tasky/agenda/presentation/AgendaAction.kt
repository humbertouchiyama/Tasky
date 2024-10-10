package com.humberto.tasky.agenda.presentation

sealed interface AgendaAction {
    data object OnNewEventClick: AgendaAction
    data object OnNewTaskClick: AgendaAction
    data object OnNewReminderClick: AgendaAction
    data object OnLogoutClick: AgendaAction
}