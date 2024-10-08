package com.humberto.tasky.core.data.auth

import android.content.SharedPreferences
import com.humberto.tasky.core.domain.model.AuthInfo
import com.humberto.tasky.core.domain.repository.SessionManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SessionManagerImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
): SessionManager {
    companion object {
        private const val KEY_AUTH_INFO = "KEY_AUTH_INFO"
    }

    override fun get(): AuthInfo? {
        val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
        json?.let {
            return Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo()
        }
        return null
    }

    override fun set(info: AuthInfo?) {
        if (info == null) {
            sharedPreferences.edit().remove(KEY_AUTH_INFO).apply()
            return
        }

        val json = Json.encodeToString(info.toAuthInfoSerializable())
        sharedPreferences
            .edit()
            .putString(KEY_AUTH_INFO, json)
            .apply()
    }
}