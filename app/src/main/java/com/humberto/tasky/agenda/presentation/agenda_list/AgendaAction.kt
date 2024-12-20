package com.humberto.tasky.agenda.presentation.agenda_list

import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import com.humberto.tasky.main.navigation.AgendaDetails
import java.time.LocalDate

sealed interface AgendaAction {
    data object OnLogoutClick: AgendaAction
    data class OnNewAgendaItemClick(val agendaDetails: AgendaDetails): AgendaAction
    data class OnOpenAgendaItemClick(val agendaDetails: AgendaDetails): AgendaAction
    data class OnEditAgendaItemClick(val agendaDetails: AgendaDetails): AgendaAction
    data class OnDeleteAgendaItemClick(val itemToBeDeleted: AgendaItemUi): AgendaAction
    data object OnConfirmDeleteAgendaItemClick: AgendaAction
    data object OnDismissDeleteAgendaItemClick: AgendaAction
    data class OnSelectDate(
        val selectedDate: LocalDate
    ): AgendaAction
    data object OnRefresh: AgendaAction
}