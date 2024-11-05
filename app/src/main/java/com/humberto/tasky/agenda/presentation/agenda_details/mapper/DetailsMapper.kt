package com.humberto.tasky.agenda.presentation.agenda_details.mapper

import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi
import com.humberto.tasky.core.presentation.ui.UiText
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.round

fun AgendaItem.toAgendaDetailsUi(): AgendaDetailsUi {
    return when (this) {
        is AgendaItem.TaskItem -> AgendaDetailsUi(
            id = task.id ?: UUID.randomUUID().toString(),
            title = task.title,
            description = task.description ?: "",
            atDate = task.time.toLocalDate(),
            atTime = task.time.toLocalTime(),
            agendaItemType = AgendaItemType.TASK
        )
        is AgendaItem.EventItem -> AgendaDetailsUi(
            id = event.id ?: UUID.randomUUID().toString(),
            title = event.title,
            description = event.description ?: "",
            fromTime = event.from.toLocalTime(),
            fromDate = event.from.toLocalDate(),
            toTime = event.to.toLocalTime(),
            toDate = event.to.toLocalDate(),
            agendaItemType = AgendaItemType.EVENT
        )
        is AgendaItem.ReminderItem -> AgendaDetailsUi(
            id = reminder.id ?: UUID.randomUUID().toString(),
            title = reminder.title,
            description = reminder.description ?: "",
            atDate = reminder.time.toLocalDate(),
            atTime = reminder.time.toLocalTime(),
            agendaItemType = AgendaItemType.REMINDER
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

val AgendaDetailsUi.eventDateTime: LocalDateTime
    get() = when(agendaItemType) {
        AgendaItemType.EVENT -> fromDate.atTime(fromTime)
        else -> atDate.atTime(atTime)
    }

fun AgendaDetailsUi.toRemindAtText(): UiText {
    val differenceInSeconds = Duration.between(remindAt, eventDateTime).seconds
    val differenceInMinutes = round(differenceInSeconds / 60.0).toLong()
    return getReminderOptions[differenceInMinutes]!!
}

val getReminderOptions = mapOf(
    10L to UiText.StringResource(id = R.string.ten_minutes_before),
    30L to UiText.StringResource(id = R.string.thirty_minutes_before),
    60L to UiText.StringResource(id = R.string.one_hour_before),
    360L to UiText.StringResource(id = R.string.six_hours_before),
    1440L to UiText.StringResource(id = R.string.one_day_before)
)