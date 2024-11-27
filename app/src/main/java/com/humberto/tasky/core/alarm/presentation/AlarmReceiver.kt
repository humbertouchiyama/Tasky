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
import com.humberto.tasky.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel()
        val id = intent.getStringExtra(ALARM_ID)
        val title = intent.getStringExtra(ALARM_TITLE)?.takeIf { it.isNotEmpty() } ?: context.getString(R.string.no_title)
        val description = intent.getStringExtra(ALARM_DESCRIPTION)
        val itemType = intent.getStringExtra(ITEM_TYPE)
        val selectedDate = intent.getLongExtra(ITEM_DATE, LocalDate.now().toEpochDay())

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            data =
                "tasky://agenda_item/${itemType}?agendaItemId=${id}&isEditing=${0}&selectedDateEpochDay=${selectedDate}"
            .toUri()
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.tasky_logo_notif)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id.hashCode(), notification)
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

        const val ALARM_ID = "ALARM_ID"
        const val ALARM_TITLE = "ALARM_TITLE"
        const val ALARM_DESCRIPTION = "ALARM_DESCRIPTION"
        const val ITEM_TYPE = "ITEM_TYPE"
        const val ITEM_DATE = "ITEM_DATE"
    }
}