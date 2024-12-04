package com.humberto.tasky.agenda.data.task

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.database.entity.TaskEntity
import com.humberto.tasky.core.database.entity.TaskPendingSyncEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime

fun TaskEntity.toTask(): AgendaItem.Task {
    return AgendaItem.Task(
        id = id,
        title = title,
        description = description,
        from = time.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC"),
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        time = from.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli(),
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskRequest(): TaskRequest {
    return TaskRequest(
        id = id,
        title = title,
        description = description,
        time = from.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli(),
        isDone = isDone
    )
}

fun TaskEntity.toTaskPendingSyncEntity(userId: String): TaskPendingSyncEntity {
    return TaskPendingSyncEntity(
        userId = userId,
        task = this
    )
}