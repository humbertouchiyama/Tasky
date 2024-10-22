package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.AttendeeEntity
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.PhotoEntity

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(reminderEntity: EventEntity)

    @Upsert
    suspend fun upsertEvents(reminderEntities: List<EventEntity>)

    @Query("SELECT * FROM evententity WHERE id=:id")
    fun getEvent(id: String): EventEntity

    @Query("DELETE FROM evententity WHERE id=:id")
    fun deleteEvent(id: String)

    @Query("DELETE FROM evententity")
    fun deleteAllEvents()

    @Query("SELECT * FROM photoentity WHERE `key` IN (:keys)")
    fun getPhotosByKeys(keys: List<String>): List<PhotoEntity>

    @Query("SELECT * FROM attendeeentity WHERE `userId` IN (:ids)")
    fun getAttendeesByIds(ids: List<String>): List<AttendeeEntity>
}