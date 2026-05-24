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
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import com.rohitneel.todomaster.util.DisplayPlaceholderImage
import com.rohitneel.todomaster.util.DropdownMenuPopup
import com.rohitneel.todomaster.util.ShowSnackBarMessage
import com.rohitneel.todomaster.util.Utils
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReminderScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val selectedItems = remember { mutableStateListOf<TaskModel>() }
    var expandedTaskId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isSnackBarShow by rememberSaveable { mutableStateOf(false) }
    var taskId: String? by rememberSaveable { mutableStateOf(null) }
    val taskEvents by taskViewModel.tasksEvent.collectAsState(TaskDetailEvent.Initial)
    val tasksWithReminder by taskViewModel.taskReminder.collectAsState()
    val categoryColors by taskViewModel.categoryColors.collectAsState(initial = emptyMap())
    val isDarkMode by settingViewModel.isDarkMode.collectAsState()

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
                    Text(
                        text = stringResource(Res.string.reminders),
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
                colors = TopAppBarDefaults.topAppBarColors(Color(taskViewModel.themeColor.value.toArgb()))
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { paddingValue ->
        val filteredTasks = tasksWithReminder.filter { it.reminder != null }
        if (filteredTasks.isEmpty()) {
            DisplayPlaceholderImage(
                image = Res.drawable.task_reminder_placeholder,
                text = stringResource(Res.string.reminder_placeholder),
                alpha = 0.15f,
                isDarkMode = isDarkMode
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValue),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                items(filteredTasks) { task ->
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
                                isPinned = false,
                                isFavorite = task.isFavorite,
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
                                    expandedTaskId = null
                                    showDeleteDialog = true
                                },
                                onShareTask = {
                                    Utils.shareText(task.title)
                                }
                            )
                        }
                        if (showDeleteDialog) {
                            ShowConfirmationDialog(
                                title = stringResource(Res.string.delete_confirmation_title),
                                primaryActionText = stringResource(Res.string.delete),
                                secondaryActionText = stringResource(Res.string.cancel),
                                onConfirm = {
                                    taskViewModel.moveTaskToTrash(task)
                                    showDeleteDialog = false
                                    isSnackBarShow = true
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
    }
}
