package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.humberto.tasky.agenda.presentation.AgendaItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AgendaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val agendaItemId: String? = savedStateHandle.get<String>("agendaItemId")
    private val agendaItemType: AgendaItemType = savedStateHandle.get<AgendaItemType>("agendaItemType")!!
    private val isEditing: Boolean = savedStateHandle.get<Boolean>("isEditing") ?: false

    private val _agendaDetailsState = MutableStateFlow(
        AgendaDetailsState(
            agendaItemType = agendaItemType,
            agendaItemId = agendaItemId,
            isEditing = isEditing
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