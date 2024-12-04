package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.ui.UiText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

enum class ReminderType(val duration: Duration) {
    TenMinutes(10.minutes),
    ThirtyMinutes(30.minutes),
    OneHour(1.hours),
    SixHours(6.hours),
    OneDay(1.days);

    companion object {
        fun fromDuration(duration: Duration): ReminderType? {
            return entries.find { it.duration == duration }
        }
    }
}

fun ReminderType.toReminderText(): UiText {
    return when(this) {
        ReminderType.TenMinutes -> UiText.StringResource(id = R.string.ten_minutes_before)
        ReminderType.ThirtyMinutes -> UiText.StringResource(id = R.string.thirty_minutes_before)
        ReminderType.OneHour -> UiText.StringResource(id = R.string.one_hour_before)
        ReminderType.SixHours -> UiText.StringResource(id = R.string.six_hours_before)
        ReminderType.OneDay -> UiText.StringResource(id = R.string.one_day_before)
    }
}