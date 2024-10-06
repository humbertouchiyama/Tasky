package com.humberto.tasky.auth.presentation.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.humberto.tasky.R
import com.humberto.tasky.auth.domain.AuthRepository
import com.humberto.tasky.auth.domain.UserDataValidator
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.presentation.ui.UiText
import com.humberto.tasky.core.presentation.ui.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator
): ViewModel() {

    var state by mutableStateOf(RegisterState())
        private set

    private val eventChannel = Channel<RegisterEvent>()
    val events = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            collectFullName()
        }
        viewModelScope.launch {
            collectEmail()
        }
        viewModelScope.launch {
            collectPassword()
        }
    }

    private suspend fun collectFullName() {
        snapshotFlow { state.fullName.text }
            .collectLatest { fullName ->
                val isValidFullName = userDataValidator.isValidFullName(fullName.toString())
                state = state.copy(
                    isValidFullName = isValidFullName,
                    canRegister = isValidFullName &&
                            state.isValidEmail &&
                            !state.isRegistering &&
                            state.passwordValidationState.isValidPassword
                )
            }
    }

    private suspend fun collectEmail() {
        snapshotFlow { state.email.text }
            .collectLatest { email ->
                val isValidEmail = userDataValidator.isValidEmail(email.toString())
                state = state.copy(
                    isValidEmail = isValidEmail,
                    canRegister = state.isValidFullName &&
                            isValidEmail &&
                            !state.isRegistering &&
                            state.passwordValidationState.isValidPassword
                )
            }
    }

    private suspend fun collectPassword() {
        snapshotFlow { state.password.text }
            .collectLatest { password ->
                val passwordValidationState =
                    userDataValidator.validatePassword(password.toString())
                state = state.copy(
                    passwordValidationState = passwordValidationState,
                    canRegister = state.isValidFullName &&
                            state.isValidEmail &&
                            !state.isRegistering &&
                            passwordValidationState.isValidPassword
                )
            }
    }

    fun onAction(action: RegisterAction) {
        when(action) {
            RegisterAction.OnRegisterClick -> register()
            RegisterAction.OnTogglePasswordVisibilityClick -> {
                state = state.copy(
                    isPasswordVisible = !state.isPasswordVisible
                )
            }
            else -> Unit
        }
    }

    private fun register() {
        viewModelScope.launch {
            state = state.copy(isRegistering = true)
            authRepository.register(
                fullName = state.fullName.text.toString(),
                email = state.email.text.toString().trim(),
                password =  state.password.text.toString()
            )
                .onSuccess {
                    eventChannel.send(RegisterEvent.RegisterSuccess)
                }
                .onError { error ->
                    if (error == DataError.Network.CONFLICT) {
                        eventChannel.send(RegisterEvent.Error(
                            UiText.StringResource(R.string.error_email_exists)
                        ))
                    } else {
                        eventChannel.send(RegisterEvent.Error(error.asUiText()))
                    }
                }
            state = state.copy(isRegistering = false)
        }
    }
}