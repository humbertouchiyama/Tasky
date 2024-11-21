package com.humberto.tasky.agenda.data

import com.humberto.tasky.agenda.data.event.CheckAttendeeExistsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AgendaApiService {
    @GET("/logout")
    suspend fun logout(): Response<Unit>

    @GET("/attendee")
    suspend fun checkAttendeeExists(
        @Query("email") email: String
    ): Response<CheckAttendeeExistsResponse>
}