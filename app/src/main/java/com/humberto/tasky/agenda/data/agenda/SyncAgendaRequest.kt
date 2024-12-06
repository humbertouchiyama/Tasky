package com.humberto.tasky.agenda.data.agenda

import kotlinx.serialization.Serializable

@Serializable
data class SyncAgendaRequest(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
)
