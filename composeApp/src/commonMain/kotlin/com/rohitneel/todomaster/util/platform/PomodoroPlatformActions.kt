package com.rohitneel.todomaster.util.platform

import androidx.compose.runtime.staticCompositionLocalOf

interface PomodoroPlatformActions {
    fun onStart()
    fun onStop()
    fun onCancel()
    fun onSetDuration(durationInSeconds: Long)
    fun onVibrate()
}

val LocalPomodoroPlatformActions = staticCompositionLocalOf<PomodoroPlatformActions> {
    object : PomodoroPlatformActions {
        override fun onStart() {}
        override fun onStop() {}
        override fun onCancel() {}
        override fun onSetDuration(durationInSeconds: Long) {}
        override fun onVibrate() {}
    }
}
