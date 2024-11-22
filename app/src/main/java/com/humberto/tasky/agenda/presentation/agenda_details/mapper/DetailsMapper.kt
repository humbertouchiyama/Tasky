package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsState
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.agenda.presentation.agenda_details.getReminderType
import com.humberto.tasky.agenda.presentation.agenda_details.model.AttendeeUi
import com.humberto.tasky.agenda.presentation.agenda_details.toReminderDateFromDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun AgendaDetailsState.updateWithAgendaItem(agendaItem: AgendaItem): AgendaDetailsState {
    val from = agendaItem.from.withZoneSameInstant(ZoneId.systemDefault())
    return when (agendaItem) {
        is AgendaItem.Task -> this.copy(
            id = agendaItem.id,
            title = agendaItem.title,
            description = agendaItem.description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = agendaItem.getReminderType() ?: ReminderType.ThirtyMinutes,
            agendaItem = AgendaItemDetails.Task(
                isDone = agendaItem.isDone
            ),
        )
        is AgendaItem.Event -> {
            val to = agendaItem.to.withZoneSameInstant(ZoneId.systemDefault())
            AgendaDetailsState(
                id = agendaItem.id,
                title = agendaItem.title,
                description = agendaItem.description ?: "",
                fromTime = from.toLocalTime(),
                fromDate = from.toLocalDate(),
                reminderType = agendaItem.getReminderType() ?: ReminderType.ThirtyMinutes,
                agendaItem = AgendaItemDetails.Event(
                    toTime = to.toLocalTime(),
                    toDate = to.toLocalDate(),
                    attendees = agendaItem.attendees.map { it.toAttendeeUi() }
                ),
            )
        }
        is AgendaItem.Reminder -> AgendaDetailsState(
            id = agendaItem.id,
            title = agendaItem.title,
            description = agendaItem.description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = agendaItem.getReminderType() ?: ReminderType.ThirtyMinutes,
            agendaItem = AgendaItemDetails.Reminder
        )
    }
}

fun AgendaDetailsState.toAgendaItem(): AgendaItem {
    val remindAt = reminderType.toReminderDateFromDateTime(fromDate.atTimeToUtc(fromTime))
    val from = fromDate.atTimeToUtc(fromTime)
    return when (agendaItem) {
        is AgendaItemDetails.Task -> AgendaItem.Task(
            id = id,
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            isDone = agendaItem.isDone,
        )
        is AgendaItemDetails.Event -> AgendaItem.Event(
            id = id,
            title = title,
            description = description,
            from = from,
            remindAt = remindAt,
            to = agendaItem.toDate.atTimeToUtc(agendaItem.toTime),
            isUserEventCreator = agendaItem.isUserEventCreator,
            attendees = agendaItem.attendees.map {
                it.toAttendee(
                    eventId = id,
                    remindAt = remindAt
                )
            },
            photos = listOf(), // TODO
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

fun AttendeeUi.toAttendee(eventId: String, remindAt: ZonedDateTime): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt,
        isEventCreator = isEventCreator
    )
}

fun Attendee.toAttendeeUi(): AttendeeUi {
    return AttendeeUi(
        userId = userId,
        email = email,
        fullName = fullName,
        isGoing = isGoing,
        isEventCreator = isEventCreator
    )
}

private fun LocalDate.atTimeToUtc(localTime: LocalTime): ZonedDateTime {
    val zonedDateTime = this.atTime(localTime).atZone(ZoneOffset.systemDefault())
    return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
}