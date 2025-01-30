package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedEventSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val eventId: String,
    val userId: String
)
