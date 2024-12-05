package com.humberto.tasky.agenda.data

import com.humberto.tasky.agenda.data.event.CheckAttendeeExistsResponse
import com.humberto.tasky.agenda.data.event.CreateEventRequest
import com.humberto.tasky.agenda.data.event.UpdateEventRequest
import com.humberto.tasky.agenda.data.reminder.ReminderRequest
import com.humberto.tasky.agenda.data.task.TaskRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface AgendaApiService {
    @GET("/logout")
    suspend fun logout(): Response<Unit>

    @POST("/syncAgenda")
    suspend fun syncAgenda(
        @Body syncAgendaRequest: SyncAgendaRequest
    ): Response<Unit>

    @GET("/fullAgenda")
    suspend fun getFullAgenda(): Response<GetFullAgendaResponse>

    @GET("/attendee")
    suspend fun checkAttendeeExists(
        @Query("email") email: String
    ): Response<CheckAttendeeExistsResponse>

    @DELETE("/attendee")
    suspend fun deleteAttendee(
        @Query("eventId") email: String
    ): Response<Unit>

    @POST("/task")
    suspend fun createTask(@Body task: TaskRequest): Response<Unit>

    @PUT("/task")
    suspend fun updateTask(@Body task: TaskRequest): Response<Unit>

    @DELETE("/task")
    suspend fun deleteTask(@Query("taskId") taskId: String): Response<Unit>

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Part("create_event_request") createEventRequest: CreateEventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<Unit>

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Part("update_event_request") createEventRequest: UpdateEventRequest,
        @Part photos: List<MultipartBody.Part>
    ): Response<Unit>

    @DELETE("/event")
    suspend fun deleteEvent(@Query("eventId") eventId: String): Response<Unit>

    @POST("/reminder")
    suspend fun createReminder(@Body reminder: ReminderRequest): Response<Unit>

    @PUT("/reminder")
    suspend fun updateReminder(@Body reminder: ReminderRequest): Response<Unit>

    @DELETE("/reminder")
    suspend fun deleteReminder(@Query("reminderId") reminderId: String): Response<Unit>
}