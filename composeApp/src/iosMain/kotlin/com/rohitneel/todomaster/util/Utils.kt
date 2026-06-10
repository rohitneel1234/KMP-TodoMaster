package com.rohitneel.todomaster.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleCancel
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleActionSheet
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIDatePicker
import platform.UIKit.UIDatePickerMode
import platform.UIKit.UIDatePickerStyle
import platform.UIKit.UIDevice
import kotlin.random.Random

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
        val window = platform.UIKit.UIApplication.sharedApplication.keyWindow
        val rootViewController = window?.rootViewController
        val activityViewController = platform.UIKit.UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        rootViewController?.presentViewController(activityViewController, true, null)
    }

    actual fun shareApp() {
        shareText("Check out TodoMaster app!")
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

    actual fun handleDateTimeClick(
        onEditTask: Boolean,
        isDueDate: Boolean,
        task: TaskModel?,
        snoozeDuration: Long,
        taskViewModel: TaskViewModel,
        snackBarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope,
        workerTag: String
    ) {
        val datePicker = platform.UIKit.UIDatePicker()
        datePicker.datePickerMode = platform.UIKit.UIDatePickerMode.UIDatePickerModeDateAndTime
        if (platform.UIKit.UIDevice.currentDevice.systemVersion.toDouble() >= 14.0) {
            datePicker.preferredDatePickerStyle = platform.UIKit.UIDatePickerStyle.UIDatePickerStyleWheels
        }

        val alert = platform.UIKit.UIAlertController.alertControllerWithTitle(
            title = if (isDueDate) "Set Due Date" else "Set Reminder",
            message = "\n\n\n\n\n\n\n\n\n\n", // Space for date picker
            preferredStyle = UIAlertControllerStyleActionSheet
        )

        alert.view.addSubview(datePicker)

        val okAction = platform.UIKit.UIAlertAction.actionWithTitle(
            title = "OK",
            style = UIAlertActionStyleDefault
        ) {
            val selectedDate = datePicker.date
            val timestamp = (selectedDate.timeIntervalSince1970 * 1000).toLong()
            if (isDueDate) {
                taskViewModel.dueDate = timestamp
            } else {
                taskViewModel.reminder = timestamp
            }
        }

        val cancelAction = platform.UIKit.UIAlertAction.actionWithTitle(
            title = "Cancel",
            style = UIAlertActionStyleCancel,
            handler = null
        )

        alert.addAction(okAction)
        alert.addAction(cancelAction)

        val window = platform.UIKit.UIApplication.sharedApplication.keyWindow
        window?.rootViewController?.presentViewController(alert, true, null)
    }

    @Composable
    actual fun voiceRecognizerLauncher(onResult: (String) -> Unit): () -> Unit {
        return {
            // iOS implementation for speech recognition usually requires SFSpeechRecognizer
            // For now, providing a placeholder that informs the user
            val alert = platform.UIKit.UIAlertController.alertControllerWithTitle(
                title = "Voice Input",
                message = "Voice recognition is not yet implemented for iOS.",
                preferredStyle = UIAlertControllerStyleAlert
            )
            alert.addAction(platform.UIKit.UIAlertAction.actionWithTitle("OK", UIAlertActionStyleDefault, null))
            platform.UIKit.UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(alert, true, null)
        }
    }
}
