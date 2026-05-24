package com.rohitneel.todomaster.presentation

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohitneel.todomaster.domain.usecase.TaskUseCases
import com.rohitneel.todomaster.util.AppConstants

class TrashCleanupWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val taskUseCase: TaskUseCases
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val taskId = inputData.getInt(AppConstants.TASK_ID, -1)
        val expiryDate = inputData.getLong(AppConstants.EXPIRY_DATE, -1L)
        if (taskId != -1 && expiryDate != -1L) {
            taskUseCase.deleteExpiredTask(taskId, expiryDate)
            Log.d("TrashCleanupWorker", "Task with ID $taskId and expiry date $expiryDate deleted")
        }
        return Result.success()
    }
}
