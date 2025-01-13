package com.humberto.tasky.core.data.alarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import com.humberto.tasky.core.domain.alarm.AlarmItem
import com.humberto.tasky.core.domain.alarm.AlarmScheduler
import javax.inject.Inject

class AgendaAlarmScheduler @Inject constructor(
    private val app: Application
): AlarmScheduler {

    private val alarmManager = app.getSystemService(AlarmManager::class.java)

    override fun scheduleAlarm(alarmItem: AlarmItem) {
        val now = System.currentTimeMillis()
        if (alarmItem.triggerAt < now) return

        val intent = Intent(app, AlarmReceiver::class.java).apply {
            putExtra("item", alarmItem)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            app,
            alarmItem.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            alarmManager.canScheduleExactAlarms()
        ) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmItem.triggerAt,
                pendingIntent
            )
        }
    }

    override fun cancelAlarm(id: String) {
        val intent = Intent(app, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            app,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
