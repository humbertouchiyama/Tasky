package com.humberto.tasky.agenda.data.reminder

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.data.helper.WorkerHelper
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository.Companion.REMINDER_ID
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.entity.DeletedReminderSyncEntity
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.isRetryable
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val agendaApi: AgendaApiService,
    private val sessionManager: SessionManager,
    private val workerHelper: WorkerHelper
): ReminderRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

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
            val result = safeCall {
                agendaApi.createReminder(reminder.toReminderRequest())
            }.onError { error ->
                if (error.isRetryable()) {
                    reminderDao.insertReminderPendingSync(
                        reminderEntity.toReminderPendingSyncEntity(
                            userId = localUserId!!
                        )
                    )
                    workerHelper.enqueueSyncPendingWorker<CreateReminderWorker>(
                        idKey = REMINDER_ID,
                        id = reminder.id
                    )
                    return Result.Success(Unit)
                }
            }
            return result
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun updateReminder(reminder: AgendaItem.Reminder): EmptyResult<DataError> {
        return try {
            val reminderEntity = reminder.toReminderEntity()
            reminderDao.upsertReminder(reminderEntity)
            val result = safeCall {
                agendaApi.updateReminder(reminder.toReminderRequest())
            }.onError { error ->
                if (error.isRetryable()) {
                    reminderDao.insertReminderPendingSync(
                        reminderEntity.toReminderPendingSyncEntity(
                            userId = localUserId!!
                        )
                    )
                    workerHelper.enqueueSyncPendingWorker<UpdateReminderWorker>(
                        idKey = REMINDER_ID,
                        id = reminder.id
                    )
                    return Result.Success(Unit)
                }
            }
            return result
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteReminder(reminderId: String): EmptyResult<DataError> {
        reminderDao.deleteReminder(reminderId)
        return safeCall {
            agendaApi.deleteReminder(reminderId)
        }.onError { error ->
            if (error.isRetryable()) {
                reminderDao.insertDeletedReminderSync(
                    DeletedReminderSyncEntity(
                        reminderId = reminderId,
                        userId = localUserId!!
                    )
                )
            }
        }
    }

    override suspend fun syncPendingUpdateReminder(reminderId: String) {
        val pendingReminderEntity = reminderDao.getReminderPendingSync(
            userId = localUserId ?: "",
            reminderId = reminderId
        )
        safeCall {
            agendaApi.updateReminder(pendingReminderEntity.reminder.toReminder().toReminderRequest())
        }.onSuccess {
            reminderDao.deleteReminderPendingSync(pendingReminderEntity.reminderId)
        }
    }

    override suspend fun syncPendingCreateReminder(reminderId: String) {
        val pendingReminderEntity = reminderDao.getReminderPendingSync(
            userId = localUserId ?: "",
            reminderId = reminderId
        )
        safeCall {
            agendaApi.createReminder(pendingReminderEntity.reminder.toReminder().toReminderRequest())
        }.onSuccess {
            reminderDao.deleteReminderPendingSync(pendingReminderEntity.reminderId)
        }
    }
}