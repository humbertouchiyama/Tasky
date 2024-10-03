package com.humberto.tasky.auth.presentation.login

import androidx.compose.foundation.text.input.TextFieldState

data class LoginState(
    val email: TextFieldState = TextFieldState(),
    val isEmailValid: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val canLogin: Boolean = false,
    val isLoggingIn: Boolean = false,
)
