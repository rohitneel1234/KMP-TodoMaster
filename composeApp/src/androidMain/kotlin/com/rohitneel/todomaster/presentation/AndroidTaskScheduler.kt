package com.rohitneel.todomaster.presentation

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.rohitneel.todomaster.util.AppConstants
import java.util.concurrent.TimeUnit

class AndroidTaskScheduler(private val context: Context) : TaskScheduler {

    override fun scheduleTaskExpirationCleanup(taskId: Int, expiryDate: Long) {
        val workRequest = OneTimeWorkRequestBuilder<TrashCleanupWorker>()
            .setInitialDelay(expiryDate - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(AppConstants.TASK_ID to taskId, AppConstants.EXPIRY_DATE to expiryDate))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork("TaskExpirationCleanup_$taskId", ExistingWorkPolicy.REPLACE, workRequest)
    }

    override fun cancelTaskExpirationCleanup(taskId: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("TaskExpirationCleanup_$taskId")
    }

    override fun cancelReminderWorkRequest(tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

    override fun cancelOverdueWorkRequest(tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }
}
