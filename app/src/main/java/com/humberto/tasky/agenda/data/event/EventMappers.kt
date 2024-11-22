package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.Photo
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.LocalAttendee
import com.humberto.tasky.core.database.entity.PhotoEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime

fun EventEntity.toEvent(
    photos: List<Photo>
): AgendaItem {
    return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime("UTC"),
        to = to.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC"),
        attendees = attendees.map { it.toAttendee() },
        photos = photos,
        isUserEventCreator = isUserEventCreator
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    val remindAtEpoch = remindAt.toInstant().toEpochMilli()
    return EventEntity(
        id = id,
        title = title,
        description = description,
        from = from.toInstant().toEpochMilli(),
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAtEpoch,
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
        remindAt = remindAt.toZonedDateTime("UTC"),
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
        remindAt = remindAt!!.toInstant().toEpochMilli(),
        isEventCreator = isEventCreator
    )
}