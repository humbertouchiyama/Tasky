package com.humberto.tasky.agenda.domain.task

import java.time.ZonedDateTime

data class Task(
    val id: String?,
    val title: String,
    val description: String?,
    val time: ZonedDateTime,
    val remindAt: ZonedDateTime,
    val isDone: Boolean
)
