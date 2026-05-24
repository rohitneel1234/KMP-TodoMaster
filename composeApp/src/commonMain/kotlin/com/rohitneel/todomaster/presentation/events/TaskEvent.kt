package com.rohitneel.todomaster.presentation.events

import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.domain.model.FontStyleModel

sealed class TaskEvent {
    data class ChangeColor(val color: Int): TaskEvent()
    data class ChangeGradient(val gradientColor: List<Color>): TaskEvent()
    data class ChangeFontStyleModel(val fontStyleModel: FontStyleModel) : TaskEvent()
    data object SaveTask: TaskEvent()
}
