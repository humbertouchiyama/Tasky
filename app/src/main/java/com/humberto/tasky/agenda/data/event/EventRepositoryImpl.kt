package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.data.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val agendaApiService: AgendaApiService
) : EventRepository {

    override suspend fun getEvent(eventId: String): Result<AgendaItem, DataError> {
        val eventEntity = eventDao.getEvent(eventId)
        return eventEntity?.let {
            val photos = eventDao
                .getPhotosByKeys(eventEntity.photoKeys)
                .map { it.toPhoto() }
            Result.Success(
                eventEntity.toEvent(
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

    override suspend fun checkAttendeeExists(
        email: String
    ): Result<Attendee, DataError> {
        return safeCall {
            agendaApiService.checkAttendeeExists(email)
        }.let { result ->
            when(result) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> {
                    return if(result.data.doesUserExist) {
                        Result.Success(
                            Attendee(
                                userId = result.data.attendee!!.userId,
                                fullName = result.data.attendee.fullName,
                                email = result.data.attendee.email
                            )
                        )
                    } else {
                        Result.Error(DataError.Network.NOT_FOUND)
                    }
                }
            }
        }
    }
}