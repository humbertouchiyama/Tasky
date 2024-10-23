package com.humberto.tasky.agenda.presentation.model

import com.humberto.tasky.agenda.presentation.AgendaItemType

data class AgendaItemUi(
    val id: String,
    val title: String,
    val description: String = "",
    val dateTime: String,
    val agendaItemType: AgendaItemType,
    val isItemChecked: Boolean? = false,
) {
    val isItemCheckable
        get() = agendaItemType == AgendaItemType.TASK
}
