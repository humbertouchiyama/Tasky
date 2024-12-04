package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedReminderSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val reminderId: String,
    val userId: String
)
