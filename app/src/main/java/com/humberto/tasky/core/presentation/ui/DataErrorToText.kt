package com.humberto.tasky.core.presentation.ui

import com.humberto.tasky.R
import com.humberto.tasky.core.domain.util.DataError

fun DataError.asUiText(): UiText {
    return when(this) {
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.error_request_timeout
        )
        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.error_no_internet
        )
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.error_payload_too_large
        )
        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.error_server_error
        )
        else -> UiText.StringResource(
            R.string.error_unknown
        )
    }
}