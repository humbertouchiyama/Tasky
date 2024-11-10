package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao
) : EventRepository {
    override suspend fun getEvent(eventId: String): Result<AgendaItem, DataError> {
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

    override suspend fun createEvent(agendaItem: AgendaItem.Event): EmptyResult<DataError> {
        val eventEntity = agendaItem.toEventEntity()
        eventDao.upsertEvent(eventEntity)
        return Result.Success(Unit)
    }

    override suspend fun deleteEvent(eventId: String) {
        eventDao.deleteEvent(eventId)
    }
}