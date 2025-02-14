package com.humberto.tasky.agenda.data.event

import kotlinx.serialization.Serializable

@Serializable
data class AttendeeDto(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    val isGoing: Boolean,
    val remindAt: Long
)
