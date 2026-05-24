package com.rohitneel.todomaster.presentation

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.NotificationUtil

class OverdueWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        NotificationUtil.createNotification(
            context = context,
            title = inputData.getString(AppConstants.TITLE_KEY) ?: "",
            contentText = "Task Overdue",
            notificationID = AppConstants.OVERDUE_NOTIFICATION_ID
        )
        return Result.success()
    }
}