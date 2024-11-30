package com.humberto.tasky.core.alarm.domain

import com.humberto.tasky.agenda.presentation.AgendaItemType
import java.time.LocalDate

data class AlarmItem(
    val id: String,
    val title: String,
    val description: String?,
    val itemType: AgendaItemType,
    val triggerAt: Long,
    val itemDate: LocalDate
)
