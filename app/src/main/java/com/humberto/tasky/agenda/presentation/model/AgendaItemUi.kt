package com.humberto.tasky.agenda.presentation.model

import com.humberto.tasky.agenda.presentation.EventType

data class AgendaItemUi(
    val title: String,
    val description: String,
    val dateTime: String,
    val eventType: EventType,
    val isItemChecked: Boolean? = false,
) {
    val isItemCheckable
        get() = eventType == EventType.Task
}
