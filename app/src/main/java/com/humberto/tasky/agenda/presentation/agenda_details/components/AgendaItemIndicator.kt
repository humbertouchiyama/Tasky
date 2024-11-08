package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.AgendaItemDetails
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyLight2
import com.humberto.tasky.core.presentation.designsystem.TaskyLightGreen


@Composable
fun AgendaItemIndicator(
    modifier: Modifier,
    agendaItemDetails: AgendaItemDetails
) {
    val color = when(agendaItemDetails) {
        is AgendaItemDetails.Task -> TaskyGreen
        is AgendaItemDetails.Event -> TaskyLightGreen
        AgendaItemDetails.Reminder -> TaskyLight2
    }
    val type = when(agendaItemDetails) {
        is AgendaItemDetails.Task -> stringResource(id = R.string.task)
        is AgendaItemDetails.Event -> stringResource(id = R.string.event)
        AgendaItemDetails.Reminder -> stringResource(id = R.string.reminder)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(color = color)
                .size(20.dp)
                .border(
                    if (agendaItemDetails is AgendaItemDetails.Reminder) 1.dp else 0.dp,
                    TaskyGray
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = type,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}