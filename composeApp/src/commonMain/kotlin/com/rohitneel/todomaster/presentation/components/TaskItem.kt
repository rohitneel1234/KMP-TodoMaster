package com.rohitneel.todomaster.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.presentation.theme.AppColors.White
import com.rohitneel.todomaster.presentation.theme.Clarendon
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.util.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.baseline_calendar_month_24
import todomaster.composeapp.generated.resources.baseline_cancel_24
import todomaster.composeapp.generated.resources.baseline_delete_forever_24
import todomaster.composeapp.generated.resources.baseline_notifications_24
import todomaster.composeapp.generated.resources.ic_alarm
import todomaster.composeapp.generated.resources.ic_checked
import todomaster.composeapp.generated.resources.ic_edit
import todomaster.composeapp.generated.resources.ic_filled_sharp_star_24
import todomaster.composeapp.generated.resources.ic_pin
import todomaster.composeapp.generated.resources.ic_unchecked
import todomaster.composeapp.generated.resources.overdue
import kotlin.math.abs

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: TaskModel,
    checkListModels: List<CheckListModel>,
    settingViewModel: SettingViewModel,
    isListViewEnable: Boolean,
    onSwipeEdit: (Boolean) -> Unit,
    onCancelOverdue: () -> Unit,
    onCompletedTask: (Boolean) -> Unit,
    onSwipeDelete: (Boolean) -> Unit,
    isPinned: Boolean,
    isTrashScreen: Boolean = false,
    categoryColors: Map<String, Color>,
    themeColor: Color,
) {
    val globalSwipeGestureEnable by settingViewModel.swipeGestureEnable.collectAsState()
    val swipeGestureEnabled = remember { isTrashScreen.not() && globalSwipeGestureEnable }
    val isSwipeGestureEnable by rememberUpdatedState(swipeGestureEnabled)
    val showReminder by settingViewModel.reminderEnable.collectAsState()
    var isChecked by rememberSaveable(task.id) { mutableStateOf(task.isCompleted) }
    var show by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    val threshold by remember { mutableStateOf(100.dp) }
    var offset by remember { mutableFloatStateOf(0f) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (isSwipeGestureEnable && (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart)) {
                show = false
                true
            } else
                false
        }
    )
    val offsetMatch by remember { derivedStateOf { abs(offset) >= with(density) { threshold.toPx() } } }
    LaunchedEffect(dismissState) {
        snapshotFlow {
            dismissState.offset.value
        }
            .drop(1)
            .collect { newOffset ->
                offset = newOffset
            }
    }

    if (isSwipeGestureEnable) {
        AnimatedVisibility(
            visible = show,
            exit = fadeOut(spring())
        ) {
            SwipeToDismiss(
                modifier = modifier
                    .animateContentSize(),
                state = dismissState,
                background = {
                    DismissBackground(dismissState, offsetMatch)
                },
                dismissContent = {
                    SwipeItem(
                        modifier,
                        task,
                        checkListModels,
                        isListViewEnable,
                        isChecked,
                        isPinned,
                        isTrashScreen,
                        showReminder,
                        onCheckedChange = {
                            isChecked = it
                        },
                        onCompletedTask = {
                            onCompletedTask(it)
                        },
                        onSwipeDelete = {
                            onSwipeDelete(it)
                        },
                        onCancelOverdue = onCancelOverdue,
                        categoryColors = categoryColors,
                        themeColor = themeColor
                    )
                },
            )
        }
    } else {
        SwipeItem(
            modifier,
            task,
            checkListModels,
            isListViewEnable,
            isChecked,
            isPinned,
            isTrashScreen,
            showReminder,
            onCheckedChange = {
                isChecked = it
            },
            onCompletedTask = {
                onCompletedTask(it)
            },
            onSwipeDelete = {
                onSwipeDelete(it)
            },
            onCancelOverdue = onCancelOverdue,
            categoryColors = categoryColors,
            themeColor = themeColor
        )
    }

    if (!show) {
        LaunchedEffect(dismissState.dismissDirection) {
            delay(200)
            when (dismissState.dismissDirection) {
                DismissDirection.EndToStart -> {
                    onSwipeDelete(true)
                    show = true
                }
                DismissDirection.StartToEnd -> {
                    withContext(Dispatchers.Main) {
                        onSwipeEdit(true)
                        show = true
                    }
                }
                else -> { /* No action needed */ }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SwipeItem(
    modifier: Modifier,
    task: TaskModel,
    checkListModels: List<CheckListModel>,
    isListViewEnable: Boolean,
    isChecked: Boolean,
    isPinned: Boolean,
    isTrashScreen: Boolean,
    showReminder: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onCompletedTask: (Boolean) -> Unit,
    onSwipeDelete: (Boolean) -> Unit,
    onCancelOverdue: () -> Unit,
    categoryColors: Map<String, Color>,
    themeColor: Color,
) {
    val taskBackgroundBrush = task.gradientColor?.takeIf { it.isNotEmpty() }
        ?.let { Brush.horizontalGradient(it.map { Color(it).takeUnless { it == Color.White } ?: MaterialTheme.colorScheme.onPrimary }) }
        ?: SolidColor(Color(task.color).takeUnless { it == Color.White } ?: MaterialTheme.colorScheme.onPrimary)
    Column(
        modifier
            .fillMaxWidth()
            .background(
                color = categoryColors[task.category] ?: MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    bottomStart = 8.dp,
                    topEnd = 10.dp,
                    bottomEnd = 10.dp
                )
            )
            .then(
                if ((task.color == White.toArgb() && task.gradientColor?.firstOrNull()?.let { Color(it) } == White)
                        || (taskBackgroundBrush == SolidColor(White) || task.gradientColor?.firstOrNull()?.let { Color(it) } == White)) {
                    Modifier.border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .padding(start = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = taskBackgroundBrush,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
        ) {
            if (isPinned) {
                Icon(
                    painter = painterResource(Res.drawable.ic_pin),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 4.dp)
                )
            }
            val tempModifier =
                if (isListViewEnable) Modifier.fillMaxWidth() else Modifier
            Row(tempModifier) {
                isTrashScreen.takeIf { it }?.let {
                    Spacer(modifier = Modifier.width(4.dp))
                } ?: run {
                    CircleCheckbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            onCompletedTask(it)
                            onCheckedChange(it)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = task.title,
                                fontFamily = Clarendon,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (task.isCheck) {
                                checkListModels.forEach { checkList ->
                                    DisplayCheckListItem(checkList, task)
                                }
                            } else {
                                if (task.description.isNotEmpty()) {
                                    LinkableText(
                                        task = task,
                                        description = task.description,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(color = themeColor)
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.baseline_calendar_month_24),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = DateUtil.getDate(task.creationDate!!),
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                AnimatedVisibility(task.isFavorite) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_filled_sharp_star_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                if (task.isFavorite && showReminder && task.reminder != null) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                AnimatedVisibility(showReminder && task.reminder != null) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_alarm),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                task.dueDate?.takeIf { dueDate ->
                                    !task.isCompleted && DateUtil.getTimeDiff(dueDate) < 0
                                }?.let {
                                    Text(
                                        text = stringResource(Res.string.overdue),
                                        fontSize = 12.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_cancel_24),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.clickable { onCancelOverdue() }
                                    )
                                } ?: run {
                                    task.dueDate?.let { dueDate ->
                                        Text(
                                            text = DateUtil.getTime(dueDate),
                                            fontSize = 12.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                        Icon(
                                            painter = painterResource(Res.drawable.baseline_notifications_24),
                                            contentDescription = "",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.clickable { onCancelOverdue() }
                                        )
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        ) {
                            if (task.isCompleted) {
                                IconButton(
                                    onClick = {
                                        onSwipeDelete(true)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.baseline_delete_forever_24),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DismissBackground(dismissState: DismissState, offsetMatch: Boolean) {
    val color by animateColorAsState(
        targetValue = when (dismissState.dismissDirection) {
            DismissDirection.EndToStart -> if (offsetMatch) Color(0xffff6347) else Color(0xFF2ED573)
            DismissDirection.StartToEnd -> if (offsetMatch) Color(0xFF039BE5) else Color(0xFF2ED573)
            null ->  Color.Transparent
        },
        label = ""
    )
    val rotationAngle by animateFloatAsState(
        targetValue = if (offsetMatch) -45f else 0f,
        label = ""
    )
    val direction = dismissState.dismissDirection

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = color,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (direction == DismissDirection.StartToEnd) {
            Icon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = "Edit",
                modifier = Modifier.graphicsLayer { rotationZ = rotationAngle }
            )
        }
        Spacer(modifier = Modifier)
        if (direction == DismissDirection.EndToStart) {
            Icon(
                painter = painterResource(Res.drawable.baseline_delete_forever_24),
                contentDescription = "Delete",
                modifier = Modifier.graphicsLayer { rotationZ = rotationAngle }
            )
        }
    }
}

@Composable
fun CircleCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeColor: Color = AppColors.VibrantBlue,
) {
    val icon =
        if (checked) painterResource(Res.drawable.ic_checked)
        else painterResource(Res.drawable.ic_unchecked)
    val tint =
        if (checked) activeColor.copy(alpha = 0.8f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
    val background = if (checked) MaterialTheme.colorScheme.surface else Color.Transparent

    IconButton(
        onClick = { onCheckedChange(!checked) },
        modifier = modifier.offset(x = 4.dp, y = 4.dp),
        enabled = enabled
    ) {
        Icon(
            icon,
            tint = tint,
            modifier = Modifier.background(background, shape = CircleShape),
            contentDescription = "checkbox"
        )
    }
}
