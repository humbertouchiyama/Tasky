package com.humberto.tasky.agenda.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.PlusIcon
import com.humberto.tasky.core.presentation.designsystem.components.TaskyFloatingActionButton
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem

@Composable
fun FloatingActionButtonWithDropDownMenu(
    menuItems: List<DropDownItem>
) {
    var isEventDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Box {
        DropdownMenu(
            expanded = isEventDropDownOpen,
            onDismissRequest = {
                isEventDropDownOpen = false
            }
        ) {
            menuItems.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { item.onClick }
                        .fillMaxWidth()
                        .defaultMinSize(minWidth = 56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = item.title)
                }
            }
        }
        TaskyFloatingActionButton(
            icon = PlusIcon,
            onClick = {
                isEventDropDownOpen = true
            },
            contentDescription = stringResource(id = R.string.add_new_event)
        )
    }
}