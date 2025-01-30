package com.humberto.tasky.agenda.di

import android.app.Application
import android.content.Context
import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.data.agenda.AgendaRepositoryImpl
import com.humberto.tasky.agenda.data.agenda.AgendaSynchronizerImpl
import com.humberto.tasky.agenda.data.event.EventRepositoryImpl
import com.humberto.tasky.agenda.data.event.EventUploaderImpl
import com.humberto.tasky.agenda.data.photo.BitmapPhotoCompressor
import com.humberto.tasky.agenda.data.photo.PhotoExtensionParserImpl
import com.humberto.tasky.agenda.data.reminder.ReminderRepositoryImpl
import com.humberto.tasky.agenda.data.task.TaskRepositoryImpl
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.domain.AgendaSynchronizer
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.event.EventUploader
import com.humberto.tasky.agenda.domain.photo.PhotoCompressor
import com.humberto.tasky.agenda.domain.photo.PhotoExtensionParser
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
import com.humberto.tasky.core.data.di.ApplicationScope
import com.humberto.tasky.core.database.AgendaDatabase
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.domain.repository.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
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
        agendaDatabase: AgendaDatabase,
        taskDao: TaskDao,
        eventDao: EventDao,
        reminderDao: ReminderDao,
        sessionManager: SessionManager,
        @ApplicationScope applicationScope: CoroutineScope
    ): AgendaRepository {
        return AgendaRepositoryImpl(
            agendaApiService,
            agendaDatabase,
            taskDao,
            eventDao,
            reminderDao,
            sessionManager,
            applicationScope
        )
    }

    @Provides
    fun providesEventRepository(
        eventDao: EventDao,
        agendaApiService: AgendaApiService,
        sessionManager: SessionManager,
        eventUploader: EventUploader
    ): EventRepository {
        return EventRepositoryImpl(
            eventDao,
            agendaApiService,
            sessionManager,
            eventUploader
        )
    }

    @Provides
    fun providesReminderRepository(
        reminderDao: ReminderDao,
        agendaApi: AgendaApiService,
        sessionManager: SessionManager
    ): ReminderRepository {
        return ReminderRepositoryImpl(
            reminderDao,
            agendaApi,
            sessionManager
        )
    }

    @Provides
    fun providesTaskRepository(
        reminderDao: TaskDao,
        agendaApi: AgendaApiService,
        sessionManager: SessionManager
    ): TaskRepository {
        return TaskRepositoryImpl(
            reminderDao,
            agendaApi,
            sessionManager
        )
    }

    @Provides
    @Singleton
    fun providesEventUploader(
        app: Application
    ): EventUploader = EventUploaderImpl(app)

    @Provides
    @Singleton
    fun providesPhotoCompressor(
        @ApplicationContext context: Context
    ): PhotoCompressor {
        return BitmapPhotoCompressor(context)
    }

    @Provides
    @Singleton
    fun providesPhotoExtensionParser(
        application: Application
    ): PhotoExtensionParser {
        return PhotoExtensionParserImpl(application)
    }

    @Provides
    @Singleton
    fun providesAgendaSynchronizer(app: Application): AgendaSynchronizer {
        return AgendaSynchronizerImpl(app)
    }
}