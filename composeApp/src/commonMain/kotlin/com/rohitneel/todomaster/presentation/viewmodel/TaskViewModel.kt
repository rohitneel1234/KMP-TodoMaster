package com.rohitneel.todomaster.presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitneel.todomaster.data.model.CheckListModel
import com.rohitneel.todomaster.data.model.InvalidTaskException
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.FontStyleModel
import com.rohitneel.todomaster.domain.model.SearchAppBarState
import com.rohitneel.todomaster.domain.model.SortOrder
import com.rohitneel.todomaster.domain.usecase.CheckListUseCases
import com.rohitneel.todomaster.domain.usecase.TaskUseCases
import com.rohitneel.todomaster.presentation.TaskScheduler
import com.rohitneel.todomaster.presentation.events.TaskDetailEvent
import com.rohitneel.todomaster.presentation.events.TaskEvent
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.presentation.theme.gradients
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.datapreferences.DataStorePreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskUseCase: TaskUseCases,
    private val checkListUseCase: CheckListUseCases,
    private val dataStorePreferenceManager : DataStorePreferenceManager,
    private val taskScheduler: TaskScheduler
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder = MutableStateFlow(SortOrder.BY_DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var isCheck by mutableStateOf(false)
    var dueDate by mutableStateOf<Long?>(null)
    var category by mutableStateOf("")
    var reminder by mutableStateOf<Long?>(null)
    var fontFamily by mutableStateOf(AppConstants.DEFAULT)
    var fontSize by mutableFloatStateOf(16f)
    var fontStyleModel by mutableStateOf(
        FontStyleModel(
            isBold = false,
            isItalic = false,
            isUnderlined = false,
            alignmentSelected = false,
            textColorSelected = false,
            isUpperCase = false
        )
    )

    private val _taskFlow = MutableStateFlow<TaskModel?>(null)
    val taskFlow get() = _taskFlow.asStateFlow()

    private val _taskColor = mutableIntStateOf(TaskModel.taskColors.first().toArgb())
    val taskColor: State<Int> = _taskColor

    private val _taskGradientColor = mutableStateOf(gradients.first())
    val taskGradientColor: State<List<Color>> = _taskGradientColor

    private val _isGradientSelected = mutableStateOf(false)
    val isGradientSelected: State<Boolean> = _isGradientSelected

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val taskEventChannel = Channel<TaskDetailEvent>()
    val tasksEvent = taskEventChannel.receiveAsFlow()

    val searchAppBarState: MutableState<SearchAppBarState> = mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    private val _selectedCategoryIndex = MutableStateFlow(0)
    val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex

    private val _selectedCategory = mutableStateOf("Category")
    val selectedCategory: State<String> = _selectedCategory

    private val _selectedTask = MutableStateFlow<TaskModel?>(null)
    val selectedTask: StateFlow<TaskModel?> = _selectedTask

    private val _taskCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val taskCounts: StateFlow<Map<String, Int>> = _taskCounts.asStateFlow()

    private val _taskReminder = MutableStateFlow<List<TaskModel>>(emptyList())
    val taskReminder: StateFlow<List<TaskModel>> = _taskReminder

    private val _favoriteTasks = MutableStateFlow<List<TaskModel>>(emptyList())
    val favoriteTasks: StateFlow<List<TaskModel>> = _favoriteTasks

    var themeColor = mutableStateOf(AppColors.VibrantBlue)
        private set
    var themeImage = mutableStateOf(0) // Placeholder
        private set

    private val _currentFocusRequestId = mutableStateOf(-1L)
    val currentFocusRequestId: State<Long> = _currentFocusRequestId

    private var _checkListModels = mutableStateListOf<CheckListModel>()
    val checkListModels: List<CheckListModel> get() = _checkListModels

    private val newId: Long
        get() = 0L // Need a multiplatform way for unique ID if timestamp is not enough

    private val _checkListMap = mutableStateMapOf<Int, List<CheckListModel>>()
    val checkListMap: SnapshotStateMap<Int, List<CheckListModel>> = _checkListMap

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String?> = mutableStateOf(null)
    val startDestination: State<String?> = _startDestination

    private val _categoryTypes = MutableStateFlow<List<String>>(emptyList())
    val categoryTypes: StateFlow<List<String>> = _categoryTypes.asStateFlow()

    private val _categoryColors = MutableStateFlow<Map<String, Color>>(emptyMap())
    val categoryColors: StateFlow<Map<String, Color>> = _categoryColors

    private var recentlyDeletedCheckList: List<CheckListModel>? = null

    var isPaused by mutableStateOf(false)
        private set

    var isReset by mutableStateOf(false)
        private set

    private val _tasksInTrash = MutableStateFlow<List<TaskModel>>(emptyList())
    val tasksInTrash: StateFlow<List<TaskModel>> = _tasksInTrash

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<TaskModel>> = searchQuery
        .combine(sortOrder) { query, order ->
            Pair(query, order)
        }
        .combine(selectedCategoryIndex) { (query, order), categoryIndex ->
            Triple(query, order, categoryIndex)
        }
        .flatMapLatest { (query, order, categoryIndex) ->
            val category = _categoryTypes.value.getOrNull(categoryIndex) ?: ""
            if (query.isEmpty()) {
                if (category == AppConstants.ALL) {
                    taskUseCase.getTask("", order)
                } else {
                    taskUseCase.getTaskByCategory(category)
                }
            } else {
                taskUseCase.getTaskByName(query)
            }
        }
        .map { taskList ->
            taskList.filter { it.deletedAt == null }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        viewModelScope.launch {
            dataStorePreferenceManager.readOnBoardingState().collect { completed ->
                if (completed) {
                    _startDestination.value = NavDestinations.TaskDetail.route // Need to migrate NavDestinations
                } else {
                    _startDestination.value =  NavDestinations.Onboarding.route
                }
                delay(AppConstants.SPLASH_DELAY)
                _isLoading.value = false
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            taskUseCase.getTask("", SortOrder.BY_DATE_DESC).collect { tasks ->
                updateTaskCounts(tasks)
            }
        }
        viewModelScope.launch {
            dataStorePreferenceManager.themeColorFlow.collect { (color, imageRes)  ->
                themeColor.value = color
                themeImage.value = imageRes
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            taskUseCase.getTaskByReminder().collect { result ->
                _taskReminder.value = result.filter { it.deletedAt == null }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            taskUseCase.getFavoriteTasks().collect { result ->
                _favoriteTasks.value = result.filter { it.deletedAt == null }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            dataStorePreferenceManager.categoriesFlow.collect { (categories, colorMapping) ->
                _categoryTypes.emit(categories)
                _categoryColors.emit(colorMapping)
            }
        }
        refreshTasksInTrash()
    }

    fun onEvent(event: TaskEvent) {
        when(event) {
            is TaskEvent.ChangeColor -> {
                _taskColor.intValue = event.color
                _isGradientSelected.value = false
            }
            is TaskEvent.ChangeGradient -> {
                _taskGradientColor.value = event.gradientColor
                _isGradientSelected.value = true
            }
            is TaskEvent.ChangeFontStyleModel -> {
                fontStyleModel = event.fontStyleModel
            }
            is TaskEvent.SaveTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val taskId = taskUseCase.insertTask(
                            TaskModel(
                                id = 0,
                                title = title,
                                description = description,
                                isCheck = isCheck,
                                creationDate = dueDate ?: 0L, // Need System.currentTimeMillis() in KMP
                                dueDate = dueDate,
                                color = taskColor.value.takeIf { !_isGradientSelected.value } ?: -1,
                                gradientColor = taskGradientColor.value.takeIf { _isGradientSelected.value }?.let { convertColorsToInts(it) },
                                category = category,
                                reminder = reminder,
                                fontFamily = fontFamily,
                                fontSize = fontSize,
                                fontStyleModel = fontStyleModel
                            )
                        )
                        if (isCheck) {
                            val checkable = checkListModels.map {
                                it.copy(taskId = taskId.toInt())
                            }
                            checkListUseCase.insertCheckList(checkable)
                        }
                    } catch(e: InvalidTaskException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                message = e.message ?: "Couldn't save task"
                            )
                        )
                    }
                }
            }
            else -> {}
        }
    }

    fun onUndoDeleteClick(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
        taskScheduler.cancelTaskExpirationCleanup(task.id)
        taskUseCase.insertTask(task.copy(deletedAt = null))
        recentlyDeletedCheckList?.let { checkList ->
            checkListUseCase.insertCheckList(checkList.map { it.copy(taskId = task.id) })
            _checkListModels.clear()
            _checkListModels.addAll(checkList)
            _checkListMap[task.id] = checkList
        }
        _tasksInTrash.emit(_tasksInTrash.value.filterNot { it.id == task.id })
        taskEventChannel.send(TaskDetailEvent.ShowUndoDeleteTaskMessage(task))
        recentlyDeletedCheckList = null
    }

    fun onTaskSwiped(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
        taskScheduler.cancelTaskExpirationCleanup(task.id)
        recentlyDeletedCheckList = checkListUseCase.getCheckList(task.id)
        checkListUseCase.deleteCheckList(task.id.toLong())
        taskUseCase.deleteTask(task)
    }

    fun onTaskUpdated(task: TaskModel, isChecked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        taskUseCase.updateTask(task.copy(isCompleted = isChecked))
    }

    fun searchTasks(query: String) {
        _searchQuery.value = query
    }

    fun getTaskById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _taskFlow.value = taskUseCase.getTaskByID(id)
        }
    }

    fun setTask(task: TaskModel) {
        _taskFlow.value = task
    }

    fun selectCategory(index: Int) {
        _selectedCategoryIndex.value = index
    }

    fun selectCategoryName(categoryName: String) {
        _selectedCategory.value = categoryName
        category = categoryName
    }

    fun getCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskUseCase.getTaskByCategory(category)
        }
    }

    fun setSelectedTask(task: TaskModel) {
        _selectedTask.value = task
    }

    private fun updateTaskCounts(tasks: List<TaskModel>) {
        val counts = _categoryTypes.value.associateWith { category ->
            tasks.count { it.category == category }
        }
        _taskCounts.value = counts
    }

    fun updateTask(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
       taskUseCase.updateTask(task)
    }

    fun deleteTask(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
        taskUseCase.deleteTask(task)
    }

    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
        searchTasks(searchQuery.value)
    }

    fun updateThemeColor(color: Color, imageRes: Int) {
        viewModelScope.launch {
            dataStorePreferenceManager.setThemeColor(color, imageRes)
        }
    }

    fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStorePreferenceManager.saveOnBoardingState(completed = completed)
        }
    }

    fun toggleCheckBoxVisibility(taskId: Int) {
        isCheck = !isCheck
        if (isCheck) {
            onAddCheckListItem(taskId)
        }
    }

    suspend fun getCheckList(taskId: Int): List<CheckListModel> {
        _checkListModels.clear()
        val checkListItem = checkListUseCase.getCheckList(taskId)
        _checkListMap[taskId] = checkListItem.filter { it.value.isNotEmpty() }
        _checkListModels.addAll(_checkListMap[taskId].orEmpty())
        return checkListItem
    }

    fun updateCheckList(checkListModel: List<CheckListModel>) = viewModelScope.launch(Dispatchers.IO) {
        val newCheckable = checkListModel.filter { it.id == 0L }
        if (newCheckable.isNotEmpty()) {
            checkListUseCase.insertCheckList(newCheckable)
        }
        val existingCheckable = checkListModel.filter { it.id != 0L }
        if (existingCheckable.isNotEmpty()) {
            checkListUseCase.updateCheckList(existingCheckable)
        }
    }

    fun onAddCheckListItem(taskId: Int, item: CheckListModel? = null) {
        val id = 0L // Placeholder
        val checkListModel = CheckListModel(uid = id, taskId = taskId, value = "", checked = false)
        _currentFocusRequestId.value = id
        if (item == null) {
            _checkListModels.add(checkListModel)
        } else {
            val index = _checkListModels.indexOfFirst { it.uid == item.uid }
            if (index > -1) {
                _checkListModels.add(index + 1, checkListModel)
            } else {
                _checkListModels.add(checkListModel)
            }
        }
    }

    fun onCheckListItemCheck(item: CheckListModel, checked: Boolean) {
        val index = _checkListModels.indexOfFirst { item.uid == it.uid }
        if (index >= 0) {
            _checkListModels[index] = item.copy(checked = checked)
        }
    }

    fun onDeleteCheckListItem(item: CheckListModel) {
        _checkListModels.remove(item)
        viewModelScope.launch(Dispatchers.IO) {
            checkListUseCase.deleteCheckList(item.id)
        }
    }

    fun onCheckListItemValueChange(item: CheckListModel, value: String) {
        val index = _checkListModels.indexOfFirst { it.uid == item.uid }
        if (index >= 0) {
            _checkListModels[index] = item.copy(value = value)
        }
    }

    fun onFocusRequest(item: CheckListModel) {
        _currentFocusRequestId.value = item.uid
    }

    fun refreshTrashScreen() {
        refreshTasksInTrash()
    }

    fun deleteAllTaskInTrash(tasksInTrash: List<TaskModel>) = viewModelScope.launch(Dispatchers.IO) {
        tasksInTrash.forEach { task ->
            taskScheduler.cancelTaskExpirationCleanup(task.id)
        }
        taskUseCase.deleteAllTaskInTrash()
    }

    fun onCancelOverdue(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
        dueDate = null
        updateTask(task.copy(dueDate = null))
        taskScheduler.cancelOverdueWorkRequest(AppConstants.OVERDUE_WORK_MANAGER_TAG)
    }

    fun moveTaskToTrash(task: TaskModel) = viewModelScope.launch {
        handleTaskStateUpdate(task) { updatedTask ->
            taskEventChannel.send(TaskDetailEvent.ShowUndoDeleteTaskMessage(updatedTask))
        }
    }

    fun onRedoTrashClick(task: TaskModel) = viewModelScope.launch(Dispatchers.IO) {
        handleTaskStateUpdate(task)
    }

    private suspend fun handleTaskStateUpdate(
        task: TaskModel,
        onTaskUpdated: suspend (TaskModel) -> Unit = {}
    ) {
        recentlyDeletedCheckList = checkListUseCase.getCheckList(task.id)
        checkListUseCase.deleteCheckList(task.id.toLong())
        val updatedTask = task.copy(deletedAt = 0L) // Placeholder for current time
        taskUseCase.updateTask(updatedTask)
        onTaskUpdated(updatedTask)
        // Schedule cleanup
        refreshTasksInTrash()
    }

    private fun refreshTasksInTrash() = viewModelScope.launch(Dispatchers.IO) {
        taskUseCase.getTaskInTrash(0L).collect { tasks ->
            _tasksInTrash.emit(tasks)
        }
    }

    fun convertColorsToInts(colors: List<Color>): List<Int> {
        return colors.map { it.toArgb() }
    }

    fun addCategory(newCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!_categoryTypes.value.contains(newCategory)) {
                val updatedCategories = _categoryTypes.value + newCategory
                _categoryTypes.emit(updatedCategories)
                val newColors = Color.Blue // Simplified
                val updatedColorMap = _categoryColors.value.toMutableMap().apply {
                    this[newCategory] = newColors
                }
                _categoryColors.emit(updatedColorMap)
                dataStorePreferenceManager.saveCategories(updatedCategories, updatedColorMap)
                updateTaskCounts(tasks.value)
            }
        }
    }

    fun updateCategories(updatedCategories: List<String>, updatedColors: Map<String, Color>) {
        viewModelScope.launch(Dispatchers.IO) {
            _categoryTypes.emit(updatedCategories)
            _categoryColors.emit(updatedColors)
            dataStorePreferenceManager.saveCategories(updatedCategories, updatedColors)
        }
    }

    fun deleteCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tasksToDelete = taskUseCase.getTaskByCategory(category).first()
            if (tasksToDelete.isNotEmpty()) {
                tasksToDelete.forEach { task ->
                    taskUseCase.deleteTask(task)
                }
            }
            val updatedCategories = _categoryTypes.value.filter { it != category }
            _categoryTypes.emit(updatedCategories)
            val updatedColorMap = _categoryColors.value.toMutableMap().apply {
                remove(category)
            }
            _categoryColors.emit(updatedColorMap)
            dataStorePreferenceManager.saveCategories(updatedCategories, updatedColorMap)
        }
    }

    fun renameCategory(oldCategory: String, newCategory: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_categoryTypes.value.contains(oldCategory)) {
                val updatedCategories = _categoryTypes.value.map {
                    if (it == oldCategory) newCategory else it
                }
                _categoryTypes.emit(updatedCategories)
                val updatedColorMap = _categoryColors.value.toMutableMap().apply {
                    val categoryColor = this[oldCategory]
                    remove(oldCategory)
                    if (categoryColor != null) {
                        this[newCategory] = categoryColor
                    }
                }
                _categoryColors.emit(updatedColorMap)
                dataStorePreferenceManager.saveCategories(updatedCategories, updatedColorMap)
                updateTaskCounts(tasks.value)
            }
        }
    }

    fun startTimer() {
        isPaused = false
    }

    fun pauseTimer() {
        isPaused = true
    }

    fun resetTimer() {
        isPaused = false
        isReset = true
    }

    fun resetTask() {
        title = ""
        description = ""
        isCheck = false
        category = ""
        dueDate = null
        reminder = null
        _taskColor.intValue = TaskModel.taskColors.first().toArgb()
        _taskGradientColor.value = gradients.first()
        _isGradientSelected.value = false
        fontFamily = AppConstants.DEFAULT
        fontSize = 16f
        fontStyleModel = FontStyleModel()
        _currentFocusRequestId.value = -1L
        _checkListModels.clear()
        _checkListMap.clear()
    }

    enum class TaskAction {
        UNDO, REDO
    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String): UiEvent()
    }
}
