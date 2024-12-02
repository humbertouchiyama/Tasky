package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.humberto.tasky.core.database.ModificationType

@Entity
data class ModifiedTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: String,
    val modificationType: ModificationType,
    val title: String? = null,
    val description: String? = null,
    val time: Long? = null,
    val remindAt: Long? = null,
    val isDone: Boolean? = null,
    val timestamp: Long = System.currentTimeMillis()
)
