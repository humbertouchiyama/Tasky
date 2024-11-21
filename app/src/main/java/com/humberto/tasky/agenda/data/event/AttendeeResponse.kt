package com.humberto.tasky.agenda.data.event

import kotlinx.serialization.Serializable

@Serializable
data class CheckAttendeeExistsResponse(
    val doesUserExist: Boolean,
    val attendee: AttendeeResponse?
)

@Serializable
data class AttendeeResponse(
    val email: String,
    val fullName: String,
    val userId: String
)
