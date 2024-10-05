package com.humberto.tasky.auth.presentation.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humberto.tasky.auth.domain.UserDataValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userDataValidator: UserDataValidator
): ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            collectCombinedEmailAndPassword()
        }
    }

    private fun collectCombinedEmailAndPassword() {
        combine(
            snapshotFlow { state.fullName.text },
            snapshotFlow { state.email.text },
            snapshotFlow { state.password.text }
        ) { fullName, email, password ->
            val isValidFullName = userDataValidator.isValidFullName(fullName.toString())
            val isValidEmail = userDataValidator.isValidEmail(email.toString())
            val passwordValidationState =
                userDataValidator.validatePassword(password.toString())
            state = state.copy(
                isFullNameValid = isValidFullName,
                isEmailValid = isValidEmail,
                passwordValidationState = passwordValidationState,
                canRegister = isValidEmail &&
                        !state.isRegisteringIn &&
                        passwordValidationState.isValidPassword
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {
        when(action) {
            RegisterAction.OnRegisterClick -> {}
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            else -> Unit
        }
    }
}