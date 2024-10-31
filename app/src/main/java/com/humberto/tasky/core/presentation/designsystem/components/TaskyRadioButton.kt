package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.core.presentation.designsystem.TaskyBlack
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun TaskyRadioButton(
    selected: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    radioButtonColor: Color = TaskyBlack
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                color = radioButtonColor,
                shape = CircleShape
            )
            .padding(4.dp)
            .clickable(enabled = enabled) {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = radioButtonColor,
            )
        }
    }
}

@Preview
@Composable
private fun TaskyRadioButtonPreview() {
    TaskyTheme {
        TaskyRadioButton(
            selected = true,
            onClick = { },
            radioButtonColor = MaterialTheme.colorScheme.onPrimary
        )
    }
}