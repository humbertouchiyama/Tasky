package com.humberto.tasky.agenda.di

import com.humberto.tasky.agenda.data.AgendaApiService
import com.humberto.tasky.agenda.data.AgendaRepositoryImpl
import com.humberto.tasky.agenda.domain.AgendaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AgendaModule {

    @Provides
    @Singleton
    fun providesAgendaApiService(retrofit: Retrofit): AgendaApiService {
        return retrofit.create(AgendaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesAgendaRepository(
        agendaApiService: AgendaApiService,
    ): AgendaRepository {
        return AgendaRepositoryImpl(
            agendaApiService
        )
    }
}