package com.humberto.tasky.core.data.networking

import com.humberto.tasky.core.data.model.AccessTokenRequest
import com.humberto.tasky.core.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccessTokenService {

    @POST("/accessToken")
    suspend fun refreshAccessToken(@Body accessTokenRequest: AccessTokenRequest): Response<AccessTokenResponse>
}