package com.rohitneel.todomaster.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.SearchAppBarState
import com.rohitneel.todomaster.presentation.components.ArrowDirection
import com.rohitneel.todomaster.presentation.components.ChipGroup
import com.rohitneel.todomaster.presentation.components.CustomTopAppBar
import com.rohitneel.todomaster.presentation.components.TaskItem
import com.rohitneel.todomaster.presentation.components.drawBubble
import com.rohitneel.todomaster.presentation.events.TaskDetailEvent
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.theme.AppColors.Violet
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants.SCREEN_CONTENT_WIDTH_FRACTION
import com.rohitneel.todomaster.util.CustomFloatingActionButton
import com.rohitneel.todomaster.util.CustomTabIndicator
import com.rohitneel.todomaster.util.CustomTabItem
import com.rohitneel.todomaster.util.DisplayPlaceholderImage
import com.rohitneel.todomaster.util.DropdownMenuPopup
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import com.rohitneel.todomaster.util.ShowSnackBarMessage
import com.rohitneel.todomaster.util.Utils
import com.rohitneel.todomaster.util.WindowSize
import com.rohitneel.todomaster.util.WindowType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.add_favorites_message
import todomaster.composeapp.generated.resources.cancel
import todomaster.composeapp.generated.resources.create_task_tool_tip
import todomaster.composeapp.generated.resources.delete
import todomaster.composeapp.generated.resources.delete_confirmation_title
import todomaster.composeapp.generated.resources.done
import todomaster.composeapp.generated.resources.remove_favorites_message
import todomaster.composeapp.generated.resources.task_detail_placeholder
import todomaster.composeapp.generated.resources.todo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskDetailScreen(
    navController: NavHostController,
    windowSize: WindowSize,
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel = koinViewModel(),
    onOpenDrawer: () -> Unit
) {
    val tasks by taskViewModel.tasks.collectAsState(initial = emptyList())
    val taskEvents by taskViewModel.tasksEvent.collectAsState(TaskDetailEvent.Initial)
    val categoryTypes by taskViewModel.categoryTypes.collectAsState(initial = emptyList())
    val categoryColors by taskViewModel.categoryColors.collectAsState(initial = emptyMap())
    val isDarkMode by settingViewModel.isDarkMode.collectAsState()
    val lazyGridListState = rememberLazyStaggeredGridState()
    var isListViewEnable by rememberSaveable { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    var isSnackBarShow by rememberSaveable { mutableStateOf(false) }
    var isTaskDone by rememberSaveable { mutableStateOf(false) }
    val selectedItems = remember { mutableStateListOf<TaskModel>() }
    var isActionModeActive by rememberSaveable { mutableStateOf(false) }
    val searchAppBarState: SearchAppBarState by taskViewModel.searchAppBarState
    var taskId: String? by rememberSaveable { mutableStateOf(null) }
    val selectedTabIndex = if (isTaskDone) 1 else 0
    val scope = rememberCoroutineScope()
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val selectedIndex by taskViewModel.selectedCategoryIndex.collectAsState()
    var showTaskCompletedAnimation by remember { mutableStateOf(false) }
    val resetSelectionMode = {
        isActionModeActive = false
        selectedItems.clear()
    }
    var taskToDelete by remember { mutableStateOf<TaskModel?>(null) }

    ShowSnackBarMessage(
        taskEvents = taskEvents,
        taskViewModel = taskViewModel,
        snackBarHostState = snackBarHostState,
        isSnackBarShow = isSnackBarShow,
        onDismissSnackBar = { isSnackBarShow = false }
    )
    Scaffold(
        topBar = {
            CustomTopAppBar(
                taskViewModel = taskViewModel,
                navController = navController,
                searchAppBarState = searchAppBarState,
                isListViewEnable = isListViewEnable,
                onListViewToggle = { isListViewEnable = it },
                settingViewModel = settingViewModel,
                onOpenDrawer = onOpenDrawer,
                snackBarHostState = snackBarHostState,
                selectedItems = selectedItems,
                isActionModeActive = isActionModeActive,
                onActionModeActiveChange = { isActive ->
                    isActionModeActive = isActive
                },
                onSelectAllToggle = {
                    if (selectedItems.size == tasks.size) {
                        selectedItems.clear()
                    } else {
                        selectedItems.clear()
                        selectedItems.addAll(tasks)
                    }
                },
                onDeleteSelectedItem = {
                    selectedItems.map {
                        taskViewModel.deleteTask(it)
                    }
                    resetSelectionMode()
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (tasks.isEmpty()) {
                    TooltipTextWithBubble()
                }
                Spacer(modifier = Modifier.height(16.dp))
                CustomFloatingActionButton(
                    onClick = {
                        navController.navigate(NavDestinations.AddTask.route)
                    },
                    taskViewModel = taskViewModel
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { paddingValue ->
        val filteredTasks: List<TaskModel> by remember(tasks, isTaskDone, selectedIndex) {
            derivedStateOf {
                val category = categoryTypes.getOrNull(selectedIndex) ?: ""
                tasks.filter { task ->
                    task.isCompleted == isTaskDone && (selectedIndex == 0 || task.category == category)
                }
            }
        }
        val (pinnedTasks, unpinnedTasks) = filteredTasks.partition { it.isPinned }
        val sortedTasks = pinnedTasks + unpinnedTasks
        LazyVerticalStaggeredGrid(
            state = lazyGridListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .background(colorScheme.background),
            contentPadding = PaddingValues(horizontal = 8.dp),
            columns = StaggeredGridCells.Fixed(
                if (isListViewEnable) {
                    when (windowSize.width) {
                        WindowType.Compact -> 2
                        WindowType.Medium -> 3
                        WindowType.Expanded -> 4
                    }
                } else {
                    when (windowSize.width) {
                        WindowType.Compact -> 1
                        WindowType.Medium -> 2
                        WindowType.Expanded -> 3
                    }
                }
            ),
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item(span = StaggeredGridItemSpan.FullLine) {
                ChipGroup(
                    items = categoryTypes,
                    selectedIndex = selectedIndex,
                    taskViewModel = taskViewModel
                ) {
                    taskViewModel.selectCategory(it)
                    taskViewModel.getCategory(categoryTypes[it])
                    taskViewModel.selectCategoryName(categoryTypes[it])
                }
            }
            if (tasks.isEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    DisplayPlaceholderImage(
                        image = Res.drawable.task_detail_placeholder,
                        text = "",
                        alpha = 0.06f,
                        isDarkMode = isDarkMode
                    )
                }
            } else {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(PaddingValues(vertical = 16.dp, horizontal = 4.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TaskTabButtons(
                            items = listOf(stringResource(Res.string.todo), stringResource(Res.string.done)),
                            selectedItemIndex = selectedTabIndex,
                            onClick = { index ->
                                isTaskDone = index == 1
                                showTaskCompletedAnimation = false
                            },
                            taskViewModel = taskViewModel
                        )
                    }
                }
                items(
                    items = sortedTasks,
                    key = { task -> task.id }
                ) { task ->
                    val isSelected = selectedItems.contains(task)
                    val checkListItem = taskViewModel.checkListMap[task.id] ?: emptyList()
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .padding(4.dp)
                    ) {
                        LaunchedEffect(task.id) {
                            taskViewModel.getCheckList(task.id)
                        }
                        TaskItem(
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        if (isActionModeActive) {
                                            if (isSelected)
                                                selectedItems.remove(task)
                                            else
                                                selectedItems.add(task)
                                        } else {
                                            taskViewModel.setTask(task)
                                            taskId = task.id.toString()
                                            navController.navigate(NavDestinations.AddTask.route + "?taskColor=${task.color}&taskId=${taskId}&onEditTask=true")
                                        }
                                    },
                                    onLongClick = {
                                        expandedTaskId = task.id
                                    }
                                )
                                .border(
                                    width = if (isSelected) 2.dp else (-1).dp,
                                    color = colorScheme.primary,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            task = task,
                            checkListModels = checkListItem,
                            settingViewModel = settingViewModel,
                            isListViewEnable = isListViewEnable,
                            isPinned = task.isPinned,
                            onSwipeEdit = {
                                taskViewModel.setTask(task)
                                taskId = task.id.toString()
                                navController.navigate(NavDestinations.AddTask.route + "?taskColor=${task.color}&taskId=${taskId}&onEditTask=true")
                            },
                            onCancelOverdue = {
                                taskViewModel.onCancelOverdue(task)
                            },
                            onCompletedTask = { isCompleted ->
                                if (isCompleted) {
                                    // mediaPlayer.start() // Placeholder for sound
                                    showTaskCompletedAnimation = true
                                }
                                taskViewModel.onTaskUpdated(task, isCompleted)
                            },
                            onSwipeDelete = { isDone ->
                                if (isDone) {
                                    taskViewModel.moveTaskToTrash(task)
                                    isSnackBarShow = true
                                }
                            },
                            categoryColors = categoryColors,
                            themeColor = Color(taskViewModel.themeColor.value.toArgb())
                        )
                        if (expandedTaskId == task.id) {
                            DropdownMenuPopup(
                                onDismissRequest = { expandedTaskId = null },
                                isPinned = task.isPinned,
                                isFavorite = task.isFavorite,
                                onPinTask = {
                                    taskViewModel.updateTask(task.copy(isPinned = !task.isPinned))
                                    expandedTaskId = null
                                },
                                onFavoriteTask = {
                                    val isFavoriteNow = !task.isFavorite
                                    taskViewModel.updateTask(task.copy(isPinned = task.isPinned, isFavorite = isFavoriteNow))
                                    expandedTaskId = null
                                    scope.launch {
                                        if (task.isFavorite) {
                                            snackBarHostState.showSnackbar("Removed from favorites")
                                        } else {
                                            snackBarHostState.showSnackbar("Added to favorites")
                                        }
                                    }
                                },
                                onDeleteTask = {
                                    expandedTaskId = null
                                    taskToDelete = task
                                    showDeleteDialog = true
                                },
                                onShareTask = {
                                    val description = if (task.isCheck) {
                                        checkListItem.joinToString(separator = "\n") { checkList ->
                                            val icon = if (checkList.checked) "\u2705" else "\u2610"
                                            "$icon ${checkList.value}"
                                        }
                                    } else {
                                        task.description
                                    }
                                    Utils.shareText(task.title + "\n" + description)
                                }
                            )
                        }
                        if (showDeleteDialog && taskToDelete != null) {
                            ShowConfirmationDialog(
                                title = stringResource(Res.string.delete_confirmation_title),
                                primaryActionText = stringResource(Res.string.delete),
                                secondaryActionText = stringResource(Res.string.cancel),
                                onConfirm = {
                                    taskToDelete?.let { task ->
                                        taskViewModel.moveTaskToTrash(task)
                                        isSnackBarShow = true
                                    }
                                    showDeleteDialog = false
                                    taskToDelete = null
                                },
                                onDismissRequest = {
                                    showDeleteDialog = false
                                },
                                taskViewModel = taskViewModel
                            )
                        }
                    }
                }
            }
        }
        if (showTaskCompletedAnimation) {
            TaskCompletedAnimation(
                onAnimationFinished = { showTaskCompletedAnimation = false }
            )
        }
    }
}

@Composable
fun TooltipTextWithBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val travelDistance = with(LocalDensity.current) { 20.dp.toPx() }

    Box(
        modifier = Modifier
            .offset(x = (-16).dp)
            .graphicsLayer { translationY = dy * travelDistance }
            .drawBubble(
                arrowWidth = 24.dp,
                arrowHeight = 20.dp,
                arrowOffset = 10.dp,
                arrowDirection = ArrowDirection.BottomRight,
                elevation = 4.dp,
                color = Violet,
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.create_task_tool_tip),
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.height(45.dp).align(Alignment.Center)
        )
    }
}

@Composable
fun TaskTabButtons(
    selectedItemIndex: Int,
    items: List<String>,
    modifier: Modifier = Modifier,
    taskViewModel: TaskViewModel,
    onClick: (index: Int) -> Unit,
) {
    val tabWidth = 100.dp // Placeholder for fixed width in KMP for now
    val indicatorOffset: Dp by animateDpAsState(
        targetValue = tabWidth * selectedItemIndex,
        animationSpec = tween(easing = LinearEasing), label = "",
    )
    Box(
        modifier = modifier
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(4.dp)
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        CustomTabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = Color(taskViewModel.themeColor.value.toArgb()),
        )
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            items.mapIndexed { index, text ->
                val isSelected = index == selectedItemIndex
                CustomTabItem(
                    isSelected = isSelected,
                    onClick = {
                        onClick(index)
                    },
                    tabWidth = tabWidth,
                    text = text,
                )
            }
        }
    }
}

@Composable
fun TaskCompletedAnimation(onAnimationFinished: () -> Unit) {
    // Placeholder for Lottie animation in KMP
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text("Animation Placeholder")
    }
    // Automatically finish for now
    LaunchedEffect(Unit) {
        delay(1000)
        onAnimationFinished()
    }
}
