package com.humberto.tasky.agenda.presentation.agenda_details

import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.core.presentation.ui.UiText
import java.time.Duration
import java.time.ZonedDateTime

typealias Minutes = Long

enum class ReminderType {
    TenMinutes,
    ThirtyMinutes,
    OneHour,
    SixHours,
    OneDay
}

fun AgendaItem.getReminderType(): ReminderType? {
    val minutesDifference = Duration.between(from, remindAt).toMinutes()
    return ReminderType.entries.find { it.toReminderMinutes() == minutesDifference }
}

private fun ReminderType.toReminderMinutes(): Minutes {
    return when (this) {
        ReminderType.TenMinutes -> 10L
        ReminderType.ThirtyMinutes -> 30L
        ReminderType.OneHour -> 60L
        ReminderType.SixHours -> 360L
        ReminderType.OneDay -> 1440L
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

fun ReminderType.toReminderDateFromDateTime(dateTime: ZonedDateTime): ZonedDateTime {
    return dateTime.minusMinutes(toReminderMinutes())
}