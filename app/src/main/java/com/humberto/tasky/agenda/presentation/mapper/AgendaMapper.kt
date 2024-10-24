package com.humberto.tasky.agenda.presentation.mapper

import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.model.AgendaItemUi
import com.humberto.tasky.core.domain.agenda.AgendaItem
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun AgendaItem.toAgendaItemUi(): AgendaItemUi {
    return when (this) {
        is AgendaItem.TaskItem -> AgendaItemUi(
            id = task.id ?: UUID.randomUUID().toString(),
            title = task.title,
            description = task.description ?: "",
            dateTime = task.time.toFormattedDateTime(),
            agendaItemType = AgendaItemType.TASK
        )
        is AgendaItem.EventItem -> AgendaItemUi(
            id = event.id ?: UUID.randomUUID().toString(),
            title = event.title,
            description = event.description ?: "",
            dateTime = "${event.from.toFormattedDateTime()} - ${event.to.toFormattedDateTime()}",
            agendaItemType = AgendaItemType.EVENT
        )
        is AgendaItem.ReminderItem -> AgendaItemUi(
            id = reminder.id ?: UUID.randomUUID().toString(),
            title = reminder.title,
            description = reminder.description ?: "",
            dateTime = reminder.time.toFormattedDateTime(),
            agendaItemType = AgendaItemType.REMINDER
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