package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.humberto.tasky.core.database.entity.EventEntity
import com.humberto.tasky.core.database.entity.ReminderEntity
import com.humberto.tasky.core.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AgendaDao {
    @Query("SELECT * FROM taskentity WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM evententity 
        WHERE (`from` BETWEEN :startOfDay AND :endOfDay)
        OR (`to` BETWEEN :startOfDay AND :endOfDay)
    """)
    fun getEventsForDay(startOfDay: Long, endOfDay: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM reminderentity WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getRemindersForDay(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>
}