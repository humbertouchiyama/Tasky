package com.humberto.tasky.agenda.data.task

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
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
            taskDao.upsertTask(task.toTaskEntity())
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }
}