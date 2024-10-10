package com.humberto.tasky.core.presentation.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.humberto.tasky.R
import com.humberto.tasky.core.presentation.designsystem.TaskyGreen
import com.humberto.tasky.core.presentation.designsystem.TaskyWhite
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun TaskyDatePicker(
    dialogState: MaterialDialogState = rememberMaterialDialogState(),
    initialDate: LocalDate = LocalDate.now(),
    onDateChange: (LocalDate) -> Unit = {}
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
        datepicker(
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = TaskyGreen,
                dateActiveBackgroundColor = TaskyGreen,
                dateActiveTextColor = TaskyWhite
            ),
            initialDate = initialDate
        ) { date ->
            onDateChange(date)
        }
    }
}