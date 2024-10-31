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

    private val _agendaDetailsState = MutableStateFlow(
        AgendaDetailsState(
            agendaItemType = agendaDetailsArgs.agendaItemType,
            agendaItemId = agendaDetailsArgs.agendaItemId,
            isEditing = agendaDetailsArgs.isEditing
        )
    )
    val agendaDetailsState: StateFlow<AgendaDetailsState> = _agendaDetailsState.asStateFlow()

    private fun toggleEditingState() {
        _agendaDetailsState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun onAction(agendaDetailsAction: AgendaDetailsAction) {
        when(agendaDetailsAction) {
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