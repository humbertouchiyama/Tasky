package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.Photo
import com.humberto.tasky.core.database.entity.AttendeeEntity
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.PhotoEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import java.util.UUID

fun EventEntity.toEvent(
    attendees: List<Attendee>,
    photos: List<Photo>
): AgendaItem {
    return AgendaItem.Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime("UTC"),
        to = to.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC"),
        attendees = attendees,
        photos = photos,
    )
}

fun AgendaItem.Event.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description,
        from = from.toInstant().toEpochMilli(),
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli(),
        attendeeIds = attendees.map { it.userId },
        photoKeys = photos.map { it.key },
    )
}

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        key = key,
        url = url
    )
}

fun Photo.toPhotoEntity(): PhotoEntity {
    return PhotoEntity(
        key = key,
        url = url
    )
}

fun AttendeeEntity.toAttendee(): Attendee {
    return Attendee(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toZonedDateTime("UTC")
    )
}

fun Attendee.toAttendeeEntity(): AttendeeEntity {
    return AttendeeEntity(
        userId = userId,
        email = email,
        fullName = fullName,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toInstant().toEpochMilli()
    )
}