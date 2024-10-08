package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem

@Composable
fun ProfileMenuButton(
    initials: String,
    menuItems: List<DropDownItem> = emptyList(),
) {
    var isDropDownOpen by rememberSaveable {
        mutableStateOf(false)
    }
    Box {
        DropdownMenu(
            expanded = isDropDownOpen,
            onDismissRequest = {
                isDropDownOpen = false
            }
        ) {
            if (menuItems.isNotEmpty()) {
                menuItems.forEach{ item ->
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
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable(onClick = {
                    isDropDownOpen = true
                }),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initials,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}