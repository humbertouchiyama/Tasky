package com.humberto.tasky.core.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.humberto.tasky.core.database.ModificationType

@Entity
data class TaskPendingSyncEntity(
    @Embedded val task: TaskEntity,
    @PrimaryKey(autoGenerate = false)
    val taskId: String = task.id,
    val userId: String,
    val type: ModificationType
)
