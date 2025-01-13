package com.humberto.tasky.core.data.di

import android.app.Application
import com.humberto.tasky.core.data.alarm.AgendaAlarmScheduler
import com.humberto.tasky.core.data.alarm.AlarmNotificationManagerImpl
import com.humberto.tasky.core.domain.alarm.AlarmNotificationManager
import com.humberto.tasky.core.domain.alarm.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAgendaAlarmScheduler(
        application: Application
    ): AlarmScheduler {
        return AgendaAlarmScheduler(application)
    }

    @Provides
    @Singleton
    fun provideAlarmNotificationManager(
        application: Application,
    ): AlarmNotificationManager {
        return AlarmNotificationManagerImpl(application)
    }
}