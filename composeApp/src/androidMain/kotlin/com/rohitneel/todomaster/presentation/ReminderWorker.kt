package com.rohitneel.todomaster.presentation

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rohitneel.todomaster.presentation.service.AlarmService
import com.rohitneel.todomaster.util.AppConstants

class ReminderWorker(
    context: Context,
    workerParameter: WorkerParameters
) : Worker(context, workerParameter) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, AlarmService::class.java).apply {
            putExtra(AppConstants.TITLE_KEY, inputData.getString(AppConstants.TITLE_KEY) ?: "")
            putExtra(AppConstants.SNOOZE_DURATION_KEY, inputData.getLong(AppConstants.SNOOZE_DURATION_KEY, 300_000L))
        }
        ContextCompat.startForegroundService(applicationContext, intent)
        return Result.success()
    }
}