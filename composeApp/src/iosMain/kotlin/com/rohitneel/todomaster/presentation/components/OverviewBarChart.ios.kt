package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel

@Composable
actual fun OverviewBarChart(
    modifier: Modifier,
    taskData: Map<String, Int>,
    daysOfWeek: List<String>,
    taskViewModel: TaskViewModel
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Bar Chart (iOS Implementation Pending)")
    }
}
