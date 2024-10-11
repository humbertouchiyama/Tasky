package com.humberto.tasky.agenda.data

import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val agendaApiService: AgendaApiService
): AgendaRepository {
    override suspend fun logout(): EmptyResult<DataError.Network> {
        return safeCall {
            agendaApiService.logout()
        }
    }
}