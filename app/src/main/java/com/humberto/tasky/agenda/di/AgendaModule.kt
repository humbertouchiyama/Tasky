package com.humberto.tasky.agenda.di

import android.content.Context
import androidx.work.WorkManager
import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.data.agenda.AgendaRepositoryImpl
import com.humberto.tasky.agenda.data.event.EventRepositoryImpl
import com.humberto.tasky.agenda.data.helper.WorkerHelper
import com.humberto.tasky.agenda.data.reminder.ReminderRepositoryImpl
import com.humberto.tasky.agenda.data.task.TaskRepositoryImpl
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.agenda.domain.event.EventRepository
import com.humberto.tasky.agenda.domain.reminder.ReminderRepository
import com.humberto.tasky.agenda.domain.task.TaskRepository
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
        sessionManager: SessionManager
    ): AgendaRepository {
        return AgendaRepositoryImpl(
            agendaApiService,
            agendaDatabase,
            taskDao,
            eventDao,
            reminderDao,
            sessionManager
        )
    }

    @Provides
    fun providesEventRepository(
        eventDao: EventDao,
        agendaApiService: AgendaApiService,
        sessionManager: SessionManager
    ): EventRepository {
        return EventRepositoryImpl(
            eventDao,
            agendaApiService,
            sessionManager
        )
    }

    @Provides
    fun providesReminderRepository(
        reminderDao: ReminderDao,
        agendaApi: AgendaApiService,
        sessionManager: SessionManager,
        workerHelper: WorkerHelper
    ): ReminderRepository {
        return ReminderRepositoryImpl(
            reminderDao,
            agendaApi,
            sessionManager,
            workerHelper
        )
    }

    @Provides
    fun providesTaskRepository(
        reminderDao: TaskDao,
        agendaApi: AgendaApiService,
        sessionManager: SessionManager,
        workerHelper: WorkerHelper
    ): TaskRepository {
        return TaskRepositoryImpl(
            reminderDao,
            agendaApi,
            sessionManager,
            workerHelper
        )
    }

    @Provides
    @Singleton
    fun providesWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideWorkerHelper(workManager: WorkManager): WorkerHelper = WorkerHelper(workManager)
}