package com.humberto.tasky.reminder.domain

import java.time.ZonedDateTime

data class Reminder(
    val id: String?,
    val title: String,
    val description: String?,
    val time: ZonedDateTime,
    val remindAt: ZonedDateTime
)
