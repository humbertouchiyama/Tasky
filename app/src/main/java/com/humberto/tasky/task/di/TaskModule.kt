package com.humberto.tasky.task.di

import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.task.data.TaskRepositoryImpl
import com.humberto.tasky.task.domain.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class TaskModule {

    @Provides
    fun providesTaskRepository(
        reminderDao: TaskDao
    ): TaskRepository {
        return TaskRepositoryImpl(reminderDao)
    }
}