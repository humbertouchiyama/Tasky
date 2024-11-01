package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.humberto.tasky.main.navigation.AgendaDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AgendaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val agendaDetailsArgs = savedStateHandle.toRoute<AgendaDetails>()

    private val _state = MutableStateFlow(
        AgendaDetailsState(
            agendaItemType = agendaDetailsArgs.agendaItemType,
            agendaItemId = agendaDetailsArgs.agendaItemId,
            isEditing = agendaDetailsArgs.isEditing
        )
    )
    val state: StateFlow<AgendaDetailsState> = _state.asStateFlow()

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
            else -> Unit
        }
    }
}