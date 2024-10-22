package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.ReminderEntity

@Dao
interface ReminderDao {

    @Upsert
    suspend fun upsertReminder(reminderEntity: ReminderEntity)

    @Upsert
    suspend fun upsertReminders(reminderEntities: List<ReminderEntity>)

    @Query("SELECT * FROM reminderentity WHERE id=:id")
    fun getReminder(id: String): ReminderEntity

    @Query("DELETE FROM reminderentity WHERE id=:id")
    fun deleteReminder(id: String)

    @Query("DELETE FROM reminderentity")
    fun deleteAllReminders()
}