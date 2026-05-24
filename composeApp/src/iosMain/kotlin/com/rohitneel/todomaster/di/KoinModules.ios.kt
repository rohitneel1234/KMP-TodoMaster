package com.rohitneel.todomaster.di

import org.koin.core.module.Module
import org.koin.dsl.module
import com.rohitneel.todomaster.presentation.TaskScheduler

actual fun platformModule(): Module = module {
    // Platform specific implementations for iOS
    single<TaskScheduler> { 
        object : TaskScheduler {
            override fun scheduleTaskExpirationCleanup(taskId: Int, expiryDate: Long) {}
            override fun cancelTaskExpirationCleanup(taskId: Int) {}
            override fun cancelReminderWorkRequest(tag: String) {}
            override fun cancelOverdueWorkRequest(tag: String) {}
        }
    }
}
