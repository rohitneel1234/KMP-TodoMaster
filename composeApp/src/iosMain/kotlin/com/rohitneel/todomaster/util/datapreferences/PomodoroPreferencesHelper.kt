package com.rohitneel.todomaster.util.datapreferences

import platform.Foundation.NSUserDefaults

actual class PomodoroPreferencesHelper {
    private val defaults = NSUserDefaults.standardUserDefaults
    private val key = "pomodoro_duration"

    actual fun saveSelectedDuration(durationInSeconds: Long) {
        defaults.setInteger(durationInSeconds, key)
    }

    actual fun getSelectedDuration(): Long {
        val duration = defaults.integerForKey(key)
        return if (duration == 0L) 300L else duration
    }
}
