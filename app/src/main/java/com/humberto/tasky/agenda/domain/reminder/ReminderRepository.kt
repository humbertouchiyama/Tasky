package com.humberto.tasky.agenda.domain.reminder

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface ReminderRepository {
    suspend fun getReminder(reminderId: String): Result<AgendaItem, DataError>
    suspend fun createReminder(reminder: AgendaItem.Reminder): EmptyResult<DataError>
    suspend fun deleteReminder(reminderId: String)
}