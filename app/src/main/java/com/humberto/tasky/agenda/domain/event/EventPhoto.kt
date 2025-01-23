package com.humberto.tasky.agenda.domain.event

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
sealed interface EventPhoto: Parcelable {

    @Serializable
    data class Local(
        val key: String,
        val uriString: String
    ) : EventPhoto

    @Serializable
    data class Remote(
        val key: String,
        val photoUrl: String
    ) : EventPhoto
}