package com.humberto.tasky.agenda.data.agenda

import com.humberto.tasky.agenda.data.event.EventDto
import com.humberto.tasky.agenda.data.reminder.ReminderDto
import com.humberto.tasky.agenda.data.task.TaskDto
import kotlinx.serialization.Serializable

@Serializable
data class GetFullAgendaResponse(
    val events: List<EventDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>,
)
