package com.rohitneel.todomaster.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rohitneel.todomaster.data.model.TaskModel

@Composable
actual fun LinkableText(
    task: TaskModel,
    description: String,
    color: Color,
    modifier: Modifier,
) {
    BasicLinkableText(task, description, color, modifier)
}
