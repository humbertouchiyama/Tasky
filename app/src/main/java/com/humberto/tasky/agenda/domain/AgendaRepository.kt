package com.humberto.tasky.agenda.domain

import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult

interface AgendaRepository {
    suspend fun logout(): EmptyResult<DataError.Network>
}