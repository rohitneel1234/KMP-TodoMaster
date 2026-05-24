package com.rohitneel.todomaster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel : ViewModel() {

    private var _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted

    private var _hours = MutableStateFlow(0)
    val hours: StateFlow<Int> = _hours

    private var _minutes = MutableStateFlow(0)
    val minutes: StateFlow<Int> = _minutes

    private var _seconds = MutableStateFlow(0)
    val seconds: StateFlow<Int> = _seconds

    private var _timerState = MutableStateFlow("IDLE")
    val timerState: StateFlow<String> = _timerState

    private var totalDurationInSeconds: Long = 0L

    fun setTimerDuration(durationInSeconds: Long) {
        totalDurationInSeconds = durationInSeconds
        _hours.value = (durationInSeconds / 3600).toInt()
        _minutes.value = ((durationInSeconds % 3600) / 60).toInt()
        _seconds.value = (durationInSeconds % 60).toInt()
        resetProgress()
    }

    fun updateTimerState(state: String) {
        _timerState.value = state
    }

    fun updateProgressFromPlatform(remainingTimeInSeconds: Long) {
        if (totalDurationInSeconds > 0) {
            _progress.value = 100f - ((remainingTimeInSeconds.toFloat() / totalDurationInSeconds.toFloat()) * 100f)
        } else {
            _progress.value = 0f
        }
        _hours.value = (remainingTimeInSeconds / 3600).toInt()
        _minutes.value = ((remainingTimeInSeconds % 3600) / 60).toInt()
        _seconds.value = (remainingTimeInSeconds % 60).toInt()
        _isCompleted.value = remainingTimeInSeconds <= 0L
    }

    fun resetProgress() {
        _progress.value = 0f
        _isCompleted.value = false
    }
}
