package com.humberto.tasky.agenda.presentation.edit_text

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.main.navigation.EditTextScreen
import timber.log.Timber

@Composable
fun EditTextScreenRoot(
    onGoBack: () -> Unit,
    onSaveClick: (EditTextScreen) -> Unit,
    viewModel: EditTextViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    EditTextScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is EditTextAction.OnBackClick -> onGoBack()
                is EditTextAction.OnSaveClick -> {
                    Timber.d("OnSaveClick Screen")
                    onSaveClick(EditTextScreen(
                        editTextScreenType = state.editTextScreenType,
                        content = action.content
                    ))
                }
                else -> Unit
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTextScreen(
    state: EditTextState,
    onAction: (EditTextAction) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    val title = when(state.editTextScreenType) {
        EditTextScreenType.TITLE -> stringResource(id = R.string.edit_title)
        EditTextScreenType.DESCRIPTION -> stringResource(id = R.string.edit_description)
    }
    val textStyle = when(state.editTextScreenType) {
        EditTextScreenType.TITLE -> MaterialTheme.typography.headlineMedium.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Normal
        )
        EditTextScreenType.DESCRIPTION -> MaterialTheme.typography.labelMedium
    }
    TaskyScaffold(
        showRoundedBordersBackground = false,
        topAppBar = {
            TaskyToolbar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = {
                                onAction(EditTextAction.OnBackClick)
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                endContent = {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 36.dp)
                            .padding(end = 8.dp)
                            .clickable(onClick = {
                                onAction(EditTextAction.OnSaveClick(
                                    content = state.content.text.toString()
                                ))
                            }),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        Text(
                            text = stringResource(id = R.string.save),
                            style = MaterialTheme.typography.titleSmall,
                            color = TaskyGreen
                        )
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
        ) {
            BasicTextField(
                state = state.content,
                textStyle = textStyle,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .focusRequester(focusRequester)
            )
        }
    }
}

@Preview
@Composable
private fun AgendaEditTextScreenPreview() {
    TaskyTheme {
        EditTextScreen(
            state = EditTextState(
                editTextScreenType = EditTextScreenType.TITLE
            ),
            onAction = {}
        )
    }
}