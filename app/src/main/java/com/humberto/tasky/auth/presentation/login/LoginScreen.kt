package com.humberto.tasky.auth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.Inter
import com.humberto.tasky.core.presentation.designsystem.TaskyLinkBlue
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.RoundedBordersBackground
import com.humberto.tasky.core.presentation.designsystem.components.TaskyActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyPasswordTextField
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTextField
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents
import timber.log.Timber

@Composable
fun LoginScreenRoot(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is LoginEvent.Error -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            LoginEvent.LoginSuccess -> {
                keyboardController?.hide()
                Toast.makeText(
                    context,
                    R.string.login_successful,
                    Toast.LENGTH_LONG
                ).show()

                onLoginSuccess()
            }
        }
    }
    LoginScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                is LoginAction.OnRegisterClick -> onSignUpClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    RoundedBordersBackground(
        title = stringResource(id = R.string.welcome_back),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(vertical = 32.dp)
                .padding(top = 16.dp)
        ) {
            TaskyTextField(
                state = state.email,
                endIcon = if(state.isEmailValid) {
                    Icons.Default.Check
                } else null,
                keyboardType = KeyboardType.Email,
                hint = stringResource(id = R.string.email_address),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TaskyPasswordTextField(
                state = state.password,
                isPasswordVisible = state.isPasswordVisible,
                onTogglePasswordVisibility = {
                    onAction(LoginAction.OnTogglePasswordVisibilityClick)
                },
                hint = stringResource(id = R.string.password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            TaskyActionButton(
                text = stringResource(id = R.string.get_started),
                isLoading = state.isLoggingIn,
                enabled = state.canLogin && !state.isLoggingIn,
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                val annotatedString = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = Inter,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        append(stringResource(id = R.string.dont_have_account) + " ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = TaskyLinkBlue,
                                fontFamily = Inter
                            )
                        ) {
                            withLink(
                                LinkAnnotation.Clickable("clickable_text") {
                                    Timber.d("clickable_text clicked")
                                    onAction(LoginAction.OnRegisterClick)
                                    TextLinkStyles(
                                        style = SpanStyle(color = TaskyLinkBlue),
                                    )
                                }
                            ) {
                                append(stringResource(id = R.string.sign_up))
                            }
                        }
                    }
                }
                BasicText(
                    text = annotatedString,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview
@Composable
private fun ScreenPreview() {
     TaskyTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}