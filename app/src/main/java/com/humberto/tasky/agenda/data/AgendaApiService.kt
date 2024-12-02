package com.humberto.tasky.agenda.data

import com.humberto.tasky.agenda.data.event.CheckAttendeeExistsResponse
import com.humberto.tasky.agenda.data.task.TaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AgendaApiService {
    @GET("/logout")
    suspend fun logout(): Response<Unit>

    @GET("/attendee")
    suspend fun checkAttendeeExists(
        @Query("email") email: String
    ): Response<CheckAttendeeExistsResponse>

    @POST("/task")
    suspend fun createTask(@Body task: TaskRequest): Response<Unit>

    @DELETE("/task")
    suspend fun deleteTask(@Query("taskId") taskId: String): Response<Unit>
}