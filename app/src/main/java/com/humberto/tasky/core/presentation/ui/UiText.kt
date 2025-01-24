package com.humberto.tasky.core.presentation.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.parcelize.RawValue

sealed interface UiText {
    data class DynamicString(val value: String): UiText
    class StringResource(
        @StringRes val id: Int,
        vararg val args: @RawValue Any
    ): UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> stringResource(id = id, *args)
        }
    }

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> context.getString(id, *args)
        }
    }
}