package com.humberto.tasky.core.database.di

import android.content.Context
import androidx.room.Room
import com.humberto.tasky.core.database.AgendaDatabase
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.data_source.RoomLocalAgendaDataSource
import com.humberto.tasky.core.domain.agenda.LocalAgendaDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun providesAgendaDatabase(
        @ApplicationContext context: Context
    ): AgendaDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AgendaDatabase::class.java,
            "agenda.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesTaskDao(
        agendaDatabase: AgendaDatabase
    ): TaskDao {
        return agendaDatabase.taskDao
    }

    @Provides
    @Singleton
    fun providesEventDao(
        eventDatabase: AgendaDatabase
    ): EventDao {
        return eventDatabase.eventDao
    }

    @Provides
    @Singleton
    fun providesReminderDao(
        reminderDatabase: AgendaDatabase
    ): ReminderDao {
        return reminderDatabase.reminderDao
    }

    @Provides
    @Singleton
    fun providesRoomLocalAgendaDataSource(
        taskDao: TaskDao,
        eventDao: EventDao,
        reminderDao: ReminderDao,
        agendaDatabase: AgendaDatabase
    ): LocalAgendaDataSource {
        return RoomLocalAgendaDataSource(
            taskDao = taskDao,
            eventDao = eventDao,
            reminderDao = reminderDao,
            agendaDatabase = agendaDatabase
        )
    }
}