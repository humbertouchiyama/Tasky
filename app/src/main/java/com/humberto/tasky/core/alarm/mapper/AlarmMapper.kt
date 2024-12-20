package com.humberto.tasky.core.alarm.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.presentation.AgendaItemType
import com.humberto.tasky.core.alarm.data.AlarmItemParcelable
import com.humberto.tasky.core.alarm.domain.AlarmItem
import java.time.ZoneId

fun AgendaItem.toAlarmItem(): AlarmItem {
    val fromLocalZone = from.withZoneSameInstant(ZoneId.systemDefault())
    val fromMillis = fromLocalZone.toInstant().toEpochMilli()
    val remindAt = fromMillis - reminderType.duration.inWholeMilliseconds
    return AlarmItem(
        id = id,
        title = title,
        description = description,
        triggerAt = remindAt,
        itemType = when(this) {
            is AgendaItem.Event -> AgendaItemType.EVENT
            is AgendaItem.Reminder -> AgendaItemType.REMINDER
            is AgendaItem.Task -> AgendaItemType.TASK
        },
        itemDate = fromLocalZone.toLocalDate()
    )
}

fun AlarmItem.toParcelable(): AlarmItemParcelable {
    return AlarmItemParcelable(
        id = id,
        title = title,
        description = description,
        itemType = itemType,
        itemDateEpochDay = itemDate.toEpochDay(),
    )
}