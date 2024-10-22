package com.humberto.tasky.core.domain.reminder

import java.time.ZonedDateTime

data class Reminder(
    val id: String?,
    val title: String,
    val description: String?,
    val time: ZonedDateTime,
    val remindAt: ZonedDateTime
)
