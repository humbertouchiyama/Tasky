package com.humberto.tasky.reminder.data

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.reminder.domain.Reminder
import com.humberto.tasky.reminder.domain.ReminderRepository
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
): ReminderRepository {
    override suspend fun getReminder(reminderId: String): Reminder {
        return reminderDao.getReminder(reminderId).toReminder()
    }

    override suspend fun createReminder(reminder: Reminder): EmptyResult<DataError> {
        return try {
            val reminderEntity = reminder.toReminderEntity()
            reminderDao.upsertReminder(reminderEntity)
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteReminder(reminderId: String) {
        reminderDao.deleteReminder(reminderId)
    }
}