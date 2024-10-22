package com.humberto.tasky.core.database.mapper

import com.humberto.tasky.core.database.entity.AttendeeEntity
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.PhotoEntity
import com.humberto.tasky.core.domain.event.Attendee
import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.event.Photo
import java.util.UUID

fun EventEntity.toEvent(
    getAttendeesEntitiesByIds: (List<String>) -> List<AttendeeEntity>,
    getPhotosEntitiesByUrls: (List<String>) -> List<PhotoEntity>
): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        from = from.toZonedDateTime("UTC"),
        to = to.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC"),
        attendees = getAttendeesEntitiesByIds(attendeeIds).map { it.toAttendee() },
        photos = getPhotosEntitiesByUrls(photoKeys).map { it.toPhoto() },
        isGoing = isGoing
    )
}

fun Event.toEventEntity(): EventEntity {
    return EventEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description,
        from = from.toInstant().toEpochMilli(),
        to = to.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli(),
        attendeeIds = attendees.map { it.userId },
        photoKeys = photos.map { it.key },
        isGoing = isGoing
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