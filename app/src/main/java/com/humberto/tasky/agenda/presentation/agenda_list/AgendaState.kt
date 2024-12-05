package com.humberto.tasky.agenda.presentation.agenda_list

import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import com.humberto.tasky.core.presentation.ui.UiText
import com.humberto.tasky.core.presentation.ui.buildHeaderDate
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import java.time.LocalDate

data class AgendaState(
    val initials: String = "",
    val dateLabel: UiText = LocalDate.now().buildHeaderDate(),
    val selectedDate: LocalDate = LocalDate.now(),
    val upperCaseMonth: String = LocalDate.now().displayUpperCaseMonth(),
    val isLoggingOut: Boolean = false,
    val agendaItems: List<AgendaItemUi> = listOf(),
    val isLoadingAgendaItems: Boolean = false,
    val confirmingItemToBeDeleted: AgendaItemUi? = null,
    val isDeletingItem: Boolean = false,
    val isSyncingPendingItems: Boolean = false,
    val isRefreshing: Boolean = false
)