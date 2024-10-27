package com.humberto.tasky.agenda.presentation.agenda_details

sealed interface AgendaDetailsAction {
    data object OnBackClick: AgendaDetailsAction
    data object OnEditClick: AgendaDetailsAction
    data object OnSaveClick: AgendaDetailsAction
}