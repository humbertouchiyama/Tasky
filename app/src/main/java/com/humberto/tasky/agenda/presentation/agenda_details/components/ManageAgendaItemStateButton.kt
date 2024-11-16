package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun ManageAgendaItemStateButton(
    onClick: () -> Unit,
    agendaItem: AgendaItemDetails,
    modifier: Modifier = Modifier
) {
    val manageItemButtonText = when(agendaItem) {
        is AgendaItemDetails.Task -> stringResource(id = R.string.delete_task).uppercase()
        is AgendaItemDetails.Event -> {
            if(agendaItem.isUserEventCreator) {
                stringResource(id = R.string.delete_event).uppercase()
            } else {
                //TODO join_event if user not event creator and is not going
                stringResource(id = R.string.leave_event).uppercase()
            }
        }
        AgendaItemDetails.Reminder -> stringResource(id = R.string.delete_reminder).uppercase()
    }
    Box(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = manageItemButtonText,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TaskyGray
        )
    }
}

@Preview
@Composable
private fun ManageItemButtonPreview() {
    TaskyTheme {
        ManageAgendaItemStateButton(
            onClick = { },
            agendaItem = AgendaItemDetails.Event()
        )
    }
}