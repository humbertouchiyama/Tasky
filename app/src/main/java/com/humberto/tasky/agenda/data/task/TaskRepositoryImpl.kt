package com.humberto.tasky.agenda.data.task

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.agenda.data.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.ModificationType
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.entity.ModifiedTaskEntity
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.onError
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val agendaApi: AgendaApiService
): TaskRepository {
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
            val result = safeCall {
                taskDao.upsertTask(task.toTaskEntity())
                agendaApi.createTask(task.toTaskRequest())
            }.onError { error ->
                if(error == DataError.Network.NO_INTERNET) {
                    taskDao.insertModifiedTask(
                        task.toModifiedTaskEntity(
                            modificationType = ModificationType.Created
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

    override suspend fun deleteTask(taskId: String) {
        val result = safeCall {
            taskDao.deleteTask(taskId)
            agendaApi.deleteTask(taskId)
        }
        if(result is Result.Error) {
            taskDao.insertModifiedTask(
                ModifiedTaskEntity(
                    taskId = taskId,
                    modificationType = ModificationType.Deleted
                )
            )
        }
    }
}