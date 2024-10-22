package com.humberto.tasky.core.domain.event

import java.time.ZonedDateTime

data class Attendee(
    val userId: String,
    val email: String,
    val fullName: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: ZonedDateTime
)
