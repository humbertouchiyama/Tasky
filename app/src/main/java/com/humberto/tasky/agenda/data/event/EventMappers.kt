package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.Photo
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.LocalAttendee
import com.humberto.tasky.core.database.entity.PhotoEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

fun EventEntity.toEvent(
    photos: List<Photo>
): AgendaItem {
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
        isUserEventCreator = isUserEventCreator
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
        photoKeys = photos.map { it.key }
    )
}

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}

fun LocalAttendee.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing,
        isEventCreator = isEventCreator
    )
}

fun Attendee.toLocalAttendee(): LocalAttendee {
    return LocalAttendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId!!,
        isGoing = isGoing,
        isEventCreator = isEventCreator
    )
}