package com.humberto.tasky.agenda.presentation.agenda_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.presentation.mapper.toAgendaItemUi
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import com.humberto.tasky.event.domain.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope,
    private val agendaRepository: AgendaRepository,
    private val eventRepository: EventRepository
): ViewModel() {

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState: StateFlow<AgendaState> = _agendaState.asStateFlow()

    private val eventChannel = Channel<AgendaEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        buildUserInitials()
        getAgendaForDate(LocalDate.now())
    }

    private fun buildUserInitials() {
        viewModelScope.launch {
            sessionManager.get()?.let { authInfo ->
                _agendaState.update { state ->
                    state.copy(
                        initials = authInfo.fullName.toInitials()
                    )
                }
            }
        }
    }

    fun onAction(action: AgendaAction) {
        when(action) {
            is AgendaAction.OnSelectDate -> {
                val selectedDate = action.selectedDate
                _agendaState.update { state ->
                    state.copy(
                        selectedDate = selectedDate,
                        upperCaseMonth = selectedDate.displayUpperCaseMonth(),
                        selectedDateIsToday = selectedDate.isEqual(LocalDate.now())
                    )
                }
                getAgendaForDate(selectedDate)
            }
            AgendaAction.OnLogoutClick -> {
                logout()
            }
            is AgendaAction.OnDeleteAgendaItemClick -> {}
            is AgendaAction.OnEditAgendaItemClick -> TODO()
            is AgendaAction.OnOpenAgendaItemClick -> TODO()
            else -> Unit
        }
    }

    private fun getAgendaForDate(selectedDate: LocalDate) {
        viewModelScope.launch {
            _agendaState.update { state ->
                state.copy(isLoadingAgendaItems = true)
            }
            agendaRepository.getAgendaForDate(selectedDate)
                .collect { agendaItems ->
                    _agendaState.update { state ->
                        state.copy(
                            agendaItems = agendaItems.map { it.toAgendaItemUi() },
                            isLoadingAgendaItems = false
                        )
                    }
                }

        }
    }

    private fun logout() {
        applicationScope.launch {
            _agendaState.update { state ->
                state.copy(isLoggingOut = true)
            }
            agendaRepository.deleteAllAgenda()
            agendaRepository.logout()
            sessionManager.set(null)
            eventChannel.send(AgendaEvent.LogoutSuccess)
            _agendaState.update { state ->
                state.copy(isLoggingOut = false)
            }
        }
    }
}