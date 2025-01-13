package com.humberto.tasky.core.data.alarm

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.humberto.tasky.R
import com.humberto.tasky.core.domain.alarm.AlarmItem
import com.humberto.tasky.core.domain.alarm.AlarmNotificationManager
import com.humberto.tasky.core.domain.alarm.AlarmNotificationManager.Companion.ALARM_CHANNEL_ID
import com.humberto.tasky.main.MainActivity

class AlarmNotificationManagerImpl(
    private val app: Application
): AlarmNotificationManager {

    private val notificationManager = app.getSystemService(NotificationManager::class.java)

    override fun showNotification(item: AlarmItem) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            app.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val activityIntent = Intent(app, MainActivity::class.java).apply {
            data =
                "tasky://agenda_item/${item.itemType}?agendaItemId=${item.id}&isEditing=${0}&selectedDateEpochDay=${item.itemDate.toEpochDay()}"
                    .toUri()
        }

        val pendingIntent = TaskStackBuilder.create(app).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(app, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.tasky_logo_notif)
            .setContentTitle(item.title)
            .setContentText(item.description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(item.id.hashCode(), notification)
    }
}