package com.rohitneel.todomaster.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope

expect object Utils {
    fun generateRandomColor(): Color
    fun shareText(text: String)
    fun shareApp()
    fun formatText(text: String, isUpperCase: Boolean, toggleUpperCase: Boolean): String
    fun getVisibleCategories(tasks: List<TaskModel>): Map<String, Int>
    fun determineChartEvent(tasks: List<TaskModel>): ChartEvent

    fun handleDateTimeClick(
        onEditTask: Boolean,
        isDueDate: Boolean,
        task: TaskModel?,
        snoozeDuration: Long,
        taskViewModel: TaskViewModel,
        snackBarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope,
        workerTag: String
    )

    @Composable
    fun voiceRecognizerLauncher(onResult: (String) -> Unit): () -> Unit
}
