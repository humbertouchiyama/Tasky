package com.humberto.tasky.core.mapper

import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaItemType
import com.humberto.tasky.core.domain.alarm.AlarmItem
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