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
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.eventDateTime
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAgendaDetailsUi
import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.map
import com.humberto.tasky.core.presentation.ui.asUiText
import com.humberto.tasky.main.navigation.AgendaDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            agendaItem = AgendaDetailsUi(
                id = agendaDetailsArgs.agendaItemId,
                agendaItemType = agendaDetailsArgs.agendaItemType,
            ),
            isEditing = agendaDetailsArgs.isEditing
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
    
    fun updateTitle(title: String, description: String) {
        _state.update { currentState ->
            val updatedAgendaItem = currentState.agendaItem.copy(
                title = title,
                description = description
            )
            currentState.copy(agendaItem = updatedAgendaItem)
        }
    }

    private fun getItemById(id: String, type: AgendaItemType) {
        viewModelScope.launch {
            val result = when (type) {
                AgendaItemType.TASK -> taskRepository.getTask(id).map { AgendaItem.TaskItem(it) }
                AgendaItemType.EVENT -> eventRepository.getEvent(id).map { AgendaItem.EventItem(it) }
                AgendaItemType.REMINDER -> reminderRepository.getReminder(id).map { AgendaItem.ReminderItem(it) }
            }

            when (result) {
                is Result.Success -> {
                    val agendaItem = result.data
                    _state.update {
                        it.copy(agendaItem = agendaItem.toAgendaDetailsUi())
                    }
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
                _state.update { it.copy(selectedFilter = agendaDetailsAction.filterType) }
            }
            is AgendaDetailsAction.OnSaveClick -> {
                toggleEditingState()
            }
            AgendaDetailsAction.OnEditClick -> {
                toggleEditingState()
            }
            is AgendaDetailsAction.OnSelectAtDate -> {
                updateAgendaItem { it.copy(atDate = agendaDetailsAction.atDate) }
            }
            is AgendaDetailsAction.OnSelectAtTime -> {
                updateAgendaItem { it.copy(atTime = agendaDetailsAction.atTime) }
            }
            is AgendaDetailsAction.OnSelectFromDate -> {
                updateAgendaItem { it.copy(
                        fromDate = agendaDetailsAction.fromDate,
                        toDate = agendaDetailsAction.fromDate
                    )
                }
            }
            is AgendaDetailsAction.OnSelectFromTime -> {
                val newFromTime = agendaDetailsAction.fromTime
                val newToTime = newFromTime.plusMinutes(30)
                updateAgendaItem { it.copy(fromTime = newFromTime, toTime = newToTime) }
            }
            is AgendaDetailsAction.OnSelectToDate -> {
                updateAgendaItem { it.copy(toDate = agendaDetailsAction.toDate) }
            }
            is AgendaDetailsAction.OnSelectToTime -> {
                updateAgendaItem { it.copy(toTime = agendaDetailsAction.toTime) }
            }
            is AgendaDetailsAction.OnSelectReminderAt -> {
                updateSelectedRemindAt(agendaDetailsAction.remindAt)
            }
            else -> Unit
        }
    }

    private fun updateAgendaItem(transform: (AgendaDetailsUi) -> AgendaDetailsUi) {
        _state.update { currentState ->
            currentState.copy(agendaItem = transform(currentState.agendaItem))
        }
    }

    private fun updateSelectedRemindAt(remindAtMinutes: Long) {
        val remindAt = _state.value.agendaItem.eventDateTime
            .minusMinutes(remindAtMinutes)
        updateAgendaItem { it.copy(remindAt = remindAt) }
    }
}