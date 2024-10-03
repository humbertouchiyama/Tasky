package com.humberto.tasky.auth.presentation.login

import com.humberto.tasky.core.presentation.ui.UiText

sealed interface LoginEvent {
    data object LoginSuccess: LoginEvent
    data class Error(val error: UiText): LoginEvent
}