package com.humberto.tasky.core.domain.alarm

interface AlarmNotificationManager {
    fun showNotification(item: AlarmItem)

    companion object {
        const val ALARM_CHANNEL_ID = "alarm_channel"
    }
}