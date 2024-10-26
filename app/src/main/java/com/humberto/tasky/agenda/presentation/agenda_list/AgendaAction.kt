package com.humberto.tasky.agenda.presentation.agenda_list

import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import java.time.LocalDate

sealed interface AgendaAction {
    data object OnLogoutClick: AgendaAction
    data class OnNewAgendaItemClick(val agendaItemType: AgendaItemType): AgendaAction
    data class OnOpenAgendaItemClick(val agendaItemUi: AgendaItemUi): AgendaAction
    data class OnEditAgendaItemClick(val agendaItemUi: AgendaItemUi): AgendaAction
    data class OnDeleteAgendaItemClick(val agendaItemUi: AgendaItemUi): AgendaAction
    data class OnSelectDate(
        val selectedDate: LocalDate
    ): AgendaAction
}