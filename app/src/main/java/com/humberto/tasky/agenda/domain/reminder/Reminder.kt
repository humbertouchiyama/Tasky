package com.humberto.tasky.agenda.domain.reminder

import java.time.ZonedDateTime

data class Reminder(
    val id: String?,
    val title: String,
    val description: String?,
    val time: ZonedDateTime,
    val remindAt: ZonedDateTime
)
