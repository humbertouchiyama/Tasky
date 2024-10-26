package com.humberto.tasky.agenda.domain

import com.humberto.tasky.event.domain.Event
import com.humberto.tasky.reminder.domain.Reminder
import com.humberto.tasky.task.domain.Task
import java.time.ZonedDateTime

sealed class AgendaItem(val dateTime: ZonedDateTime) {
    data class EventItem(val event: Event) : AgendaItem(event.from)
    data class TaskItem(val task: Task) : AgendaItem(task.time)
    data class ReminderItem(val reminder: Reminder) : AgendaItem(reminder.time)
}