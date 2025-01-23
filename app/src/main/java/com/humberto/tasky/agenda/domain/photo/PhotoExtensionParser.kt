package com.humberto.tasky.agenda.domain.photo

import android.net.Uri

interface PhotoExtensionParser {
    fun getExtensionForUri(uri: Uri): String
}