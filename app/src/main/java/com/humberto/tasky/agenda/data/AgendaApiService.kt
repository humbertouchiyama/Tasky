package com.humberto.tasky.agenda.data

import retrofit2.Response
import retrofit2.http.GET

interface AgendaApiService {
    @GET("/logout")
    suspend fun logout(): Response<Unit>
}