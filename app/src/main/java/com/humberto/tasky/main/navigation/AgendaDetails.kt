package com.humberto.tasky.main.navigation

import com.humberto.tasky.agenda.presentation.AgendaItemType
import kotlinx.serialization.Serializable

@Serializable
data class AgendaDetails(
    val agendaItemId: String? = null,
    val agendaItemType: AgendaItemType,
    val isEditing: Boolean = false
)
