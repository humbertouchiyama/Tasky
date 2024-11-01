package com.humberto.tasky.agenda.presentation.agenda_details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.FilterType
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme

@Composable
fun AttendanceFilter(
    onSelectFilter: (selectedFilter: FilterType) -> Unit,
    selectedFilter: FilterType
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        FilterType.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            val backgroundColor =
                if (isSelected)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            val textColor =
                if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            Button(
                onClick = { onSelectFilter(filter) },
                modifier = Modifier.weight(1f).height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(id = filter.toDisplayName()),
                    textAlign = TextAlign.Center,
                    color = textColor
                )
            }
        }
    }
}

private fun FilterType.toDisplayName(): Int = when (this) {
    FilterType.ALL -> R.string.filter_all
    FilterType.GOING -> R.string.filter_going
    FilterType.NOT_GOING -> R.string.filter_not_going
}

@Preview
@Composable
private fun AttendanceFilterPreview() {
    TaskyTheme {
        AttendanceFilter(
            selectedFilter = FilterType.ALL,
            onSelectFilter = { }
        )
    }
}