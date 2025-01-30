package com.humberto.tasky.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.humberto.tasky.core.database.entity.DeletedTaskSyncEntity
import com.humberto.tasky.core.database.entity.TaskEntity
import com.humberto.tasky.core.database.entity.TaskPendingSyncEntity
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
    suspend fun insertTaskPendingSync(taskPendingSyncEntity: TaskPendingSyncEntity)

    @Query("SELECT * from taskpendingsyncentity WHERE userId = :userId")
    suspend fun getPendingTasksSync(userId: String): List<TaskPendingSyncEntity>

    @Query("DELETE FROM taskpendingsyncentity WHERE taskId = :taskId")
    suspend fun deleteTaskPendingSync(taskId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedTaskSync(deletedTaskSyncEntity: DeletedTaskSyncEntity)

    @Query("SELECT taskId from deletedtasksyncentity WHERE userId = :userId")
    suspend fun getDeletedTaskSync(userId: String): List<String>

    @Query("DELETE FROM deletedtasksyncentity WHERE taskId IN (:taskIds)")
    suspend fun deleteDeletedTasksSync(taskIds: List<String>)
}
