package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

fun AgendaItem.toAgendaDetailsUi(): AgendaDetailsUi {
    return when (this) {
        is AgendaItem.TaskItem -> AgendaDetailsUi(
            id = task.id ?: UUID.randomUUID().toString(),
            title = task.title,
            description = task.description ?: "",
            atDate = task.time.toLocalDate(),
            atTime = task.time.toLocalTime(),
            agendaItemType = AgendaItemType.TASK
        )
        is AgendaItem.EventItem -> AgendaDetailsUi(
            id = event.id ?: UUID.randomUUID().toString(),
            title = event.title,
            description = event.description ?: "",
            fromTime = event.from.toLocalTime(),
            fromDate = event.from.toLocalDate(),
            toTime = event.to.toLocalTime(),
            toDate = event.to.toLocalDate(),
            agendaItemType = AgendaItemType.EVENT
        )
        is AgendaItem.ReminderItem -> AgendaDetailsUi(
            id = reminder.id ?: UUID.randomUUID().toString(),
            title = reminder.title,
            description = reminder.description ?: "",
            atDate = reminder.time.toLocalDate(),
            atTime = reminder.time.toLocalTime(),
            agendaItemType = AgendaItemType.REMINDER
        )
    }
}

fun LocalDate.toFormatted(): String {
    val dateInLocalZone = this
        .atStartOfDay(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd yyyy")
        .format(dateInLocalZone)
}

fun LocalTime.toFormatted(): String {
    return DateTimeFormatter
        .ofPattern("HH:mm")
        .format(this)
}