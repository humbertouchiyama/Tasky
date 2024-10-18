package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownListItem

@Composable
fun MoreButtonWithDropDownMenu(
    menuItems: List<DropDownItem>,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {
        DropdownMenu(
            expanded = isDropDownOpen,
            onDismissRequest = {
                isDropDownOpen = false
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
        ) {
            menuItems.forEachIndexed { index, item ->
                DropDownListItem(
                    title = item.title,
                    onClick = item.onClick
                )
                if (index < menuItems.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.tertiary)
                }
            }
        }
        Box(
            modifier = Modifier.clickable { isDropDownOpen = true }
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = stringResource(id = R.string.open_event_options),
                tint = iconColor
            )
        }
    }
}