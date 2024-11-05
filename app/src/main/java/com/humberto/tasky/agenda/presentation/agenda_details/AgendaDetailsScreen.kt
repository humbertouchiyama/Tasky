package com.humberto.tasky.agenda.presentation.agenda_details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.humberto.tasky.R
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.agenda.presentation.agenda_details.components.AgendaItemIndicator
import com.humberto.tasky.agenda.presentation.agenda_details.components.AttendanceFilter
import com.humberto.tasky.agenda.presentation.agenda_details.components.DateTimeSection
import com.humberto.tasky.agenda.presentation.agenda_details.components.PhotosSection
import com.humberto.tasky.agenda.presentation.agenda_details.components.ReminderDropdownField
import com.humberto.tasky.agenda.presentation.agenda_details.components.TaskyEditableField
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.getReminderOptions
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toRemindAtText
import com.humberto.tasky.agenda.presentation.agenda_details.model.AgendaDetailsUi
import com.humberto.tasky.core.presentation.designsystem.PlusIcon
import com.humberto.tasky.core.presentation.designsystem.TaskyGray
import com.humberto.tasky.core.presentation.designsystem.TaskyTheme
import com.humberto.tasky.core.presentation.designsystem.components.TaskyRadioButton
import com.humberto.tasky.core.presentation.designsystem.components.TaskyScaffold
import com.humberto.tasky.core.presentation.designsystem.components.TaskyToolbar
import com.humberto.tasky.core.presentation.designsystem.components.util.DropDownItem
import com.humberto.tasky.core.presentation.ui.ObserveAsEvents
import com.humberto.tasky.core.presentation.ui.toFormattedDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

@Composable
fun AgendaDetailsScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AgendaDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is AgendaDetailsEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            AgendaDetailsEvent.SaveSuccess -> {
                Toast.makeText(
                    context,
                    R.string.saved_successful,
                    Toast.LENGTH_LONG
                ).show()
            }
            AgendaDetailsEvent.DeleteSuccess -> {
                Toast.makeText(
                    context,
                    R.string.delete_successful,
                    Toast.LENGTH_LONG
                ).show()

                onBackClick()
            }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AgendaDetailsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AgendaDetailsAction.OnBackClick -> onBackClick()
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
    val agendaItem = state.agendaItem
    TaskyScaffold(
        topAppBar = {
            TaskyToolbar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = {
                                onAction(AgendaDetailsAction.OnBackClick)
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
                        text = ZonedDateTime.now().toFormattedDateTime().uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                endContent = {
                    Box(
                        modifier = Modifier
                            .size(width = 48.dp, height = 36.dp)
                            .padding(end = 8.dp)
                            .clickable(onClick = {
                                if(state.isEditing) {
                                    onAction(AgendaDetailsAction.OnSaveClick)
                                } else {
                                    onAction(AgendaDetailsAction.OnEditClick)
                                }
                            }),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if(state.isEditing) {
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
        }
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
                    agendaItemType = agendaItem.agendaItemType)
                TaskyEditableField(
                    isEditing = state.isEditing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskyRadioButton(enabled = false)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = agendaItem.title
                                .ifEmpty { stringResource(id = R.string.add_title) },
                            style = MaterialTheme.typography.headlineMedium,
                            color = if(agendaItem.title.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                                else TaskyGray
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.tertiary
                )
                TaskyEditableField(
                    isEditing = state.isEditing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = agendaItem.description
                            .ifEmpty { stringResource(id = R.string.add_description) },
                        style = MaterialTheme.typography.labelMedium,
                        color = if(agendaItem.description.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                            else TaskyGray,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (agendaItem.agendaItemType == AgendaItemType.EVENT) {
                PhotosSection(
                    photos = agendaItem.photosUrlList
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            DateTimeSection(
                isEditing = state.isEditing,
                onAction = { action-> onAction(action) },
                agendaItem = agendaItem
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            ReminderDropdownField(
                isEditing = state.isEditing,
                remindAtText = agendaItem.toRemindAtText().asString(),
                menuItems = getReminderOptions.map { (key, value) ->
                    DropDownItem(
                        title = value.asString(),
                        onClick = {
                            onAction(
                                AgendaDetailsAction.OnSelectReminderAt(key)
                            )
                        },
                    )
                }
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.tertiary
            )
            if(agendaItem.agendaItemType == AgendaItemType.EVENT) {
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
                        if(state.isEditing) {
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
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
                        selectedFilter = state.selectedFilter,
                        onSelectFilter = { selectedFilter ->
                            onAction(AgendaDetailsAction.OnSelectFilter(selectedFilter))
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun AgendaDetailsScreenPreview() {
    TaskyTheme {
        AgendaDetailsScreen(
            state = AgendaDetailsState(
                agendaItem = AgendaDetailsUi(
                    id = "123",
                    agendaItemType = AgendaItemType.EVENT,
                    title = "Meeting",
                    description = "Description",
                    fromDate = LocalDate.now(),
                    fromTime = LocalTime.now(),
                    toDate = LocalDate.now(),
                    toTime = LocalTime.now().plusMinutes(15),
                    atDate = LocalDate.now(),
                    atTime = LocalTime.now(),
                    remindAt = LocalDateTime.now().minusMinutes(30)
                ),
                isEditing = true
            ),
            onAction = {}
        )
    }
}