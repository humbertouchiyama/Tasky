package com.humberto.tasky.agenda.data.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository.Companion.REMINDER_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val reminderRepository: ReminderRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val reminderId = inputData.getString(REMINDER_ID) ?: return Result.failure()

        return try {
            reminderRepository.syncPendingUpdateReminder(reminderId = reminderId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}