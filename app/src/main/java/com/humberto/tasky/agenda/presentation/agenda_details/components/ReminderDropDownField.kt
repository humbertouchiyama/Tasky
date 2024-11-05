package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownListItem

@Composable
fun ReminderDropdownField(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    menuItems: List<DropDownItem>,
    remindAtText: String
) {
    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }
    DropdownMenu(
        expanded = isDropDownOpen,
        onDismissRequest = {
            isDropDownOpen = false
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (menuItems.isNotEmpty()) {
            menuItems.forEach{ item ->
                DropDownListItem(
                    modifier = Modifier,
                    title = item.title,
                    onClick = {
                        item.onClick()
                        isDropDownOpen = false
                    }
                )
            }
        }
    }
    TaskyEditableField(
        modifier = modifier,
        onClick = {
            isDropDownOpen = true
        },
        isEditing = isEditing
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = TaskyGray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = remindAtText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}