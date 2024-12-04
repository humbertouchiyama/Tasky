package com.humberto.tasky.core.domain.util

sealed interface DataError: Error {
    enum class Network: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
        NOT_FOUND
    }

    enum class Local: DataError {
        DISK_FULL,
        NOT_FOUND
    }
}

fun DataError.isRetryable(): Boolean {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT,
        DataError.Network.TOO_MANY_REQUESTS,
        DataError.Network.NO_INTERNET,
        DataError.Network.SERVER_ERROR -> true
        else -> false
    }
}
