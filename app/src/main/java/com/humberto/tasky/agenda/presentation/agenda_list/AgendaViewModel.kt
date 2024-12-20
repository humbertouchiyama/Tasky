package com.humberto.tasky.agenda.presentation.agenda_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.humberto.tasky.agenda.data.agenda.SyncFullAgendaWorker
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.mapper.toAgendaItemUi
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.alarm.domain.AlarmScheduler
import com.humberto.tasky.core.domain.ConnectivityObserver
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.presentation.ui.asUiText
import com.humberto.tasky.core.presentation.ui.buildHeaderDate
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit
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
    connectivityObserver: ConnectivityObserver,
    private val workManager: WorkManager
): ViewModel() {

    private val _agendaState = MutableStateFlow(AgendaState())
    val agendaState: StateFlow<AgendaState> = _agendaState.asStateFlow()

    private val eventChannel = Channel<AgendaEvent>()
    val events = eventChannel.receiveAsFlow()

    private val isConnected = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    init {
        buildUserInitials()
        getAgendaForDate(LocalDate.now())
        syncFullAgenda()
    }

    private fun syncFullAgenda() {
        val request = PeriodicWorkRequestBuilder<SyncFullAgendaWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            "SyncFullAgendaWork",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
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
            AgendaAction.OnRefresh -> {
                _agendaState.update { it.copy(isRefreshing = true) }
                // agendaRepository.getAgenda()
                _agendaState.update { it.copy(isRefreshing = false) }
            }
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

    private fun deleteItem() {
        applicationScope.launch {
            _agendaState.update { it.copy(isDeletingItem = true) }
            _agendaState.value.confirmingItemToBeDeleted?.let { itemUi ->
                when(itemUi.agendaItemType) {
                    AgendaItemType.TASK -> taskRepository.deleteTask(itemUi.id)
                    AgendaItemType.EVENT -> eventRepository.deleteEvent(itemUi.id)
                    AgendaItemType.REMINDER -> reminderRepository.deleteReminder(itemUi.id)
                }.onSuccess {
                    alarmScheduler.cancelAlarm(itemUi.id)
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

    fun syncAllPendingItems() {
        applicationScope.launch {
            isConnected.collect { connected ->
                if(connected && !_agendaState.value.isSyncingPendingItems) {
                    _agendaState.update { state ->
                        state.copy(isSyncingPendingItems = true)
                    }
                    listOf(
                        async { agendaRepository.syncDeletedAgendaItems() },
                    ).awaitAll()
                    _agendaState.update { state ->
                        state.copy(isSyncingPendingItems = false)
                    }
                    return@collect
                }
            }
        }
    }
}