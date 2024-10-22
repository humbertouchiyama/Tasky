package com.humberto.tasky.core.domain.agenda

import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.reminder.Reminder
import com.humberto.tasky.core.domain.task.Task

data class Agenda(
    val events: List<Event>,
    val tasks: List<Task>,
    val reminders: List<Reminder>
)
