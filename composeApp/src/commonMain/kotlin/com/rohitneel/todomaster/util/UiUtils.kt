package com.rohitneel.todomaster.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import com.rohitneel.todomaster.presentation.events.TaskDetailEvent
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.presentation.theme.AppColors.PrimaryColor
import com.rohitneel.todomaster.presentation.theme.AppColors.SecondaryColor
import com.rohitneel.todomaster.presentation.theme.AppColors.SubTextColor
import com.rohitneel.todomaster.presentation.theme.Poppins
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@Composable
fun CustomTabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(indicatorWidth)
            .offset(x = indicatorOffset)
            .clip(RoundedCornerShape(8.dp))
            .background(indicatorColor)
    )
}

@Composable
fun CustomTabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    text: String,
) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Black,
        animationSpec = tween(easing = LinearEasing), label = "",
    )
    Box(
        modifier = Modifier
            .width(tabWidth)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = tabTextColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CustomFloatingActionButton(
    onClick: () -> Unit,
    taskViewModel: TaskViewModel
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Color(taskViewModel.themeColor.value.toArgb()),
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Task",
            tint = Color.White
        )
    }
}

@Composable
fun ShowTaskStatusMessage(statusMessage: String) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for image
        Text(
            text = statusMessage,
            fontFamily = Poppins,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun ShowSnackBarMessage(
    taskEvents: TaskDetailEvent,
    taskViewModel: TaskViewModel,
    snackBarHostState: SnackbarHostState,
    isSnackBarShow: Boolean,
    message: String = "",
    taskAction: TaskViewModel.TaskAction = TaskViewModel.TaskAction.UNDO,
    onDismissSnackBar: () -> Unit
) {
    if (isSnackBarShow) {
        LaunchedEffect(Unit) {
            val result = snackBarHostState.showSnackbar(
                message = message,
                actionLabel = taskAction.name,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                when (taskEvents) {
                    is TaskDetailEvent.ShowUndoDeleteTaskMessage -> {
                        if (taskAction == TaskViewModel.TaskAction.UNDO) {
                           taskViewModel.onUndoDeleteClick(taskEvents.task)
                        } else {
                           taskViewModel.onRedoTrashClick(taskEvents.task)
                        }
                    }
                    else -> {}
                }
            }
            onDismissSnackBar()
        }
    }
}

@Composable
fun DropdownMenuPopup(
    onDismissRequest: () -> Unit,
    isPinned: Boolean = false,
    isFavorite: Boolean,
    isTaskInTrash: Boolean = false,
    onPinTask: () -> Unit = {},
    onFavoriteTask: () -> Unit = {},
    onDeleteTask: () -> Unit = {},
    onShareTask: () -> Unit = {},
    onRestoreTask: () -> Unit = {},
    isPinVisible: Boolean = true
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(x = (-16).dp, y = 0.dp),
        properties = PopupProperties(focusable = true)
    ) {
        if (!isTaskInTrash) {
            if (isPinVisible) {
                DropdownMenuItem(
                    text = { Text(if (isPinned) "Unpin" else "Pin") },
                    onClick = {
                        onPinTask()
                        onDismissRequest()
                    }
                )
            }
            DropdownMenuItem(
                text = { Text(if (isFavorite) "Remove Favorites" else "Add to Favorites") },
                onClick = {
                    onFavoriteTask()
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                text = { Text("Share") },
                onClick = {
                    onShareTask()
                    onDismissRequest()
                }
            )
        } else {
            DropdownMenuItem(
                text = { Text("Restore") },
                onClick = {
                    onRestoreTask()
                    onDismissRequest()
                }
            )
        }
        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                onDeleteTask()
                onDismissRequest()
            }
        )
    }
}

@Composable
fun ShowConfirmationDialog(
    title: String,
    primaryActionText: String,
    secondaryActionText: String,
    message: String = "",
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    taskViewModel: TaskViewModel
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { if (message.isNotEmpty()) Text(text = message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(primaryActionText, color = Color(taskViewModel.themeColor.value.toArgb()))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(secondaryActionText, color = Color(taskViewModel.themeColor.value.toArgb()))
            }
        }
    )
}

@Composable
fun <T> SelectionDialog(
    title: String,
    options: List<Pair<String, T>>,
    selectedOption: Pair<String, T>,
    onOptionSelected: (Pair<String, T>) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    taskViewModel: TaskViewModel
) {
    var selected by remember { mutableStateOf(selectedOption) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp)
                ) {
                    items(options) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .selectable(
                                    selected = (option == selected),
                                    onClick = { selected = option }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (option == selected),
                                onClick = { selected = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(taskViewModel.themeColor.value.toArgb()),
                                    unselectedColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = option.first, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(taskViewModel.themeColor.value.toArgb())
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            onOptionSelected(selected)
                            onConfirm()
                        }
                    ) {
                        Text(
                            text = stringResource(Res.string.select),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(taskViewModel.themeColor.value.toArgb())
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressBar(percentage: Float) {
    val animatedPercentage = remember { Animatable(0f) }
    LaunchedEffect(percentage) {
        animatedPercentage.animateTo(
            targetValue = percentage,
            animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
        )
    }
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
        ) {
            drawCircle(
                brush = SolidColor(Color(0xFFE3E5E7)),
                radius = size.width / 2,
                style = Stroke(34f)
            )
            val convertedValue = (animatedPercentage.value / 100) * 360
            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(SecondaryColor, PrimaryColor)
                ),
                startAngle = -90f,
                sweepAngle = convertedValue,
                useCenter = false,
                style = Stroke(34f, cap = StrokeCap.Round)
            )
        }
        val annotatedString = AnnotatedString.Builder()
            .apply {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("${animatedPercentage.value.toInt()}%")
                }
                append("\n")
                withStyle(
                    style = SpanStyle(
                        color = SubTextColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("Done")
                }
            }
        Text(
            text = annotatedString.toAnnotatedString(),
            fontFamily = Poppins,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DisplayPlaceholderImage(
    image: DrawableResource,
    text: String,
    description: String = "",
    alpha: Float,
    contentScale: ContentScale = ContentScale.Crop,
    isFixedSize: Boolean = false,
    isDarkMode: Boolean,
    isTrashScreen: Boolean = false
) {
    val colorFilter = ColorFilter.tint(AppColors.AmberYellow).takeIf { isDarkMode } ?: ColorFilter.tint(AppColors.RedOrange).takeIf { isTrashScreen }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(image),
                contentDescription = "No tasks",
                contentScale = contentScale,
                colorFilter = colorFilter,
                modifier = Modifier.then(if (isFixedSize) Modifier.size(140.dp) else Modifier.fillMaxWidth())
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun ProgressBarDialog(progressMessage: String, progress: Float, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = progressMessage,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun CustomDialog(
    onDismissRequest: () -> Unit,
    onButtonClick: () -> Unit,
    taskViewModel: TaskViewModel
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(taskViewModel.themeColor.value.toArgb()),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Checked",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.sync_dialog_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(taskViewModel.themeColor.value.toArgb()))
                ) {
                    Text(text = stringResource(Res.string.got_it), color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun ShowCategoryDialog(
    onDismissRequest: () -> Unit,
    onButtonClick: (String) -> Unit,
    taskViewModel: TaskViewModel,
    initialCategory: String? = null,
    dialogTitle: String = "New Category"
) {
    var textFieldValue by remember { mutableStateOf(initialCategory ?: "") }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = dialogTitle,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    placeholder = { Text(stringResource(Res.string.enter_category_placeholder)) },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = true
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = stringResource(Res.string.cancel),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(taskViewModel.themeColor.value.toArgb())
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            if (textFieldValue.isNotBlank()) {
                                onButtonClick(textFieldValue.trim())
                            }
                        }
                    ) {
                        Text(
                            text = if (initialCategory != null) stringResource(Res.string.rename) else stringResource(Res.string.add),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(taskViewModel.themeColor.value.toArgb())
                        )
                    }
                }
            }
        }
    }
}
