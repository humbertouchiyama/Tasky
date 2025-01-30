package com.humberto.tasky.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.humberto.tasky.core.database.ModificationType

@Entity
data class EventPendingSyncEntity(
    @Embedded val event: EventEntity,
    @PrimaryKey(autoGenerate = false)
    val eventId: String = event.id,
    val userId: String,
    val type: ModificationType
)
