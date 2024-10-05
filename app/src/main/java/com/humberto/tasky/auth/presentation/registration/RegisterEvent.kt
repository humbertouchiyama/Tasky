package com.humberto.tasky.auth.presentation.registration

import com.humberto.tasky.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegisterSuccess: RegisterEvent
    data class Error(val error: UiText): RegisterEvent
}