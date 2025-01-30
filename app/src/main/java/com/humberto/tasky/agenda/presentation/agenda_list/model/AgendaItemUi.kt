package com.humberto.tasky.agenda.presentation.agenda_list.model

import com.humberto.tasky.agenda.domain.AgendaItem
import java.time.ZonedDateTime

sealed interface AgendaItemUi {
    data class Item(val item: AgendaItem): AgendaItemUi
    data object Needle: AgendaItemUi
}

fun insertNeedle(items: List<AgendaItemUi>): List<AgendaItemUi> {
    val now = ZonedDateTime.now()

    val itemsWithoutNeedle = items.filter { it !is AgendaItemUi.Needle }

    val sortedItems = itemsWithoutNeedle
        .filterIsInstance<AgendaItemUi.Item>()
        .sortedBy { it.item.from }

    val needlePosition = sortedItems.indexOfFirst { it.item.from.isAfter(now) }

    val position = if (needlePosition == -1) sortedItems.size else needlePosition

    return sortedItems
        .map { it as AgendaItemUi }
        .toMutableList()
        .apply {
            add(position, AgendaItemUi.Needle)
        }
}