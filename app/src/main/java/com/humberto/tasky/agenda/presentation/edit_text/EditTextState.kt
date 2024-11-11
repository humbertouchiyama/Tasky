package com.humberto.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.text.input.TextFieldState

data class EditTextState(
    val editTextScreenType: EditTextScreenType,
    val textToBeUpdated: TextFieldState = TextFieldState()
)
