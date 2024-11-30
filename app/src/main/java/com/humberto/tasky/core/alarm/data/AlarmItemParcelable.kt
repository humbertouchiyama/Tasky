package com.humberto.tasky.core.alarm.data

import android.os.Parcelable
import com.humberto.tasky.agenda.presentation.AgendaItemType
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlarmItemParcelable(
    val id: String,
    val title: String,
    val description: String?,
    val itemType: AgendaItemType,
    val itemDateEpochDay: Long
): Parcelable
