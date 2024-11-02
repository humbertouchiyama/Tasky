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
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyLight2
import com.humberto.tasky.core.presentation.designsystem.TaskyLightGreen


@Composable
fun AgendaItemIndicator(agendaItemType: AgendaItemType) {
    val color = when(agendaItemType) {
        AgendaItemType.TASK -> TaskyGreen
        AgendaItemType.EVENT -> TaskyLightGreen
        AgendaItemType.REMINDER -> TaskyLight2
    }
    val type = when(agendaItemType) {
        AgendaItemType.TASK -> stringResource(id = R.string.task)
        AgendaItemType.EVENT -> stringResource(id = R.string.event)
        AgendaItemType.REMINDER -> stringResource(id = R.string.reminder)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(color = color)
                .size(20.dp)
                .border(
                    if (agendaItemType == AgendaItemType.REMINDER) 1.dp else 0.dp,
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