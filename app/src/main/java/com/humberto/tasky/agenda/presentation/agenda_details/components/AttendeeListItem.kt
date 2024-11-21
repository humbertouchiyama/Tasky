package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.model.AttendeeUi
import com.humberto.tasky.auth.domain.toInitials
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyLightBlue
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.TaskyWhite
import com.humberto.tasky.core.presentation.designsystem.components.ProfileMenuButton

@Composable
fun AttendeeListItem(
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
    attendee: AttendeeUi
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileMenuButton(
            initials = attendee.fullName.toInitials(),
            backgroundColor = TaskyGray,
            textColor = TaskyWhite,
            clickable = false
        )
        Text(
            text = attendee.fullName,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if(attendee.isAttendeeEventCreator) {
            Text(
                text = stringResource(id = R.string.creator),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TaskyLightBlue
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
private fun AttendeeListItemPreview() {
    TaskyTheme {
        AttendeeListItem(
            onDeleteClick = { },
            attendee = AttendeeUi(
                userId = "123",
                fullName = "Humberto Costa",
                email = "test@email.com",
                isGoing = true,
                isAttendeeEventCreator = false
            ),
        )
    }
}