package com.humberto.tasky.agenda.domain.task

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result

interface TaskRepository {
    suspend fun getTask(taskId: String): Result<AgendaItem, DataError>
    suspend fun createTask(task: AgendaItem.Task): EmptyResult<DataError>
    suspend fun deleteTask(taskId: String): EmptyResult<DataError>
    suspend fun syncPendingTasks()
}