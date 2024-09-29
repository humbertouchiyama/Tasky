package com.humberto.tasky.core.data.di

import android.content.Context
import com.humberto.tasky.BuildConfig
import com.humberto.tasky.core.data.auth.AccessTokenAuthenticator
import com.humberto.tasky.core.data.auth.AccessTokenManagerImpl
import com.humberto.tasky.core.data.networking.AccessTokenService
import com.humberto.tasky.core.domain.repository.AccessTokenManager
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreDataModule {

    @Provides
    @Singleton
    fun provideAccessTokenService(
        retrofit: Retrofit
    ): AccessTokenService {
        return retrofit.create(AccessTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideAccessTokenManager(
        @ApplicationContext context: Context
    ): AccessTokenManager {
        return AccessTokenManagerImpl(context.getSharedPreferences("access_token_prefs", Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: AccessTokenManager,
        accessTokenService: Lazy<AccessTokenService>
    ): AccessTokenAuthenticator {
        return AccessTokenAuthenticator(tokenManager, accessTokenService)
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Timber.d(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun providesHeadersInterceptor(
        accessTokenManager: AccessTokenManager,
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            val authInfo = accessTokenManager.get()
            authInfo?.let {
                request.addHeader("Authorization", "Bearer ${authInfo.accessToken}")
            }
            request.addHeader("api_key", BuildConfig.API_KEY)
            request.addHeader("Content-Type", "application/json")
            chain.proceed(request.build())
        }
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headersInterceptor: Interceptor,
        authenticator: AccessTokenAuthenticator,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(authenticator)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headersInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()))
            .build()
    }
}