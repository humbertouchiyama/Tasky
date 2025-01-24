package com.humberto.tasky.agenda.domain.event

data class Attendee(
    val userId: String,
    val email: String,
    val fullName: String,
    val eventId: String? = null,
    val isGoing: Boolean = true
)