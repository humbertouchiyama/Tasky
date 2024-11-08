package com.humberto.tasky.agenda.presentation.agenda_details

import java.time.LocalDate
import java.time.LocalTime

data class AgendaDetailsState(
    val id: String? = null,
    val agendaItem: AgendaItemDetails,
    val title: String = "",
    val description: String = "",
    val fromDate: LocalDate = LocalDate.now(),
    val fromTime: LocalTime = LocalTime.now(),
    val reminderType: ReminderType = ReminderType.ThirtyMinutes,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
)