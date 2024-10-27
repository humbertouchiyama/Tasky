package com.humberto.tasky.task.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface TaskRepository {
    suspend fun getTask(taskId: String): Result<Task, DataError>
    suspend fun createTask(task: Task): EmptyResult<DataError>
    suspend fun deleteTask(taskId: String)
}