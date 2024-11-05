package com.humberto.tasky.agenda.presentation.agenda_details.model

import com.humberto.tasky.agenda.presentation.AgendaItemType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AgendaDetailsUi(
    val id: String? = null,
    val agendaItemType: AgendaItemType,
    val title: String = "",
    val description: String = "",
    val fromDate: LocalDate = LocalDate.now(),
    val toDate: LocalDate = LocalDate.now(),
    val fromTime: LocalTime = LocalTime.now(),
    val toTime: LocalTime = LocalTime.now().plusMinutes(30L),
    val atDate: LocalDate = LocalDate.now(),
    val atTime: LocalTime = LocalTime.now(),
    val remindAt: LocalDateTime = LocalDateTime.now().minusMinutes(30L),
    val photosUrlList: List<String> = listOf()
)
