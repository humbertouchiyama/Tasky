package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.EventPhoto
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.LocalAttendee
import com.humberto.tasky.core.domain.util.toZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

fun EventEntity.toEvent(): AgendaItem.Event {
    val remindDuration = (from - remindAt).milliseconds
    return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime("UTC"),
        to = to.toZonedDateTime("UTC"),
        reminderType = ReminderType.fromDuration(remindDuration) ?: ReminderType.ThirtyMinutes,
        attendees = attendees.map { it.toAttendee() },
        photos = photos,
        isUserEventCreator = isUserEventCreator,
        host = host
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return EventEntity(
        id = id,
        title = title,
        description = description,
        from = from,
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAt,
        isUserEventCreator = isUserEventCreator,
        attendees = attendees.map { it.toLocalAttendee() },
        photos = photos.filterIsInstance<EventPhoto.Remote>(),
        host = host
    )
}

fun EventDto.toEvent(): AgendaItem.Event {
    val remindDuration = (from - remindAt).milliseconds
    return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime("UTC"),
        to = to.toZonedDateTime("UTC"),
        reminderType = ReminderType.fromDuration(remindDuration) ?: ReminderType.ThirtyMinutes,
        attendees = attendees.map { it.toAttendee() },
        photos = photos.map {
            EventPhoto.Remote(
                key = it.key,
                photoUrl = it.url
            )
        },
        isUserEventCreator = isUserEventCreator,
        host = host
    )
}

fun AgendaItem.Event.toCreateEventRequest(): CreateEventRequest {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return CreateEventRequest(
        id = id,
        title = title,
        description = description,
        from = from,
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAt,
        attendeeIds = attendees.map { it.userId },
    )
}

fun AgendaItem.Event.toUpdateEventRequest(
    deletedPhotoKeys: List<String>,
    isGoing: Boolean
): UpdateEventRequest {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return UpdateEventRequest(
        id = id,
        title = title,
        description = description,
        from = from,
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAt,
        attendeeIds = attendees.map { it.userId },
        deletedPhotoKeys = deletedPhotoKeys,
        isGoing = isGoing,
    )
}

fun LocalAttendee.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing
    )
}

fun Attendee.toLocalAttendee(): LocalAttendee {
    return LocalAttendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId!!,
        isGoing = isGoing
    )
}

fun AttendeeDto.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing
    )
}