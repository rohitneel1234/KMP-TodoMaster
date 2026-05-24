package com.rohitneel.todomaster.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.components.CustomCircularProgressBar
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.theme.Clarendon
import com.rohitneel.todomaster.presentation.viewmodel.PomodoroViewModel
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.platform.LocalPomodoroPlatformActions
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.Utils
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.add_task_message
import todomaster.composeapp.generated.resources.baseline_access_time_filled_24
import todomaster.composeapp.generated.resources.baseline_play_arrow_24
import todomaster.composeapp.generated.resources.baseline_stop_24
import todomaster.composeapp.generated.resources.baseline_timer_24
import todomaster.composeapp.generated.resources.focusing
import todomaster.composeapp.generated.resources.outline_pause_24
import todomaster.composeapp.generated.resources.pomodoro
import todomaster.composeapp.generated.resources.select_task
import todomaster.composeapp.generated.resources.tasks
import todomaster.composeapp.generated.resources.time

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    navController: NavController,
    taskViewModel: TaskViewModel,
    pomodoroViewModel: PomodoroViewModel,
    settingViewModel: SettingViewModel
) {
    val platformActions = LocalPomodoroPlatformActions.current
    val hours by pomodoroViewModel.hours.collectAsState()
    val minutes by pomodoroViewModel.minutes.collectAsState()
    val seconds by pomodoroViewModel.seconds.collectAsState()
    val currentTimerState by pomodoroViewModel.timerState.collectAsState()
    val tasks = taskViewModel.tasks.collectAsState(initial = emptyList())
    val categoryColors by taskViewModel.categoryColors.collectAsState(initial = emptyMap())
    val activeTasks = tasks.value.filter { !it.isCompleted }
    val progress by pomodoroViewModel.progress.collectAsState()
    val isCompleted by pomodoroViewModel.isCompleted.collectAsState()
    val vibrateEnable by settingViewModel.vibrateEnable.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val selectedTask by taskViewModel.selectedTask.collectAsState()
    var selectedTitle by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Gray) }
    val offsetY = remember { Animatable(0f) }
    var currentBottomSheet by remember { mutableStateOf<BottomSheetSelectionType?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var selectedTime by remember { mutableLongStateOf(300L) } // Default 5 mins
    val timeOptions = remember {
        listOf(
            3600L to "${AppConstants.TimeDurations.ONE_HOUR} ${AppConstants.TimeUnits.HOUR}",
            2700L to "${AppConstants.TimeDurations.FORTY_FIVE_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            1800L to "${AppConstants.TimeDurations.THIRTY_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            1500L to "${AppConstants.TimeDurations.TWENTY_FIVE_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            1200L to "${AppConstants.TimeDurations.TWENTY_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            900L to "${AppConstants.TimeDurations.FIFTEEN_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            600L to "${AppConstants.TimeDurations.TEN_MINUTES} ${AppConstants.TimeUnits.MINUTES}",
            300L to "${AppConstants.TimeDurations.FIVE_MINUTES} ${AppConstants.TimeUnits.MINUTES}"
        )
    }

    LaunchedEffect(tasks.value, selectedTask) {
        selectedTask?.let { task ->
            val updatedTask = tasks.value.find { it.id == task.id }
            if (updatedTask?.isCompleted == true) {
                selectedTitle = "Select Task"
                selectedColor = Color.Gray
            } else {
                selectedTitle = task.title
                selectedColor = categoryColors[task.category] ?: Color.Gray
            }
        }
    }

    val alphaValue = remember { Animatable(1f) }
    LaunchedEffect(taskViewModel.isPaused) {
        if (taskViewModel.isPaused) {
            alphaValue.animateTo(
                targetValue = 0.2f,
                animationSpec = infiniteRepeatable(
                    tween(
                        delayMillis = 1000,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            alphaValue.snapTo(1f)
        }
    }

    LaunchedEffect(taskViewModel.isReset) {
        if (taskViewModel.isReset) {
            alphaValue.snapTo(1f)
        }
    }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            if (vibrateEnable) platformActions.onVibrate()
            taskViewModel.resetTimer()
            selectedTask?.let { task ->
                taskViewModel.onTaskUpdated(task, true)
                selectedTitle = "Select Task"
                selectedColor = Color.Gray
            }
            platformActions.onCancel()
            navController.navigate(NavDestinations.PomodoroComplete.route)
        }
    }

    if (currentBottomSheet != null) {
        ModalBottomSheet(
            onDismissRequest = {
                currentBottomSheet = null
            },
            sheetState = sheetState,
            tonalElevation = 8.dp,
            containerColor = MaterialTheme.colorScheme.onPrimary,
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BottomSheetDefaults.DragHandle()
                    Text(
                        text = if (currentBottomSheet == BottomSheetSelectionType.TIME_SELECTION) stringResource(Res.string.time) else stringResource(Res.string.tasks),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                }
            },
            modifier = Modifier
                .offset { IntOffset(0, offsetY.value.toInt()) }
        ) {
            when (currentBottomSheet) {
                BottomSheetSelectionType.TIME_SELECTION -> {
                    TimeSelectionSheet(
                        timeOptions = timeOptions,
                        selectedTime = selectedTime,
                        onTimeSelected = { time ->
                            selectedTime = time
                            pomodoroViewModel.setTimerDuration(time)
                            platformActions.onSetDuration(time)
                            currentBottomSheet = null
                            scope.launch { sheetState.hide() }
                        }
                    )
                }
                BottomSheetSelectionType.TASK_SELECTION -> {
                    TaskSelectionSheet(
                        activeTasks = activeTasks,
                        categoryColorMapping = categoryColors,
                        taskViewModel =  taskViewModel,
                        onTaskSelected = { task ->
                            selectedTitle = task.title
                            taskViewModel.setSelectedTask(task)
                            currentBottomSheet = null
                            scope.launch { sheetState.hide() }
                        }
                    )
                }
                null -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(Color(taskViewModel.themeColor.value.toArgb())),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                            .animateContentSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AnimatedVisibility(visible = currentTimerState != "STARTED") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.baseline_timer_24),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                        Text(
                            text = if (currentTimerState == "STARTED") stringResource(Res.string.focusing) else stringResource(Res.string.pomodoro),
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(top = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            currentBottomSheet = BottomSheetSelectionType.TASK_SELECTION
                            scope.launch { sheetState.show() }
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectedColor != Color.Transparent) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(selectedColor, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = selectedTitle.ifEmpty { stringResource(Res.string.select_task) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Task",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(contentAlignment = Alignment.Center) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .alpha(alphaValue.value)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (currentTimerState == "IDLE") {
                                    currentBottomSheet = BottomSheetSelectionType.TIME_SELECTION
                                    scope.launch { sheetState.show() }
                                }
                            }
                        )
                ) {
                    Text(
                        text = hours.toString().padStart(2, '0') + ":",
                        fontFamily = Clarendon,
                        fontSize = 36.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = minutes.toString().padStart(2, '0') + ":",
                        fontFamily = Clarendon,
                        fontSize = 36.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    AnimatedContent(
                        targetState = seconds,
                        label = seconds.toString(),
                        transitionSpec = { timerTextAnimation() }
                    ) { secondsValue ->
                        Text(
                            text = secondsValue.toString().padStart(2, '0'),
                            fontFamily = Clarendon,
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    if (currentTimerState == "IDLE") {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }
                CustomCircularProgressBar(progress = progress, progressBarColor = Color(taskViewModel.themeColor.value.toArgb()))
            }
            Spacer(modifier = Modifier.height(36.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTitle.isEmpty()) {
                            scope.launch {
                                snackBarHostState.showSnackbar(message = "Please select a task to start pomodoro session")
                            }
                        } else {
                            if (currentTimerState == "STARTED") {
                                taskViewModel.pauseTimer()
                                platformActions.onStop()
                            } else {
                                taskViewModel.startTimer()
                                platformActions.onStart()
                            }
                        }
                    },
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    containerColor = Color(taskViewModel.themeColor.value.toArgb()),
                    contentColor = Color.LightGray
                ) {
                    Icon(
                        painter = if (currentTimerState == "STARTED") painterResource(Res.drawable.outline_pause_24) else painterResource(Res.drawable.baseline_play_arrow_24),
                        tint = Color.White,
                        contentDescription = null
                    )
                }
                AnimatedVisibility(visible = currentTimerState != "IDLE") {
                    FloatingActionButton(
                        onClick = {
                            pomodoroViewModel.resetProgress()
                            taskViewModel.resetTimer()
                            selectedTask?.let { task ->
                                taskViewModel.onTaskUpdated(task, true)
                                selectedTitle = ""
                                selectedColor = Color.Gray
                            }
                            platformActions.onCancel()
                        },
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        containerColor = Color(taskViewModel.themeColor.value.toArgb()),
                        contentColor = Color.LightGray
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_stop_24),
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun TimeSelectionSheet(
    timeOptions: List<Pair<Long, String>>,
    selectedTime: Long,
    onTimeSelected: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        timeOptions.forEach { (time, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTimeSelected(time) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painter = painterResource(Res.drawable.baseline_access_time_filled_24), contentDescription = "", tint = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(
                    checked = time == selectedTime,
                    onCheckedChange = { onTimeSelected(time) }
                )
            }
        }
    }
}

@Composable
fun TaskSelectionSheet(
    activeTasks: List<TaskModel>,
    categoryColorMapping: Map<String, Color>,
    taskViewModel: TaskViewModel,
    onTaskSelected: (TaskModel) -> Unit,
) {
    val selectedTask = taskViewModel.selectedTask.collectAsState().value
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        items(activeTasks) { task ->
            val isSelected = selectedTask?.id == task.id
            val itemColor = categoryColorMapping[task.category] ?: Color.Gray
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp, max = 80.dp)
                    .padding(8.dp)
                    .clickable {
                        taskViewModel.setSelectedTask(task)
                        onTaskSelected(task)
                    }
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(itemColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        if (activeTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.add_task_message),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight -> -fullHeight } +
            fadeOut(animationSpec = tween(duration))
}

enum class BottomSheetSelectionType {
    TIME_SELECTION,
    TASK_SELECTION
}
