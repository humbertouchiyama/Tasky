package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeletedTaskSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val taskId: String,
    val userId: String
)
