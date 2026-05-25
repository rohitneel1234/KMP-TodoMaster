package com.rohitneel.todomaster.presentation.components

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants.INITIAL_MAX_TASK_COUNT
import com.rohitneel.todomaster.util.AppConstants.MAX_Y_AXIS_LABEL_COUNT
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.no_task_completed
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Font
import org.jetbrains.skia.Typeface

@Composable
actual fun OverviewBarChart(
    modifier: Modifier,
    taskData: Map<String, Int>,
    daysOfWeek: List<String>,
    taskViewModel: TaskViewModel
) {
    val density = LocalDensity.current
    val labelTextSize = with(density) { 14.sp.toPx() }
    val maxTaskCount = taskData.values.maxOrNull() ?: 0
    var dynamicMaxTaskCount by remember { mutableIntStateOf(INITIAL_MAX_TASK_COUNT) }
    var stepCount by remember { mutableIntStateOf(2) }

    LaunchedEffect(maxTaskCount) {
        if (maxTaskCount > dynamicMaxTaskCount) {
            while (dynamicMaxTaskCount < maxTaskCount) {
                dynamicMaxTaskCount += 4
                stepCount += 1
            }
        } else if (maxTaskCount < dynamicMaxTaskCount) {
            dynamicMaxTaskCount = maxOf(8, dynamicMaxTaskCount - 4)
            stepCount = maxOf(2, stepCount - 1)
        }
    }
    val barWidth = remember { 22.dp }
    val barSpacing = remember { 20.dp }
    val chartHeight = 200.dp
    val axisLineColor = remember { Color.Gray }
    val labelColor = MaterialTheme.colorScheme.onSurface
    
    val skiaFont = remember {
        Font(Typeface.makeFromName("Arial", org.jetbrains.skia.FontStyle.BOLD), 12f)
    }
    val skiaPaint = remember {
        Paint().apply {
            color = labelColor.toArgb()
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

            val adjustedBarSpacing = if (totalChartWidth > canvasWidth) {
                (canvasWidth - totalBarWidth) / (daysOfWeek.size)
            } else {
                barSpacing.toPx()
            }

            val yAxisEnd = canvasHeight - (0f / dynamicMaxTaskCount) * maxBarHeight
            
            drawLine(
                color = axisLineColor,
                start = Offset(0f, 0f),
                end = Offset(0f, yAxisEnd),
                strokeWidth = 4f
            )
            
            (0..dynamicMaxTaskCount step stepCount).forEach { count ->
                val yOffset = canvasHeight - (count.toFloat() / dynamicMaxTaskCount) * maxBarHeight
                drawIntoCanvas { canvas ->
                    skiaFont.size = 20f
                    canvas.nativeCanvas.drawString("$count", -30f, yOffset + 2f, skiaFont, skiaPaint)
                }
            }

            drawLine(
                color = axisLineColor,
                start = Offset(0f, canvasHeight),
                end = Offset(canvasWidth, canvasHeight),
                strokeWidth = 4f
            )

            if (taskData.values.all { it == 0 }) {
                drawIntoCanvas { canvas ->
                    skiaFont.size = labelTextSize
                    val textWidth = skiaFont.measureTextWidth(noTaskMessage)
                    canvas.nativeCanvas.drawString(noTaskMessage, (size.width - textWidth) / 2, size.height / 2, skiaFont, skiaPaint)
                }
            }

            daysOfWeek.forEachIndexed { index, day ->
                val taskCount = taskData[day] ?: 0
                val animatedHeight = (taskCount.toFloat() / dynamicMaxTaskCount) * maxBarHeight
                val xOffset = (index * (barWidth.toPx() + adjustedBarSpacing)) + adjustedBarSpacing
                
                drawRect(
                    color = Color(taskViewModel.themeColor.value.toArgb()),
                    topLeft = Offset(x = xOffset, y = canvasHeight - animatedHeight),
                    size = Size(barWidth.toPx(), animatedHeight)
                )

                val textX = xOffset + (barWidth.toPx() / 4) - 10
                val textY = canvasHeight + 20.dp.toPx()
                drawIntoCanvas { canvas ->
                    skiaFont.size = 18f
                    canvas.nativeCanvas.drawString(day, textX, textY, skiaFont, skiaPaint)
                }
            }
        }
    }
}
