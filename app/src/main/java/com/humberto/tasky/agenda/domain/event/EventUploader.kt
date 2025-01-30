package com.humberto.tasky.agenda.domain.event

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface EventUploader {
    fun upload(
        id: String,
        type: Type,
        requestJson: String,
        photoUris: Array<String>
    ): Flow<Result<PhotoSizeTooLargeCount, DataError.Network>>

    enum class Type {
        Create, Update
    }
}

typealias PhotoSizeTooLargeCount = Int