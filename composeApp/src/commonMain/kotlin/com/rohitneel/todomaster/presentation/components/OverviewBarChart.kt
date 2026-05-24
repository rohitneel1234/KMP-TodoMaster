package com.rohitneel.todomaster.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel

@Composable
expect fun OverviewBarChart(
    modifier: Modifier = Modifier,
    taskData: Map<String, Int>,
    daysOfWeek: List<String>,
    taskViewModel: TaskViewModel
)
