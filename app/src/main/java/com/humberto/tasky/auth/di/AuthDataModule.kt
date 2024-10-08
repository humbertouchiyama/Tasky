package com.humberto.tasky.auth.di

import com.humberto.tasky.auth.data.AuthApiService
import com.humberto.tasky.auth.data.AuthRepositoryImpl
import com.humberto.tasky.auth.data.EmailPatternValidator
import com.humberto.tasky.auth.domain.AuthRepository
import com.humberto.tasky.auth.domain.PatternValidator
import com.humberto.tasky.auth.domain.UserDataValidator
import com.humberto.tasky.core.domain.repository.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthDataModule {

    @Provides
    @Singleton
    fun providesAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesAuthRepository(
        authApiService: AuthApiService,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepositoryImpl(
            authApiService,
            sessionManager
        )
    }

    @Provides
    @Singleton
    fun providesPatternValidator(): PatternValidator {
        return EmailPatternValidator
    }

    @Provides
    @Singleton
    fun providesUserDataValidator(
        patternValidator: PatternValidator
    ): UserDataValidator {
        return UserDataValidator(
            patternValidator
        )
    }
}