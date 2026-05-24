package com.rohitneel.todomaster.presentation.navigation

sealed class NavDestinations(val route: String) {
    data object Onboarding : NavDestinations("onboarding")
    data object TaskDetail : NavDestinations("task_detail")
    data object AddTask : NavDestinations("add_task")
    data object Settings : NavDestinations("settings")
    data object Overview : NavDestinations("overview")
    data object Calender : NavDestinations("calender")
    data object Reminders : NavDestinations("reminders")
    data object Favorites : NavDestinations("favorites")
    data object FAQ : NavDestinations("faq")
    data object Theme : NavDestinations("theme")
    data object Pomodoro : NavDestinations("pomodoro")
    data object PomodoroComplete : NavDestinations("pomodoro_complete")
    data object ModifyCategories : NavDestinations("modify_categories")
    data object PrivacyPolicy : NavDestinations("privacy_policy")
    data object Trash : NavDestinations("trash")
}
