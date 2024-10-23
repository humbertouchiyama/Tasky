package com.humberto.tasky.agenda.presentation.mapper

import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.model.AgendaItemUi
import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.reminder.Reminder
import com.humberto.tasky.core.domain.task.Task
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun Task.toAgendaItemUi(): AgendaItemUi =
    AgendaItemUi(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description ?: "",
        dateTime = time.toFormattedDateTime(),
        agendaItemType = AgendaItemType.TASK,
        isItemChecked = isDone
    )

fun Event.toAgendaItemUi(): AgendaItemUi =
    AgendaItemUi(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description ?: "",
        dateTime = "${from.toFormattedDateTime()} - ${to.toFormattedDateTime()}",
        agendaItemType = AgendaItemType.EVENT
    )

fun Reminder.toAgendaItemUi(): AgendaItemUi =
    AgendaItemUi(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description ?: "",
        dateTime = time.toFormattedDateTime(),
        agendaItemType = AgendaItemType.REMINDER
    )

private fun ZonedDateTime.toFormattedDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd, HH:mm")
        .format(timeInLocalTime)
}