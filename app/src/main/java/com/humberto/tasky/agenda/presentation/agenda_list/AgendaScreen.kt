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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_list.components.AgendaListItem
import com.humberto.tasky.agenda.presentation.agenda_list.mapper.toAgendaItemUi
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.FloatingActionButtonWithDropDownMenu
import com.humberto.tasky.core.presentation.designsystem.components.ProfileMenuButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyCalendarHeader
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDatePicker
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDialog
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTextButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents
import com.humberto.tasky.core.presentation.ui.buildHeaderDate
import com.humberto.tasky.main.navigation.AgendaDetails
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
fun AgendaScreenRoot(
    onLogoutSuccess: () -> Unit,
    onGoToAgendaDetailsClick: (AgendaDetails) -> Unit,
    selectedDateEpochDay: Long?,
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
    LaunchedEffect(Unit) {
        selectedDateEpochDay?.let {
            viewModel.updateSelectedDate( LocalDate.ofEpochDay(it) )
        }
    }
    val state by viewModel.agendaState.collectAsStateWithLifecycle()
    AgendaScreen(
        state = state,
        onAction = { action ->
            val agendaDetails = when (action) {
                is AgendaAction.OnNewAgendaItemClick -> action.agendaDetails
                is AgendaAction.OnOpenAgendaItemClick -> action.agendaDetails
                is AgendaAction.OnEditAgendaItemClick -> action.agendaDetails
                else -> null
            }
            if (agendaDetails != null) {
                onGoToAgendaDetailsClick(agendaDetails)
            }
            viewModel.onAction(action)
        }
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

    if(state.confirmingItemToBeDeleted != null) {
        val itemTitle = state.confirmingItemToBeDeleted.title.ifEmpty { stringResource(id = R.string.no_title) }
        val itemType = when(state.confirmingItemToBeDeleted.agendaItemType) {
            AgendaItemType.TASK -> stringResource(id = R.string.delete_task)
            AgendaItemType.EVENT -> stringResource(id = R.string.delete_event)
            AgendaItemType.REMINDER -> stringResource(id = R.string.delete_reminder)
        }
        TaskyDialog(
            dialogHeader = {
                Text(
                    text = itemType,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onDismiss = {
                onAction(AgendaAction.OnDismissDeleteAgendaItemClick)
            },
            primaryButton = {
                TaskyActionButton(
                    text = stringResource(id = R.string.ok),
                    isLoading = state.isDeletingItem,
                    onClick = {
                        onAction(AgendaAction.OnConfirmDeleteAgendaItemClick)
                    },
                )
            },
            secondaryButton = {
                TaskyTextButton(
                    text = stringResource(id = R.string.cancel).uppercase(),
                    onClick = {
                        onAction(AgendaAction.OnDismissDeleteAgendaItemClick)
                    },
                )
            }
        ) {
            Text(
                text = stringResource(
                    id = R.string.are_you_sure_you_want_to_delete,
                    itemTitle
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

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
                        )),
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        textColor = MaterialTheme.colorScheme.onTertiary
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
                                AgendaAction.OnNewAgendaItemClick(
                                    AgendaDetails(
                                        agendaItemType = AgendaItemType.EVENT,
                                        isEditing = true,
                                        selectedDateEpochDay = state.selectedDate.toEpochDay()
                                    )
                                )
                            )
                        },
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.task),
                        onClick = {
                            onAction(
                                AgendaAction.OnNewAgendaItemClick(
                                    AgendaDetails(
                                        agendaItemType = AgendaItemType.TASK,
                                        isEditing = true,
                                        selectedDateEpochDay = state.selectedDate.toEpochDay()
                                    )
                                )
                            )
                        }
                    ),
                    DropDownItem(
                        title = stringResource(id = R.string.reminder),
                        onClick = {
                            onAction(
                                AgendaAction.OnNewAgendaItemClick(
                                    AgendaDetails(
                                        agendaItemType = AgendaItemType.REMINDER,
                                        isEditing = true,
                                        selectedDateEpochDay = state.selectedDate.toEpochDay()
                                    )
                                )
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
                selectedDate = state.selectedDate,
                onSelectDate = { date ->
                    onAction(AgendaAction.OnSelectDate(date))
                },
            )
            Text(
                text = state.dateLabel.asString(),
                modifier = Modifier
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.agendaItems, key = { it.id }) { agendaItem ->
                    AgendaListItem(
                        agendaItem = agendaItem,
                        onOpenItem = { onAction(AgendaAction.OnOpenAgendaItemClick(
                            AgendaDetails(
                                agendaItemId = agendaItem.id,
                                agendaItemType = agendaItem.agendaItemType
                            )
                        )) },
                        onEditItem = { onAction(AgendaAction.OnEditAgendaItemClick(
                            AgendaDetails(
                                agendaItemId = agendaItem.id,
                                agendaItemType = agendaItem.agendaItemType,
                                isEditing = true,
                                selectedDateEpochDay = state.selectedDate.toEpochDay()
                            )
                        )) },
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
                dateLabel = LocalDate.now().buildHeaderDate(),
                initials = "HC",
                agendaItems = listOf(
                    AgendaItem.Task(
                        id = "1",
                        title = "Task",
                        description = "Description",
                        from = ZonedDateTime.now(),
                        remindAt = ZonedDateTime.now(),
                        isDone = true
                    ).toAgendaItemUi(),
                    AgendaItem.Event(
                        id = "2",
                        title = "Event",
                        description = "Description",
                        from = ZonedDateTime.now(),
                        to = ZonedDateTime.now().plusMinutes(30),
                        remindAt = ZonedDateTime.now(),
                        isUserEventCreator = true,
                        attendees = listOf(),
                        photos = listOf()
                    ).toAgendaItemUi(),
                    AgendaItem.Reminder(
                        id = "3",
                        title = "Reminder",
                        description = "Description",
                        from = ZonedDateTime.now(),
                        remindAt = ZonedDateTime.now()
                    ).toAgendaItemUi(),
                ),
                selectedDate = LocalDate.now()
            ),
            onAction = {},
        )
    }
}