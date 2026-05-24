package com.rohitneel.todomaster.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskModel): Long

    fun getAllTasks(
        query: String,
        sortOrder: SortOrder,
    ): Flow<List<TaskModel>>  =
        when (sortOrder) {
            SortOrder.BY_NAME -> getAllTaskByName(query)
            SortOrder.BY_NAME_DESC -> getAllTaskByNameDesc(query)
            SortOrder.BY_DATE -> getAllTaskByDate(query)
            SortOrder.BY_DATE_DESC -> getAllTaskByDateDesc(query)
        }

    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY pinned DESC,title ASC")
    fun getAllTaskByName(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY pinned DESC,title DESC")
    fun getAllTaskByNameDesc(searchQuery: String): Flow<List<TaskModel>>

    //date DESC
    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY pinned DESC, creation_date ASC")
    fun getAllTaskByDate(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' ORDER BY pinned DESC, creation_date DESC")
    fun getAllTaskByDateDesc(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE title LIKE '%' || :searchQuery || '%' ")
    fun getTaskByName(searchQuery: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE category LIKE '%' || :category || '%' ")
    fun getTaskByCategory(category: String): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskModel

    @Query("SELECT * FROM task_table WHERE reminder IS NOT NULL ORDER BY pinned DESC, title ASC")
    fun getTasksByReminder(): Flow<List<TaskModel>>

    @Query("SELECT * FROM task_table WHERE isFavorite = 1 ORDER BY pinned DESC, title ASC")
    fun getFavoriteTasks(): Flow<List<TaskModel>>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(task: TaskModel)

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Delete
    suspend fun delete(task: TaskModel)

    @Query("SELECT * FROM task_table WHERE deletedAt IS NOT NULL AND deletedAt > :expiryDate")
    fun getTasksInTrash(expiryDate: Long): Flow<List<TaskModel>>

    @Query("DELETE FROM task_table WHERE id = :taskId AND deletedAt IS NOT NULL AND deletedAt < :expiryDate")
    suspend fun deleteExpiredTasks(taskId: Int, expiryDate: Long)

    @Query("DELETE FROM task_table WHERE deletedAt IS NOT NULL")
    suspend fun deleteAllTasksInTrash()
}
