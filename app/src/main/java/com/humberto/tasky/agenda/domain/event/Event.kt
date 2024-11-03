package com.humberto.tasky.agenda.domain.event

import java.time.ZonedDateTime

data class Event(
    val id: String?,
    val title: String,
    val description: String?,
    val from: ZonedDateTime,
    val to: ZonedDateTime,
    val remindAt: ZonedDateTime,
    val attendees: List<Attendee>,
    val photos: List<Photo>,
    val isGoing: Boolean
)
