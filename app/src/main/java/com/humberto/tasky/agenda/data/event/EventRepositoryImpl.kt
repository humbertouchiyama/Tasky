package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.data.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val agendaApiService: AgendaApiService,
    private val sessionManager: SessionManager
) : EventRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

    override suspend fun getEvent(eventId: String): Result<AgendaItem, DataError> {
        val eventEntity = eventDao.getEvent(eventId)
        return eventEntity?.let {
            val photos = eventDao
                .getPhotosByKeys(eventEntity.photoKeys)
                .map { it.toPhoto() }
            Result.Success(
                eventEntity.toEvent(photos = photos)
            )
        } ?: Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun createEvent(agendaItem: AgendaItem.Event): EmptyResult<DataError> {
        val itemWithEventCreator =
            agendaItem.copy(
                attendees = agendaItem.addEventCreatorIfNonExistent()
            )

        val eventEntity = itemWithEventCreator.toEventEntity()
        eventDao.upsertEvent(eventEntity)
        return Result.Success(Unit)
    }

    private fun AgendaItem.Event.addEventCreatorIfNonExistent(): List<Attendee> {
        val authInfo = sessionManager.get() ?: return attendees
        if (attendees.any { it.userId == localUserId }) return attendees
        val eventCreator = Attendee(
            userId = authInfo.userId,
            email = "",
            fullName = authInfo.fullName,
            eventId = id,
            remindAt = remindAt,
            isEventCreator = true
        )

        return listOf(eventCreator) + attendees
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
                        val attendee = result.data.attendee!!
                        val isEventCreator = attendee.userId == localUserId
                        Result.Success(
                            Attendee(
                                userId = attendee.userId,
                                fullName = attendee.fullName,
                                email = attendee.email,
                                isEventCreator = isEventCreator
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