package com.rohitneel.todomaster.util.datapreferences

actual class PomodoroPreferencesHelper {
    actual fun saveSelectedDuration(durationInSeconds: Long) {
        // iOS implementation placeholder
    }

    actual fun getSelectedDuration(): Long {
        return 300L // Default 5 mins
    }
}
