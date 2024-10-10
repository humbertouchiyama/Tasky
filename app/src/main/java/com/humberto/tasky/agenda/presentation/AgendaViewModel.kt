package com.humberto.tasky.agenda.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(): ViewModel() {

    var state by mutableStateOf(AgendaState())
        private set

    fun onAction(action: AgendaAction) {
        when(action) {
            is AgendaAction.OnSelectedDate -> {
                val selectedDate = action.selectedDate
                state = state.copy(
                    selectedDate = selectedDate,
                    upperCaseMonth = selectedDate.displayUpperCaseMonth(),
                    selectedDateIsToday = selectedDate.isEqual(LocalDate.now())
                )
            }
            AgendaAction.OnLogoutClick -> { }
            AgendaAction.OnNewEventClick -> { }
            AgendaAction.OnNewReminderClick -> { }
            AgendaAction.OnNewTaskClick -> { }
            else -> Unit
        }
    }
}