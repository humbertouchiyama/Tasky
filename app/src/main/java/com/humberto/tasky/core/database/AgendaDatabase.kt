package com.humberto.tasky.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
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
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = AgendaDatabase.Migration1To2::class)
    ]
)
@TypeConverters(Converters::class)
abstract class AgendaDatabase: RoomDatabase() {

    abstract val taskDao: TaskDao
    abstract val eventDao: EventDao
    abstract val reminderDao: ReminderDao

    @DeleteColumn(tableName = "EventEntity", columnName = "isGoing")
    class Migration1To2 : AutoMigrationSpec
}