package com.humberto.tasky.agenda.data.task

import android.database.sqlite.SQLiteFullException
import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.humberto.tasky.agenda.data.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.entity.DeletedTaskSyncEntity
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.isRetryable
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val agendaApi: AgendaApiService,
    private val sessionManager: SessionManager,
    private val workManager: WorkManager
): TaskRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

    override suspend fun getTask(taskId: String): Result<AgendaItem, DataError> {
        val task = taskDao.getTask(taskId)?.toTask()
        return if(task != null) {
            Result.Success(task)
        } else {
            Result.Error(DataError.Local.NOT_FOUND)
        }
    }

    override suspend fun createTask(task: AgendaItem.Task): EmptyResult<DataError> {
        return try {
            val taskEntity = task.toTaskEntity()
            taskDao.upsertTask(taskEntity)
            val result = safeCall {
                agendaApi.createTask(task.toTaskRequest())
            }.onError { error ->
                if (error.isRetryable()) {
                    taskDao.insertTaskPendingSync(
                        taskEntity.toTaskPendingSyncEntity(
                            userId = localUserId!!
                        )
                    )
                    enqueuePendingWorker<CreateTaskWorker>(taskId = task.id)
                    return Result.Success(Unit)
                }
            }
            return result
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun updateTask(task: AgendaItem.Task): EmptyResult<DataError> {
        return try {
            val taskEntity = task.toTaskEntity()
            taskDao.upsertTask(taskEntity)
            val result = safeCall {
                agendaApi.updateTask(task.toTaskRequest())
            }.onError { error ->
                if (error.isRetryable()) {
                    taskDao.insertTaskPendingSync(
                        taskEntity.toTaskPendingSyncEntity(
                            userId = localUserId!!
                        )
                    )
                    enqueuePendingWorker<UpdateTaskWorker>(taskId = task.id)
                    return Result.Success(Unit)
                }
            }
            return result
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteTask(taskId: String): EmptyResult<DataError> {
        taskDao.deleteTask(taskId)
        return safeCall {
            agendaApi.deleteTask(taskId)
        }.onError { error ->
            if (error.isRetryable()) {
                taskDao.insertDeletedTaskSync(
                    DeletedTaskSyncEntity(
                        taskId = taskId,
                        userId = localUserId!!
                    )
                )
            }
        }
    }

    private inline fun <reified T: ListenableWorker> enqueuePendingWorker(taskId: String) {
        val request = OneTimeWorkRequestBuilder<T>()
            .setInputData(
                workDataOf("TASK_ID" to taskId)
            )
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setConstraints(Constraints(
                requiredNetworkType = NetworkType.CONNECTED
            ))
            .build()

        workManager.enqueue(request)
    }

    override suspend fun syncPendingUpdateTask(taskId: String) {
        val pendingTaskEntity = taskDao.getTaskPendingSync(
            userId = localUserId ?: "",
            taskId = taskId
        )
        safeCall {
            agendaApi.updateTask(pendingTaskEntity.task.toTask().toTaskRequest())
        }.onSuccess {
            taskDao.deleteTaskPendingSync(pendingTaskEntity.taskId)
        }
    }

    override suspend fun syncPendingCreateTask(taskId: String) {
        val pendingTaskEntity = taskDao.getTaskPendingSync(
            userId = localUserId ?: "",
            taskId = taskId
        )
        safeCall {
            agendaApi.createTask(pendingTaskEntity.task.toTask().toTaskRequest())
        }.onSuccess {
            taskDao.deleteTaskPendingSync(pendingTaskEntity.taskId)
        }
    }
}