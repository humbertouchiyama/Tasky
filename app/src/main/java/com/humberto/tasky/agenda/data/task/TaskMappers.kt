package com.humberto.tasky.agenda.data.task

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.database.ModificationType
import com.humberto.tasky.core.database.entity.TaskEntity
import com.humberto.tasky.core.database.entity.TaskPendingSyncEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

fun TaskEntity.toTask(): AgendaItem.Task {
    val remindDuration = (time - remindAt).milliseconds
    return AgendaItem.Task(
        id = id,
        title = title,
        description = description,
        from = time.toZonedDateTime("UTC"),
        reminderType = ReminderType.fromDuration(remindDuration) ?: ReminderType.ThirtyMinutes,
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskEntity(): TaskEntity {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        time = from,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun AgendaItem.Task.toTaskRequest(): TaskRequest {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return TaskRequest(
        id = id,
        title = title,
        description = description,
        time = from,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun TaskDto.toTask(): AgendaItem.Task {
    val remindDuration = (time - remindAt).milliseconds
    return AgendaItem.Task(
        id = id,
        title = title,
        description = description,
        from = time.toZonedDateTime("UTC"),
        reminderType = ReminderType.fromDuration(remindDuration) ?: ReminderType.ThirtyMinutes,
        isDone = isDone
    )
}