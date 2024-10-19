package com.humberto.tasky.agenda.presentation

sealed class AgendaItemType {
    data object Task : AgendaItemType()
    data object Event : AgendaItemType()
    data object Reminder : AgendaItemType()
}