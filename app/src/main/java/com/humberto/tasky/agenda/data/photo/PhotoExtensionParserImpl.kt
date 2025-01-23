package com.humberto.tasky.agenda.data.photo

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import com.humberto.tasky.agenda.domain.photo.PhotoExtensionParser
import java.io.File

class PhotoExtensionParserImpl(
    private val app: Application
): PhotoExtensionParser {

    override fun getExtensionForUri(uri: Uri): String {
        return if(uri.scheme == ContentResolver.SCHEME_CONTENT) {
            MimeTypeMap.getSingleton().getExtensionFromMimeType(
                app.contentResolver.getType(uri)
            ) ?: ""
        } else {
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path ?: "")).toString())
        }
    }
}