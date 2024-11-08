package com.humberto.tasky.agenda.presentation.agenda_details

import java.time.LocalDate
import java.time.LocalTime

sealed interface AgendaItemDetails {
    data class Event(
        val toDate: LocalDate = LocalDate.now(),
        val toTime: LocalTime = LocalTime.now().plusMinutes(30L),
        val selectedFilter: FilterType = FilterType.ALL,
        val photosUrlList: List<String> = listOf()
    ): AgendaItemDetails

    data class Task(val isDone: Boolean = false): AgendaItemDetails

    data object Reminder: AgendaItemDetails
}
