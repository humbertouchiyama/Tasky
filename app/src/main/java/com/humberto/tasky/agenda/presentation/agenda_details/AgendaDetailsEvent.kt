package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.core.presentation.ui.UiText

sealed interface AgendaDetailsEvent {
    data class SaveSuccess(val message: UiText): AgendaDetailsEvent
    data object DeleteSuccess: AgendaDetailsEvent
}