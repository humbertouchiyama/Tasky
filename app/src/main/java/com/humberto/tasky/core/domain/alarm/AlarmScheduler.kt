package com.humberto.tasky.core.domain.alarm

interface AlarmScheduler {
    fun scheduleAlarm(alarmItem: AlarmItem)
    fun cancelAlarm(id: String)
}