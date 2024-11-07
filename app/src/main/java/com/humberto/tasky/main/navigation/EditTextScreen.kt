package com.humberto.tasky.main.navigation

import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import kotlinx.serialization.Serializable

@Serializable
data class EditTextScreen(
    val editTextScreenType: EditTextScreenType,
    val content: String
)
