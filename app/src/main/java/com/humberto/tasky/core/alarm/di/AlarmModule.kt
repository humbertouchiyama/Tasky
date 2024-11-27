package com.humberto.tasky.core.alarm.di

import android.app.NotificationManager
import android.content.Context
import com.humberto.tasky.core.alarm.data.AgendaAlarmScheduler
import com.humberto.tasky.core.alarm.domain.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAgendaAlarmScheduler(
        @ApplicationContext context: Context
    ): AlarmScheduler {
        return AgendaAlarmScheduler(context)
    }

    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(NotificationManager::class.java)!!
    }
}