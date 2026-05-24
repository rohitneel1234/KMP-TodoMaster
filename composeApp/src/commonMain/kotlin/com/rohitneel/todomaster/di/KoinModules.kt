package com.rohitneel.todomaster.di

import com.rohitneel.todomaster.data.repository.CheckListRepository
import com.rohitneel.todomaster.data.repository.CheckListRepositoryImpl
import com.rohitneel.todomaster.data.repository.TaskRepository
import com.rohitneel.todomaster.data.repository.TaskRepositoryImpl
import com.rohitneel.todomaster.domain.usecase.*
import com.rohitneel.todomaster.util.datapreferences.DataStorePreferenceManager
import com.rohitneel.todomaster.presentation.viewmodel.PomodoroViewModel
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun platformModule(): Module

val dataModule = module {
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<CheckListRepository> { CheckListRepositoryImpl(get()) }
    single { DataStorePreferenceManager(get()) }
}

val viewModelModule = module {
    viewModel { TaskViewModel(get(), get(), get(), get()) }
    viewModel { SettingViewModel(get()) }
    viewModel { PomodoroViewModel() }
}

val useCaseModule = module {
    single { InsertTask(get()) }
    single { GetTask(get()) }
    single { GetTaskByName(get()) }
    single { GetTaskByCategory(get()) }
    single { GetTaskByID(get()) }
    single { GetTaskByReminder(get()) }
    single { GetFavoriteTasks(get()) }
    single { GetTaskInTrash(get()) }
    single { UpdateTask(get()) }
    single { DeleteTask(get()) }
    single { DeleteExpiredTask(get()) }
    single { DeleteAllTaskInTrash(get()) }
    single { DeleteAllTasks(get()) }

    single {
        TaskUseCases(
            insertTask = get(),
            getTask = get(),
            getTaskByName = get(),
            getTaskByCategory = get(),
            getTaskByID = get(),
            getTaskByReminder = get(),
            getFavoriteTasks = get(),
            getTaskInTrash = get(),
            updateTask = get(),
            deleteTask = get(),
            deleteExpiredTask = get(),
            deleteAllTaskInTrash = get(),
            deleteAllTasks = get()
        )
    }

    single { InsertCheckList(get()) }
    single { UpdateCheckList(get()) }
    single { DeleteCheckList(get()) }
    single { GetCheckList(get()) }

    single {
        CheckListUseCases(
            insertCheckList = get(),
            updateCheckList = get(),
            deleteCheckList = get(),
            getCheckList = get()
        )
    }
}
