package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.lifecycle.ViewModel
import com.humberto.tasky.main.navigation.AgendaDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AgendaDetailsViewModel @Inject constructor(

): ViewModel() {

    private val _agendaDetailsState = MutableStateFlow<AgendaDetailsState?>(null)
    val agendaDetailsState: StateFlow<AgendaDetailsState?> = _agendaDetailsState.asStateFlow()

    fun setStateFromNav(agendaDetails: AgendaDetails) {
        _agendaDetailsState.update {
            AgendaDetailsState(
                agendaItemId = agendaDetails.agendaItemId,
                agendaItemType = agendaDetails.agendaItemType,
                isEditing = agendaDetails.isEditing,
            )
        }
    }

    private fun toggleEditingState() {
        _agendaDetailsState.update { it?.copy(isEditing = !it.isEditing) }
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