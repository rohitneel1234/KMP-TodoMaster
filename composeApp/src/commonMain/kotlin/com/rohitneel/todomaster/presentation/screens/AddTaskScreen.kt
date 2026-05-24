package com.rohitneel.todomaster.presentation.screens

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.BottomSheetType
import com.rohitneel.todomaster.domain.model.FontFamilyOption
import com.rohitneel.todomaster.domain.model.FontProperties
import com.rohitneel.todomaster.presentation.components.CheckListItem
import com.rohitneel.todomaster.presentation.events.TaskEvent
import com.rohitneel.todomaster.presentation.theme.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import org.jetbrains.compose.resources.Font
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.Clock

import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AddTaskScreen(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    taskColor: Int,
    taskId: String,
    onEditTask: MutableState<Boolean>,
) {
    val settingViewModel: SettingViewModel = koinViewModel()
    val snoozeDuration by settingViewModel.snoozeDuration.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false
    )
    val taskBackgroundAnimatable = remember {
        Animatable(Color(if (taskColor != -1) taskColor else taskViewModel.taskColor.value))
    }
    val taskBackgroundBrush = if (taskViewModel.isGradientSelected.value) {
        Brush.horizontalGradient( taskViewModel.taskGradientColor.value.map {
            it.takeIf { color -> color != AppColors.White } ?: MaterialTheme.colorScheme.onPrimary })
    } else {
        SolidColor(Color(taskViewModel.taskColor.value).takeIf { it != AppColors.White }
            ?: MaterialTheme.colorScheme.onPrimary)
    }
    val containerColor = when (taskBackgroundBrush) {
        is SolidColor -> taskBackgroundBrush.value
        else -> Color.Unspecified
    }
    val snackBarHostState = remember { SnackbarHostState() }
    val task by taskViewModel.taskFlow.collectAsState()
    val currentBottomSheet = remember { mutableStateOf<BottomSheetType?>(null) }
    var selectedFontName by rememberSaveable { mutableStateOf(taskViewModel.fontFamily) }
    
    val textUnitSaver = Saver<TextUnit, Float>(
        save = { it.value },
        restore = { it.sp }
    )
    val selectedFontFamily = FontFamily.Default // Placeholder
    var selectedFontSize by rememberSaveable(stateSaver = textUnitSaver) { mutableStateOf(taskViewModel.fontSize.sp) }
    val selectedTextStyle = MaterialTheme.typography.titleSmall.copy(
        fontWeight = if (taskViewModel.fontStyleModel.isBold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (taskViewModel.fontStyleModel.isItalic) FontStyle.Italic else FontStyle.Normal,
        color = if (taskViewModel.fontStyleModel.textColorSelected) Color.Red else MaterialTheme.colorScheme.onSurface,
        textAlign = if (taskViewModel.fontStyleModel.alignmentSelected) TextAlign.Center else TextAlign.Start,
        textDecoration = if (taskViewModel.fontStyleModel.isUnderlined) TextDecoration.Underline else TextDecoration.None
    )
    var toggleUpperCase by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val titleFieldFocused = remember { mutableStateOf(false) }
    var voiceInput by remember { mutableStateOf("") }
    
    val voiceLauncher = Utils.voiceRecognizerLauncher { result ->
        voiceInput = result
    }
    
    LaunchedEffect(onEditTask.value) {
        if (onEditTask.value) {
            taskViewModel.getTaskById(taskId.toInt())
            taskViewModel.getCheckList(taskId.toInt())
        } else {
            taskBackgroundAnimatable.snapTo(TaskModel.taskColors.first())
            taskViewModel.resetTask()
            selectedFontName = AppConstants.DEFAULT
            selectedFontSize = AppConstants.DEFAULT_FONT_SIZE.sp
        }
        snapshotFlow { task }
            .filterNotNull()
            .collect { task ->
                if (onEditTask.value) {
                    taskViewModel.title = task.title
                    taskViewModel.description = task.description
                    taskViewModel.isCheck = task.isCheck
                    taskViewModel.category = task.category
                    taskViewModel.dueDate = task.dueDate
                    taskViewModel.reminder = task.reminder
                    task.gradientColor?.takeIf { it.isNotEmpty() }
                        ?.let { colors -> taskViewModel.onEvent(TaskEvent.ChangeGradient(colors.map { Color(it) })) }
                        ?: taskViewModel.onEvent(TaskEvent.ChangeColor(task.color))
                    taskViewModel.fontFamily = task.fontFamily
                    taskViewModel.fontSize = task.fontSize
                    selectedFontName = task.fontFamily
                    selectedFontSize = task.fontSize.sp
                    taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(task.fontStyleModel))
                    taskViewModel.getCheckList(task.id)
                }
            }
    }

    LaunchedEffect(key1 = true) {
        taskViewModel.eventFlow.collectLatest { event ->
            when(event) {
                is TaskViewModel.UiEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (sheetState.isVisible) {
            keyboardController?.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (currentBottomSheet.value) {
                BottomSheetType.COLOR -> ColorSelectionBottomSheet(
                    taskViewModel = taskViewModel,
                    scope = scope,
                    taskBackgroundAnimatable = taskBackgroundAnimatable,
                    taskBackgroundBrush = taskBackgroundBrush
                )
                BottomSheetType.FONT -> FontSelectionBottomSheet(
                    selectedFontName = selectedFontName,
                    selectedFontSize = selectedFontSize,
                    onFontSelected = { fontName ->
                        selectedFontName = fontName
                    },
                    onFontSizeSelected = { fontSize ->
                        selectedFontSize = fontSize
                    },
                    fontProperties = FontProperties(
                        isBold = taskViewModel.fontStyleModel.isBold,
                        isItalic = taskViewModel.fontStyleModel.isItalic,
                        isUnderlined = taskViewModel.fontStyleModel.isUnderlined,
                        alignmentSelected = taskViewModel.fontStyleModel.alignmentSelected,
                        textColorSelected = taskViewModel.fontStyleModel.textColorSelected,
                        isUpperCase = taskViewModel.fontStyleModel.isUpperCase,
                        onBoldClick = {
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(isBold = !taskViewModel.fontStyleModel.isBold)))
                        },
                        onItalicClick = {
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(isItalic = !taskViewModel.fontStyleModel.isItalic)))
                        },
                        onUnderlineClick = {
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(isUnderlined = !taskViewModel.fontStyleModel.isUnderlined)))
                        },
                        onTextColorClick = {
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(textColorSelected = !taskViewModel.fontStyleModel.textColorSelected)))
                        },
                        onCenterAlignClick = {
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(alignmentSelected = !taskViewModel.fontStyleModel.alignmentSelected)))
                        },
                        onUpperCaseClick = {
                            toggleUpperCase = !toggleUpperCase
                            taskViewModel.onEvent(TaskEvent.ChangeFontStyleModel(taskViewModel.fontStyleModel.copy(isUpperCase = !taskViewModel.fontStyleModel.isUpperCase)))
                        }
                    )
                )
                else -> {}
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(taskViewModel.themeColor.value),
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
                    title = {
                        Text(
                            text = if (onEditTask.value) "Edit Task" else "New Task",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = containerColor,
                    actions = {
                        IconButton(
                            onClick = {
                                currentBottomSheet.value = BottomSheetType.COLOR
                                scope.launch { sheetState.show() }
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.outline_color_lens_32),
                                contentDescription = "colors"
                            )
                        }
                        IconButton(
                            onClick = {
                                if (!titleFieldFocused.value) {
                                    currentBottomSheet.value = BottomSheetType.FONT
                                    scope.launch { sheetState.show() }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.sharp_match_case_36),
                                contentDescription = "fonts",
                                tint = if (titleFieldFocused.value) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = {
                                taskViewModel.toggleCheckBoxVisibility(task?.id ?: 0)
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.outline_checkbox_32),
                                contentDescription = "checkbox"
                            )
                        }
                        IconButton(
                            onClick = {
                                voiceLauncher()
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_mic_32),
                                contentDescription = "voice input"
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                if (taskViewModel.category.isEmpty() || taskViewModel.category == "Category") {
                                    taskViewModel.category = AppConstants.ALL
                                }
                                if (onEditTask.value) {
                                    task?.let {
                                        taskViewModel.updateTask(
                                            it.copy(
                                                title = taskViewModel.title,
                                                description = taskViewModel.description,
                                                isCheck = taskViewModel.isCheck,
                                                color = taskBackgroundAnimatable.value.toArgb(),
                                                gradientColor = taskViewModel.taskGradientColor.value.takeIf { taskViewModel.isGradientSelected.value }
                                                    ?.let { gradientColor -> taskViewModel.convertColorsToInts(gradientColor) },
                                                creationDate = taskViewModel.dueDate ?: Clock.System.now().toEpochMilliseconds(),
                                                dueDate = taskViewModel.dueDate,
                                                category = taskViewModel.category,
                                                reminder = taskViewModel.reminder,
                                                fontFamily = selectedFontName,
                                                fontSize = selectedFontSize.value,
                                                fontStyleModel = taskViewModel.fontStyleModel
                                            )
                                        )
                                    }
                                    if (taskViewModel.isCheck) {
                                        val checkable = taskViewModel.checkListModels.map {
                                            it.copy(taskId = taskId.toInt())
                                        }
                                        taskViewModel.updateCheckList(checkable)
                                    }
                                } else {
                                    taskViewModel.fontFamily = selectedFontName
                                    taskViewModel.fontSize = selectedFontSize.value
                                    taskViewModel.onEvent(TaskEvent.SaveTask)
                                }
                                if (taskViewModel.title.isNotEmpty()) {
                                    navController.navigateUp()
                                }
                            },
                            containerColor = taskViewModel.themeColor.value
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Save",
                                tint = Color.White
                            )
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            }
        ) {
            AddEditTaskContent(
                task = task,
                taskViewModel = taskViewModel,
                modifier = Modifier
                    .background(taskBackgroundBrush)
                    .padding(it),
                onEditTask = onEditTask.value,
                selectedFontFamily = selectedFontFamily,
                selectedFontSize = selectedFontSize,
                selectedTextStyle = selectedTextStyle,
                onTitleFieldFocusChange = { focused ->
                    titleFieldFocused.value = focused
                    if (focused) {
                        scope.launch { sheetState.hide() }
                    }
                },
                toggleUpperCase = toggleUpperCase,
                voiceInput = voiceInput,
                snackBarHostState = snackBarHostState,
                snoozeDuration = snoozeDuration
            )
        }
    }
}

@Composable
fun AddEditTaskContent(
    taskViewModel: TaskViewModel,
    modifier: Modifier,
    onEditTask: Boolean,
    task: TaskModel?,
    selectedFontFamily: FontFamily,
    selectedFontSize: TextUnit,
    selectedTextStyle: TextStyle,
    onTitleFieldFocusChange: (Boolean) -> Unit,
    toggleUpperCase: Boolean,
    voiceInput: String,
    snackBarHostState: SnackbarHostState,
    snoozeDuration: Long
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val categoryTypes by taskViewModel.categoryTypes.collectAsState(initial = emptyList())
    val selectedCategory by taskViewModel.selectedCategory
    var selectedItem by rememberSaveable { mutableStateOf(selectedCategory) }

    var setReminder by remember { mutableStateOf(task?.reminder != null) }
    var setDueDate by remember { mutableStateOf(task?.dueDate != null) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    
    var voiceInputText by remember { mutableStateOf("") }
    if (selectedItem != AppConstants.ALL && selectedItem != "Category") {
        taskViewModel.category = selectedItem
    }
    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }
    LaunchedEffect(taskViewModel.reminder, taskViewModel.dueDate) {
        setReminder = taskViewModel.reminder != null
        setDueDate = taskViewModel.dueDate != null
    }
    LaunchedEffect(voiceInput) {
        if (voiceInput.isNotBlank()) {
            voiceInputText = voiceInput
            taskViewModel.description = voiceInput
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_category),
                    contentDescription = "category",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                expanded = true
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (onEditTask) taskViewModel.category else selectedItem,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "arrow down",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.requiredSizeIn(maxHeight = 300.dp).background(color = MaterialTheme.colorScheme.onPrimary)
                ) {
                    categoryTypes.forEach { item ->
                        val isSelected = remember(selectedItem) { item == selectedItem }
                        DropdownMenuItem(
                            text = { Text(text = item) },
                            onClick = {
                                taskViewModel.category = item
                                selectedItem = item
                                expanded = false
                            },
                            modifier = Modifier.background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(Res.drawable.outline_today_24),
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                Utils.handleDateTimeClick(
                                    onEditTask = onEditTask,
                                    isDueDate = true,
                                    task = task,
                                    snoozeDuration = snoozeDuration,
                                    taskViewModel = taskViewModel,
                                    snackBarHostState = snackBarHostState,
                                    coroutineScope = coroutineScope,
                                    workerTag = AppConstants.OVERDUE_WORK_MANAGER_TAG
                                )
                            }
                        ),
                    contentDescription = "due date",
                    tint = if (onEditTask && setDueDate) taskViewModel.themeColor.value else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(Res.drawable.ic_time),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(18.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                Utils.handleDateTimeClick(
                                    onEditTask = onEditTask,
                                    isDueDate = false,
                                    task = task,
                                    snoozeDuration = snoozeDuration,
                                    taskViewModel = taskViewModel,
                                    snackBarHostState = snackBarHostState,
                                    coroutineScope = coroutineScope,
                                    workerTag = AppConstants.REMINDER_WORK_MANAGER_TAG
                                )
                            }
                        ),
                    contentDescription = "reminder",
                    tint = if (onEditTask && setReminder) taskViewModel.themeColor.value else MaterialTheme.colorScheme.onSurface
                )
            }
            TextField(
                value = taskViewModel.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        onTitleFieldFocusChange(focusState.isFocused)
                    },
                onValueChange = { taskViewModel.title = it },
                placeholder = {
                    Text(
                        text = "Title",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 22.sp,
                    )
                },
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = Clarendon
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    imeAction = ImeAction.Next,
                ),
            )
        }
        if (taskViewModel.isCheck) {
            items(
                items = taskViewModel.checkListModels,
                key = { it.uid }
            ) {
                CheckListItem(
                    item = it,
                    task = task,
                    taskViewModel = taskViewModel,
                    selectedFontFamily = selectedFontFamily,
                    selectedFontSize =  selectedFontSize,
                    selectedTextStyle = selectedTextStyle,
                    toggleUpperCase = toggleUpperCase,
                    voiceInputText = voiceInputText,
                )
            }
            item {
                TextButton(
                    modifier = Modifier.padding(start = 32.dp),
                    onClick = {
                        taskViewModel.onAddCheckListItem(taskViewModel.checkListModels.firstOrNull()?.taskId ?: 0)
                        voiceInputText = ""
                    }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "", tint = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Add Item", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        } else {
            item {
                OutlinedTextField(
                    value = Utils.formatText(
                            taskViewModel.description.replace("\\n", "\n"),
                            taskViewModel.fontStyleModel.isUpperCase,
                            toggleUpperCase
                        ),
                    onValueChange = {
                        voiceInputText = it
                        taskViewModel.description = it
                    },
                    placeholder = { Text("Description") },
                    textStyle = selectedTextStyle.copy(
                        fontSize = selectedFontSize,
                        fontFamily = selectedFontFamily
                    ),
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrectEnabled = true
                    )
                )
            }
        }
    }

}

@Composable
fun ColorSelectionBottomSheet(
    taskViewModel: TaskViewModel,
    scope: CoroutineScope,
    taskBackgroundAnimatable: Animatable<Color, AnimationVector4D>,
    taskBackgroundBrush: Brush,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(taskBackgroundBrush)
            .padding(vertical = 20.dp, horizontal = 12.dp)
    ) {
        Text(
            text = "Pick Color",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val boxWidth = maxWidth / TaskModel.taskColors.size
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TaskModel.taskColors.forEach { color ->
                    val colorInt = color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(boxWidth * 0.9f)
                            .shadow(15.dp, CircleShape)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = 3.dp,
                                color = if (taskViewModel.taskColor.value == colorInt && !taskViewModel.isGradientSelected.value) {
                                    Color.Black
                                } else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    taskBackgroundAnimatable.animateTo(
                                        targetValue = Color(colorInt),
                                        animationSpec = tween(
                                            durationMillis = 100
                                        )
                                    )
                                    taskViewModel.onEvent(TaskEvent.ChangeColor(colorInt))
                                }
                            }
                    )
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Gradient",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(gradients) { gradient ->
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .run {
                            if (gradient.first() == AppColors.White) {
                                this.background(Color(0xFFF0F0F0))
                            } else {
                                this.background(Brush.horizontalGradient(gradient))
                            }
                        }
                        .border(
                            width = 2.dp,
                            color = if (taskViewModel.taskGradientColor.value == gradient && taskViewModel.isGradientSelected.value) {
                                Color.Black
                            } else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            scope.launch {
                                taskViewModel.onEvent(TaskEvent.ChangeGradient(gradient))
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun FontSelectionBottomSheet(
    selectedFontName: String,
    selectedFontSize: TextUnit,
    onFontSelected: (String) -> Unit,
    onFontSizeSelected: (TextUnit) -> Unit,
    fontProperties: FontProperties,
) {
    val textUnitSaver = Saver<TextUnit, Float>(
        save = { it.value },
        restore = { it.sp }
    )
    var fontSize by rememberSaveable(stateSaver = textUnitSaver) { mutableStateOf(selectedFontSize) }
    val fontFamilyOptions = listOf(
        FontFamilyOption(AppConstants.DEFAULT, Res.font.default_font_family),
        FontFamilyOption(AppConstants.SOUTHERN_BOLD, Res.font.southern_bold),
        FontFamilyOption(AppConstants.MEDIUM, Res.font.gotham_medium),
        FontFamilyOption(AppConstants.SERPENTINE, Res.font.serpentine_bold_italic),
        FontFamilyOption(AppConstants.MONTSERRAT, Res.font.montserrat_semibold),
        FontFamilyOption(AppConstants.ABRILFAT, Res.font.abril_fatface_regular)
    )
    val fontSizeList = listOf(10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30)
    var selectedFontFamilyOption by rememberSaveable { mutableStateOf(fontFamilyOptions.find { it.name == selectedFontName } ?: fontFamilyOptions.first())}

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        EditorControls(fontProperties = fontProperties)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(fontSizeList, key = { it }) { size ->
                val isSelected = size.sp == fontSize
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                fontSize = size.sp
                                onFontSizeSelected(fontSize)
                            }
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "$size",
                        modifier = Modifier
                            .align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = size.sp),
                        textAlign = TextAlign.Center,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        fontFamilyOptions.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { fontFamilyOption ->
                    val isSelected = selectedFontFamilyOption == fontFamilyOption
                    Card(
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    selectedFontFamilyOption = fontFamilyOption
                                    onFontSelected(fontFamilyOption.name)
                                }
                            ),
                        elevation = CardDefaults.cardElevation(2.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.app_name),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily(Font(fontFamilyOption.fontFamily)),
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}


@Composable
fun EditorControls(fontProperties: FontProperties) {
    var boldSelected by rememberSaveable { mutableStateOf(fontProperties.isBold) }
    var italicSelected by rememberSaveable { mutableStateOf(fontProperties.isItalic) }
    var underlineSelected by rememberSaveable { mutableStateOf(fontProperties.isUnderlined) }
    var textColorSelected by rememberSaveable { mutableStateOf(fontProperties.textColorSelected) }
    var alignmentSelected by rememberSaveable { mutableStateOf(fontProperties.alignmentSelected) }
    var upperCaseSelected by rememberSaveable { mutableStateOf(fontProperties.isUpperCase) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlWrapper(
                selected = boldSelected,
                onChangeClick = { boldSelected = it },
                onClick = { fontProperties.onBoldClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_format_bold_24),
                    contentDescription = "Bold Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            ControlWrapper(
                selected = italicSelected,
                onChangeClick = { italicSelected = it },
                onClick = { fontProperties.onItalicClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_format_italic_24),
                    contentDescription = "Italic Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            ControlWrapper(
                selected = underlineSelected,
                onChangeClick = { underlineSelected = it },
                onClick = { fontProperties.onUnderlineClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_format_underlined_24),
                    contentDescription = "Underline Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            ControlWrapper(
                selected = textColorSelected,
                onChangeClick = { textColorSelected = it },
                onClick = { fontProperties.onTextColorClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_text_format_color_24),
                    contentDescription = "Text Color Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            ControlWrapper(
                selected = alignmentSelected,
                onChangeClick = { alignmentSelected = it },
                onClick = { fontProperties.onCenterAlignClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.baseline_format_align_center_24),
                    contentDescription = "Center Align Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            ControlWrapper(
                selected = upperCaseSelected,
                onChangeClick = { upperCaseSelected = it },
                onClick = { fontProperties.onUpperCaseClick() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.outline_uppercase_24),
                    contentDescription = "Upper Case Control",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ControlWrapper(
    selected: Boolean,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onChangeClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(size = 6.dp))
            .clickable {
                onClick()
                onChangeClick(!selected)
            }
            .background(
                if (selected) selectedColor
                else unselectedColor
            )
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(size = 6.dp)
            )
            .padding(all = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
