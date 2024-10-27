package com.humberto.tasky.agenda.presentation.agenda_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.ui.toFormattedUppercaseDateTime
import java.time.ZonedDateTime

@Composable
fun AgendaDetailsScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AgendaDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.agendaDetailsState.collectAsStateWithLifecycle()
    AgendaDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AgendaDetailsAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgendaDetailsScreen(
    state: AgendaDetailsState,
    onAction: (AgendaDetailsAction) -> Unit
) {
    val agendaItemType = when (state.agendaItemType) {
        AgendaItemType.TASK -> stringResource(id = R.string.task)
        AgendaItemType.EVENT -> stringResource(id = R.string.event)
        AgendaItemType.REMINDER -> stringResource(id = R.string.reminder)
    }
    TaskyScaffold(
        topAppBar = {
            TaskyToolbar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = {
                                onAction(AgendaDetailsAction.OnBackClick)
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = ZonedDateTime.now().toFormattedUppercaseDateTime(),
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                endContent = {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 36.dp)
                            .padding(end = 8.dp)
                            .clickable(onClick = {
                                if(state.isEditing) {
                                    onAction(AgendaDetailsAction.OnSaveClick)
                                } else {
                                    onAction(AgendaDetailsAction.OnEditClick)
                                }
                            }),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if(state.isEditing) {
                            Text(
                                text = stringResource(id = R.string.save),
                                style = MaterialTheme.typography.titleSmall
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) {
        Column {
            Text(
                text = agendaItemType,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
private fun AgendaDetailsScreenPreview() {
    TaskyTheme {
        AgendaDetailsScreen(
            state = AgendaDetailsState(
                agendaItemId = "123",
                agendaItemType = AgendaItemType.TASK,
                isEditing = true
            ),
            onAction = {}
        )
    }
}