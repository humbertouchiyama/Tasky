package com.humberto.tasky.event.di

import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.event.data.EventRepositoryImpl
import com.humberto.tasky.event.domain.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class EventModule {

    @Provides
    fun providesEventRepository(
        eventDao: EventDao
    ): EventRepository {
        return EventRepositoryImpl(eventDao)
    }
}