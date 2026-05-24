package com.rohitneel.todomaster.presentation.events

sealed class ChartEvent {
    data object Empty : ChartEvent()
    data object Completed : ChartEvent()
    data class ShowingData(val aggregatedData: Map<String, Long>) : ChartEvent()
}
