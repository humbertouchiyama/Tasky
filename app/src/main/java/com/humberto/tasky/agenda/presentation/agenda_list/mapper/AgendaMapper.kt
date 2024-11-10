package com.humberto.tasky.agenda.presentation.agenda_list.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun AgendaItem.toAgendaItemUi(): AgendaItemUi {
    return when (this) {
        is AgendaItem.Task -> AgendaItemUi(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description ?: "",
            dateTime = from.toFormattedDateTime(),
            agendaItemType = AgendaItemType.TASK
        )
        is AgendaItem.Event -> AgendaItemUi(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description ?: "",
            dateTime = "${from.toFormattedDateTime()} - ${to.toFormattedDateTime()}",
            agendaItemType = AgendaItemType.EVENT
        )
        is AgendaItem.Reminder -> AgendaItemUi(
            id = id ?: UUID.randomUUID().toString(),
            title = title,
            description = description ?: "",
            dateTime = from.toFormattedDateTime(),
            agendaItemType = AgendaItemType.REMINDER
        )
    }
}

private fun ZonedDateTime.toFormattedDateTime(): String {
    val timeInLocalTime = this
        .withZoneSameInstant(ZoneId.systemDefault())
    return DateTimeFormatter
        .ofPattern("MMM dd, HH:mm")
        .format(timeInLocalTime)
}