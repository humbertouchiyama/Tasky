package com.humberto.tasky.agenda.presentation.agenda_list.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemDetails
import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun AgendaItem.toAgendaItemUi(): AgendaItemUi {
    return when (this) {
        is AgendaItem.Task -> AgendaItemUi(
            id = id,
            title = title,
            description = description ?: "",
            dateTime = from.toFormattedDateTime(),
            agendaItemType = AgendaItemType.TASK,
            from = from,
            itemDetails = AgendaItemDetails.Task(
                isDone = isDone
            ),
        )
        is AgendaItem.Event -> AgendaItemUi(
            id = id,
            title = title,
            description = description ?: "",
            dateTime = "${from.toFormattedDateTime()} - ${to.toFormattedDateTime()}",
            agendaItemType = AgendaItemType.EVENT,
            from = from,
            itemDetails = AgendaItemDetails.Event(
                to = to,
                isUserEventCreator = isUserEventCreator
            ),
        )
        is AgendaItem.Reminder -> AgendaItemUi(
            id = id,
            title = title,
            description = description ?: "",
            dateTime = from.toFormattedDateTime(),
            agendaItemType = AgendaItemType.REMINDER,
            from = from,
            itemDetails = AgendaItemDetails.Reminder,
        )
    }
}

private fun ZonedDateTime.toFormattedDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd, HH:mm")
        .format(timeInLocalTime)
}