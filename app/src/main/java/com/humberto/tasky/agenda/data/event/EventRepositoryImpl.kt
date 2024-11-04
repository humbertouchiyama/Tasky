package com.humberto.tasky.agenda.data.event

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.agenda.domain.event.Event
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.agenda.domain.event.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {
    override suspend fun getEvent(eventId: String): Result<Event, DataError> {
        val eventEntity = eventDao.getEvent(eventId)
        return eventEntity?.let {
            val attendees = eventDao
                .getAttendeesByIds(eventEntity.attendeeIds)
                .map { it.toAttendee() }
            val photos = eventDao
                .getPhotosByKeys(eventEntity.photoKeys)
                .map { it.toPhoto() }
            Result.Success(
                eventEntity.toEvent(
                attendees = attendees,
                photos = photos
                )
            )
        } ?: Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun createEvent(event: Event): EmptyResult<DataError> {
        return try {
            val eventEntity = event.toEventEntity()
            eventDao.upsertEvent(eventEntity)
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteEvent(eventId: String) {
        eventDao.deleteEvent(eventId)
    }
}