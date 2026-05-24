package com.rohitneel.todomaster.util

import android.content.Context
import androidx.work.*
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.OverdueWorker
import com.rohitneel.todomaster.presentation.ReminderWorker
import java.util.*
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun setOverdueWorkRequest(context: Context, task: TaskModel?, title: String, endDate: Calendar, tag: String) {
        val delay = endDate.timeInMillis - System.currentTimeMillis()
        if (delay > 0) {
            val data = workDataOf(
                AppConstants.TITLE_KEY to title,
                AppConstants.TASK_ID to (task?.id ?: 0)
            )
            val workRequest = OneTimeWorkRequestBuilder<OverdueWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(tag)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                tag + (task?.id ?: 0),
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun cancelOverdueWorkRequest(context: Context, tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

    fun setReminderWorkRequest(context: Context, title: String, calendar: Calendar, repeatInterval: String, snoozeDuration: Long, tag: String) {
        val delay = calendar.timeInMillis - System.currentTimeMillis()
        if (delay > 0) {
            val data = workDataOf(
                AppConstants.TITLE_KEY to title,
                AppConstants.SNOOZE_DURATION_KEY to snoozeDuration
            )
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(tag)
                .build()
            
            // Handle repeat logic if necessary, though basic reminder might just be one-time for now
            WorkManager.getInstance(context).enqueueUniqueWork(
                tag + title,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    fun cancelReminderWorkRequest(context: Context, tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }
}
