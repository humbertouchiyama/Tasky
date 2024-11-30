package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAttendeeUi
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.updateWithAgendaItem
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import com.humberto.tasky.core.alarm.domain.AlarmScheduler
import com.humberto.tasky.core.alarm.mapper.toAlarmItem
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.presentation.ui.UiText
import com.humberto.tasky.core.presentation.ui.asUiText
import com.humberto.tasky.main.navigation.AgendaDetails
import com.humberto.tasky.main.navigation.EditTextArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val reminderRepository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler
): ViewModel() {

    private val agendaDetailsArgs = savedStateHandle.toRoute<AgendaDetails>()

    private val _state = MutableStateFlow(
        AgendaDetailsState(
            id = agendaDetailsArgs.agendaItemId ?: UUID.randomUUID().toString(),
            isEditing = agendaDetailsArgs.isEditing,
            agendaItem = when(agendaDetailsArgs.agendaItemType) {
                AgendaItemType.EVENT -> AgendaItemDetails.Event()
                AgendaItemType.TASK -> AgendaItemDetails.Task()
                AgendaItemType.REMINDER -> AgendaItemDetails.Reminder
            }
        )
    )
    val state: StateFlow<AgendaDetailsState> = _state.asStateFlow()

    private val eventChannel = Channel<AgendaDetailsEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        agendaDetailsArgs.agendaItemId?.let { id ->
            getItemById(
                id = id,
                type = agendaDetailsArgs.agendaItemType
            )
        }
        agendaDetailsArgs.selectedDateEpochDay?.let { selectedDateEpochDay ->
            _state.update { currentState ->
                val selectedLocalDate = LocalDate.ofEpochDay(selectedDateEpochDay)
                currentState.copy(
                    fromDate = selectedLocalDate,
                    agendaItem = currentState.agendaItem.updateIfEvent {
                        copy(toDate = selectedLocalDate)
                    }
                )
            }
        }
    }
    
    fun updateStateWithEditTextArgs(editTextArgs: EditTextArgs?) {
        _state.update { currentState ->
            when(editTextArgs?.editTextScreenType) {
                EditTextScreenType.TITLE -> currentState.copy(title = editTextArgs.textToBeUpdated)
                EditTextScreenType.DESCRIPTION -> currentState.copy(description = editTextArgs.textToBeUpdated)
                else -> currentState
            }
        }
    }

    private fun getItemById(id: String, type: AgendaItemType) {
        viewModelScope.launch {
            val result = when (type) {
                AgendaItemType.TASK -> taskRepository.getTask(id)
                AgendaItemType.EVENT -> eventRepository.getEvent(id)
                AgendaItemType.REMINDER -> reminderRepository.getReminder(id)
            }

            when (result) {
                is Result.Success -> {
                    val agendaItem = result.data
                    _state.value = _state.value.updateWithAgendaItem(agendaItem)
                }
                is Result.Error -> {
                    eventChannel.send(AgendaDetailsEvent.Error(result.error.asUiText()))
                }
            }
        }
    }

    private fun toggleEditingState() {
        _state.update { it.copy(isEditing = !it.isEditing) }
    }

    fun onAction(agendaDetailsAction: AgendaDetailsAction) {
        when(agendaDetailsAction) {
            is AgendaDetailsAction.OnSelectFilter -> {
                _state.update {
                    it.copy(
                        agendaItem = it.agendaItem.updateIfEvent {
                            copy(selectedFilter = agendaDetailsAction.filterType)
                        },
                    )
                }
            }
            is AgendaDetailsAction.OnSaveClick -> {
                saveItem()
            }
            AgendaDetailsAction.OnEditClick -> {
                toggleEditingState()
            }
            is AgendaDetailsAction.OnSelectFromDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        fromDate = agendaDetailsAction.fromDate,
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toDate = agendaDetailsAction.fromDate)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectFromTime -> {
                val newFromTime = agendaDetailsAction.fromTime
                val newToTime = newFromTime.plusMinutes(30)

                _state.update { currentState ->
                    currentState.copy(
                        fromTime = newFromTime,
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toTime = newToTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toDate = agendaDetailsAction.toDate)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToTime -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toTime = agendaDetailsAction.toTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectReminderType -> {
                _state.update { it.copy(reminderType = agendaDetailsAction.reminderType) }
            }
            AgendaDetailsAction.OnManageItemStateButtonClick -> {
                _state.update { state ->
                    //TODO properly handle action depending on item state
                    state.copy(
                        isConfirmingToDelete = true
                    )
                }
            }
            AgendaDetailsAction.OnConfirmDeleteClick -> {
                deleteItem()
            }
            AgendaDetailsAction.OnDismissDeleteClick -> {
                _state.update { it.copy(isConfirmingToDelete = false) }
            }
            AgendaDetailsAction.OnOpenAttendeeDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(isAddingAttendee = true)
                        }
                    )
                }
            }
            AgendaDetailsAction.OnDismissAttendeeDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(isAddingAttendee = false)
                        }
                    )
                }
            }
            AgendaDetailsAction.OnAddAttendeeClick -> {
                checkAndAddAttendee()
            }
            is AgendaDetailsAction.SubmitNotificationPermissionInfo -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = agendaDetailsAction.showNotificationRationale
                    )
                }
            }
            AgendaDetailsAction.DismissRationaleDialog -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = false
                    )
                }
            }
            else -> Unit
        }
    }

    private inline fun <T : AgendaItemDetails> T.updateIfEvent(
        transform: AgendaItemDetails.Event.() -> AgendaItemDetails.Event
    ): AgendaItemDetails {
        return if (this is AgendaItemDetails.Event) {
            transform()
        } else {
            this
        }
    }

    private fun saveItem() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val agendaItem = _state.value.toAgendaItem()
            val result = when(agendaItem) {
                is AgendaItem.Task -> taskRepository.createTask(agendaItem)
                is AgendaItem.Event -> eventRepository.createEvent(agendaItem)
                is AgendaItem.Reminder -> reminderRepository.createReminder(agendaItem)
            }
            when (result) {
                is Result.Success -> {
                    alarmScheduler.scheduleAlarm(agendaItem.toAlarmItem())
                    toggleEditingState()
                    eventChannel.send(AgendaDetailsEvent.SaveSuccess)
                }
                is Result.Error -> {
                    eventChannel.send(AgendaDetailsEvent.Error(result.error.asUiText()))
                }
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun deleteItem() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            val agendaItem = _state.value.toAgendaItem()
            when(agendaItem) {
                is AgendaItem.Event -> eventRepository.deleteEvent(_state.value.id)
                is AgendaItem.Reminder -> reminderRepository.deleteReminder(_state.value.id)
                is AgendaItem.Task -> taskRepository.deleteTask(_state.value.id)
            }
            alarmScheduler.cancelAlarm(agendaItem.id)
            eventChannel.send(AgendaDetailsEvent.DeleteSuccess)
            _state.update {
                it.copy(
                    isDeleting = false,
                    isConfirmingToDelete = false
                )
            }
        }
    }

    private fun checkAndAddAttendee() {
        viewModelScope.launch {
            val agendaItem = _state.value.agendaItem
            if (agendaItem is AgendaItemDetails.Event) {
                _state.update {
                    it.copy(agendaItem = agendaItem.copy(isCheckingIfAttendeeExists = true))
                }
                eventRepository
                    .checkAttendeeExists(
                        email = agendaItem.newAttendeeEmail.text.toString()
                    ).onSuccess { attendee ->
                        _state.update {
                            it.copy(
                                agendaItem = agendaItem.copy(
                                    attendees = agendaItem.attendees + attendee.toAttendeeUi(),
                                    newAttendeeEmail = TextFieldState(),
                                    isAddingAttendee = false,
                                    isCheckingIfAttendeeExists = false
                                )
                            )
                        }
                    }.onError { error ->
                        when(error) {
                            DataError.Network.NOT_FOUND -> {
                                eventChannel.send(AgendaDetailsEvent.Error(
                                    UiText.StringResource(R.string.user_not_found)
                                ))
                            }
                            DataError.Network.CONFLICT -> {
                                eventChannel.send(AgendaDetailsEvent.Error(
                                    UiText.StringResource(R.string.you_cant_add_yourself)
                                ))
                            }
                            else -> eventChannel.send(AgendaDetailsEvent.Error(error.asUiText()))
                        }
                        _state.update {
                            it.copy(agendaItem = agendaItem.copy(isCheckingIfAttendeeExists = false))
                        }
                    }
            }
        }
    }
}