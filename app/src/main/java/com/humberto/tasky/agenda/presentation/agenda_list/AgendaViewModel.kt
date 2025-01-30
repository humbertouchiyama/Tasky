package com.humberto.tasky.agenda.presentation.agenda_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.domain.AgendaSynchronizer
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.presentation.agenda_list.model.AgendaItemUi
import com.humberto.tasky.agenda.presentation.agenda_list.model.insertNeedle
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.domain.ConnectivityObserver
import com.humberto.tasky.core.domain.alarm.AlarmScheduler
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.presentation.ui.asUiText
import com.humberto.tasky.core.presentation.ui.buildHeaderDate
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope,
    private val agendaRepository: AgendaRepository,
    private val eventRepository: EventRepository,
    private val taskRepository: TaskRepository,
    private val reminderRepository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler,
    private val connectivityObserver: ConnectivityObserver,
    private val agendaSynchronizer: AgendaSynchronizer
): ViewModel() {

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState: StateFlow<AgendaState> = _agendaState.asStateFlow()

    private val eventChannel = Channel<AgendaEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        buildUserInitials()
        getAgendaForDate(LocalDate.now())
        agendaSynchronizer.scheduleSync()
        observeConnectivity()
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

    fun updateSelectedDate(newSelectedDate: LocalDate) {
        _agendaState.update { state ->
            newSelectedDate.let {
                state.copy(
                    selectedDate = it,
                    upperCaseMonth = newSelectedDate.displayUpperCaseMonth(),
                    dateLabel = newSelectedDate.buildHeaderDate()
                )
            }
        }
        getAgendaForDate(newSelectedDate)
    }

    fun onAction(action: AgendaAction) {
        when(action) {
            is AgendaAction.OnSelectDate -> {
                val selectedDate = action.selectedDate
                updateSelectedDate(selectedDate)
                getAgendaForDate(selectedDate)
            }
            AgendaAction.OnLogoutClick -> {
                logout()
            }
            is AgendaAction.OnDeleteAgendaItemClick -> {
                _agendaState.update { it.copy(confirmingItemToBeDeleted = action.itemToBeDeleted) }
            }
            AgendaAction.OnConfirmDeleteAgendaItemClick -> {
                deleteItem()
            }
            AgendaAction.OnDismissDeleteAgendaItemClick -> {
                _agendaState.update { it.copy(confirmingItemToBeDeleted = null) }
            }
            AgendaAction.OnRefresh -> refreshAgenda()
            is AgendaAction.OnToggleCheckForTask -> toggleTaskCheck(action.item)
            else -> Unit
        }
    }

    private fun toggleTaskCheck(item: AgendaItem) {
        if (item !is AgendaItem.Task) return
        val newTask = item.copy(isDone = !item.isDone)
        viewModelScope.launch {
            taskRepository.updateTask(newTask)
        }
        _agendaState.update {
            it.copy(
                agendaItems = it.agendaItems.map { agendaItemUi ->
                    if (agendaItemUi is AgendaItemUi.Item && agendaItemUi.item.id == newTask.id) {
                        AgendaItemUi.Item(newTask)
                    } else agendaItemUi
                }
            )
        }
    }

    private var refreshJob: Job? = null

    private fun refreshAgenda() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _agendaState.update { it.copy(isRefreshing = true) }
            agendaRepository.syncAndUpdateCache(
                time = agendaState.value
                    .selectedDate
                    .atTime(12, 0)
                    .atZone(ZoneId.systemDefault()),
                updateTimeOnly = true
            )
                .onSuccess {
                    _agendaState.update { it.copy(isRefreshing = false) }
                }
                .onError { error ->
                    // show error msg
                    _agendaState.update { it.copy(isRefreshing = false) }
                }
        }
    }

    private fun observeConnectivity() {
        connectivityObserver
            .startObserving()
            .distinctUntilChanged()
            .onEach { status ->
                if (status == ConnectivityObserver.ConnectivityStatus.Available) {
                    refreshAgenda()
                }
            }
            .launchIn(viewModelScope)
    }

    private var getAgendaForDateJob: Job? = null

    private fun getAgendaForDate(selectedDate: LocalDate) {
        getAgendaForDateJob?.cancel()
        getAgendaForDateJob = viewModelScope.launch {
            _agendaState.update { state ->
                state.copy(isLoadingAgendaItems = true)
            }
            agendaRepository.getAgendaForDate(selectedDate)
                .collect { agendaItems ->
                    _agendaState.update { state ->
                        state.copy(
                            agendaItems = insertNeedle(agendaItems.map { AgendaItemUi.Item(it) }),
                            isLoadingAgendaItems = false
                        )
                    }
                }
        }
    }

    private fun deleteItem() {
        applicationScope.launch {
            _agendaState.update { it.copy(isDeletingItem = true) }
            _agendaState.value.confirmingItemToBeDeleted?.let { item ->
                when (item) {
                    is AgendaItem.Event -> eventRepository.deleteEvent(item.id)
                    is AgendaItem.Reminder -> reminderRepository.deleteReminder(item.id)
                    is AgendaItem.Task -> taskRepository.deleteTask(item.id)
                }.onSuccess {
                    alarmScheduler.cancelAlarm(item.id)
                }.onError { error ->
                    eventChannel.send(AgendaEvent.Error(error.asUiText()))
                }
            }
            _agendaState.update {
                it.copy(
                    isDeletingItem = false,
                    confirmingItemToBeDeleted = null
                )
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