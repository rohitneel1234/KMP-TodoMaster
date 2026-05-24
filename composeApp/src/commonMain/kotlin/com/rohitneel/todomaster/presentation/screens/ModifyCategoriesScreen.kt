package com.rohitneel.todomaster.presentation.screens

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.presentation.components.DraggableItem
import com.rohitneel.todomaster.presentation.components.rememberDragDropState
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.ShowCategoryDialog
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ModifyCategoriesScreen(
    taskViewModel: TaskViewModel,
    navController: NavHostController
) {
    val taskCounts by taskViewModel.taskCounts.collectAsState()
    val totalTasksCount = taskCounts.values.sum()
    val categoryTypes by taskViewModel.categoryTypes.collectAsState(initial = emptyList())
    val categoryColors by taskViewModel.categoryColors.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var expandedCategoryMenu by remember { mutableStateOf<String?>(null) }
    var categoryToRename by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }
    var isRenaming by remember { mutableStateOf(false) }
    var overscrollJob by remember { mutableStateOf<Job?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        val updatedCategoryTypes = categoryTypes.toMutableList().apply {
            add(toIndex, removeAt(fromIndex))
        }
        val updatedColors = updatedCategoryTypes.associateWith { categoryColors[it] ?: Color.Gray }
        taskViewModel.updateCategories(updatedCategoryTypes, updatedColors)
    }
    LaunchedEffect(Unit) {
        scope.launch {
            snackBarHostState.showSnackbar(
                message = "Long press and drag to reorder categories",
                duration = SnackbarDuration.Short
            )
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.categories),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(dragDropState) {
                        detectDragGesturesAfterLongPress(
                            onDrag = { change, offset ->
                                change.consume()
                                dragDropState.onDrag(offset = offset)
                                if (overscrollJob?.isActive == true)
                                    return@detectDragGesturesAfterLongPress
                                dragDropState
                                    .checkForOverScroll()
                                    .takeIf { it != 0f }
                                    ?.let {
                                        overscrollJob =
                                            scope.launch {
                                                dragDropState.state.animateScrollBy(
                                                    it * 1.3f, tween(easing = FastOutLinearInEasing)
                                                )
                                            }
                                    } ?: run { overscrollJob?.cancel() }
                            },
                            onDragStart = { offset ->
                                dragDropState.onDragStart(offset)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragEnd = {
                                dragDropState.onDragInterrupted()
                                overscrollJob?.cancel()
                            },
                            onDragCancel = {
                                dragDropState.onDragInterrupted()
                                overscrollJob?.cancel()
                            }
                        )
                    },
                state = listState,
            ) {
                itemsIndexed(categoryTypes) { index, category ->
                    val itemColor = categoryColors[category] ?: Color.Gray
                    val categoryTaskCount = taskCounts[category] ?: 0
                    DraggableItem(
                        dragDropState = dragDropState,
                        index = index,
                        modifier = Modifier
                    ) { _ ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(itemColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = if (category == AppConstants.ALL) "$totalTasksCount" else "$categoryTaskCount",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(24.dp))
                            Box {
                                IconButton(
                                    modifier = Modifier.size(24.dp),
                                    onClick = { expandedCategoryMenu = category }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                DropdownMenu(
                                    expanded = expandedCategoryMenu == category,
                                    onDismissRequest = { expandedCategoryMenu = null },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .background(color = MaterialTheme.colorScheme.onPrimary)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(Res.string.rename_menu)) },
                                        onClick = {
                                            expandedCategoryMenu = null
                                            categoryToRename = category
                                            isRenaming = true
                                            showCategoryDialog = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(Res.drawable.baseline_edit_24),
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(Res.string.delete_menu)) },
                                        onClick = {
                                            expandedCategoryMenu = null
                                            categoryToDelete = category
                                            showDeleteDialog = true
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(Res.drawable.baseline_delete_forever_24),
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                            if (showDeleteDialog && categoryToDelete == category) {
                                ShowConfirmationDialog(
                                    title = stringResource(Res.string.delete_confirmation_category_title),
                                    message = stringResource(Res.string.delete_confirmation_category_message),
                                    primaryActionText = stringResource(Res.string.delete),
                                    secondaryActionText = stringResource(Res.string.cancel),
                                    onConfirm = {
                                        taskViewModel.deleteCategory(category)
                                        showDeleteDialog = false
                                        categoryToDelete = null
                                    },
                                    onDismissRequest = {
                                        showDeleteDialog = false
                                        categoryToDelete = null
                                    },
                                    taskViewModel = taskViewModel
                                )
                            }
                        }
                    }
                }
            }
            SnackbarHost(
                hostState = snackBarHostState,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    isRenaming = false
                    showCategoryDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(Color(taskViewModel.themeColor.value.toArgb()))
            ) {
                Text(text = stringResource(Res.string.add_category), color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (showCategoryDialog) {
                ShowCategoryDialog(
                    onDismissRequest = { showCategoryDialog = false },
                    onButtonClick = { newCategory ->
                        taskViewModel.category = newCategory
                        if (isRenaming) {
                            taskViewModel.renameCategory(categoryToRename, newCategory)
                        } else {
                            taskViewModel.addCategory(newCategory)
                        }
                        showCategoryDialog = false
                    },
                    taskViewModel = taskViewModel,
                    initialCategory = if (isRenaming) categoryToRename else null,
                    dialogTitle = if (isRenaming) stringResource(Res.string.rename_category) else stringResource(Res.string.new_category)
                )
            }
        }
    }
}
