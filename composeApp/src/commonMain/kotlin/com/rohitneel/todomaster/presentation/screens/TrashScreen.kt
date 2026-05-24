package com.rohitneel.todomaster.presentation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.components.TaskItem
import com.rohitneel.todomaster.presentation.events.TaskDetailEvent
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.DisplayPlaceholderImage
import com.rohitneel.todomaster.util.DropdownMenuPopup
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import com.rohitneel.todomaster.util.ShowSnackBarMessage
import com.rohitneel.todomaster.util.Utils
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TrashScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val selectedItems = remember { mutableStateListOf<TaskModel>() }
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleteAllTask by remember { mutableStateOf(false) }
    var isSnackBarShow by rememberSaveable { mutableStateOf(false) }
    var taskId: String? by rememberSaveable { mutableStateOf(null) }
    val tasksInTrash by taskViewModel.tasksInTrash.collectAsState()
    val categoryColors by taskViewModel.categoryColors.collectAsState(initial = emptyMap())
    val isDarkMode by settingViewModel.isDarkMode.collectAsState()
    val taskEvents by taskViewModel.tasksEvent.collectAsState(TaskDetailEvent.Initial)
    var deleteAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    LaunchedEffect(Unit) {
        taskViewModel.refreshTrashScreen()
    }
    ShowSnackBarMessage(
        taskEvents = taskEvents,
        taskViewModel = taskViewModel,
        snackBarHostState = snackBarHostState,
        isSnackBarShow = isSnackBarShow,
        message = stringResource(Res.string.task_restored),
        taskAction = TaskViewModel.TaskAction.REDO,
        onDismissSnackBar = { isSnackBarShow = false }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.trash),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (tasksInTrash.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                showDeleteDialog = true
                                isDeleteAllTask = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_delete),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Color(taskViewModel.themeColor.value.toArgb()))
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { paddingValue ->
        if (tasksInTrash.isEmpty()) {
            DisplayPlaceholderImage(
                image = Res.drawable.empty_trash_placeholder,
                text = stringResource(Res.string.trash_title),
                description = stringResource(Res.string.trash_description),
                alpha = 0.30f,
                contentScale = ContentScale.Fit,
                isFixedSize = true,
                isDarkMode = isDarkMode,
                isTrashScreen = true
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValue),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                items(tasksInTrash) { task ->
                    val isSelected = selectedItems.contains(task)
                    val checkListItem = taskViewModel.checkListMap[task.id] ?: emptyList()
                    Row(
                        Modifier
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
                                        taskViewModel.setTask(task)
                                        taskId = task.id.toString()
                                        navController.navigate(NavDestinations.AddTask.route + "?taskColor=${task.color}&taskId=${taskId}&onEditTask=true")
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
                            isListViewEnable = false,
                            isPinned = false,
                            isTrashScreen = true,
                            onSwipeEdit = {
                                taskViewModel.setTask(task)
                                taskId = task.id.toString()
                                navController.navigate(NavDestinations.AddTask.route + "?taskColor=${task.color}&taskId=${taskId}&onEditTask=true")
                            },
                            onCancelOverdue = {
                                taskViewModel.onCancelOverdue(task)
                            },
                            onCompletedTask = { isCompleted ->
                                taskViewModel.onTaskUpdated(task, isCompleted)
                            },
                            onSwipeDelete = { isDone ->
                                if (isDone) {
                                    taskViewModel.onTaskSwiped(task)
                                    isSnackBarShow = true
                                }
                            },
                            categoryColors = categoryColors,
                            themeColor = Color(taskViewModel.themeColor.value.toArgb())
                        )
                        if (expandedTaskId == task.id) {
                            DropdownMenuPopup(
                                onDismissRequest = { expandedTaskId = null },
                                isPinned = false,
                                isFavorite = task.isFavorite,
                                isTaskInTrash = true,
                                onPinTask = {
                                    taskViewModel.updateTask(task.copy(isPinned = !task.isPinned))
                                    expandedTaskId = null
                                },
                                onFavoriteTask = {
                                    taskViewModel.updateTask(task.copy(isFavorite = !task.isFavorite))
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
                                    deleteAction = {
                                        taskViewModel.onTaskSwiped(task)
                                    }
                                    expandedTaskId = null
                                    showDeleteDialog = true
                                },
                                onRestoreTask = {
                                    taskViewModel.onUndoDeleteClick(task)
                                    isSnackBarShow = true
                                    expandedTaskId = null
                                },
                                onShareTask = {
                                    Utils.shareText(task.title)
                                }
                            )
                        }
                        if (showDeleteDialog) {
                            ShowConfirmationDialog(
                                title = if (isDeleteAllTask) stringResource(Res.string.delete_all_tasks_confirmation_title)
                                else stringResource(Res.string.delete_permanently_confirmation_title),
                                message = if (isDeleteAllTask) stringResource(Res.string.delete_all_tasks_confirmation_message) else "",
                                primaryActionText = stringResource(Res.string.delete),
                                secondaryActionText = stringResource(Res.string.cancel),
                                onConfirm = {
                                    if (isDeleteAllTask) taskViewModel.deleteAllTaskInTrash(tasksInTrash)
                                    else deleteAction?.invoke()
                                },
                                onDismissRequest = {
                                    isDeleteAllTask = false
                                    showDeleteDialog = false
                                },
                                taskViewModel = taskViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
