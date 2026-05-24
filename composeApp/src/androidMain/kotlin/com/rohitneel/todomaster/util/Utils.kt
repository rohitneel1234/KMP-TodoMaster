package com.rohitneel.todomaster.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.BuildConfig
import com.rohitneel.todomaster.R
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import kotlinx.datetime.Clock
import kotlin.random.Random

// This is a temporary way to get context in androidMain for Utils
// In a real app, you should inject a platform-specific service
lateinit var androidContext: Context

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
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share text")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        androidContext.startActivity(chooserIntent)
    }

    actual fun shareApp() {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share App")
            val shareMsg = "Check out this app: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            intent.putExtra(Intent.EXTRA_TEXT, shareMsg)
            val chooserIntent = Intent.createChooser(intent, "Share by")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            androidContext.startActivity(chooserIntent)
        } catch (e: Exception) {
            Toast.makeText(androidContext, "something went wrong!", Toast.LENGTH_SHORT).show()
        }
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
