package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun TaskyEditableField(
    modifier: Modifier = Modifier,
    isEditing: Boolean,
    onClick: () -> Unit = { },
    alignContentToCenter: Boolean = false,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(enabled = isEditing) { onClick() }
            .padding(vertical = 20.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = if (alignContentToCenter)
                Alignment.Center
                else Alignment.CenterStart,
        ) {
            content()
        }
        if(isEditing) {
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun TaskyEditableFieldPreview() {
    TaskyTheme {
        TaskyEditableField(
            isEditing = true,
            alignContentToCenter = true
        ) {
            Text(text = "Jul 21 2022")
        }
    }
}