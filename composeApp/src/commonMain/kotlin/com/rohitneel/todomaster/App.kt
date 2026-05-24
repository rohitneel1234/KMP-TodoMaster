package com.rohitneel.todomaster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rohitneel.todomaster.presentation.navigation.*
import com.rohitneel.todomaster.presentation.onboarding.OnboardingScreen
import com.rohitneel.todomaster.presentation.screens.*
import com.rohitneel.todomaster.presentation.theme.TodoMasterTheme
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.presentation.viewmodel.PomodoroViewModel
import com.rohitneel.todomaster.util.WindowSize
import com.rohitneel.todomaster.util.WindowType
import com.rohitneel.todomaster.util.rememberWindowSize
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import todomaster.composeapp.generated.resources.*

@Composable
fun App(
    windowSize: WindowSize = rememberWindowSize(),
    onVibrate: () -> Unit = {},
    onStartPomodoro: () -> Unit = {},
    onStopPomodoro: () -> Unit = {},
    onCancelPomodoro: () -> Unit = {},
    pomodoroHours: Int = 0,
    pomodoroMinutes: Int = 0,
    pomodoroSeconds: Int = 0,
    pomodoroTimerState: String = "IDLE"
) {
    TodoMasterTheme {
        KoinContext {
            val taskViewModel: TaskViewModel = koinViewModel()
            val settingViewModel: SettingViewModel = koinViewModel()
            val pomodoroViewModel: PomodoroViewModel = koinViewModel()

            val startDestination by taskViewModel.startDestination
            val categoryTypes by taskViewModel.categoryTypes.collectAsState(initial = emptyList())
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination
            val scope = rememberCoroutineScope()

            val navDrawerItems = listOf(
                NavDrawerItem(
                    title = stringResource(Res.string.task_detail),
                    route = NavDestinations.TaskDetail.route,
                    icon = Res.drawable.baseline_task_alt_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.task_overview),
                    route = NavDestinations.Overview.route,
                    icon = Res.drawable.baseline_bar_chart_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.calendar),
                    route = NavDestinations.Calender.route,
                    icon = Res.drawable.baseline_calendar_month_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.reminders),
                    route = NavDestinations.Reminders.route,
                    icon = Res.drawable.outline_notifications_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.favorites),
                    route = NavDestinations.Favorites.route,
                    icon = Res.drawable.ic_outlined_sharp_star_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.category),
                    icon = Res.drawable.outline_category_24,
                    isExpandable = true,
                    children = categoryTypes.mapIndexed { index, category ->
                        NavDrawerItem(
                            title = category,
                            icon = Res.drawable.outline_category_type_24,
                            badgeCount = index
                        )
                    }
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.theme),
                    icon = Res.drawable.outline_color_lens_24,
                    route = NavDestinations.Theme.route
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.trash),
                    route = NavDestinations.Trash.route,
                    icon = Res.drawable.ic_trash_delete_24
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.feedback),
                    icon = Res.drawable.outline_feedback_24,
                    action = {
                        // Needs abstraction or platform-specific check
                    }
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.faq),
                    icon = Res.drawable.outline_help_24,
                    route = NavDestinations.FAQ.route
                ),
                NavDrawerItem(
                    title = stringResource(Res.string.settings),
                    route = NavDestinations.Settings.route,
                    icon = Res.drawable.outline_settings_24
                ),
            )

            val bottomNavItems = listOf(
                BottomNavItem(
                    selectedIcon = Res.drawable.baseline_task_alt_24,
                    label = stringResource(Res.string.task),
                    route = NavDestinations.TaskDetail
                ),
                BottomNavItem(
                    selectedIcon = Res.drawable.baseline_calendar_month_24,
                    route = NavDestinations.Calender,
                    label = stringResource(Res.string.calendar)
                ),
                BottomNavItem(
                    selectedIcon = Res.drawable.baseline_bar_chart_24,
                    route = NavDestinations.Overview,
                    label = stringResource(Res.string.overview)
                ),
                BottomNavItem(
                    selectedIcon = Res.drawable.outline_timer_24,
                    route = NavDestinations.Pomodoro,
                    label = stringResource(Res.string.pomodoro)
                ),
            )

            Scaffold(
                modifier = Modifier.imePadding(),
                scaffoldState = scaffoldState,
                bottomBar = {
                    if (currentRoute?.route.isBottomBarEnabled()) {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 12.dp, shape = RectangleShape),
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            tonalElevation = 10.dp
                        ) {
                            bottomNavItems.forEach { item ->
                                val isSelected = currentRoute?.hierarchy?.any { it.route == item.route.route } == true
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        navController.navigate(item.route.route) {
                                            navController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                                popUpTo(startDestinationRoute) {
                                                    saveState = true
                                                }
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = {
                                        BadgedBox(badge = {}) {
                                            Icon(
                                                painter = painterResource(item.selectedIcon),
                                                contentDescription = null,
                                                tint = if (isSelected) Color(taskViewModel.themeColor.value.toArgb()) else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            color = if (isSelected) Color(taskViewModel.themeColor.value.toArgb()) else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }
                        }
                    }
                },
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier.fillMaxWidth().background(color = MaterialTheme.colorScheme.background),
                        drawerShape = RoundedCornerShape(0.dp)
                    ) {
                        NavDrawerHeader(taskViewModel)
                        NavDrawerBody(
                            drawerItems = navDrawerItems,
                            currentRoute = currentRoute?.route,
                            taskViewModel = taskViewModel
                        ) { currentNavigationItem ->
                            currentNavigationItem.route?.let { route ->
                                navController.navigate(route) {
                                    navController.graph.startDestinationRoute?.let { startDestinationRoute ->
                                        popUpTo(startDestinationRoute) { saveState = true }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } ?: currentNavigationItem.action?.invoke()
                            scope.launch { scaffoldState.drawerState.close() }
                        }
                    }
                },
                drawerGesturesEnabled = currentRoute?.route.isGesturesEnabled()
            ) { paddingValue ->
                if (startDestination != null) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination!!,
                        modifier = Modifier.padding(paddingValue)
                    ) {
                        composable(route = NavDestinations.Onboarding.route) {
                            OnboardingScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(NavDestinations.TaskDetail.route) {
                            TaskDetailScreen(
                                navController = navController,
                                windowSize = windowSize,
                                onOpenDrawer = { scope.launch { scaffoldState.drawerState.open() } },
                                taskViewModel = taskViewModel
                            )
                        }
                        composable(
                            route = NavDestinations.AddTask.route + "?taskColor={taskColor}&taskId={taskId}&onEditTask={onEditTask}",
                            arguments = listOf(
                                navArgument("taskColor") { type = NavType.IntType; defaultValue = -1 },
                                navArgument("taskId") { type = NavType.StringType; defaultValue = "" },
                                navArgument("onEditTask") { type = NavType.BoolType; defaultValue = false }
                            )
                        ) { backStackEntry ->
                            val taskColor = backStackEntry.arguments?.getInt("taskColor") ?: -1
                            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                            val onEditTaskArg = backStackEntry.arguments?.getBoolean("onEditTask") ?: false
                            val onEditTask = remember { mutableStateOf(onEditTaskArg) }

                            AddTaskScreen(
                                navController = navController,
                                taskViewModel = taskViewModel,
                                taskColor = taskColor,
                                taskId = taskId,
                                onEditTask = onEditTask
                            )
                        }
                        composable(route = NavDestinations.Overview.route) {
                            OverviewScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(route = NavDestinations.Settings.route) {
                            SettingScreen(navController = navController, taskViewModel = taskViewModel, settingViewModel = settingViewModel)
                        }
                        composable(route = NavDestinations.Calender.route) {
                            CalenderScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(route = NavDestinations.Reminders.route) {
                            ReminderScreen(navController = navController, taskViewModel = taskViewModel, settingViewModel = settingViewModel)
                        }
                        composable(route = NavDestinations.Favorites.route) {
                            FavoritesScreen(navController = navController, taskViewModel = taskViewModel, settingViewModel = settingViewModel)
                        }
                        composable(route = NavDestinations.Trash.route) {
                            TrashScreen(navController = navController, taskViewModel = taskViewModel, settingViewModel = settingViewModel)
                        }
                        composable(route = NavDestinations.FAQ.route) {
                            FAQScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(route = NavDestinations.Theme.route) {
                            ThemeScreen(navController = navController, taskViewModel = taskViewModel, windowSize = windowSize)
                        }
                        composable(route = NavDestinations.PrivacyPolicy.route) {
                            PrivacyPolicyScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(route = NavDestinations.ModifyCategories.route) {
                            ModifyCategoriesScreen(navController = navController, taskViewModel = taskViewModel)
                        }
                        composable(route = NavDestinations.Pomodoro.route) {
                            PomodoroScreen(
                                navController = navController,
                                taskViewModel = taskViewModel,
                                pomodoroViewModel = pomodoroViewModel,
                                settingViewModel = settingViewModel,
                                onStartService = onStartPomodoro,
                                onStopService = onStopPomodoro,
                                onCancelService = onCancelPomodoro,
                                onVibrate = onVibrate,
                                hours = pomodoroHours,
                                minutes = pomodoroMinutes,
                                seconds = pomodoroSeconds,
                                currentTimerState = pomodoroTimerState
                            )
                        }
                        composable(route = NavDestinations.PomodoroComplete.route) {
                            PomodoroComplete(navController = navController, taskViewModel = taskViewModel, pomodoroViewModel = pomodoroViewModel)
                        }
                    }
                }
            }
        }
    }
}

private fun String?.isGesturesEnabled(): Boolean {
    return this == NavDestinations.TaskDetail.route
}

private fun String?.isBottomBarEnabled(): Boolean {
    return this in listOf(
        NavDestinations.TaskDetail.route,
        NavDestinations.Calender.route,
        NavDestinations.Overview.route,
        NavDestinations.Pomodoro.route
    )
}
