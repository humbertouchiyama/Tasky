package com.humberto.tasky.agenda.data.agenda

import com.humberto.tasky.agenda.data.event.toEvent
import com.humberto.tasky.agenda.data.reminder.toReminder
import com.humberto.tasky.agenda.data.task.toTask
import com.humberto.tasky.agenda.domain.AgendaItem

fun AgendaDto.toAgendaItems(): List<AgendaItem> {
    val eventsList = events.map { it.toEvent() }
    val tasksList = tasks.map { it.toTask() }
    val remindersList = reminders.map { it.toReminder() }
    return eventsList + tasksList + remindersList
}
