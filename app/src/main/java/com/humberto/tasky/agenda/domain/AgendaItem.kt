package com.humberto.tasky.agenda.domain

import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.EventPhoto
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import java.time.ZonedDateTime

sealed class AgendaItem(
    open val id: String,
    open val title: String,
    open val description: String?,
    open val from: ZonedDateTime,
    open val reminderType: ReminderType
) {
    data class Event(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val reminderType: ReminderType,
        val to: ZonedDateTime,
        val attendees: List<Attendee>,
        val photos: List<EventPhoto>,
        val isUserEventCreator: Boolean
    ) : AgendaItem(id, title, description, from, reminderType) {

        fun getAttendee(userId: String): Attendee? {
            return attendees.find { it.userId == userId }
        }

        companion object {
            const val MAX_PHOTO_AMOUNT = 10
            const val MAX_PHOTO_SIZE = 1_000_000
        }
    }

    data class Task(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val reminderType: ReminderType,
        val isDone: Boolean
    ) : AgendaItem(id, title, description, from, reminderType)

    data class Reminder(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val reminderType: ReminderType
    ) : AgendaItem(id, title, description, from, reminderType)
}