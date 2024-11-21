package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.FilterType
import com.humberto.tasky.agenda.presentation.agenda_details.model.AttendeeUi

@Composable
fun AttendeeList(
    modifier: Modifier = Modifier,
    attendeeList: List<AttendeeUi>,
    selectedAttendanceFilter: FilterType
) {
    val goingList by remember(attendeeList) {
        mutableStateOf(
            attendeeList.filter { it.isGoing }
        )
    }
    val notGoingList by remember(attendeeList) {
        mutableStateOf(
            attendeeList.filter { !it.isGoing }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (selectedAttendanceFilter == FilterType.ALL || selectedAttendanceFilter == FilterType.GOING) {
            if (goingList.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.filter_going),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                goingList.forEach { attendee ->
                    AttendeeListItem(
                        onDeleteClick = { },
                        attendee = attendee
                    )
                }
            }
        }

        if (selectedAttendanceFilter == FilterType.ALL || selectedAttendanceFilter == FilterType.NOT_GOING) {
            if (notGoingList.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.filter_not_going),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                notGoingList.forEach { attendee ->
                    AttendeeListItem(
                        onDeleteClick = { },
                        attendee = attendee
                    )
                }
            }
        }
    }
}