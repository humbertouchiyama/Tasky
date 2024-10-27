package com.humberto.tasky.event.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface EventRepository {
    suspend fun getEvent(eventId: String): Result<Event, DataError>
    suspend fun createEvent(event: Event): EmptyResult<DataError>
    suspend fun deleteEvent(eventId: String)
}