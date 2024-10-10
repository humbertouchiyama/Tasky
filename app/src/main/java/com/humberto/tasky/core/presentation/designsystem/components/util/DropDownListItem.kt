package com.humberto.tasky.core.presentation.designsystem.components.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DropDownListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title: String
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .defaultMinSize(minWidth = 144.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium
        )
    }
}