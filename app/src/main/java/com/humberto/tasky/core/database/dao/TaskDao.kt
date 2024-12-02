package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.ModifiedTaskEntity
import com.humberto.tasky.core.database.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Upsert
    suspend fun upsertTasks(taskEntities: List<TaskEntity>)

    @Query("SELECT * FROM taskentity WHERE time BETWEEN :startOfDay AND :endOfDay")
    fun getTasksForDay(startOfDay: Long, endOfDay: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM taskentity WHERE id=:id")
    suspend fun getTask(id: String): TaskEntity?

    @Query("DELETE FROM taskentity WHERE id=:id")
    suspend fun deleteTask(id: String)

    @Query("DELETE FROM taskentity")
    suspend fun deleteAllTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiedTask(modifiedTaskEntity: ModifiedTaskEntity)

    @Query("SELECT * from modifiedtaskentity ORDER BY timestamp ASC")
    fun getAllModifiedTasks(): Flow<List<ModifiedTaskEntity>>

    @Query("DELETE FROM modifiedtaskentity WHERE taskId=:taskId")
    suspend fun deleteModifiedTask(taskId: String)
}
