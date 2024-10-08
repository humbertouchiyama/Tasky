package com.humberto.tasky.core.domain.repository

import com.humberto.tasky.core.domain.model.AuthInfo

interface SessionManager {
    fun get(): AuthInfo?
    fun set(info: AuthInfo?)
}