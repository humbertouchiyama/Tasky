package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.humberto.tasky.agenda.domain.event.EventPhoto
import java.util.UUID

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String?,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val isUserEventCreator: Boolean,
    val attendees: List<LocalAttendee>,
    val photos: List<EventPhoto.Remote>
)