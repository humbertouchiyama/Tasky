package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.PlusIcon
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownListItem

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
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            menuItems.forEachIndexed { index, item ->
                DropDownListItem(
                    modifier = Modifier,
                    title = item.title,
                    onClick = item.onClick
                )
                if (index < menuItems.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.tertiary)
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

@Preview
@Composable
private fun FloatingActionButtonWithDropDownMenuPreview() {
    FloatingActionButtonWithDropDownMenu(
        menuItems = listOf(
            DropDownItem(
                title = stringResource(id = R.string.event),
                onClick = { },
            ),
            DropDownItem(
                title = stringResource(id = R.string.task),
                onClick = { },
            ),
            DropDownItem(
                title = stringResource(id = R.string.reminder),
                onClick = { },
            )
        ),
    )
}