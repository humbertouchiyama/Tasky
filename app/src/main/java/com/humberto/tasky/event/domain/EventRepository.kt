package com.humberto.tasky.event.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult

interface EventRepository {
    suspend fun getEvent(eventId: String): Event
    suspend fun createEvent(event: Event): EmptyResult<DataError>
    suspend fun deleteEvent(eventId: String)
}