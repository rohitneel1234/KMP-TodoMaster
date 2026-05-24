package com.rohitneel.todomaster.presentation.events

import com.rohitneel.todomaster.data.model.TaskModel

sealed class TaskDetailEvent {
    data object Initial : TaskDetailEvent()
    data class ShowUndoDeleteTaskMessage(val task: TaskModel) : TaskDetailEvent()
}
