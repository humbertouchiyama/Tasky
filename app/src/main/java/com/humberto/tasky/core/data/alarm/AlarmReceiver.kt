package com.humberto.tasky.core.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.humberto.tasky.core.domain.alarm.AlarmItem
import com.humberto.tasky.core.domain.alarm.AlarmNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var alarmNotificationManager: AlarmNotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val item = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("item", AlarmItem::class.java)
        } else intent.getParcelableExtra("item")

        item?.let(alarmNotificationManager::showNotification)
    }
}