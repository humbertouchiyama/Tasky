package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class ReminderEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long
)