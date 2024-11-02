package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi

data class AgendaDetailsState(
    val agendaItem: AgendaDetailsUi,
    val isEditing: Boolean = false,
    val selectedFilter: FilterType = FilterType.ALL,
)