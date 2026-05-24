package com.rohitneel.todomaster.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberWindowSize(): WindowSize {
    // Basic placeholder for iOS
    return WindowSize(
        width = WindowType.Compact,
        height = WindowType.Compact
    )
}
