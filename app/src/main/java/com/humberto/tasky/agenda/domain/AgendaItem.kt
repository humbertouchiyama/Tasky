package com.humberto.tasky.agenda.domain

import com.humberto.tasky.agenda.domain.event.Attendee
import com.humberto.tasky.agenda.domain.event.Photo
import java.time.ZonedDateTime

sealed class AgendaItem(
    open val id: String?,
    open val title: String,
    open val description: String?,
    open val from: ZonedDateTime,
    open val remindAt: ZonedDateTime
) {
    data class Event(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val remindAt: ZonedDateTime,
        val to: ZonedDateTime,
        val attendees: List<Attendee>,
        val photos: List<Photo>,
    ) : AgendaItem(id, title, description, from, remindAt)

    data class Task(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val remindAt: ZonedDateTime,
        val isDone: Boolean
    ) : AgendaItem(id, title, description, from, remindAt)

    data class Reminder(
        override val id: String,
        override val title: String,
        override val description: String?,
        override val from: ZonedDateTime,
        override val remindAt: ZonedDateTime
    ) : AgendaItem(id, title, description, from, remindAt)
}