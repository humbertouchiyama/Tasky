package com.humberto.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.humberto.tasky.main.navigation.EditTextScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val editTextScreenArgs = savedStateHandle.toRoute<EditTextScreen>()

    private val _state = MutableStateFlow(
        EditTextState(
            editTextScreenType = editTextScreenArgs.editTextScreenType,
            content = TextFieldState(initialText = editTextScreenArgs.content)
        )
    )
    val state: StateFlow<EditTextState> = _state.asStateFlow()
}