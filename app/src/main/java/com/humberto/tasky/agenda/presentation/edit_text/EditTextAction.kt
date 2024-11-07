package com.humberto.tasky.agenda.presentation.edit_text

interface EditTextAction {
    data object OnBackClick: EditTextAction
    data class OnSaveClick(val content: String): EditTextAction
}