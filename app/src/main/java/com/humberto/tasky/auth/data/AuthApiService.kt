package com.humberto.tasky.auth.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    @POST("/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<Unit>
}