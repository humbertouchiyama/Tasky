package com.humberto.tasky.auth.presentation.registration

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.humberto.tasky.R
import com.humberto.tasky.auth.domain.UserDataValidator
import com.humberto.tasky.core.presentation.designsystem.CheckIcon
import com.humberto.tasky.core.presentation.designsystem.CrossIcon
import com.humberto.tasky.core.presentation.designsystem.TaskyError
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyFloatingActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyPasswordTextField
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTextField
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents

@Composable
fun RegisterScreenRoot(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is RegisterEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            RegisterEvent.RegisterSuccess -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.register_success,
                    Toast.LENGTH_LONG
                ).show()

                onRegisterSuccess()
            }
        }
    }
    RegisterScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is RegisterAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    val (
        nameFocusRequester,
        emailFocusRequester,
        passwordFocusRequester,
    ) = FocusRequester.createRefs()

    var isNameFocused by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val keyboardActionHandler = KeyboardActionHandler {
        if (state.canRegister) {
            keyboardController?.hide()
            onAction(RegisterAction.OnRegisterClick)
        }
    }

    TaskyScaffold(
        title = stringResource(id = R.string.create_your_account),
        floatingActionButton = {
            TaskyFloatingActionButton(
                icon = Icons.Default.ArrowBackIosNew,
                onClick = {
                    onAction(RegisterAction.OnBackClick)
                },
                contentDescription = stringResource(id = R.string.back_to_login)
            )
        },
        floatingActionButtonPosition = FabPosition.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(vertical = 40.dp)
                .imePadding()
        ) {
            TaskyTextField(
                state = state.fullName,
                endIcon = if(state.isFullNameValid) {
                    CheckIcon
                } else null,
                keyboardType = KeyboardType.Text,
                hint = stringResource(id = R.string.name),
                modifier = Modifier.fillMaxWidth(),
                hasError = !isNameFocused &&
                        !state.isFullNameValid &&
                        state.fullName.text.isNotEmpty(),
                isFocused = isNameFocused,
                onFocusChange = {
                    isNameFocused = it.isFocused
                },
                focusRequester = nameFocusRequester,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))
            TaskyTextField(
                state = state.email,
                endIcon = if(state.isEmailValid) {
                    CheckIcon
                } else null,
                keyboardType = KeyboardType.Email,
                hint = stringResource(id = R.string.email_address),
                modifier = Modifier.fillMaxWidth(),
                hasError = !isEmailFocused &&
                        !state.isEmailValid &&
                        state.email.text.isNotEmpty(),
                isFocused = isEmailFocused,
                onFocusChange = {
                    isEmailFocused = it.isFocused
                },
                focusRequester = emailFocusRequester,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))
            TaskyPasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(RegisterAction.OnTogglePasswordVisibilityClick)
                },
                hint = stringResource(id = R.string.password),
                modifier = Modifier.fillMaxWidth(),
                isFocused = isPasswordFocused,
                onFocusChange = {
                    isPasswordFocused = it.isFocused
                },
                focusRequester = passwordFocusRequester,
                imeAction = ImeAction.Go,
                keyboardActionHandler = keyboardActionHandler
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordRequirement(
                text = stringResource(
                    R.string.at_least_x_characters,
                    UserDataValidator.MIN_PASSWORD_LENGTH
                ),
                isValid = state.passwordValidationState.hasMinLength
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(R.string.at_least_one_number),
                isValid = state.passwordValidationState.hasNumber
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(R.string.contains_lowercase_char),
                isValid = state.passwordValidationState.hasLowerCaseCharacter
            )
            Spacer(modifier = Modifier.height(4.dp))
            PasswordRequirement(
                text = stringResource(R.string.contains_uppercase_char),
                isValid = state.passwordValidationState.hasUpperCaseCharacter
            )
            Spacer(modifier = Modifier.height(32.dp))
            TaskyActionButton(
                text = stringResource(id = R.string.get_started),
                isLoading = state.isRegisteringIn,
                onClick = {
                    onAction(RegisterAction.OnRegisterClick)
                },
                enabled = state.canRegister && !state.isRegisteringIn
            )
        }
    }
}

@Composable
fun PasswordRequirement(
    text: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) {
                CheckIcon
            } else {
                CrossIcon
            },
            contentDescription = null,
            tint = if (isValid) TaskyGreen else TaskyError,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun ScreenPreview() {
     TaskyTheme {
        RegisterScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}