package com.rohitneel.todomaster.presentation.onboarding

import org.jetbrains.compose.resources.DrawableResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

sealed class OnBoardingModel(
    val image: DrawableResource,
    val title: String,
    val description: String
) {
    object First : OnBoardingModel(
        image = Res.drawable.onboarding_new_task,
        title = "Create new task",
        description = "Create new tasks seamlessly to keep your to-do list up to date."
    )

    object Second : OnBoardingModel(
        image = Res.drawable.onboarding_search_task,
        title = "Search task",
        description = "Quickly find any task with our powerful search feature. Easily locate tasks by title or dates."
    )

    object Third : OnBoardingModel(
        image = Res.drawable.onboarding_reminder,
        title = "Set Reminder",
        description = "Set up alarms to remind you about important tasks and deadlines."
    )

    object Fourth : OnBoardingModel(
        image = Res.drawable.onboarding_overview_task,
        title = "Task Progress",
        description = "Visualize your progress with clear graphs that show both pending and completed tasks."
    )

    object Fifth : OnBoardingModel(
        image = Res.drawable.onboarding_theme,
        title = "Customize Theme",
        description = "Choose a theme and customize your app. Your selected theme will define the appearance of app."
    )
}
