package com.humberto.tasky.agenda.data.task

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository.Companion.TASK_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CreateTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(TASK_ID) ?: return Result.failure()

        return try {
            taskRepository.syncPendingCreateTask(taskId = taskId)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}