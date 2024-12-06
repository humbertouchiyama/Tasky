package com.humberto.tasky.agenda.data.event

import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    val key: String,
    val url: String
)
