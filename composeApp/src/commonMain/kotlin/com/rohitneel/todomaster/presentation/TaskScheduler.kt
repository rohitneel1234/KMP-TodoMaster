package com.rohitneel.todomaster.presentation

import com.rohitneel.todomaster.data.model.TaskModel

interface TaskScheduler {
    fun scheduleTaskExpirationCleanup(taskId: Int, expiryDate: Long)
    fun cancelTaskExpirationCleanup(taskId: Int)
    fun cancelReminderWorkRequest(tag: String)
    fun cancelOverdueWorkRequest(tag: String)
}
