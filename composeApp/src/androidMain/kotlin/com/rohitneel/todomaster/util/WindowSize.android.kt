package com.rohitneel.todomaster.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
actual fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return WindowSize(
        width = getScreenWidth(configuration.screenWidthDp.dp),
        height = getScreenHeight(configuration.screenHeightDp.dp)
    )
}
