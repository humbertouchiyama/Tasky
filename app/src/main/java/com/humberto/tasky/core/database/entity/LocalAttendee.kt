package com.humberto.tasky.core.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class LocalAttendee(
    val userId: String,
    val email: String,
    val fullName: String,
    val eventId: String,
    val isGoing: Boolean = false,
    val remindAt: Long
)
