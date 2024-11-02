package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.core.presentation.ui.UiText

sealed interface AgendaDetailsEvent {
    data object SaveSuccess: AgendaDetailsEvent
    data object DeleteSuccess: AgendaDetailsEvent
    data class Error(val error: UiText): AgendaDetailsEvent
}