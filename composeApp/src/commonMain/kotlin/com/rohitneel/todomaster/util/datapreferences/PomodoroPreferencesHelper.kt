package com.rohitneel.todomaster.util.datapreferences

expect class PomodoroPreferencesHelper {
    fun saveSelectedDuration(durationInSeconds: Long)
    fun getSelectedDuration(): Long
}
