package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.updateWithAgendaItem
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import com.humberto.tasky.core.domain.util.Result
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val reminderRepository: ReminderRepository
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
            },
        )
    )
    val state: StateFlow<AgendaDetailsState> = _state.asStateFlow()

    private val eventChannel = Channel<AgendaDetailsEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        if (agendaDetailsArgs.agendaItemId != null) {
            getItemById(
                id = agendaDetailsArgs.agendaItemId,
                type = agendaDetailsArgs.agendaItemType
            )
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
                        agendaItem = it.agendaItem.updateIfType<AgendaItemDetails.Event> {
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
                        agendaItem = currentState.agendaItem.updateIfType<AgendaItemDetails.Event> {
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
                        agendaItem = currentState.agendaItem.updateIfType<AgendaItemDetails.Event> {
                            copy(toTime = newToTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfType<AgendaItemDetails.Event> {
                            copy(toDate = agendaDetailsAction.toDate)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToTime -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfType<AgendaItemDetails.Event> {
                            copy(toTime = agendaDetailsAction.toTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectReminderType -> {
                _state.update { it.copy(reminderType = agendaDetailsAction.reminderType) }
            }
            else -> Unit
        }
    }

    private inline fun <reified T : AgendaItemDetails> AgendaItemDetails.updateIfType(
        crossinline transform: T.() -> T
    ): AgendaItemDetails {
        return (this as? T)?.transform() ?: this
    }

    private fun saveItem() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val state = _state.value
            val result = when(val agendaItem = state.toAgendaItem()) {
                is AgendaItem.Task -> taskRepository.createTask(agendaItem)
                is AgendaItem.Event -> eventRepository.createEvent(agendaItem)
                is AgendaItem.Reminder -> reminderRepository.createReminder(agendaItem)
            }
            when (result) {
                is Result.Success -> {
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

}