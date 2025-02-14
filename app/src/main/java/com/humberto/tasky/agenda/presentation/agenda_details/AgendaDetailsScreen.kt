package com.humberto.tasky.agenda.presentation.agenda_details

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.agenda_details.components.AddEventAttendeeDialog
import com.humberto.tasky.agenda.presentation.agenda_details.components.AgendaItemIndicator
import com.humberto.tasky.agenda.presentation.agenda_details.components.AttendanceFilter
import com.humberto.tasky.agenda.presentation.agenda_details.components.AttendeeList
import com.humberto.tasky.agenda.presentation.agenda_details.components.DateTimeSection
import com.humberto.tasky.agenda.presentation.agenda_details.components.ManageAgendaItemStateButton
import com.humberto.tasky.agenda.presentation.agenda_details.components.PhotosSection
import com.humberto.tasky.agenda.presentation.agenda_details.components.ReminderDropdownField
import com.humberto.tasky.agenda.presentation.agenda_details.components.TaskyEditableField
import com.humberto.tasky.agenda.presentation.agenda_details.model.AttendeeUi
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import com.humberto.tasky.core.presentation.designsystem.PlusIcon
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyActionButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyDialog
import com.humberto.tasky.core.presentation.designsystem.components.TaskyRadioButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyTextButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents
import com.humberto.tasky.core.presentation.ui.toFormattedDate
import com.humberto.tasky.main.navigation.EditTextArgs
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AgendaDetailsScreenRoot(
    onBackClick: (Long?) -> Unit,
    onEditTextClick: (EditTextArgs) -> Unit,
    editTextArgs: EditTextArgs?,
    viewModel: AgendaDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        val selectedDateEpochDay = state.fromDate.toEpochDay()
        when(event) {
            is AgendaDetailsEvent.SaveSuccess -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
                onBackClick(selectedDateEpochDay)
            }
            AgendaDetailsEvent.DeleteSuccess -> {
                Toast.makeText(
                    context,
                    R.string.delete_successful,
                    Toast.LENGTH_LONG
                ).show()
                onBackClick(selectedDateEpochDay)
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.updateStateWithEditTextArgs(editTextArgs)
    }
    AgendaDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AgendaDetailsAction.OnBackClick -> onBackClick(null)
                is AgendaDetailsAction.OnEditTextClick -> onEditTextClick(action.editTextArgs)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgendaDetailsScreen(
    state: AgendaDetailsState,
    onAction: (AgendaDetailsAction) -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()
        onAction(
            AgendaDetailsAction.SubmitNotificationPermissionInfo(
                showNotificationRationale = showNotificationRationale
            )
        )
    }
    LaunchedEffect(key1 = true) {
        // TODO deal with permission denied twice
        // TODO ask notification permission when user taps on save instead of launch
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            AgendaDetailsAction.SubmitNotificationPermissionInfo(
                showNotificationRationale = showNotificationRationale
            )
        )
        if (!showNotificationRationale) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    if (state.showNotificationRationale) {
        TaskyDialog(
            dialogHeader = {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.permission_required),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onDismiss = {
                onAction(AgendaDetailsAction.DismissRationaleDialog)
            },
            primaryButton = {
                TaskyActionButton(
                    text = stringResource(id = R.string.allow),
                    isLoading = false,
                    onClick = {
                        onAction(AgendaDetailsAction.DismissRationaleDialog)
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            },
            secondaryButton = {
                TaskyTextButton(
                    text = stringResource(id = R.string.cancel),
                    onClick = {
                        onAction(AgendaDetailsAction.DismissRationaleDialog)
                    },
                )
            }
        ) {
            Text(
                text = stringResource(id = R.string.notification_rationale),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    val agendaItem = state.agendaItem
    if(state.isConfirmingToDelete) {
        val itemTitle = state.title.ifEmpty { stringResource(id = R.string.no_title) }
        val itemType = when(agendaItem) {
            is AgendaItemDetails.Task -> stringResource(id = R.string.delete_task)
            is AgendaItemDetails.Event -> stringResource(id = R.string.delete_event)
            AgendaItemDetails.Reminder -> stringResource(id = R.string.delete_reminder)
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
                onAction(AgendaDetailsAction.OnDismissDeleteClick)
            },
            primaryButton = {
                TaskyActionButton(
                    text = stringResource(id = R.string.ok),
                    isLoading = state.isDeleting,
                    onClick = {
                        onAction(AgendaDetailsAction.OnConfirmDeleteClick)
                    },
                )
            },
            secondaryButton = {
                TaskyTextButton(
                    text = stringResource(id = R.string.cancel).uppercase(),
                    onClick = {
                        onAction(AgendaDetailsAction.OnDismissDeleteClick)
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

    val photoPickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onAction(AgendaDetailsAction.OnPhotoPicked(uri))
        }
    )

    if (agendaItem is AgendaItemDetails.Event) {
        LaunchedEffect(key1 = agendaItem.isAddingPhoto, key2 = photoPickLauncher) {
            if (agendaItem.isAddingPhoto) {
                photoPickLauncher.launch("image/*")
            }
        }
    }

    val snackBarState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = state.infoMessage, key2 = snackBarState) {
        state.infoMessage?.let { message ->
            snackBarState.showSnackbar(
                message = message.asString(context)
            )
            onAction(AgendaDetailsAction.OnInfoMessageSeen)
        }
    }

    val isEditing =
        remember(state.agendaItem, state.isEditing) {
            when (state.agendaItem) {
                is AgendaItemDetails.Event -> {
                    state.agendaItem.isUserEventCreator && state.isEditing
                }

                else -> state.isEditing
            }
        }

    TaskyScaffold(
        topAppBar = {
            TaskyToolbar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = {
                                onAction(
                                    AgendaDetailsAction.OnBackClick
                                )
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    Text(
                        text = state.fromDate.toFormattedDate().uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                endContent = {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 36.dp)
                            .padding(end = 8.dp)
                            .clickable(onClick = {
                                if(isEditing) {
                                    onAction(AgendaDetailsAction.OnSaveClick)
                                } else {
                                    onAction(AgendaDetailsAction.OnEditClick)
                                }
                            }),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if(state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(15.dp),
                                strokeWidth = 1.5.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else if(isEditing) {
                            Text(
                                text = stringResource(id = R.string.save),
                                style = MaterialTheme.typography.titleSmall
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarState,
                modifier = Modifier.padding(WindowInsets.ime.asPaddingValues())
            )
       },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 32.dp)
            ) {
                AgendaItemIndicator(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    agendaItemDetails = agendaItem)
                TaskyEditableField(
                    isEditing = isEditing,
                    onClick = {
                        onAction(AgendaDetailsAction.OnEditTextClick(
                            EditTextArgs(
                                editTextScreenType = EditTextScreenType.TITLE,
                                textToBeUpdated = state.title
                            )
                        ))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskyRadioButton(
                            when (agendaItem) {
                                is AgendaItemDetails.Task -> agendaItem.isDone
                                else -> false
                            },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = state.title
                                .ifEmpty { stringResource(id = R.string.add_title) },
                            style = MaterialTheme.typography.headlineMedium,
                            color = if(state.title.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                                else TaskyGray,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.tertiary
                )
                TaskyEditableField(
                    isEditing = isEditing,
                    onClick = {
                        onAction(AgendaDetailsAction.OnEditTextClick(
                            EditTextArgs(
                                editTextScreenType = EditTextScreenType.DESCRIPTION,
                                textToBeUpdated = state.description
                            )
                        ))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.description
                            .ifEmpty { stringResource(id = R.string.add_description) },
                        style = MaterialTheme.typography.labelMedium,
                        color = if(state.description.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                            else TaskyGray,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (agendaItem is AgendaItemDetails.Event) {
                Spacer(modifier = Modifier.height(16.dp))
                PhotosSection(
                    photos = agendaItem.eventPhotos,
                    onAddNewPhotoClick = {
                        onAction(AgendaDetailsAction.OnAddPhotoClick)
                    },
                    onPhotoClick = { },
                    canEditPhotos = agendaItem.canEditPhotos && isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            DateTimeSection(
                isEditing = isEditing,
                onAction = { action-> onAction(action) },
                state = state
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            ReminderDropdownField(
                isEditing = isEditing,
                remindAtText = state.reminderType.toReminderText().asString(),
                menuItems = ReminderType.entries.map { type ->
                    DropDownItem(
                        title = type.toReminderText().asString(),
                        onClick = {
                            onAction(
                                AgendaDetailsAction.OnSelectReminderType(type)
                            )
                        }
                    )
                }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            if(agendaItem is AgendaItemDetails.Event) {
                if(agendaItem.isAddingAttendee) {
                    AddEventAttendeeDialog(
                        onDismiss = { onAction(AgendaDetailsAction.OnDismissAttendeeDialog) },
                        onAdd = { onAction(AgendaDetailsAction.OnAddAttendeeClick) },
                        agendaItem = agendaItem
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.heightIn(min = 36.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.visitors),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if(isEditing) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { onAction(AgendaDetailsAction.OnOpenAttendeeDialog) }
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = PlusIcon,
                                    contentDescription = null,
                                    tint = TaskyGray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    AttendanceFilter(
                        selectedFilter = agendaItem.selectedFilter,
                        onSelectFilter = { selectedFilter ->
                            onAction(AgendaDetailsAction.OnSelectFilter(selectedFilter))
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AttendeeList(
                        attendeeList = agendaItem.attendees,
                        selectedAttendanceFilter = agendaItem.selectedFilter,
                        eventCreator = agendaItem.eventCreator
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            ManageAgendaItemStateButton(
                onClick = { onAction(AgendaDetailsAction.OnManageItemStateButtonClick) },
                modifier = Modifier,
                agendaItem = agendaItem
            )
        }
    }
}

fun ComponentActivity.shouldShowNotificationPermissionRationale(): Boolean {
    return Build.VERSION.SDK_INT >= 33 &&
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
}

@Preview
@Composable
private fun AgendaDetailsScreenPreview() {
    TaskyTheme {
        AgendaDetailsScreen(
            state = AgendaDetailsState(
                id = "123",
                title = "Meeting",
                description = "Description",
                fromDate = LocalDate.now(),
                fromTime = LocalTime.now(),
                reminderType = ReminderType.ThirtyMinutes,
                isEditing = true,
                agendaItem = AgendaItemDetails.Event(
                    toDate = LocalDate.now(),
                    toTime = LocalTime.now().plusMinutes(15),
                    selectedFilter = FilterType.GOING,
                    attendees = listOf(
                        AttendeeUi(
                            userId = "1",
                            fullName = "Humberto Costa",
                            email = "email@test.com",
                            isGoing = true
                        ),
                        AttendeeUi(
                            userId = "2",
                            fullName = "Humberto Costa",
                            email = "email2@test.com",
                            isGoing = false
                        ),
                    )
                ),
            ),
            onAction = {}
        )
    }
}