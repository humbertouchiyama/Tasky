package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.agenda.presentation.AgendaItemType

data class AgendaDetailsState(
    val agendaItemId: String? = null,
    val agendaItemType: AgendaItemType,
    val isEditing: Boolean = false,
)