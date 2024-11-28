package com.humberto.tasky.agenda.presentation.agenda_list.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
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
            remindAt = remindAt,
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
            remindAt = remindAt,
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
            remindAt = remindAt,
            itemDetails = AgendaItemDetails.Reminder,
        )
    }
}

fun AgendaItemUi.toAgendaItem(): AgendaItem {
    return when (this.itemDetails) {
        is AgendaItemDetails.Task -> AgendaItem.Task(
            id = id,
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            isDone = itemDetails.isDone
        )
        is AgendaItemDetails.Event -> AgendaItem.Event(
            id = id,
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            to = itemDetails.to,
            attendees = listOf(),
            photos = listOf(),
            isUserEventCreator = itemDetails.isUserEventCreator
        )
        AgendaItemDetails.Reminder -> AgendaItem.Reminder(
            id = id,
            title = title,
            description = description,
            from = from,
            remindAt = remindAt
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