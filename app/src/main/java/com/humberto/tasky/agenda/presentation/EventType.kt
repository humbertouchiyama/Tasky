package com.humberto.tasky.agenda.presentation

sealed class EventType {
    data object Task : EventType()
    data object Event : EventType()
    data object Reminder : EventType()
}