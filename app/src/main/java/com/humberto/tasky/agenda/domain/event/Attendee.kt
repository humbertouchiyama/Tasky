package com.humberto.tasky.agenda.domain.event

import java.time.ZonedDateTime

data class Attendee(
    val userId: String,
    val email: String,
    val fullName: String,
    val isEventCreator: Boolean,
    val eventId: String? = null,
    val isGoing: Boolean = true,
    val remindAt: ZonedDateTime? = null
)