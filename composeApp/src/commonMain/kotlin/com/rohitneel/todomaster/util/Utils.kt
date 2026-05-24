package com.rohitneel.todomaster.util

import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import kotlin.random.Random
import kotlinx.datetime.Clock

expect object Utils {
    fun generateRandomColor(): Color
    fun shareText(text: String)
    fun shareApp()
    fun formatText(text: String, isUpperCase: Boolean, toggleUpperCase: Boolean): String
    fun getVisibleCategories(tasks: List<TaskModel>): Map<String, Int>
    fun determineChartEvent(tasks: List<TaskModel>): ChartEvent
}
