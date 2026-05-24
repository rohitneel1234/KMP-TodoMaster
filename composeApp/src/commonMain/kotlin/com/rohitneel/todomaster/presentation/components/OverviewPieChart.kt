package com.rohitneel.todomaster.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import com.rohitneel.todomaster.util.Utils.determineChartEvent

@Composable
fun OverviewPieChart(
    categoryColors: Map<String, Color>,
    task: List<TaskModel>,
    arcWidth: Dp = 30.dp,
    startAngle: Float = -180f,
    pieChartSize: Dp = 200.dp,
    animDuration: Int = 1000,
) {
    val chartEvent = determineChartEvent(task)
    Column(
        modifier = Modifier.size(pieChartSize * 1.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (chartEvent) {
            is ChartEvent.Empty -> {
                // ShowTaskStatusMessage placeholder
            }
            is ChartEvent.Completed -> {
                // ShowTaskStatusMessage placeholder
            }
            is ChartEvent.ShowingData -> {
                val aggregatedData = chartEvent.aggregatedData
                val totalSum = aggregatedData.values.sum()
                val arcValues = mutableListOf<Float>()
                val categories = aggregatedData.keys.toList()
                categories.forEachIndexed { index, category ->
                    val value = aggregatedData[category] ?: 0L
                    val arc = value.toFloat() / totalSum.toFloat() * 360f
                    arcValues.add(index, arc)
                }
                var newStartAngle = startAngle
                val animationProgress = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    animationProgress.animateTo(1f, animationSpec = tween(animDuration))
                }
                Canvas(
                    modifier = Modifier
                        .size(pieChartSize)
                        .rotate(90f * animationProgress.value)
                ) {
                    categories.forEachIndexed { index, category ->
                        val color = categoryColors[category] ?: Color.Gray
                        drawArc(
                            color = color,
                            startAngle = newStartAngle,
                            useCenter = false,
                            sweepAngle = arcValues[index] * animationProgress.value,
                            style = Stroke(width = arcWidth.toPx()),
                        )
                        newStartAngle += arcValues[index]
                    }
                }
            }
        }
    }
}
