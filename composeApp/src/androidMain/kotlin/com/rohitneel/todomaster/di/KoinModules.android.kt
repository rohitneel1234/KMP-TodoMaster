package com.rohitneel.todomaster.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rohitneel.todomaster.data.db.TaskDatabase
import com.rohitneel.todomaster.data.db.getRoomDatabase
import com.rohitneel.todomaster.presentation.AndroidTaskScheduler
import com.rohitneel.todomaster.presentation.TaskScheduler
import com.rohitneel.todomaster.util.datapreferences.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import androidx.room.Room
import com.rohitneel.todomaster.presentation.TrashCleanupWorker
import com.rohitneel.todomaster.presentation.OverdueWorker
import com.rohitneel.todomaster.presentation.ReminderWorker
import com.rohitneel.todomaster.util.datapreferences.PomodoroPreferencesHelper
import org.koin.androidx.workmanager.dsl.worker

actual fun platformModule(): Module = module {
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }
    single<TaskDatabase> {
        val builder = Room.databaseBuilder(
            androidContext(),
            TaskDatabase::class.java,
            "task_database"
        )
        getRoomDatabase(builder)
    }
    single { get<TaskDatabase>().taskDao() }
    single { get<TaskDatabase>().checkListDao() }
    single<TaskScheduler> { AndroidTaskScheduler(androidContext()) }
    single { PomodoroPreferencesHelper(androidContext()) }
    worker { TrashCleanupWorker(get(), get(), get()) }
    worker { OverdueWorker(get(), get()) }
    worker { ReminderWorker(get(), get()) }
}
