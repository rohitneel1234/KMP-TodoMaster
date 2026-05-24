package com.rohitneel.todomaster.util

import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import kotlin.random.Random
import kotlinx.datetime.Clock

actual object Utils {
    actual fun generateRandomColor(): Color {
        val random = Random(Clock.System.now().toEpochMilliseconds())
        return Color(
            red = random.nextInt(256),
            green = random.nextInt(256),
            blue = random.nextInt(256),
            alpha = 255
        )
    }

    actual fun shareText(text: String) {
        // iOS implementation needed
    }

    actual fun shareApp() {
        // iOS implementation needed
    }

    actual fun formatText(text: String, isUpperCase: Boolean, toggleUpperCase: Boolean): String {
        return when {
            isUpperCase -> text.uppercase()
            toggleUpperCase -> text.lowercase()
            else -> text
        }
    }

    actual fun getVisibleCategories(tasks: List<TaskModel>): Map<String, Int> {
        return tasks
            .filter { !it.isCompleted }
            .groupBy { it.category }
            .mapValues { (_, tasks) -> tasks.size }
    }

    actual fun determineChartEvent(tasks: List<TaskModel>): ChartEvent {
        val aggregatedData = tasks.filter { !it.isCompleted }
            .groupBy { it.category }
            .mapValues { (_, tasks) -> tasks.size.toLong() }
        return when {
            tasks.isEmpty() -> ChartEvent.Empty
            aggregatedData.isEmpty() -> ChartEvent.Completed
            else -> ChartEvent.ShowingData(aggregatedData)
        }
    }
}
