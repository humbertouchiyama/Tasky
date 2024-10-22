package com.humberto.tasky.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotoEntity(
    @PrimaryKey(autoGenerate = false)
    val key: String,
    val url: String
)
