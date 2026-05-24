package com.rohitneel.todomaster.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.yearMonth
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.CalenderViewMode
import com.rohitneel.todomaster.presentation.components.DaysOfWeekTitle
import com.rohitneel.todomaster.presentation.components.DisplayCheckListItem
import com.rohitneel.todomaster.presentation.components.MonthDayView
import com.rohitneel.todomaster.presentation.components.WeekDayView
import com.rohitneel.todomaster.presentation.events.TaskDetailEvent
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.CustomFloatingActionButton
import com.rohitneel.todomaster.util.DropdownMenuPopup
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import com.rohitneel.todomaster.util.ShowSnackBarMessage
import com.rohitneel.todomaster.util.Utils
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.baseline_arrow_left_24
import todomaster.composeapp.generated.resources.baseline_arrow_right_24
import todomaster.composeapp.generated.resources.baseline_calendar_month_24
import todomaster.composeapp.generated.resources.baseline_calendar_view_week_24
import todomaster.composeapp.generated.resources.calendar
import todomaster.composeapp.generated.resources.click_to_add_task
import todomaster.composeapp.generated.resources.no_task_text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalenderScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    val localDateSaver = Saver<LocalDate?, String>(
        save = { it?.toString() ?: "" },
        restore = { if (it.isEmpty()) null else LocalDate.parse(it) }
    )

    var selectedDate: LocalDate? by rememberSaveable(stateSaver = localDateSaver) {
        mutableStateOf(today)
    }
    
    val tasks by taskViewModel.tasks.collectAsState()
    val taskEvents by taskViewModel.tasksEvent.collectAsState(TaskDetailEvent.Initial)
    
    val filterTasks by remember {
        derivedStateOf {
            tasks.filter { task ->
                 val taskDate = task.creationDate?.let {
                    Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault()).date
                 }
                 taskDate == selectedDate
            }
        }
    }
    
    val taskDates = remember(tasks) {
        tasks.mapNotNull { task ->
            task.creationDate?.let { creationDate ->
                Instant.fromEpochMilliseconds(creationDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
        }.toSet()
    }
    
    var calenderViewMode by remember { mutableStateOf(CalenderViewMode.MONTHLY) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    
    val currentMonth = remember { 
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val startMonth = LocalDate(now.year, now.month, 1)
        startMonth
    }
    
    val monthState = rememberCalendarState(
        startMonth = currentMonth.minus(3, DateTimeUnit.YEAR).toYearMonth(),
        endMonth = currentMonth.plus(3, DateTimeUnit.YEAR).toYearMonth(),
        firstVisibleMonth = currentMonth.toYearMonth(),
        firstDayOfWeek = firstDayOfWeek
    )
    
    val weekState = rememberWeekCalendarState(
        startDate = currentMonth.minus(3, DateTimeUnit.YEAR),
        endDate = currentMonth.plus(3, DateTimeUnit.YEAR),
        firstDayOfWeek = firstDayOfWeek
    )
    
    var isSnackBarShow by rememberSaveable { mutableStateOf(false) }

    ShowSnackBarMessage(
        taskEvents = taskEvents,
        taskViewModel = taskViewModel,
        snackBarHostState = snackBarHostState,
        isSnackBarShow = isSnackBarShow,
        onDismissSnackBar = { isSnackBarShow = false }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(Res.string.calendar))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                            scope.launch {
                                if (calenderViewMode == CalenderViewMode.MONTHLY) {
                                    monthState.scrollToMonth(monthState.firstVisibleMonth.yearMonth)
                                } else {
                                    // weekState.scrollToWeek
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_arrow_left_24),
                                contentDescription = "Previous",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    IconButton(onClick = {
                            scope.launch {
                                if (calenderViewMode == CalenderViewMode.MONTHLY) {
                                    monthState.scrollToMonth(monthState.firstVisibleMonth.yearMonth)
                                } else {
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_arrow_right_24),
                                contentDescription = "Next",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    IconButton(
                        onClick = {
                            calenderViewMode = if (calenderViewMode == CalenderViewMode.WEEKLY) {
                                CalenderViewMode.MONTHLY
                            } else {
                                CalenderViewMode.WEEKLY
                            }
                        }
                    ) {
                        val currentIcon = if (calenderViewMode == CalenderViewMode.WEEKLY) {
                            Res.drawable.baseline_calendar_month_24
                        } else {
                            Res.drawable.baseline_calendar_view_week_24
                        }
                        Icon(painter = painterResource(currentIcon), contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = {
                    navController.navigate(NavDestinations.AddTask.route)
                },
                taskViewModel = taskViewModel
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (calenderViewMode == CalenderViewMode.WEEKLY) {
                WeekCalendar(
                    state = weekState,
                    dayContent = { day ->
                        WeekDayView(
                            day = day,
                            selected = selectedDate == day.date,
                            indicator = day.date in taskDates,
                            themeColor = taskViewModel.themeColor.value
                        ) {
                            selectedDate = it
                        }
                    },
                )
            } else {
                HorizontalCalendar(
                    state = monthState,
                    dayContent = { day ->
                        MonthDayView(
                            day = day,
                            selected = selectedDate == day.date,
                            indicator = day.date in taskDates,
                            themeColor = taskViewModel.themeColor.value
                        ) {
                            selectedDate = it
                        }
                    },
                    monthHeader = { month ->
                        val daysOfWeek = month.weekDays.first().map { it.date.dayOfWeek }
                        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filterTasks.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_task_text),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.click_to_add_task),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn {
                    items(filterTasks, key = { it.id }) {
                        TaskOverviewItem(
                            task = it,
                            taskViewModel = taskViewModel,
                            navController = navController,
                            onSnackBarShow = { isSnackBarShow = true },
                            snackBarHostState = snackBarHostState
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskOverviewItem(
    task: TaskModel,
    taskViewModel: TaskViewModel,
    navController: NavController,
    onSnackBarShow: () -> Unit,
    snackBarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val categoryColors by taskViewModel.categoryColors.collectAsState()
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    
    val taskBackgroundBrush = task.gradientColor?.takeIf { it.isNotEmpty() }
        ?.let { Brush.horizontalGradient(it.map { Color(it) }) }
        ?: SolidColor(Color(task.color))

    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val checkListItem = taskViewModel.checkListMap[task.id] ?: emptyList()
    
    LaunchedEffect(task.id) {
        taskViewModel.getCheckList(task.id)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    taskViewModel.setTask(task)
                    navController.navigate(NavDestinations.AddTask.route + "?taskId=${task.id}")
                },
                onLongClick = {
                    expandedTaskId = task.id
                }
            )
            .background(
                color = categoryColors[task.category] ?: MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(start = 10.dp)
    ) {
         Column(
            Modifier
                .fillMaxWidth()
                .background(
                    brush = taskBackgroundBrush,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = task.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            )
            
            if (task.isCheck) {
                checkListItem.forEach {
                    DisplayCheckListItem(it, task)
                }
            } else if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    fontSize = 14.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                )
            }
         }
         
         if (expandedTaskId == task.id) {
            DropdownMenuPopup(
                onDismissRequest = { expandedTaskId = null },
                isFavorite = task.isFavorite,
                onFavoriteTask = {
                    taskViewModel.updateTask(task.copy(isFavorite = !task.isFavorite))
                    expandedTaskId = null
                },
                onDeleteTask = {
                    expandedTaskId = null
                    showDeleteDialog = true
                },
                onShareTask = {
                    Utils.shareText(task.title)
                },
                isPinVisible = false
            )
        }
        
        if (showDeleteDialog) {
            ShowConfirmationDialog(
                title = "Delete Task?",
                primaryActionText = "Delete",
                secondaryActionText = "Cancel",
                onConfirm = {
                    taskViewModel.moveTaskToTrash(task)
                    showDeleteDialog = false
                    onSnackBarShow()
                },
                onDismissRequest = {
                    showDeleteDialog = false
                },
                taskViewModel = taskViewModel
            )
        }
    }
}

// Extension to bridge LocalDate to YearMonth if using the KMP library
fun LocalDate.toYearMonth() = this.yearMonth
