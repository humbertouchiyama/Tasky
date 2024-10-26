package com.humberto.tasky.agenda.presentation.agenda_list

import com.humberto.tasky.core.presentation.ui.UiText

sealed interface AgendaEvent {
    data object LogoutSuccess: AgendaEvent
    data class Error(val error: UiText): AgendaEvent
}