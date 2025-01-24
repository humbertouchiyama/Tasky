package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsState
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.agenda.presentation.agenda_details.model.AttendeeUi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

fun AgendaDetailsState.updateWithAgendaItem(agendaItem: AgendaItem): AgendaDetailsState {
    val from = agendaItem.from.withZoneSameInstant(ZoneId.systemDefault())
    return when (agendaItem) {
        is AgendaItem.Task -> this.copy(
            id = agendaItem.id,
            title = agendaItem.title,
            description = agendaItem.description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = agendaItem.reminderType,
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
                reminderType = agendaItem.reminderType,
                agendaItem = AgendaItemDetails.Event(
                    toTime = to.toLocalTime(),
                    toDate = to.toLocalDate(),
                    attendees = agendaItem.attendees.map { it.toAttendeeUi() },
                    eventPhotos = agendaItem.photos,
                    eventCreator = agendaItem.attendees.find { it.userId == agendaItem.host }?.toAttendeeUi()
                ),
            )
        }
        is AgendaItem.Reminder -> AgendaDetailsState(
            id = agendaItem.id,
            title = agendaItem.title,
            description = agendaItem.description ?: "",
            fromDate = from.toLocalDate(),
            fromTime = from.toLocalTime(),
            reminderType = agendaItem.reminderType,
            agendaItem = AgendaItemDetails.Reminder
        )
    }
}

fun AgendaDetailsState.toAgendaItem(): AgendaItem {
    val from = fromDate.atTimeNoSecondsToUtc(fromTime)
    val id = id ?: UUID.randomUUID().toString()
    return when (agendaItem) {
        is AgendaItemDetails.Task -> AgendaItem.Task(
            id = id,
            title = title,
            description = description,
            from = from,
            reminderType = reminderType,
            isDone = agendaItem.isDone,
        )
        is AgendaItemDetails.Event -> AgendaItem.Event(
            id = id,
            title = title,
            description = description,
            from = from,
            reminderType = reminderType,
            to = agendaItem.toDate.atTimeNoSecondsToUtc(agendaItem.toTime),
            isUserEventCreator = agendaItem.isUserEventCreator,
            attendees = agendaItem.attendees.map {
                it.toAttendee(
                    eventId = id
                )
            },
            photos = agendaItem.eventPhotos,
            host = agendaItem.eventCreator?.userId
        )
        AgendaItemDetails.Reminder -> AgendaItem.Reminder(
            id = id,
            title = title,
            description = description,
            from = from,
            reminderType = reminderType
        )
    }
}

fun AttendeeUi.toAttendee(eventId: String): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing
    )
}

fun Attendee.toAttendeeUi(): AttendeeUi {
    return AttendeeUi(
        userId = userId,
        email = email,
        fullName = fullName,
        isGoing = isGoing
    )
}

fun LocalDate.atTimeNoSecondsToUtc(localTime: LocalTime): ZonedDateTime {
    val truncatedTime = localTime.truncatedTo(ChronoUnit.MINUTES)
    val zonedDateTime = this.atTime(truncatedTime).atZone(ZoneOffset.systemDefault())
    return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
}