package com.rohitneel.todomaster.presentation.theme

import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.util.AppConstants.ALL
import com.rohitneel.todomaster.util.AppConstants.HOME
import com.rohitneel.todomaster.util.AppConstants.OTHER
import com.rohitneel.todomaster.util.AppConstants.PERSONAL
import com.rohitneel.todomaster.util.AppConstants.SCHOOL
import com.rohitneel.todomaster.util.AppConstants.WORK

object AppColors {
    val VibrantBlue = Color(0xFF3797EF)
    val AmberYellow = Color(0xffffc107)
    val Purple40 = Color(0xFF6650a4)
    val PurpleGrey40 = Color(0xFF625b71)
    val Pink40 = Color(0xFF7D5260)
    val Purple80 = Color(0xFF9951FF)
    val PurpleGrey80 = Color(0xFFCCC2DC)
    val Pink80 = Color(0xFFEFB8C8)
    val White = Color(0xFFFFFFFF)
    val RedOrange = Color(0xffff6347)
    val RedPink = Color(0xfff48fb1)
    val DarkKhaki = Color(0xffbdb76b)
    val Violet = Color(0xffcf94da)
    val Chocolate = Color(0xffd2691e)
    val LightPink = Color(0xFFea86c2)
    val LightOrange = Color(0xFFfa8072)
    val Green = Color(0xFF65ba5a)
    val LightBlue = Color(0xFFD7E8DE)
    val PrimaryColor = Color(0xFF5A78EE)
    val SecondaryColor = Color(0xFF3CC5F5)
    val PrimaryTextColor = Color(0xFF060719)
    val SubTextColor = Color(0xFF858585)
    val Blue = Color(0xFF83BCFF)
    val LightGray = Color(0xFFD3D3D3)
}

object LightThemeColors {
    val LightPrimary = Color(0xFF3797EF)
    val LightOnPrimary = Color(0xFFFFFFFF)
    val LightSecondary = Color(0xFF575E71)
    val LightTertiary = Color(0xFF367EF2)
    val LightGray = Color (0xFFF8F9Fb)
    val LightPlatinumGray = Color (0xFFE1E4E8)
    val LightBackground = Color(0xFFFFFFFF)
    val LightIndicator = Color(0xFFEBF5FF)
}

object DarkThemeColors {
    val DarkPrimary = Color(0xFFAFC6FF)
    val DarkOnPrimary = Color(0xFF333333)
    val DarkSecondary = Color(0xFFBFC6DC)
    val DarkTertiary = Color(0xFFB1C5FF)
    val DarkGray = Color (0xFF4B4B4B)
    val DarkPlatinumGray = Color (0xFF1A1C1F)
    val DarkBackground = Color(0xFF1C1B1F)
}

val categoryColorMapping = mapOf(
    ALL to Color(0xFFFF00FF),
    HOME to Color(0xFFDC143C),
    PERSONAL to Color(0xFF2ED573),
    WORK to Color(0xFFFFA502),
    SCHOOL to Color(0xFF1E90FF),
    OTHER to Color(0xFF808000)
)

val gradients = listOf(
    listOf(Color(0xFFFF66FF), Color(0xFF66B3FF)),
    listOf(Color(0xFFD25F3A), Color(0xFFEAC54F)),
    listOf(Color(0xFFEF5350), Color(0xFF9C27B0)),
    listOf(Color(0xFFC49B2C), Color(0xFF8C8B30)),
    listOf(Color(0xFFDA4453), Color(0xFF4CAF50)),
    listOf(Color(0xFFBDB76B), Color(0xFF48B1BF)),
)
