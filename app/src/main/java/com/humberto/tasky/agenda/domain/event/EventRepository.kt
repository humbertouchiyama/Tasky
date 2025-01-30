package com.humberto.tasky.agenda.domain.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface EventRepository {
    suspend fun getEvent(eventId: String): Result<AgendaItem, DataError>
    suspend fun createEvent(event: AgendaItem.Event): Result<PhotoSizeTooLargeCount, DataError>
    suspend fun updateEvent(
        event: AgendaItem.Event,
        deletedRemotePhotoKeys: List<String>
    ): Result<PhotoSizeTooLargeCount, DataError>
    suspend fun deleteEvent(eventId: String): EmptyResult<DataError>
    suspend fun checkAttendeeExists(email: String): Result<Attendee, DataError>
    companion object {
        const val EVENT_ID = "EVENT_ID"
    }
}