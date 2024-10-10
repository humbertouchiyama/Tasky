@file:OptIn(ExperimentalMaterial3Api::class)

package com.humberto.tasky.agenda.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.components.FloatingActionButtonWithDropDownMenu
import com.humberto.tasky.agenda.presentation.user.UserState
import com.humberto.tasky.agenda.presentation.user.UserViewModel
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.ProfileMenuButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDatePicker
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.ui.displayUpperCaseMonth
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun AgendaScreenRoot(
    viewModel: AgendaViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    AgendaScreen(
        state = viewModel.state,
        userState = userViewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun AgendaScreen(
    state: AgendaState,
    userState: UserState,
    onAction: (AgendaAction) -> Unit
) {
    var selectedDate: LocalDate by rememberSaveable {
        mutableStateOf(LocalDate.now())
    }

    val dialogState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = dialogState,
        initialDate = selectedDate,
        onDateChange = { date ->
            selectedDate = date
        }
    )

    TaskyScaffold(
        topAppBar = {
            TaskyToolbar(
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                dialogState.show()
                            }
                    ) {
                        Text(
                            text = selectedDate.displayUpperCaseMonth(),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                endContent = {
                    ProfileMenuButton(
                        initials = userState.initials,
                        menuItems = listOf(DropDownItem(
                            title = stringResource(R.string.logout),
                            onClick = { onAction(AgendaAction.OnLogoutClick) }
                        ))
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButtonWithDropDownMenu(
                menuItems = listOf(
                    DropDownItem(
                        title = stringResource(id = R.string.event),
                        onClick = { onAction(AgendaAction.OnNewEventClick) },
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.task),
                        onClick = { onAction(AgendaAction.OnNewTaskClick) },
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.reminder),
                        onClick = { onAction(AgendaAction.OnNewReminderClick) },
                    )
                ),
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = selectedDate.toString(),
                modifier = Modifier
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Preview
@Composable
private fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreen(
            state = AgendaState(
                isLoading = false
            ),
            userState = UserState(
                initials = "HC"
            ),
            onAction = {}
        )
    }
}