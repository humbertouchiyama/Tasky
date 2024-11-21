package com.humberto.tasky.agenda.domain.event

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface EventRepository {
    suspend fun getEvent(eventId: String): Result<AgendaItem, DataError>
    suspend fun createEvent(agendaItem: AgendaItem.Event): EmptyResult<DataError>
    suspend fun deleteEvent(eventId: String)
    suspend fun checkAttendeeExists(email: String): Result<Attendee, DataError>
}