package com.humberto.tasky.agenda.presentation.agenda_list.model

import com.humberto.tasky.agenda.presentation.AgendaItemType
import java.time.ZonedDateTime

data class AgendaItemUi(
    val id: String,
    val title: String,
    val description: String = "",
    val dateTime: String,
    val agendaItemType: AgendaItemType,
    val from: ZonedDateTime,
    val itemDetails: AgendaItemDetails
) {
    val isItemCheckable
        get() = agendaItemType == AgendaItemType.TASK
}

sealed interface AgendaItemDetails {
    data class Event(
        val to: ZonedDateTime,
        val isUserEventCreator: Boolean
    ): AgendaItemDetails

    data class Task(val isDone: Boolean = false): AgendaItemDetails

    data object Reminder: AgendaItemDetails
}