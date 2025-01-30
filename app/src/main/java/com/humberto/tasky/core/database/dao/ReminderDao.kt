package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.DeletedReminderSyncEntity
import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.database.entity.ReminderPendingSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Upsert
    suspend fun upsertReminder(reminderEntity: ReminderEntity)

    @Upsert
    suspend fun upsertReminders(reminderEntities: List<ReminderEntity>)

    @Query("SELECT * FROM reminderentity WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getRemindersForDay(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminderentity WHERE id=:id")
    suspend fun getReminder(id: String): ReminderEntity?

    @Query("DELETE FROM reminderentity WHERE id=:id")
    suspend fun deleteReminder(id: String)

    @Query("DELETE FROM reminderentity")
    suspend fun deleteAllReminders()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderPendingSync(reminderPendingSyncEntity: ReminderPendingSyncEntity)

    @Query("SELECT * from reminderpendingsyncentity WHERE userId = :userId")
    suspend fun getPendingRemindersSync(userId: String): List<ReminderPendingSyncEntity>

    @Query("DELETE FROM reminderpendingsyncentity WHERE reminderId = :reminderId")
    suspend fun deleteReminderPendingSync(reminderId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedReminderSync(deletedReminderSyncEntity: DeletedReminderSyncEntity)

    @Query("SELECT reminderId from deletedremindersyncentity WHERE userId = :userId")
    suspend fun getDeletedReminderSync(userId: String): List<String>

    @Query("DELETE FROM deletedremindersyncentity WHERE reminderId IN (:reminderIds)")
    suspend fun deleteDeletedRemindersSync(reminderIds: List<String>)
}