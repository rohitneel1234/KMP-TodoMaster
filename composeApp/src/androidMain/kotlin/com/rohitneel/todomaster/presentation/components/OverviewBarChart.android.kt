package com.rohitneel.todomaster.presentation.components

import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants.INITIAL_MAX_TASK_COUNT
import com.rohitneel.todomaster.util.AppConstants.LARGE_SCREEN_DENSITY_THRESHOLD
import com.rohitneel.todomaster.util.AppConstants.MAX_Y_AXIS_LABEL_COUNT
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.no_task_completed

@Composable
actual fun OverviewBarChart(
    modifier: Modifier,
    taskData: Map<String, Int>,
    daysOfWeek: List<String>,
    taskViewModel: TaskViewModel
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenDensity = configuration.densityDpi
    val labelWidthSize = if (screenDensity >= LARGE_SCREEN_DENSITY_THRESHOLD) 30f else 15f
    val labelTextSize = with(density) { 14.sp.toPx() }
    val maxTaskCount = taskData.values.maxOrNull() ?: 0
    var dynamicMaxTaskCount by remember { mutableIntStateOf(INITIAL_MAX_TASK_COUNT) } // Initial value
    var stepCount by remember { mutableIntStateOf(2) } // Initial step value

    // Update dynamicMaxTaskCount and stepCount when maxTaskCount changes
    LaunchedEffect(maxTaskCount) {
        if (maxTaskCount > dynamicMaxTaskCount) {
            while (dynamicMaxTaskCount < maxTaskCount) {
                dynamicMaxTaskCount += 4
                stepCount += 1 // Increase stepCount as dynamicMaxTaskCount increases
            }
        } else if (maxTaskCount < dynamicMaxTaskCount) {
            dynamicMaxTaskCount = maxOf(8, dynamicMaxTaskCount - 4)
            stepCount = maxOf(2, stepCount - 1) // Decrease stepCount when dynamicMaxTaskCount decreases
        }
    }
    val barWidth = remember { 22.dp }
    val barSpacing = remember { 20.dp }
    val chartHeight = 200.dp
    val axisLineColor = remember { Color.Gray }
    val labelColor = MaterialTheme.colorScheme.onSurface
    val textPaint = remember {
        TextPaint().apply {
            color = labelColor.toArgb()
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }
    val noTaskMessage = stringResource(Res.string.no_task_completed)
    
    Box(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth()
            .height(chartHeight)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val maxBarHeight = canvasHeight - 10.dp.toPx()
            val totalBarWidth = daysOfWeek.size * barWidth.toPx()
            val totalSpacing = (daysOfWeek.size - 1) * barSpacing.toPx()
            val totalChartWidth = totalBarWidth + totalSpacing

            // If the total chart width exceeds the canvas width, adjust spacing
            val adjustedBarSpacing = if (totalChartWidth > canvasWidth) {
                (canvasWidth - totalBarWidth) / (daysOfWeek.size)
            } else {
                barSpacing.toPx()
            }

            val yAxisEnd = canvasHeight - (0f / dynamicMaxTaskCount) * maxBarHeight
            // Draw Y-axis and task count labels
            drawLine(
                color = axisLineColor,
                start = Offset(0f, 0f),
                end = Offset(0f, yAxisEnd),
                strokeWidth = 4f
            )
            (0..dynamicMaxTaskCount step stepCount).forEach { count ->
                val yOffset = canvasHeight - (count.toFloat() / dynamicMaxTaskCount) * maxBarHeight
                val textWidth = textPaint.measureText("$count")
                val labelOffsetX = if (count > MAX_Y_AXIS_LABEL_COUNT) -textWidth - 20f else -textWidth - labelWidthSize
                drawIntoCanvas { canvas ->
                    textPaint.textSize = if (screenDensity >= LARGE_SCREEN_DENSITY_THRESHOLD) 35f else 20f
                    canvas.nativeCanvas.drawText("$count", labelOffsetX, yOffset + 2f, textPaint)
                }
            }

            // Draw X-axis
            drawLine(
                color = axisLineColor,
                start = Offset(0f, canvasHeight),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 4f
            )

            if (taskData.values.all { it == 0 }) {
                val textWidth = textPaint.measureText(noTaskMessage)
                val textX = (size.width - textWidth) / 2
                val textY = size.height / 2
                drawIntoCanvas { canvas ->
                    textPaint.textSize = labelTextSize
                    canvas.nativeCanvas.drawText(noTaskMessage, textX, textY, textPaint)
                }
            }

            daysOfWeek.forEachIndexed { index, day ->
                val taskCount = taskData[day] ?: 0
                val animatedHeight = (taskCount.toFloat() / dynamicMaxTaskCount) * maxBarHeight
                val xOffset = (index * (barWidth.toPx() + adjustedBarSpacing)) + adjustedBarSpacing
                // Draw the bar
                drawRect(
                    color = Color(taskViewModel.themeColor.value.toArgb()),
                    topLeft = Offset(x = xOffset, y = canvasHeight - animatedHeight),
                    size = Size(barWidth.toPx(), animatedHeight)
                )

                // Draw the day label below the bar
                val textX = xOffset + (barWidth.toPx() / 4) - 10
                val textY = canvasHeight + 20.dp.toPx()
                drawIntoCanvas { canvas ->
                    textPaint.textSize = if (screenDensity >= LARGE_SCREEN_DENSITY_THRESHOLD) 35f else 18f
                    canvas.nativeCanvas.drawText(day, textX, textY, textPaint)
                }
            }
        }
    }
}
