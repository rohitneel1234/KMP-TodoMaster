package com.rohitneel.todomaster.domain.usecase

import com.rohitneel.todomaster.data.model.InvalidTaskException
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.data.repository.TaskRepository
import com.rohitneel.todomaster.domain.model.SortOrder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class InsertTask(private val repository: TaskRepository) {
    @Throws(InvalidTaskException::class, CancellationException::class)
    suspend operator fun invoke(task: TaskModel): Long {
        if (task.title.isEmpty()) {
            throw InvalidTaskException("The title can't be empty.")
        }
        return repository.insert(task)
    }
}

class GetTask(private val repository: TaskRepository) {
    operator fun invoke(query: String, sortOrder: SortOrder): Flow<List<TaskModel>> {
        return repository.getAllTasks(query, sortOrder)
    }
}

class GetTaskByName(private val repository: TaskRepository) {
    operator fun invoke(query: String): Flow<List<TaskModel>> {
        return repository.getTaskByName(query)
    }
}

class GetTaskByCategory(private val repository: TaskRepository) {
    operator fun invoke(category: String): Flow<List<TaskModel>> {
        return repository.getTaskByCategory(category)
    }
}

class GetTaskByID(private val repository: TaskRepository) {
    suspend operator fun invoke(id: Int): TaskModel {
        return repository.getTaskById(id)
    }
}

class GetTaskByReminder(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<TaskModel>> {
        return repository.getTasksByReminder()
    }
}

class GetFavoriteTasks(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<TaskModel>> {
        return repository.getFavoriteTasks()
    }
}

class GetTaskInTrash(private val repository: TaskRepository) {
    operator fun invoke(expiryDate: Long): Flow<List<TaskModel>> {
        return repository.getTasksInTrash(expiryDate)
    }
}

class UpdateTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskModel) {
        repository.update(task)
    }
}

class DeleteTask(private val repository: TaskRepository) {
    suspend operator fun invoke(task: TaskModel) {
        repository.delete(task)
    }
}

class DeleteExpiredTask(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: Int, expiryDate: Long) {
        repository.deleteExpiredTasks(taskId, expiryDate)
    }
}

class DeleteAllTaskInTrash(private val repository: TaskRepository) {
    suspend operator fun invoke() {
        repository.deleteAllTasksInTrash()
    }
}

class DeleteAllTasks(private val repository: TaskRepository) {
    suspend operator fun invoke() {
        repository.deleteAllTasks()
    }
}

data class TaskUseCases(
    val insertTask: InsertTask,
    val getTask: GetTask,
    val getTaskByName: GetTaskByName,
    val getTaskByCategory: GetTaskByCategory,
    val getTaskByID: GetTaskByID,
    val getTaskByReminder: GetTaskByReminder,
    val getFavoriteTasks: GetFavoriteTasks,
    val getTaskInTrash: GetTaskInTrash,
    val updateTask: UpdateTask,
    val deleteTask: DeleteTask,
    val deleteExpiredTask: DeleteExpiredTask,
    val deleteAllTaskInTrash: DeleteAllTaskInTrash,
    val deleteAllTasks: DeleteAllTasks
)
