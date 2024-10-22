package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.TaskEntity

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Upsert
    suspend fun upsertTasks(taskEntities: List<TaskEntity>)

    @Query("SELECT * FROM taskentity WHERE id=:id")
    fun getTask(id: String): TaskEntity

    @Query("DELETE FROM taskentity WHERE id=:id")
    fun deleteTask(id: String)

    @Query("DELETE FROM taskentity")
    fun deleteAllTasks()
}
