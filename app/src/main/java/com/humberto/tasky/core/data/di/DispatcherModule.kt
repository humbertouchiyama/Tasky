package com.humberto.tasky.core.data.di

import com.humberto.tasky.core.domain.util.DefaultDispatchers
import com.humberto.tasky.core.domain.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatchers
}