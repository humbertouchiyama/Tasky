package com.humberto.tasky.task.data

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.task.domain.Task
import com.humberto.tasky.task.domain.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun getTask(taskId: String): Result<Task, DataError> {
        val task = taskDao.getTask(taskId)?.toTask()
        return if(task != null) {
            Result.Success(task)
        } else {
            Result.Error(DataError.Local.NOT_FOUND)
        }
    }

    override suspend fun createTask(task: Task): EmptyResult<DataError> {
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