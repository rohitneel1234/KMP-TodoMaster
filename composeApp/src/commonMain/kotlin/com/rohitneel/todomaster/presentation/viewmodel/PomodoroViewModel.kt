package com.rohitneel.todomaster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel : ViewModel() {

    private var _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted

    private var totalDurationInSeconds: Long = 0L

    fun setTimerDuration(durationInSeconds: Long) {
        totalDurationInSeconds = durationInSeconds
        resetProgress()
    }

    fun updateProgressFromPlatform(remainingTimeInSeconds: Long) {
        if (totalDurationInSeconds > 0) {
            _progress.value = 100f - ((remainingTimeInSeconds.toFloat() / totalDurationInSeconds.toFloat()) * 100f)
        } else {
            _progress.value = 0f
        }
        _isCompleted.value = remainingTimeInSeconds <= 0L
    }

    fun resetProgress() {
        _progress.value = 0f
        _isCompleted.value = false
    }
}
