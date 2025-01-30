package com.humberto.tasky.agenda.data.task

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.ModificationType
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.entity.DeletedTaskSyncEntity
import com.humberto.tasky.core.database.entity.TaskPendingSyncEntity
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.isRetryable
import com.humberto.tasky.core.domain.util.onError
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val agendaApi: AgendaApiService,
    private val sessionManager: SessionManager
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
                        TaskPendingSyncEntity(
                            userId = localUserId!!,
                            task = taskEntity,
                            type = ModificationType.Created
                        )
                    )
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
                        TaskPendingSyncEntity(
                            userId = localUserId!!,
                            task = taskEntity,
                            type = ModificationType.Updated
                        )
                    )
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
}