package com.humberto.tasky.agenda.presentation.agenda_details.model

data class AttendeeUi(
    val userId: String,
    val fullName: String,
    val email: String,
    val isGoing: Boolean,
    val isEventCreator: Boolean
)