package com.humberto.tasky.agenda.presentation.agenda_details

import android.net.Uri
import com.humberto.tasky.main.navigation.EditTextArgs
import java.time.LocalDate
import java.time.LocalTime

sealed interface AgendaDetailsAction {
    data class OnSelectFilter(val filterType: FilterType): AgendaDetailsAction
    data object OnBackClick: AgendaDetailsAction
    data object OnEditClick: AgendaDetailsAction
    data object OnSaveClick: AgendaDetailsAction
    data class OnSelectFromDate(val fromDate: LocalDate): AgendaDetailsAction
    data class OnSelectFromTime(val fromTime: LocalTime): AgendaDetailsAction
    data class OnSelectToDate(val toDate: LocalDate): AgendaDetailsAction
    data class OnSelectToTime(val toTime: LocalTime): AgendaDetailsAction
    data class OnSelectReminderType(val reminderType: ReminderType): AgendaDetailsAction
    data class OnEditTextClick(val editTextArgs: EditTextArgs): AgendaDetailsAction
    data object OnManageItemStateButtonClick: AgendaDetailsAction
    data object OnConfirmDeleteClick: AgendaDetailsAction
    data object OnDismissDeleteClick: AgendaDetailsAction
    data object OnOpenAttendeeDialog: AgendaDetailsAction
    data object OnDismissAttendeeDialog: AgendaDetailsAction
    data object OnAddAttendeeClick: AgendaDetailsAction
    data class SubmitNotificationPermissionInfo(
        val showNotificationRationale: Boolean
    ): AgendaDetailsAction
    data object DismissRationaleDialog: AgendaDetailsAction
    data object OnAddPhotoClick: AgendaDetailsAction
    data class OnPhotoPicked(val uri: Uri?): AgendaDetailsAction
    data object NavigateToPhotoPreviewScreen: AgendaDetailsAction
    data object OnInfoMessageSeen: AgendaDetailsAction
}