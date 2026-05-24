package com.rohitneel.todomaster.data.repository

import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insert(task: TaskModel): Long

    fun getAllTasks(
        searchQuery: String,
        sortOrder: SortOrder
    ): Flow<List<TaskModel>>

    fun getTaskByName(searchQuery: String): Flow<List<TaskModel>>

    fun getTaskByCategory(category: String): Flow<List<TaskModel>>

    fun getTasksInTrash(expiryDate: Long): Flow<List<TaskModel>>

    suspend fun getTaskById(taskId: Int): TaskModel

    fun getTasksByReminder(): Flow<List<TaskModel>>

    fun getFavoriteTasks(): Flow<List<TaskModel>>

    suspend fun update(task: TaskModel)

    suspend fun deleteAllTasks()

    suspend fun delete(task: TaskModel)

    suspend fun deleteExpiredTasks(taskId: Int, expiryDate: Long)

    suspend fun deleteAllTasksInTrash()
}
