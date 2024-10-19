package com.humberto.tasky.agenda.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val agendaRepository: AgendaRepository,
    private val applicationScope: CoroutineScope
): ViewModel() {

    var state by mutableStateOf(AgendaState())
        private set

    private val eventChannel = Channel<AgendaEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        buildUserInitials()
    }

    private fun buildUserInitials() {
        val fullName = sessionManager.get()?.fullName
        fullName?.let {
            state = state.copy(
                initials = fullName.toInitials()
            )
        }
    }

    fun onAction(action: AgendaAction) {
        when(action) {
            is AgendaAction.OnSelectDate -> {
                val selectedDate = action.selectedDate
                state = state.copy(
                    selectedDate = selectedDate,
                    upperCaseMonth = selectedDate.displayUpperCaseMonth(),
                    selectedDateIsToday = selectedDate.isEqual(LocalDate.now())
                )
            }
            AgendaAction.OnLogoutClick -> logout()
            AgendaAction.OnNewEventClick -> { }
            AgendaAction.OnNewReminderClick -> { }
            AgendaAction.OnNewTaskClick -> { }
            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            state = state.copy(isLoggingOut = true)
            agendaRepository.logout()
            sessionManager.set(null)
            eventChannel.send(AgendaEvent.LogoutSuccess)
            state = state.copy(isLoggingOut = false)
        }
    }
}