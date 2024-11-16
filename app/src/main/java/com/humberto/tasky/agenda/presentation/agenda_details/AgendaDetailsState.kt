package com.humberto.tasky.agenda.presentation.agenda_details

import java.time.LocalDate
import java.time.LocalTime

data class AgendaDetailsState(
    val id: String,
    val agendaItem: AgendaItemDetails,
    val title: String = "",
    val description: String = "",
    val fromDate: LocalDate = LocalDate.now(),
    val fromTime: LocalTime = LocalTime.now(),
    val reminderType: ReminderType = ReminderType.ThirtyMinutes,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isConfirmingToDelete: Boolean = false,
    val isDeleting: Boolean = false
)

sealed interface AgendaItemDetails {
    data class Event(
        val toDate: LocalDate = LocalDate.now(),
        val toTime: LocalTime = LocalTime.now().plusMinutes(30L),
        val selectedFilter: FilterType = FilterType.ALL,
        val photosUrlList: List<String> = listOf(),
        val isUserEventCreator: Boolean = true
    ): AgendaItemDetails

    data class Task(val isDone: Boolean = false): AgendaItemDetails

    data object Reminder: AgendaItemDetails
}
