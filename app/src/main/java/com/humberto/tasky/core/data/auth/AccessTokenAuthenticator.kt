package com.humberto.tasky.core.data.auth

import com.humberto.tasky.core.data.model.AccessTokenRequest
import com.humberto.tasky.core.data.networking.AccessTokenService
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.domain.model.AuthInfo
import com.humberto.tasky.core.domain.repository.AccessTokenManager
import com.humberto.tasky.core.domain.util.Result
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AccessTokenAuthenticator @Inject constructor(
    private val tokenManager: AccessTokenManager,
    private val accessTokenService: Lazy<AccessTokenService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val authInfo = tokenManager.get()
            val newAuthInfo = authInfo?.let {
                refreshAccessToken(authInfo = authInfo)
            } ?: return@runBlocking null

            tokenManager.set(newAuthInfo)

            response.request.newBuilder()
                .header("Authorization", "Bearer ${newAuthInfo.accessToken}")
                .build()
        }
    }

    private suspend fun refreshAccessToken(
        authInfo: AuthInfo?
    ): AuthInfo? {
        return try {
            val response = safeCall {
                accessTokenService.get().refreshAccessToken(
                    AccessTokenRequest(
                        refreshToken = authInfo?.refreshToken ?: "",
                        userId = authInfo?.userId ?: ""
                ))
            }
            if (response is Result.Success) {
                AuthInfo(
                    accessToken = response.data.accessToken,
                    refreshToken = authInfo?.refreshToken ?: "",
                    userId = authInfo?.userId ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}