package com.humberto.tasky.task.domain

import java.time.ZonedDateTime

data class Task(
    val id: String?,
    val title: String,
    val description: String?,
    val time: ZonedDateTime,
    val remindAt: ZonedDateTime,
    val isDone: Boolean
)
