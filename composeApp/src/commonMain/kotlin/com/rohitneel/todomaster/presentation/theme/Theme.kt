package com.rohitneel.todomaster.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkThemeColors.DarkPrimary,
    onPrimary = DarkThemeColors.DarkOnPrimary,
    primaryContainer = DarkThemeColors.DarkBackground,
    secondary = DarkThemeColors.DarkSecondary,
    tertiary = DarkThemeColors.DarkTertiary,
    surfaceDim = DarkThemeColors.DarkGray,
    surfaceBright = DarkThemeColors.DarkPlatinumGray
)

private val LightColorScheme = lightColorScheme(
    primary = LightThemeColors.LightPrimary,
    onPrimary = LightThemeColors.LightOnPrimary,
    primaryContainer = LightThemeColors.LightBackground,
    secondary = LightThemeColors.LightSecondary,
    tertiary = LightThemeColors.LightTertiary,
    surfaceDim = LightThemeColors.LightGray,
    surfaceBright = LightThemeColors.LightPlatinumGray,
    secondaryContainer = LightThemeColors.LightIndicator
)

@Composable
fun TodoMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
