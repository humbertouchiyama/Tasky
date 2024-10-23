package com.humberto.tasky.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.presentation.mapper.toAgendaItemUi
import com.humberto.tasky.agenda.presentation.model.AgendaItemUi
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.domain.agenda.Agenda
import com.humberto.tasky.core.domain.agenda.LocalAgendaDataSource
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private val agendaRepository: AgendaRepository,
    private val applicationScope: CoroutineScope,
    private val localAgendaDataSource: LocalAgendaDataSource
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
            val fullName = sessionManager.get()?.fullName
            fullName?.let {
                _agendaState.update { state ->
                    state.copy(
                        initials = fullName.toInitials()
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
            AgendaAction.OnLogoutClick -> logout()
            AgendaAction.OnNewEventClick -> { }
            AgendaAction.OnNewReminderClick -> { }
            AgendaAction.OnNewTaskClick -> { }
            else -> Unit
        }
    }

    private fun getAgendaForDate(selectedDate: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _agendaState.update { state ->
                state.copy(isLoadingAgendaItems = true)
            }
            localAgendaDataSource.getAgendaForDate(selectedDate)
                .collect { agenda ->
                    _agendaState.update { state ->
                        state.copy(
                            agendaItems = orderAgendaItems(agenda),
                            isLoadingAgendaItems = false
                        )
                    }
                }

        }
    }

    private fun orderAgendaItems(agenda: Agenda): List<AgendaItemUi> {
        val allItems = mutableListOf<AgendaItemUi>()
        allItems.addAll(agenda.events.map { event -> event.toAgendaItemUi() })
        allItems.addAll(agenda.tasks.map { task -> task.toAgendaItemUi() })
        allItems.addAll(agenda.reminders.map { reminder -> reminder.toAgendaItemUi() })
        return allItems.sortedBy { it.dateTime }
    }

    private fun logout() {
        applicationScope.launch {
            _agendaState.update { state ->
                state.copy(isLoggingOut = true)
            }
            agendaRepository.logout()
            sessionManager.set(null)
            localAgendaDataSource.deleteAllAgenda()
            eventChannel.send(AgendaEvent.LogoutSuccess)
            _agendaState.update { state ->
                state.copy(isLoggingOut = false)
            }
        }
    }
}