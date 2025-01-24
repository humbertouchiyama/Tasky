package com.humberto.tasky.agenda.presentation.agenda_details

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.humberto.tasky.R
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaItemType
import com.humberto.tasky.agenda.domain.event.EventPhoto
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAgendaItem
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.toAttendeeUi
import com.humberto.tasky.agenda.presentation.agenda_details.mapper.updateWithAgendaItem
import com.humberto.tasky.agenda.presentation.edit_text.EditTextScreenType
import com.humberto.tasky.core.domain.ConnectivityObserver
import com.humberto.tasky.core.domain.alarm.AlarmScheduler
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.mapper.toAlarmItem
import com.humberto.tasky.core.presentation.ui.UiText
import com.humberto.tasky.core.presentation.ui.asUiText
import com.humberto.tasky.main.navigation.AgendaDetails
import com.humberto.tasky.main.navigation.EditTextArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgendaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val eventRepository: EventRepository,
    private val reminderRepository: ReminderRepository,
    private val alarmScheduler: AlarmScheduler,
    connectivityObserver: ConnectivityObserver,
    private val applicationScope: CoroutineScope
): ViewModel() {

    private val agendaDetailsArgs = savedStateHandle.toRoute<AgendaDetails>()

    private val _state = MutableStateFlow(
        AgendaDetailsState(
            id = agendaDetailsArgs.agendaItemId,
            isEditing = agendaDetailsArgs.isEditing,
            agendaItem = when(agendaDetailsArgs.agendaItemType) {
                AgendaItemType.EVENT -> AgendaItemDetails.Event(
                    isUserEventCreator = agendaDetailsArgs.agendaItemId == null
                )
                AgendaItemType.TASK -> AgendaItemDetails.Task()
                AgendaItemType.REMINDER -> AgendaItemDetails.Reminder
            }
        )
    )
    val state: StateFlow<AgendaDetailsState> = _state.asStateFlow()

    private val connectivityStatus = connectivityObserver
        .startObserving()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            ConnectivityObserver.ConnectivityStatus.Unavailable
        )

    private val eventChannel = Channel<AgendaDetailsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val deletedRemotePhotos = MutableStateFlow<List<EventPhoto.Remote>>(emptyList())

    init {
        fetchAgendaItemIfExists()
        agendaDetailsArgs.selectedDateEpochDay?.let { selectedDateEpochDay ->
            _state.update { currentState ->
                val selectedLocalDate = LocalDate.ofEpochDay(selectedDateEpochDay)
                currentState.copy(
                    fromDate = selectedLocalDate,
                    agendaItem = currentState.agendaItem.updateIfEvent {
                        copy(toDate = selectedLocalDate)
                    }
                )
            }
        }
        observeConnectivityStatus()
    }

    private fun fetchAgendaItemIfExists() {
        agendaDetailsArgs.agendaItemId?.let { id ->
            viewModelScope.launch {
                _state.update { it.copy(isLoadingItem = true) }
                when (agendaDetailsArgs.agendaItemType) {
                    AgendaItemType.TASK -> taskRepository.getTask(id)
                    AgendaItemType.EVENT -> eventRepository.getEvent(id)
                    AgendaItemType.REMINDER -> reminderRepository.getReminder(id)
                }.onSuccess { agendaItem ->
                    _state.update { it.updateWithAgendaItem(agendaItem) }
                }.onError { error ->
                    _state.update {
                        it.copy(
                            infoMessage = error.asUiText(),
                            isLoadingItem = false
                        )
                    }
                }
            }
        }
    }

    fun updateStateWithEditTextArgs(editTextArgs: EditTextArgs?) {
        _state.update { currentState ->
            when(editTextArgs?.editTextScreenType) {
                EditTextScreenType.TITLE -> currentState.copy(title = editTextArgs.textToBeUpdated)
                EditTextScreenType.DESCRIPTION -> currentState.copy(description = editTextArgs.textToBeUpdated)
                else -> currentState
            }
        }
    }

    private fun observeConnectivityStatus() {
        connectivityStatus.onEach { status ->
            _state.update { it ->
                it.copy(
                    agendaItem = if (it.agendaItem is AgendaItemDetails.Event) {
                        it.agendaItem.updateIfEvent {
                            it.agendaItem.asEventDetails?.let {
                                it.copy(canEditPhotos = status.isConnected() && it.isUserEventCreator)
                            } ?: it.agendaItem
                        }
                    } else it.agendaItem,
                    infoMessage = if (status == ConnectivityObserver.ConnectivityStatus.Lost) {
                        UiText.StringResource(R.string.lost_connection)
                    } else it.infoMessage
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun toggleEditingState() {
        _state.update { it.copy(isEditing = !it.isEditing) }
    }

    fun onAction(agendaDetailsAction: AgendaDetailsAction) {
        when(agendaDetailsAction) {
            is AgendaDetailsAction.OnSelectFilter -> {
                _state.update {
                    it.copy(
                        agendaItem = it.agendaItem.updateIfEvent {
                            copy(selectedFilter = agendaDetailsAction.filterType)
                        },
                    )
                }
            }
            is AgendaDetailsAction.OnSaveClick -> {
                saveItem()
            }
            AgendaDetailsAction.OnEditClick -> {
                toggleEditingState()
            }
            is AgendaDetailsAction.OnSelectFromDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        fromDate = agendaDetailsAction.fromDate,
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toDate = agendaDetailsAction.fromDate)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectFromTime -> {
                val newFromTime = agendaDetailsAction.fromTime
                val newToTime = newFromTime.plusMinutes(30)

                _state.update { currentState ->
                    currentState.copy(
                        fromTime = newFromTime,
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toTime = newToTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToDate -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toDate = agendaDetailsAction.toDate)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectToTime -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(toTime = agendaDetailsAction.toTime)
                        }
                    )
                }
            }
            is AgendaDetailsAction.OnSelectReminderType -> {
                _state.update { it.copy(reminderType = agendaDetailsAction.reminderType) }
            }
            AgendaDetailsAction.OnManageItemStateButtonClick -> {
                _state.update { state ->
                    //TODO properly handle action depending on item state
                    state.copy(
                        isConfirmingToDelete = true
                    )
                }
            }
            AgendaDetailsAction.OnConfirmDeleteClick -> {
                deleteItem()
            }
            AgendaDetailsAction.OnDismissDeleteClick -> {
                _state.update { it.copy(isConfirmingToDelete = false) }
            }
            AgendaDetailsAction.OnOpenAttendeeDialog -> {
                viewModelScope.launch {
                    if(connectivityStatus.value.isConnected()) {
                        _state.update { currentState ->
                            currentState.copy(
                                agendaItem = currentState.agendaItem.updateIfEvent {
                                    copy(isAddingAttendee = true)
                                }
                            )
                        }
                    } else {
                        eventChannel.send(AgendaDetailsEvent.Error(UiText.StringResource(R.string.error_connected_to_add_attendees)))
                    }
                }
            }
            AgendaDetailsAction.OnDismissAttendeeDialog -> {
                _state.update { currentState ->
                    currentState.copy(
                        agendaItem = currentState.agendaItem.updateIfEvent {
                            copy(isAddingAttendee = false)
                        }
                    )
                }
            }
            AgendaDetailsAction.OnAddAttendeeClick -> {
                checkAndAddAttendee()
            }
            is AgendaDetailsAction.SubmitNotificationPermissionInfo -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = agendaDetailsAction.showNotificationRationale
                    )
                }
            }
            AgendaDetailsAction.DismissRationaleDialog -> {
                _state.update {
                    it.copy(
                        showNotificationRationale = false
                    )
                }
            }
            AgendaDetailsAction.OnAddPhotoClick -> onClickAddPhoto()
            is AgendaDetailsAction.OnPhotoPicked -> onPhotoPicked(agendaDetailsAction.uri)
            AgendaDetailsAction.OnInfoMessageSeen -> onInfoMessageSeen()
            else -> Unit
        }
    }

    private fun onInfoMessageSeen() {
        _state.update { it.copy(infoMessage = null) }
    }

    private fun onClickAddPhoto() {
        if (!connectivityStatus.value.isConnected()) {
            _state.update {
                it.copy(
                    agendaItem = it.agendaItem.updateIfEvent {
                        copy(isAddingPhoto = false)
                    },
                    infoMessage = UiText.StringResource(R.string.error_internet_required_to_update_photos),
                )
            }
            return
        }
        _state.update {
            it.copy(
                agendaItem = it.agendaItem.updateIfEvent {
                    copy(isAddingPhoto = true)
                }
            )
        }
    }

    private fun onPhotoPicked(uri: Uri?) {
        _state.update {
            it.copy(
                agendaItem = it.agendaItem.updateIfEvent {
                    copy(isAddingPhoto = false)
                }
            )
        }
        if (state.value.isSaving) {
            return
        }
        val agendaItem = _state.value.agendaItem
        if (agendaItem is AgendaItemDetails.Event) {
            if (agendaItem.eventPhotos.size >= AgendaItem.Event.MAX_PHOTO_AMOUNT) {
                _state.update {
                    it.copy(
                        infoMessage = UiText.StringResource(
                            R.string.error_too_many_photos,
                            AgendaItem.Event.MAX_PHOTO_AMOUNT
                        )
                    )
                }
                return
            }
            uri?.let { uri ->
                viewModelScope.launch {
                    _state.update {
                        it.copy(
                            agendaItem = it.agendaItem.updateIfEvent {
                                copy(
                                    eventPhotos = agendaItem.eventPhotos + EventPhoto.Local(
                                        uriString = uri.toString(),
                                        key = UUID.randomUUID().toString(),
                                    )
                                )
                            },
//                            isEditing = true
                        )
                    }
                }
            }
        }
    }

    private inline fun <T : AgendaItemDetails> T.updateIfEvent(
        transform: AgendaItemDetails.Event.() -> AgendaItemDetails.Event
    ): AgendaItemDetails {
        return if (this is AgendaItemDetails.Event) {
            transform()
        } else {
            this
        }
    }

    private fun saveItem() {
        if (_state.value.id != null) {
            updateItem()
            return
        }
        applicationScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    isEditing = false
                )
            }
            val agendaItem = _state.value.toAgendaItem()
            when(agendaItem) {
                is AgendaItem.Task -> taskRepository.createTask(agendaItem)
                is AgendaItem.Event -> eventRepository.createEvent(agendaItem)
                is AgendaItem.Reminder -> reminderRepository.createReminder(agendaItem)
            }.onSuccess {
                alarmScheduler.scheduleAlarm(agendaItem.toAlarmItem())
                eventChannel.send(AgendaDetailsEvent.SaveSuccess)
            }.onError {
                eventChannel.send(AgendaDetailsEvent.Error(it.asUiText()))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun updateItem() {
        applicationScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    isEditing = false
                )
            }
            val agendaItem = _state.value.toAgendaItem()
            when(agendaItem) {
                is AgendaItem.Task -> taskRepository.updateTask(agendaItem)
                is AgendaItem.Event -> eventRepository.updateEvent(
                    event = agendaItem,
                    deletedRemotePhotoKeys = deletedRemotePhotos.value.map { it.key }
                )
                is AgendaItem.Reminder -> reminderRepository.updateReminder(agendaItem)
            }.onSuccess {
                alarmScheduler.cancelAlarm(agendaItem.id)
                alarmScheduler.scheduleAlarm(agendaItem.toAlarmItem())
                eventChannel.send(AgendaDetailsEvent.SaveSuccess)
            }.onError {
                eventChannel.send(AgendaDetailsEvent.Error(it.asUiText()))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun deleteItem() {
        applicationScope.launch {
            _state.value.id?.let { id ->
                _state.update { it.copy(isDeleting = true) }
                when(_state.value.agendaItem) {
                    is AgendaItemDetails.Event -> eventRepository.deleteEvent(id)
                    is AgendaItemDetails.Reminder -> reminderRepository.deleteReminder(id)
                    is AgendaItemDetails.Task -> taskRepository.deleteTask(id)
                }
                alarmScheduler.cancelAlarm(id)
                eventChannel.send(AgendaDetailsEvent.DeleteSuccess)
                _state.update {
                    it.copy(
                        isDeleting = false,
                        isConfirmingToDelete = false
                    )
                }
            }
        }
    }

    private fun checkAndAddAttendee() {
        viewModelScope.launch {
            val agendaItem = _state.value.agendaItem
            if (agendaItem is AgendaItemDetails.Event) {
                _state.update {
                    it.copy(agendaItem = agendaItem.copy(isCheckingIfAttendeeExists = true))
                }
                eventRepository
                    .checkAttendeeExists(
                        email = agendaItem.newAttendeeEmail.text.toString()
                    ).onSuccess { attendee ->
                        _state.update {
                            it.copy(
                                agendaItem = agendaItem.copy(
                                    attendees = agendaItem.attendees + attendee.toAttendeeUi(),
                                    newAttendeeEmail = TextFieldState(),
                                    isAddingAttendee = false,
                                    isCheckingIfAttendeeExists = false
                                )
                            )
                        }
                    }.onError { error ->
                        when(error) {
                            DataError.Network.NOT_FOUND -> {
                                eventChannel.send(AgendaDetailsEvent.Error(
                                    UiText.StringResource(R.string.user_not_found)
                                ))
                            }
                            DataError.Network.CONFLICT -> {
                                eventChannel.send(AgendaDetailsEvent.Error(
                                    UiText.StringResource(R.string.you_cant_add_yourself)
                                ))
                            }
                            else -> eventChannel.send(AgendaDetailsEvent.Error(error.asUiText()))
                        }
                        _state.update {
                            it.copy(agendaItem = agendaItem.copy(isCheckingIfAttendeeExists = false))
                        }
                    }
            }
        }
    }
}

val AgendaItemDetails.asEventDetails: AgendaItemDetails.Event?
    get() = this as? AgendaItemDetails.Event