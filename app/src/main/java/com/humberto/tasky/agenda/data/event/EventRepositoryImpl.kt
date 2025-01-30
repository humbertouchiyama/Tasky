package com.humberto.tasky.agenda.data.event

import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.EventPhoto
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.event.EventUploader
import com.humberto.tasky.agenda.domain.event.PhotoSizeTooLargeCount
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val agendaApiService: AgendaApiService,
    private val sessionManager: SessionManager,
    private val eventUploader: EventUploader
) : EventRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun getEvent(eventId: String): Result<AgendaItem, DataError> {
        val eventEntity = eventDao.getEvent(eventId)
        return eventEntity?.let {
            Result.Success(eventEntity.toEvent())
        } ?: Result.Error(DataError.Local.NOT_FOUND)
    }

    override suspend fun createEvent(event: AgendaItem.Event): Result<PhotoSizeTooLargeCount, DataError> {
        eventDao.upsertEvent(event.toEventEntity())
        val requestJson = json.encodeToString(event.toCreateEventRequest())

        return eventUploader.upload(
            id = event.id,
            type = EventUploader.Type.Create,
            requestJson = requestJson,
            photoUris = event.photos
                .filterIsInstance<EventPhoto.Local>()
                .map { it.uriString }
                .toTypedArray()
        ).first()
    }

    override suspend fun updateEvent(
        event: AgendaItem.Event,
        deletedRemotePhotoKeys: List<String>
    ): Result<PhotoSizeTooLargeCount, DataError> {
        eventDao.upsertEvent(event.toEventEntity())
        val requestJson = json.encodeToString(
            event.toUpdateEventRequest(
                deletedPhotoKeys = deletedRemotePhotoKeys,
                isGoing = event.getAttendee(localUserId ?: "")?.isGoing == true
            )
        )

        return eventUploader.upload(
            id = event.id,
            type = EventUploader.Type.Update,
            requestJson = requestJson,
            photoUris = event.photos
                .filterIsInstance<EventPhoto.Local>()
                .map { it.uriString }
                .toTypedArray()
        ).first()
    }

    override suspend fun deleteEvent(eventId: String): EmptyResult<DataError> {
        eventDao.deleteEvent(eventId)
        val result = safeCall {
            agendaApiService.deleteEvent(eventId)
        }

        return result
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
                        Result.Success(
                            Attendee(
                                userId = attendee.userId,
                                fullName = attendee.fullName,
                                email = attendee.email
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