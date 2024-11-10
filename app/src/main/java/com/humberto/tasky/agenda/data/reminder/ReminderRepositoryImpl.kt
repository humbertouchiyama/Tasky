package com.humberto.tasky.agenda.data.reminder

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
): ReminderRepository {
    override suspend fun getReminder(reminderId: String): Result<AgendaItem, DataError> {
        val reminder = reminderDao.getReminder(reminderId)?.toReminder()
        return if(reminder != null) {
            Result.Success(reminder)
        } else {
            Result.Error(DataError.Local.NOT_FOUND)
        }
    }

    override suspend fun createReminder(reminder: AgendaItem.Reminder): EmptyResult<DataError> {
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