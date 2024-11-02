package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalTime

@Composable
fun TaskyTimePicker(
    dialogState: MaterialDialogState = rememberMaterialDialogState(),
    initialTime: LocalTime = LocalTime.now(),
    onTimeChange: (LocalTime) -> Unit = {}
) {
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                stringResource(id = R.string.ok),
                TextStyle(
                    color = TaskyGreen
                )
            )
            negativeButton(
                stringResource(id = R.string.cancel),
                TextStyle(
                    color = TaskyGreen
                )
            )
        }
    ) {
        timepicker(
            colors = TimePickerDefaults.colors(
                activeBackgroundColor = TaskyGreen,
                selectorColor = TaskyGreen
            ),
            initialTime = initialTime
        ) { time ->
            onTimeChange(time)
        }
    }
}