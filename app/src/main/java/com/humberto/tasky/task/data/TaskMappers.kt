package com.humberto.tasky.task.data

import com.humberto.tasky.core.database.entity.TaskEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import com.humberto.tasky.task.domain.Task
import java.util.UUID

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        time = time.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC"),
        isDone = isDone
    )
}

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description,
        time = time.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli(),
        isDone = isDone
    )
}

