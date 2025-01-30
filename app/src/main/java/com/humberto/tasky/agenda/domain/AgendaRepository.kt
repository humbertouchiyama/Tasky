package com.humberto.tasky.agenda.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZonedDateTime

interface AgendaRepository {
    suspend fun logout(): EmptyResult<DataError.Network>
    fun getAgendaForDate(localDate: LocalDate): Flow<List<AgendaItem>>
    suspend fun syncAndUpdateCache(
        time: ZonedDateTime,
        updateTimeOnly: Boolean
    ): Result<List<AgendaItem>, DataError>
    suspend fun upsertFullAgenda(
        tasks: List<AgendaItem.Task>,
        events: List<AgendaItem.Event>,
        reminders: List<AgendaItem.Reminder>
    ): EmptyResult<DataError.Local>
    suspend fun deleteAllAgenda()
}