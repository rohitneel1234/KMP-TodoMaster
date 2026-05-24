package com.rohitneel.todomaster.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.Clarendon

@Composable
expect fun LinkableText(
    task: TaskModel,
    description: String,
    color: Color,
    modifier: Modifier = Modifier,
)

@Composable
fun BasicLinkableText(
    task: TaskModel,
    description: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = description,
        color = color,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Clarendon,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
        modifier = modifier
    )
}
