package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AttendeeEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val email: String,
    val fullName: String,
    val eventId: String,
    val isGoing: Boolean = false,
    val remindAt: Long
)
