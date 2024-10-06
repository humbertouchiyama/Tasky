package com.humberto.tasky.auth.presentation.registration

import androidx.compose.foundation.text.input.TextFieldState
import com.humberto.tasky.auth.domain.PasswordValidationState

data class RegisterState(
    val fullName: TextFieldState = TextFieldState(),
    val isValidFullName: Boolean = false,
    val email: TextFieldState = TextFieldState(),
    val isValidEmail: Boolean = false,
    val password: TextFieldState = TextFieldState(),
    val passwordValidationState: PasswordValidationState = PasswordValidationState(),
    val isPasswordVisible: Boolean = false,
    val canRegister: Boolean = false,
    val isRegistering: Boolean = false,
)
