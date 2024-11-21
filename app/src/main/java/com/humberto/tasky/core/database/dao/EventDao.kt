package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(reminderEntity: EventEntity)

    @Upsert
    suspend fun upsertEvents(reminderEntities: List<EventEntity>)

    @Query("""
        SELECT * FROM evententity 
        WHERE (`from` BETWEEN :startOfDay AND :endOfDay)
        OR (`to` BETWEEN :startOfDay AND :endOfDay)
    """)
    fun getEventsForDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM evententity WHERE id=:id")
    suspend fun getEvent(id: String): EventEntity?

    @Query("DELETE FROM evententity WHERE id=:id")
    suspend fun deleteEvent(id: String)

    @Query("DELETE FROM evententity")
    suspend fun deleteAllEvents()

    @Query("SELECT * FROM photoentity WHERE `key` IN (:keys)")
    suspend fun getPhotosByKeys(keys: List<String>): List<PhotoEntity>
}