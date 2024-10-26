@file:OptIn(ExperimentalMaterial3Api::class)

package com.humberto.tasky.agenda.presentation.agenda_list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.components.AgendaListItem
import com.humberto.tasky.agenda.presentation.mapper.toAgendaItemUi
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.event.domain.Event
import com.humberto.tasky.reminder.domain.Reminder
import com.humberto.tasky.task.domain.Task
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.FloatingActionButtonWithDropDownMenu
import com.humberto.tasky.core.presentation.designsystem.components.ProfileMenuButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyCalendarHeader
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDatePicker
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.ZonedDateTime

@Composable
fun AgendaScreenRoot(
    onLogoutSuccess: () -> Unit,
    viewModel: AgendaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is AgendaEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

            AgendaEvent.LogoutSuccess -> {
                Toast.makeText(
                    context,
                    R.string.logout_successful,
                    Toast.LENGTH_LONG
                ).show()

                onLogoutSuccess()
            }
        }
    }
    val state by viewModel.agendaState.collectAsState()
    AgendaScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun AgendaScreen(
    state: AgendaState,
    onAction: (AgendaAction) -> Unit
) {
    val dialogState = rememberMaterialDialogState()
    TaskyDatePicker(
        dialogState = dialogState,
        initialDate = state.selectedDate,
        onDateChange = { date ->
            onAction(AgendaAction.OnSelectDate(date))
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
                            text = state.upperCaseMonth,
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
                        initials = state.initials,
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
                        onClick = {
                            onAction(
                                AgendaAction.OnNewAgendaItemClick(AgendaItemType.EVENT)
                            )
                        },
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.task),
                        onClick = {
                            onAction(
                                AgendaAction.OnNewAgendaItemClick(AgendaItemType.TASK)
                            )
                        }
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.reminder),
                        onClick = {
                            onAction(
                                AgendaAction.OnNewAgendaItemClick(AgendaItemType.REMINDER)
                            )
                        }
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
            TaskyCalendarHeader(
                modifier = Modifier.padding(bottom = 8.dp),
                onSelectDate = { date ->
                    onAction(AgendaAction.OnSelectDate(date))
                },
                selectedDate = state.selectedDate
            )
            if (state.selectedDateIsToday) {
                Text(
                    text = stringResource(id = R.string.today),
                    modifier = Modifier
                        .padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.agendaItems, key = { it.id }) { agendaItem ->
                    AgendaListItem(
                        agendaItem = agendaItem,
                        onOpenItem = { onAction(AgendaAction.OnOpenAgendaItemClick(agendaItem)) },
                        onEditItem = { onAction(AgendaAction.OnEditAgendaItemClick(agendaItem)) },
                        onDeleteItem = { onAction(AgendaAction.OnDeleteAgendaItemClick(agendaItem)) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreen(
            state = AgendaState(
                selectedDateIsToday = true,
                initials = "HC",
                agendaItems = listOf(
                    AgendaItem.TaskItem(
                        Task(
                            id = "1",
                            title = "Task",
                            description = "Description",
                            time = ZonedDateTime.now(),
                            remindAt = ZonedDateTime.now(),
                            isDone = true
                        )
                    ).toAgendaItemUi(),
                    AgendaItem.EventItem(
                        Event(
                            id = "2",
                            title = "Event",
                            description = "Description",
                            from = ZonedDateTime.now(),
                            to = ZonedDateTime.now().plusMinutes(30),
                            remindAt = ZonedDateTime.now(),
                            attendees = listOf(),
                            photos = listOf(),
                            isGoing = true
                        )
                    ).toAgendaItemUi(),
                    AgendaItem.ReminderItem(
                        Reminder(
                            id = "3",
                            title = "Reminder",
                            description = "Description",
                            time = ZonedDateTime.now(),
                            remindAt = ZonedDateTime.now()
                        )
                    ).toAgendaItemUi(),
                )
            ),
            onAction = {},
        )
    }
}