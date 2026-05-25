package com.rohitneel.todomaster.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rohitneel.todomaster.data.db.TaskDatabase
import com.rohitneel.todomaster.data.db.TaskDatabaseConstructor
import com.rohitneel.todomaster.data.db.getRoomDatabase
import com.rohitneel.todomaster.presentation.TaskScheduler
import com.rohitneel.todomaster.util.datapreferences.PomodoroPreferencesHelper
import com.rohitneel.todomaster.util.datapreferences.createDataStore
import com.rohitneel.todomaster.util.platform.PomodoroPlatformActions
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.AudioToolbox.kSystemSoundID_Vibrate
import platform.Foundation.NSHomeDirectory

actual fun platformModule(): Module = module {
    single { createDataStore() }
    
    single<TaskDatabase> {
        val dbFile = NSHomeDirectory() + "/task_database.db"
        val builder = Room.databaseBuilder<TaskDatabase>(
            name = dbFile,
            factory = { TaskDatabaseConstructor.initialize() }
        )
        builder.setDriver(BundledSQLiteDriver())
        getRoomDatabase(builder)
    }
    
    single { get<TaskDatabase>().taskDao() }
    single { get<TaskDatabase>().checkListDao() }

    single<PomodoroPlatformActions> {
        object : PomodoroPlatformActions {
            override fun onStart() {}
            override fun onStop() {}
            override fun onCancel() {}
            override fun onSetDuration(durationInSeconds: Long) {}
            override fun onVibrate() {
                AudioServicesPlaySystemSound(kSystemSoundID_Vibrate)
            }
        }
    }
    
    single<TaskScheduler> { 
        object : TaskScheduler {
            override fun scheduleTaskExpirationCleanup(taskId: Int, expiryDate: Long) {}
            override fun cancelTaskExpirationCleanup(taskId: Int) {}
            override fun cancelReminderWorkRequest(tag: String) {}
            override fun cancelOverdueWorkRequest(tag: String) {}
        }
    }
    
    single { PomodoroPreferencesHelper() }
}
