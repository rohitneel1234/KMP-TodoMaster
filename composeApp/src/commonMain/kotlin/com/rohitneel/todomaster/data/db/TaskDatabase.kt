package com.rohitneel.todomaster.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.rohitneel.todomaster.data.dao.CheckListDao
import com.rohitneel.todomaster.data.dao.TaskDao
import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.model.TaskModel
import kotlinx.coroutines.IO

@Database(entities = [TaskModel::class, CheckListModel::class], version = 5, exportSchema = false)
@TypeConverters(FontStyleModelConverter::class, GradientColorConverter::class)
@ConstructedBy(TaskDatabaseConstructor::class)
abstract class TaskDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun checkListDao(): CheckListDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TaskDatabaseConstructor : RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<TaskDatabase>
): TaskDatabase {
    return builder
        .setQueryCoroutineContext(kotlinx.coroutines.Dispatchers.IO)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .fallbackToDestructiveMigration(true)
        .build()
}
