package com.humberto.tasky.agenda.domain

interface AgendaSynchronizer {
    fun scheduleSync()
    fun cancelSync()
}