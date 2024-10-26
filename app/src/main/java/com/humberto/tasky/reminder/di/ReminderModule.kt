package com.humberto.tasky.reminder.di

import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.reminder.data.ReminderRepositoryImpl
import com.humberto.tasky.reminder.domain.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ReminderModule {

    @Provides
    fun providesReminderRepository(
        reminderDao: ReminderDao
    ): ReminderRepository {
        return ReminderRepositoryImpl(reminderDao)
    }
}