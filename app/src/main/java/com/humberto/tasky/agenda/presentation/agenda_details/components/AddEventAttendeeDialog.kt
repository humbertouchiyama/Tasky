package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.core.presentation.designsystem.CrossIcon
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDialog
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTextField

@Composable
fun AddEventAttendeeDialog(
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    agendaItem: AgendaItemDetails.Event
) {
    if(agendaItem.isAddingAttendee) {
        var isFocused by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        TaskyDialog(
            dialogHeader = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.add_visitor),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clickable {
                                onDismiss()
                            }
                            .padding(start = 8.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = CrossIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(18.dp)
                        )
                    }
                }
            },
            onDismiss = onDismiss,
            primaryButton = {
                TaskyActionButton(
                    text = stringResource(id = R.string.add),
                    isLoading = agendaItem.isCheckingIfAttendeeExists,
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        ) {
            TaskyTextField(
                state = agendaItem.newAttendeeEmail,
                keyboardType = KeyboardType.Email,
                hint = stringResource(id = R.string.email_address),
                modifier = Modifier.fillMaxWidth(),
                isFocused = isFocused,
                onFocusChange = {
                    isFocused = it.isFocused
                },
                focusRequester = focusRequester,
                imeAction = ImeAction.Go
            )
        }
    }
}

@Preview
@Composable
private fun AddAttendeeDialogPreview() {
    TaskyTheme {
        AddEventAttendeeDialog(
            onDismiss = { },
            onAdd = { },
            agendaItem = AgendaItemDetails.Event(
                isAddingAttendee = true
            )
        )
    }
}