package com.humberto.tasky.agenda.data.reminder

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import java.util.UUID

fun ReminderEntity.toReminder(): AgendaItem {
    return AgendaItem.Reminder(
        id = id,
        title = title,
        description = description,
        from = time.toZonedDateTime("UTC"),
        remindAt = remindAt.toZonedDateTime("UTC")
    )
}

fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        time = from.toInstant().toEpochMilli(),
        remindAt = remindAt.toInstant().toEpochMilli()
    )
}