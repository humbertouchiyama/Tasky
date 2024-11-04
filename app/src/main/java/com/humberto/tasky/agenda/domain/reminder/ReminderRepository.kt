package com.humberto.tasky.agenda.domain.reminder

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface ReminderRepository {
    suspend fun getReminder(reminderId: String): Result<Reminder, DataError>
    suspend fun createReminder(reminder: Reminder): EmptyResult<DataError>
    suspend fun deleteReminder(reminderId: String)
}