package com.humberto.tasky.core.alarm.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.humberto.tasky.R
import com.humberto.tasky.core.alarm.data.AlarmItemParcelable
import com.humberto.tasky.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel()
        val alarmInfo = intent.getParcelableExtra(ALARM_INFO, AlarmItemParcelable::class.java)

        alarmInfo?.let {
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                data =
                    "tasky://agenda_item/${it.itemType}?agendaItemId=${it.id}&isEditing=${0}&selectedDateEpochDay=${it.itemDateEpochDay}"
                .toUri()
            }

            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(activityIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.tasky_logo_notif)
                .setContentTitle(it.title)
                .setContentText(it.description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(it.id.hashCode(), notification)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_NAME = "Tasky Agenda Alarms"
        private const val CHANNEL_ID = "tasky_alarms"

        const val ALARM_INFO = "ALARM_INFO"
    }
}