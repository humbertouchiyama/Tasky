package com.humberto.tasky.agenda.data.reminder

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.database.entity.ReminderPendingSyncEntity
import com.humberto.tasky.core.domain.util.toZonedDateTime
import kotlin.time.Duration.Companion.milliseconds

fun ReminderEntity.toReminder(): AgendaItem.Reminder {
    val remindDuration = (time - remindAt).milliseconds
    return AgendaItem.Reminder(
        id = id,
        title = title,
        description = description,
        from = time.toZonedDateTime("UTC"),
        reminderType = ReminderType.fromDuration(remindDuration) ?: ReminderType.ThirtyMinutes
    )
}

fun AgendaItem.Reminder.toReminderEntity(): ReminderEntity {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        time = from,
        remindAt = remindAt
    )
}

fun AgendaItem.Reminder.toReminderRequest(): ReminderRequest {
    val from = from.toInstant().toEpochMilli()
    val remindAt = from - reminderType.duration.inWholeMilliseconds
    return ReminderRequest(
        id = id,
        title = title,
        description = description,
        time = from,
        remindAt = remindAt
    )
}

fun ReminderEntity.toReminderPendingSyncEntity(userId: String): ReminderPendingSyncEntity {
    return ReminderPendingSyncEntity(
        userId = userId,
        reminder = this
    )
}