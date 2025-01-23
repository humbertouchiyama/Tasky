package com.humberto.tasky.agenda.domain.photo

import android.net.Uri

interface PhotoCompressor {
    suspend fun compress(contentUri: Uri, compressionThreshold: Long): ByteArray?
}