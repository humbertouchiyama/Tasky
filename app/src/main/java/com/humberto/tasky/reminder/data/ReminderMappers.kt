package com.humberto.tasky.reminder.data

import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import com.humberto.tasky.reminder.domain.Reminder
import java.util.UUID

fun ReminderEntity.toReminder(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        time = time.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC")
    )
}

fun Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id ?: UUID.randomUUID().toString(),
        title = title,
        description = description,
        time = time.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli()
    )
}