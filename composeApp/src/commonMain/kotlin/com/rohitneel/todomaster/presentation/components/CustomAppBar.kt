package com.rohitneel.todomaster.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.domain.model.SearchAppBarState
import com.rohitneel.todomaster.domain.model.SortOrder
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.SelectionDialog
import com.rohitneel.todomaster.util.ShowConfirmationDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.baseline_cancel_24
import todomaster.composeapp.generated.resources.baseline_sort_24
import todomaster.composeapp.generated.resources.cancel
import todomaster.composeapp.generated.resources.date
import todomaster.composeapp.generated.resources.date_desc
import todomaster.composeapp.generated.resources.delete
import todomaster.composeapp.generated.resources.delete_selected_confirmation_title
import todomaster.composeapp.generated.resources.ic_delete
import todomaster.composeapp.generated.resources.ic_grid_view
import todomaster.composeapp.generated.resources.ic_list_view
import todomaster.composeapp.generated.resources.ic_more_select
import todomaster.composeapp.generated.resources.ic_select_all_24
import todomaster.composeapp.generated.resources.modify_categories_menu
import todomaster.composeapp.generated.resources.name
import todomaster.composeapp.generated.resources.name_desc
import todomaster.composeapp.generated.resources.outline_widgets_24
import todomaster.composeapp.generated.resources.search
import todomaster.composeapp.generated.resources.select_menu
import todomaster.composeapp.generated.resources.selected_text
import todomaster.composeapp.generated.resources.sort_menu
import todomaster.composeapp.generated.resources.sorting_option
import todomaster.composeapp.generated.resources.top_bar_title

@Composable
fun CustomTopAppBar(
    taskViewModel: TaskViewModel,
    searchAppBarState: SearchAppBarState,
    isListViewEnable: Boolean,
    onListViewToggle: (Boolean) -> Unit,
    settingViewModel: SettingViewModel,
    onOpenDrawer: () -> Unit,
    snackBarHostState: SnackbarHostState,
    selectedItems: SnapshotStateList<TaskModel>,
    isActionModeActive: Boolean,
    onActionModeActiveChange: (Boolean) -> Unit,
    onSelectAllToggle: () -> Unit,
    onDeleteSelectedItem: () -> Unit,
    navController: NavHostController
) {
    when (searchAppBarState) {
        SearchAppBarState.CLOSED -> {
            DefaultTopAppBar(
                onSearchClicked = {
                    taskViewModel.searchAppBarState.value =
                        SearchAppBarState.OPENED
                },
                isListViewEnable = isListViewEnable,
                onListViewToggle = onListViewToggle,
                taskViewModel = taskViewModel,
                settingViewModel = settingViewModel,
                onOpenDrawer = onOpenDrawer,
                snackBarHostState = snackBarHostState,
                selectedItems = selectedItems,
                isActionModeActive = isActionModeActive,
                onActionModeActiveChange = onActionModeActiveChange,
                onSelectAllToggle = onSelectAllToggle,
                onDeleteSelectedItem = onDeleteSelectedItem,
                navController = navController
            )
        }
        SearchAppBarState.OPENED -> {
            SearchTopAppBar(
                onCloseClicked = {
                    taskViewModel.searchAppBarState.value =
                        SearchAppBarState.CLOSED
                    taskViewModel.searchTextState.value = ""
                },
                taskViewModel = taskViewModel,
                isListViewEnable = isListViewEnable,
                onListViewToggle = onListViewToggle,
                settingViewModel = settingViewModel,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    onSearchClicked: () -> Unit,
    isListViewEnable: Boolean,
    onListViewToggle: (Boolean) -> Unit,
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel,
    onOpenDrawer: () -> Unit,
    snackBarHostState: SnackbarHostState,
    selectedItems: SnapshotStateList<TaskModel>,
    isActionModeActive: Boolean,
    onActionModeActiveChange: (Boolean) -> Unit,
    onSelectAllToggle: () -> Unit,
    onDeleteSelectedItem: () -> Unit,
    navController: NavHostController,
) {
    var expanded by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var isSelectMode by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = if (isSelectMode) {
                    "${selectedItems.size} ${stringResource(Res.string.selected_text)}"
                } else {
                    stringResource(Res.string.top_bar_title)
                },
                fontSize = 20.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }, 
        navigationIcon = {
            if (isSelectMode) {
                IconButton(onClick = {
                    onActionModeActiveChange(false)
                    isSelectMode = false
                    selectedItems.clear()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "menu Icon"
                    )
                }
            } else {
                IconButton(onClick = { onOpenDrawer() }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        tint = Color.White,
                        contentDescription = "menu Icon"
                    )
                }
            }
        },
        actions = {
            TopBarActions(
                isSearchBarVisible = true,
                onSearchClicked = onSearchClicked,
                isListViewEnable = isListViewEnable,
                onListViewToggle = onListViewToggle,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                showSortDialog = showSortDialog,
                onShowSortDialogChange = { showSortDialog = it },
                snackBarHostState = snackBarHostState,
                isSelectMode = isSelectMode,
                onSelectChange = { isSelectMode = it },
                selectedItems = selectedItems,
                isActionModeActive = isActionModeActive,
                onActionModeActiveChange = onActionModeActiveChange,
                onSelectAllToggle = onSelectAllToggle,
                onDeleteSelectedItem = onDeleteSelectedItem,
                taskViewModel = taskViewModel,
                settingViewModel = settingViewModel,
                navController = navController
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(Color(taskViewModel.themeColor.value.toArgb()))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    onCloseClicked: () -> Unit,
    taskViewModel: TaskViewModel,
    isListViewEnable: Boolean,
    onListViewToggle: (Boolean) -> Unit,
    settingViewModel: SettingViewModel,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }
    TopAppBar(
        title = {
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    taskViewModel.searchTasks(it)
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.search),
                        color = Color.White,
                        style = TextStyle(fontSize = 16.sp)
                    )
                },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(fontSize = 16.sp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                taskViewModel.searchTasks("")
                            }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.baseline_cancel_24),
                                contentDescription = "Cancel Icon",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onCloseClicked()
                taskViewModel.searchTasks("")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Arrow",
                    tint = Color.White
                )
            }
        },
        actions = {
            TopBarActions(
                isListViewEnable = isListViewEnable,
                onListViewToggle = onListViewToggle,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                showSortDialog = showSortDialog,
                onShowSortDialogChange = { showSortDialog = it },
                taskViewModel = taskViewModel,
                settingViewModel = settingViewModel,
                navController = navController
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(Color(taskViewModel.themeColor.value.toArgb()))
    )
}

@Composable
fun TopBarActions(
    isSearchBarVisible: Boolean = false,
    onSearchClicked: () -> Unit = {},
    isListViewEnable: Boolean,
    onListViewToggle: (Boolean) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    showSortDialog: Boolean,
    onShowSortDialogChange: (Boolean) -> Unit,
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
    isSelectMode: Boolean = false,
    onSelectChange: (Boolean) -> Unit = {},
    selectedItems: SnapshotStateList<TaskModel> = mutableStateListOf(),
    isActionModeActive: Boolean = false,
    onActionModeActiveChange: (Boolean) -> Unit = {},
    onSelectAllToggle: () -> Unit = {},
    onDeleteSelectedItem: () -> Unit = {},
    taskViewModel: TaskViewModel,
    settingViewModel: SettingViewModel,
    navController: NavHostController
) {
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (isSelectMode) {
        IconButton(
            onClick = {
                if (selectedItems.isNotEmpty()) {
                    showDeleteConfirmationDialog = true
                } else {
                    scope.launch {
                        snackBarHostState.showSnackbar("No item selected")
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = "Delete",
                tint = Color.White
            )
        }
        IconButton(onClick = { onSelectAllToggle() }) {
            Icon(
                painter = painterResource(Res.drawable.ic_select_all_24),
                contentDescription = "Select All",
                tint = Color.White
            )
        }
        if (showDeleteConfirmationDialog) {
            ShowConfirmationDialog(
                title = stringResource(Res.string.delete_selected_confirmation_title),
                primaryActionText = stringResource(Res.string.delete),
                secondaryActionText = stringResource(Res.string.cancel),
                onConfirm = {
                    onDeleteSelectedItem()
                    onSelectChange(false)
                },
                onDismissRequest = {
                    showDeleteConfirmationDialog = false
                },
                taskViewModel = taskViewModel
            )
        }
    } else {
        Row {
            if (isSearchBarVisible) {
                IconButton(onClick = { onSearchClicked() }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "search",
                        tint = Color.White
                    )
                }
            }
            IconButton(onClick = {
                onListViewToggle(!isListViewEnable)
            }) {
                Icon(
                    if (isListViewEnable)
                        painterResource(Res.drawable.ic_list_view)
                    else
                        painterResource(Res.drawable.ic_grid_view),
                    contentDescription = null,
                    tint = Color.White
                )
            }
            IconButton(onClick = { onExpandedChange(true) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more",
                    tint = Color.White
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.onPrimary)
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.modify_categories_menu)) },
                    onClick = {
                        onExpandedChange(false)
                        navController.navigate(NavDestinations.ModifyCategories.route)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.outline_widgets_24),
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.select_menu)) },
                    onClick = {
                        onExpandedChange(false)
                        onSelectChange(true)
                        onActionModeActiveChange(true)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_more_select),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.sort_menu)) },
                    onClick = {
                        onExpandedChange(false)
                        onShowSortDialogChange(true)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.baseline_sort_24),
                            contentDescription = null
                        )
                    }
                )
            }
        }
        if (showSortDialog) {
            SortSelectionDialog(
                onDismissRequest = { onShowSortDialogChange(false) },
                taskViewModel = taskViewModel,
                selectedSortOption = settingViewModel.selectedSortOption.value,
                settingViewModel = settingViewModel
            )
        }
    }
}

@Composable
fun SortSelectionDialog(
    onDismissRequest: () -> Unit,
    taskViewModel: TaskViewModel,
    selectedSortOption: String,
    settingViewModel: SettingViewModel
) {
    val sortOptions = listOf(
        stringResource(Res.string.date_desc) to SortOrder.BY_DATE_DESC,
        stringResource(Res.string.date) to SortOrder.BY_DATE,
        stringResource(Res.string.name) to SortOrder.BY_NAME,
        stringResource(Res.string.name_desc) to SortOrder.BY_NAME_DESC,
    )
    val initialOption = sortOptions.firstOrNull { it.first == selectedSortOption } ?: sortOptions[0]
    var selectedOption by remember { mutableStateOf(initialOption) }

    SelectionDialog(
        title = stringResource(Res.string.sorting_option),
        options = sortOptions,
        selectedOption = selectedOption,
        onOptionSelected = { newOption ->
            selectedOption = newOption
        },
        onDismissRequest = onDismissRequest,
        onConfirm = {
            taskViewModel.updateSortOrder(selectedOption.second)
            settingViewModel.onSortOptionChange(selectedOption.first)
            onDismissRequest()
        },
        taskViewModel = taskViewModel
    )
}
