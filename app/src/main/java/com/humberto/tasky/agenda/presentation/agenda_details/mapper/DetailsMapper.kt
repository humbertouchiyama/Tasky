package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsState
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.agenda.presentation.agenda_details.getReminderType
import com.humberto.tasky.agenda.presentation.agenda_details.toReminderDateFromDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

fun AgendaItem.toAgendaState(): AgendaDetailsState {
    return when (this) {
        is AgendaItem.Task -> AgendaDetailsState(
            id = id,
            title = title,
            description = description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = getReminderType() ?: ReminderType.ThirtyMinutes,
            agendaItem = AgendaItemDetails.Task(
                isDone = isDone
            ),
        )
        is AgendaItem.Event -> AgendaDetailsState(
            id = id,
            title = title,
            description = description ?: "",
            fromTime = from.toLocalTime(),
            fromDate = from.toLocalDate(),
            reminderType = getReminderType() ?: ReminderType.ThirtyMinutes,
            agendaItem = AgendaItemDetails.Event(
                toTime = to.toLocalTime(),
                toDate = to.toLocalDate(),
            ),
        )
        is AgendaItem.Reminder -> AgendaDetailsState(
            id = id,
            title = title,
            description = description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = getReminderType() ?: ReminderType.ThirtyMinutes,
            agendaItem = AgendaItemDetails.Reminder
        )
    }
}

fun AgendaDetailsState.toAgendaItem(): AgendaItem {
    val remindAt = reminderType.toReminderDateFromDateTime(fromDate.atTimeToUtc(fromTime))
    val from = fromDate.atTimeToUtc(fromTime)
    return when (agendaItem) {
        is AgendaItemDetails.Task -> AgendaItem.Task(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            isDone = agendaItem.isDone,
        )
        is AgendaItemDetails.Event -> AgendaItem.Event(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            to = agendaItem.toDate.atTimeToUtc(agendaItem.toTime),
            attendees = listOf(), // TODO
            photos = listOf(), // TODO
        )
        AgendaItemDetails.Reminder -> AgendaItem.Reminder(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            from = from,
            remindAt = remindAt
        )
    }
}

private fun LocalDate.atTimeToUtc(localTime: LocalTime): ZonedDateTime {
    val zonedDateTime = this.atTime(localTime).atZone(ZoneOffset.systemDefault())
    return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
}