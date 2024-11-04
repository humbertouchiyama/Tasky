package com.humberto.tasky.agenda.domain

import com.humberto.tasky.agenda.domain.event.Event
import com.humberto.tasky.agenda.domain.reminder.Reminder
import com.humberto.tasky.agenda.domain.task.Task
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface AgendaRepository {
    suspend fun logout(): EmptyResult<DataError.Network>
    fun getAgendaForDate(localDate: LocalDate): Flow<List<AgendaItem>>
    suspend fun upsertFullAgenda(
        tasks: List<Task>,
        events: List<Event>,
        reminders: List<Reminder>
    ): EmptyResult<DataError.Local>
    suspend fun deleteAllAgenda()
}