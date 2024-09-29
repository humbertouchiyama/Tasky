package com.humberto.tasky.core.data.networking

import com.humberto.tasky.core.data.model.AccessTokenRequest
import com.humberto.tasky.core.data.model.AccessTokenResponse
import retrofit2.Response
import retrofit2.http.POST

interface AccessTokenService {

    @POST("/accessToken")
    fun refreshAccessToken(accessTokenRequest: AccessTokenRequest): Response<AccessTokenResponse>
}