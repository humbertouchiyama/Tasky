package com.humberto.tasky.reminder.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult

interface ReminderRepository {
    suspend fun getReminder(reminderId: String): Reminder
    suspend fun createReminder(reminder: Reminder): EmptyResult<DataError>
    suspend fun deleteReminder(reminderId: String)
}