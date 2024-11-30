package com.humberto.tasky.core.alarm.domain

interface AlarmScheduler {
    fun scheduleAlarm(alarmItem: AlarmItem)
    fun cancelAlarm(id: String)
}