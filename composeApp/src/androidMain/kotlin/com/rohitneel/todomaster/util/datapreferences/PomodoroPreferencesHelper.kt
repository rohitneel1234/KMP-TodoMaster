package com.rohitneel.todomaster.util.datapreferences

import android.content.Context
import com.rohitneel.todomaster.util.AppConstants

actual class PomodoroPreferencesHelper(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(AppConstants.POMODORO_PREFERENCES, Context.MODE_PRIVATE)

    actual fun saveSelectedDuration(durationInSeconds: Long) {
        sharedPreferences.edit().putLong(AppConstants.DataStorePreference.SELECTED_DURATION, durationInSeconds).apply()
    }

    actual fun getSelectedDuration(): Long {
        return sharedPreferences.getLong(AppConstants.DataStorePreference.SELECTED_DURATION, 5 * 60L)
    }
}
