package com.humberto.tasky.core.data.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.humberto.tasky.BuildConfig
import com.humberto.tasky.core.data.AndroidConnectivityObserver
import com.humberto.tasky.core.data.auth.AccessTokenAuthenticator
import com.humberto.tasky.core.data.auth.SessionManagerImpl
import com.humberto.tasky.core.data.networking.AccessTokenService
import com.humberto.tasky.core.domain.ConnectivityObserver
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.main.TaskyApp
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
    ): SessionManager {
        return SessionManagerImpl(context.getSharedPreferences("access_token_prefs", Context.MODE_PRIVATE))
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: SessionManager,
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
        sessionManager: SessionManager,
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            val authInfo = sessionManager.get()
            authInfo?.let {
                request.addHeader("Authorization", "Bearer ${authInfo.accessToken}")
            }
            request.addHeader("x-api-key", BuildConfig.API_KEY)
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

    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context.getSystemService<ConnectivityManager>()!!
    }

    @Provides
    @Singleton
    fun providesConnectivityObserver(
        connectivityManager: ConnectivityManager
    ): ConnectivityObserver {
        return AndroidConnectivityObserver(
            connectivityManager
        )
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(app: Application): CoroutineScope {
        return (app as TaskyApp).applicationScope
    }
}