package com.humberto.tasky.auth.presentation.registration

sealed interface RegisterAction {
    data object OnTogglePasswordVisibilityClick: RegisterAction
    data object OnRegisterClick: RegisterAction
    data object OnBackClick: RegisterAction
}