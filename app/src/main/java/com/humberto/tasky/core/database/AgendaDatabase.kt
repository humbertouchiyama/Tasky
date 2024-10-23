package com.humberto.tasky.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.entity.AttendeeEntity
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.PhotoEntity
import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.database.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        EventEntity::class,
        ReminderEntity::class,
        AttendeeEntity::class,
        PhotoEntity::class
               ],
    version = 1)
@TypeConverters(Converters::class)
abstract class AgendaDatabase: RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val eventDao: EventDao
    abstract val reminderDao: ReminderDao
}