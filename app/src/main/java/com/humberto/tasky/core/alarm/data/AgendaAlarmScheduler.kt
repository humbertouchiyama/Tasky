package com.humberto.tasky.core.alarm.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.humberto.tasky.core.alarm.domain.AlarmItem
import com.humberto.tasky.core.alarm.domain.AlarmScheduler
import com.humberto.tasky.core.alarm.mapper.toParcelable
import com.humberto.tasky.core.alarm.presentation.AlarmReceiver
import javax.inject.Inject

class AgendaAlarmScheduler @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager
): AlarmScheduler {

    override fun scheduleAlarm(alarmItem: AlarmItem) {
        val now = System.currentTimeMillis()
        if (alarmItem.triggerAt < now) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.ALARM_INFO, alarmItem.toParcelable())
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

    override fun cancelAlarm(id: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
