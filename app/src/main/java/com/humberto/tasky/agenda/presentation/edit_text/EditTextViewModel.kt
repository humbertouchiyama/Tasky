package com.humberto.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.humberto.tasky.main.navigation.EditTextArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val editTextArgsArgs = savedStateHandle.toRoute<EditTextArgs>()

    private val _state = MutableStateFlow(
        EditTextState(
            editTextScreenType = editTextArgsArgs.editTextScreenType,
            textToBeUpdated = TextFieldState(initialText = editTextArgsArgs.textToBeUpdated)
        )
    )
    val state: StateFlow<EditTextState> = _state.asStateFlow()
}