package com.humberto.tasky.agenda.presentation.agenda_details.model

import com.humberto.tasky.agenda.presentation.AgendaItemType
import java.time.LocalDate
import java.time.LocalTime

data class AgendaDetailsUi(
    val id: String? = null,
    val agendaItemType: AgendaItemType,
    val title: String = "",
    val description: String = "",
    val fromDate: LocalDate = LocalDate.now(),
    val toDate: LocalDate = LocalDate.now(),
    val fromTime: LocalTime = LocalTime.now(),
    val toTime: LocalTime = LocalTime.now().plusMinutes(30),
    val atDate: LocalDate = LocalDate.now(),
    val atTime: LocalTime = LocalTime.now(),
    val remindAt: String = "",
    val photosUrlList: List<String> = listOf()
)
