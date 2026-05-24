package com.rohitneel.todomaster.data.repository

import com.rohitneel.todomaster.data.dao.TaskDao
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.SortOrder
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(private val taskDao: TaskDao) : TaskRepository {
    override suspend fun insert(task: TaskModel): Long {
        return taskDao.insert(task)
    }

    override fun getAllTasks(
        searchQuery: String,
        sortOrder: SortOrder
    ): Flow<List<TaskModel>> {
       return taskDao.getAllTasks(searchQuery, sortOrder)
    }

    override fun getTaskByName(searchQuery: String): Flow<List<TaskModel>> {
        return taskDao.getTaskByName(searchQuery)
    }

    override fun getTaskByCategory(category: String): Flow<List<TaskModel>> {
        return taskDao.getTaskByCategory(category)
    }

    override fun getTasksInTrash(expiryDate: Long): Flow<List<TaskModel>> {
        return taskDao.getTasksInTrash(expiryDate)
    }

    override suspend fun getTaskById(taskId: Int): TaskModel {
       return taskDao.getTaskById(taskId)
    }

    override fun getTasksByReminder(): Flow<List<TaskModel>> {
        return taskDao.getTasksByReminder()
    }

    override fun getFavoriteTasks(): Flow<List<TaskModel>> {
        return taskDao.getFavoriteTasks()
    }

    override suspend fun update(task: TaskModel) {
       taskDao.update(task)
    }

    override suspend fun deleteAllTasks() {
       taskDao.deleteAllTasks()
    }

    override suspend fun delete(task: TaskModel) {
        taskDao.delete(task)
    }

    override suspend fun deleteExpiredTasks(taskId: Int, expiryDate: Long) {
        taskDao.deleteExpiredTasks(taskId, expiryDate)
    }

    override suspend fun deleteAllTasksInTrash() {
        taskDao.deleteAllTasksInTrash()
    }
}
