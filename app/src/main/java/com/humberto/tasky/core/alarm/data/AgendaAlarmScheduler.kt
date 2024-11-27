package com.humberto.tasky.core.alarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.humberto.tasky.core.alarm.domain.AlarmItem
import com.humberto.tasky.core.alarm.domain.AlarmScheduler
import com.humberto.tasky.core.alarm.presentation.AlarmReceiver
import javax.inject.Inject

class AgendaAlarmScheduler @Inject constructor(
    private val context: Context
): AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleAlarm(alarmItem: AlarmItem) {
        val now = System.currentTimeMillis()
        if (alarmItem.triggerAt < now) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.ALARM_ID, alarmItem.id)
            putExtra(AlarmReceiver.ALARM_TITLE, alarmItem.title)
            putExtra(AlarmReceiver.ALARM_DESCRIPTION, alarmItem.description)
            putExtra(AlarmReceiver.ITEM_TYPE, alarmItem.itemType.name)
            putExtra(AlarmReceiver.ITEM_DATE, alarmItem.itemDate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmItem.triggerAt,
                pendingIntent
            )
        }
    }

    override fun cancelAlarm(alarmItem: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.ALARM_ID, alarmItem.id)
            putExtra(AlarmReceiver.ALARM_TITLE, alarmItem.title)
            putExtra(AlarmReceiver.ALARM_DESCRIPTION, alarmItem.description)
            putExtra(AlarmReceiver.ITEM_TYPE, alarmItem.itemType.name)
            putExtra(AlarmReceiver.ITEM_DATE, alarmItem.itemDate)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmItem.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
