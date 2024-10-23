package com.humberto.tasky.core.domain.agenda

import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.reminder.Reminder
import com.humberto.tasky.core.domain.task.Task
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface LocalAgendaDataSource {
    fun getAgendaForDate(localDate: LocalDate): Flow<Agenda>
    suspend fun upsertFullAgenda(
        tasks: List<Task>,
        events: List<Event>,
        reminders: List<Reminder>
    ): EmptyResult<DataError.Local>
    suspend fun deleteAllAgenda()
}