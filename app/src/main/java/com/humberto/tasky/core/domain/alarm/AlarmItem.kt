package com.humberto.tasky.core.domain.alarm

import android.os.Parcelable
import com.humberto.tasky.agenda.domain.AgendaItemType
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class AlarmItem(
    val id: String,
    val title: String,
    val description: String?,
    val itemType: AgendaItemType,
    val triggerAt: Long,
    val itemDate: LocalDate
): Parcelable
