package com.humberto.tasky.event.domain

import java.time.ZonedDateTime

data class Attendee(
    val userId: String,
    val email: String,
    val fullName: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: ZonedDateTime
)
