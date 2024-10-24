package com.humberto.tasky.core.domain.agenda

import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.reminder.Reminder
import com.humberto.tasky.core.domain.task.Task
import java.time.ZonedDateTime

sealed class AgendaItem(val dateTime: ZonedDateTime) {
    data class EventItem(val event: Event) : AgendaItem(event.from)
    data class TaskItem(val task: Task) : AgendaItem(task.time)
    data class ReminderItem(val reminder: Reminder) : AgendaItem(reminder.time)
}