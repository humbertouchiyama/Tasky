package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.DeletedEventSyncEntity
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.EventPendingSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Upsert
    suspend fun upsertEvent(eventEntity: EventEntity)

    @Upsert
    suspend fun upsertEvents(eventEntities: List<EventEntity>)

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventPendingSync(eventPendingSyncEntity: EventPendingSyncEntity)

    @Query("SELECT * from eventpendingsyncentity WHERE userId = :userId")
    suspend fun getPendingEventsSync(userId: String): List<EventPendingSyncEntity>

    @Query("DELETE FROM eventpendingsyncentity WHERE eventId = :eventId")
    suspend fun deleteEventPendingSync(eventId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedEventSync(deletedEventSyncEntity: DeletedEventSyncEntity)

    @Query("SELECT eventId from deletedeventsyncentity WHERE userId = :userId")
    suspend fun getDeletedEventSync(userId: String): List<String>

    @Query("DELETE FROM deletedeventsyncentity WHERE eventId IN (:eventIds)")
    suspend fun deleteDeletedEventsSync(eventIds: List<String>)
}