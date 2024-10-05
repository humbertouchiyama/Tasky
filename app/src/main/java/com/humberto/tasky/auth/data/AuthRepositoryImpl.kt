package com.humberto.tasky.auth.data

import com.humberto.tasky.auth.domain.AuthRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.domain.model.AuthInfo
import com.humberto.tasky.core.domain.repository.AccessTokenManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.asEmptyDataResult
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: AccessTokenManager,
): AuthRepository {
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = safeCall {
            authApiService.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )
        }.onSuccess { loginResponse ->
            tokenManager.set(
                AuthInfo(
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken,
                    userId = loginResponse.userId
                )
            )
            Timber.d("login success")
        }.onError {
            Timber.d("login failed")
        }

        return result.asEmptyDataResult()
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): EmptyResult<DataError.Network> {
        return safeCall {
            authApiService.register(
                RegisterRequest(
                    fullName = fullName,
                    email = email,
                    password = password
                )
            )
        }
    }
}