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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaDetailsAction
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toFormatted
import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDatePicker
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTimePicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


@Composable
fun DateTimeSection(
    isEditing: Boolean,
    onAction: (AgendaDetailsAction) -> Unit,
    agendaItem: AgendaDetailsUi
) {
    val fromDateState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = fromDateState,
        initialDate = agendaItem.fromDate,
        onDateChange = { date ->
            onAction(AgendaDetailsAction.OnSelectFromDate(date))
        }
    )
    val toDateState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = toDateState,
        initialDate = agendaItem.toDate,
        onDateChange = { date ->
            onAction(AgendaDetailsAction.OnSelectToDate(date))
        }
    )
    val atDateState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = atDateState,
        initialDate = agendaItem.atDate,
        onDateChange = { date ->
            onAction(AgendaDetailsAction.OnSelectAtDate(date))
        }
    )
    val fromTimeState = rememberMaterialDialogState()
    TaskyTimePicker(
        dialogState = fromTimeState,
        initialTime = agendaItem.fromTime,
        onTimeChange = { time ->
            onAction(AgendaDetailsAction.OnSelectFromTime(time))
        }
    )
    val toTimeState = rememberMaterialDialogState()
    TaskyTimePicker(
        dialogState = toTimeState,
        initialTime = agendaItem.toTime,
        onTimeChange = { time ->
            onAction(AgendaDetailsAction.OnSelectToTime(time))
        }
    )
    val atTimeState = rememberMaterialDialogState()
    TaskyTimePicker(
        dialogState = atTimeState,
        initialTime = agendaItem.atTime,
        onTimeChange = { time ->
            onAction(AgendaDetailsAction.OnSelectAtTime(time))
        }
    )
    when(agendaItem.agendaItemType) {
        AgendaItemType.EVENT -> {
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
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = agendaItem.fromTime.toFormatted(),
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
                        text = agendaItem.fromDate.toFormatted(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
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
                            text = stringResource(id = R.string.to),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = agendaItem.toTime.toFormatted(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
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
        AgendaItemType.TASK,
        AgendaItemType.REMINDER -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskyEditableField(
                    isEditing = isEditing,
                    onClick = { atTimeState.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    Row {
                        Text(
                            text = stringResource(id = R.string.at),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = agendaItem.atTime.toFormatted(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                TaskyEditableField(
                    isEditing = isEditing,
                    alignContentToCenter = true,
                    onClick = { atDateState.show() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = agendaItem.atDate.toFormatted(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}