package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsAction
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsState
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.core.domain.util.toFormatted
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDatePicker
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTimePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@Composable
fun DateTimeSection(
    isEditing: Boolean,
    onAction: (AgendaDetailsAction) -> Unit,
    state: AgendaDetailsState
) {
    val agendaItem = state.agendaItem
    val fromDateState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = fromDateState,
        initialDate = state.fromDate,
        onDateChange = { date ->
            onAction(AgendaDetailsAction.OnSelectFromDate(date))
        }
    )
    val fromTimeState = rememberMaterialDialogState()
    TaskyTimePicker(
        dialogState = fromTimeState,
        initialTime = state.fromTime,
        onTimeChange = { time ->
            onAction(AgendaDetailsAction.OnSelectFromTime(time))
        }
    )
    val toDateState = rememberMaterialDialogState()
    val toTimeState = rememberMaterialDialogState()
    if(agendaItem is AgendaItemDetails.Event) {
        TaskyDatePicker(
            dialogState = toDateState,
            initialDate = agendaItem.toDate,
            onDateChange = { date ->
                onAction(AgendaDetailsAction.OnSelectToDate(date))
            }
        )
        TaskyTimePicker(
            dialogState = toTimeState,
            initialTime = agendaItem.toTime,
            onTimeChange = { time ->
                onAction(AgendaDetailsAction.OnSelectToTime(time))
            }
        )

    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskyEditableField(
            isEditing = isEditing,
            onClick = { fromTimeState.show() },
            modifier = Modifier.weight(1f)
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.from),
                    modifier = Modifier.width(40.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = state.fromTime.toFormatted(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        TaskyEditableField(
            isEditing = isEditing,
            alignContentToCenter = true,
            onClick = { fromDateState.show() },
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = state.fromDate.toFormatted(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.tertiary
    )
    if(agendaItem is AgendaItemDetails.Event) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskyEditableField(
                isEditing = isEditing,
                onClick = { toTimeState.show() },
                modifier = Modifier.weight(1f)
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.to_label),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(40.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = agendaItem.toTime.toFormatted(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            TaskyEditableField(
                isEditing = isEditing,
                alignContentToCenter = true,
                onClick = { toDateState.show() },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = agendaItem.toDate.toFormatted(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Preview
@Composable
private fun DateTimeSectionPreview() {
    TaskyTheme {
        DateTimeSection(
            isEditing = false,
            onAction = {},
            state = AgendaDetailsState(
                agendaItem = AgendaItemDetails.Event()
            )
        )
    }
}