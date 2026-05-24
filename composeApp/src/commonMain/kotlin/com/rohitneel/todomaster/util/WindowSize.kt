package com.rohitneel.todomaster.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class WindowSize(
    val width: WindowType,
    val height: WindowType
)

enum class WindowType {
    Compact, Medium, Expanded
}

@Composable
expect fun rememberWindowSize(): WindowSize

fun getScreenWidth(width: Dp): WindowType = when {
    width < 600.dp -> WindowType.Compact
    width < 840.dp -> WindowType.Medium
    else -> WindowType.Expanded
}

fun getScreenHeight(height: Dp): WindowType = when {
    height < 480.dp -> WindowType.Compact
    height < 900.dp -> WindowType.Medium
    else -> WindowType.Expanded
}
