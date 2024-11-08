package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsState
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.agenda.presentation.agenda_details.ReminderType
import com.humberto.tasky.core.presentation.ui.UiText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

fun AgendaItem.toAgendaState(): AgendaDetailsState {
    return when (this) {
        is AgendaItem.TaskItem -> AgendaDetailsState(
            id = task.id ?: UUID.randomUUID().toString(),
            title = task.title,
            description = task.description ?: "",
            fromDate = task.time.toLocalDate(),
            fromTime = task.time.toLocalTime(),
            agendaItem = AgendaItemDetails.Task(),
        )
        is AgendaItem.EventItem -> AgendaDetailsState(
            id = event.id ?: UUID.randomUUID().toString(),
            title = event.title,
            description = event.description ?: "",
            fromTime = event.from.toLocalTime(),
            fromDate = event.from.toLocalDate(),
            agendaItem = AgendaItemDetails.Event(
                toTime = event.to.toLocalTime(),
                toDate = event.to.toLocalDate(),
            ),
        )
        is AgendaItem.ReminderItem -> AgendaDetailsState(
            id = reminder.id ?: UUID.randomUUID().toString(),
            title = reminder.title,
            description = reminder.description ?: "",
            fromDate = reminder.time.toLocalDate(),
            fromTime = reminder.time.toLocalTime(),
            agendaItem = AgendaItemDetails.Reminder
        )
    }
}

fun LocalDate.toFormatted(): String {
    val dateInLocalZone = this
        .atStartOfDay(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd yyyy")
        .format(dateInLocalZone)
}

fun LocalTime.toFormatted(): String {
    return DateTimeFormatter
        .ofPattern("HH:mm")
        .format(this)
}

val AgendaDetailsState.eventDateTime: LocalDateTime
    get() =  fromDate.atTime(fromTime)

fun ReminderType.toReminderText(): UiText {
    return when(this) {
        ReminderType.TenMinutes -> UiText.StringResource(id = R.string.ten_minutes_before)
        ReminderType.ThirtyMinutes -> UiText.StringResource(id = R.string.thirty_minutes_before)
        ReminderType.OneHour -> UiText.StringResource(id = R.string.one_hour_before)
        ReminderType.SixHours -> UiText.StringResource(id = R.string.six_hours_before)
        ReminderType.OneDay -> UiText.StringResource(id = R.string.one_day_before)
    }
}

fun ReminderType.toReminderTime(): Long {
    return when (this) {
        ReminderType.TenMinutes -> 10L
        ReminderType.ThirtyMinutes -> 30L
        ReminderType.OneHour -> 60L
        ReminderType.SixHours -> 360L
        ReminderType.OneDay -> 1440L
    }
}