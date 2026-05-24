package com.rohitneel.todomaster.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.domain.model.LanguageFlag
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.theme.Poppins
import com.rohitneel.todomaster.presentation.viewmodel.SettingViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.AppConstants.TimeDurations
import com.rohitneel.todomaster.util.AppConstants.TimeUnits
import org.jetbrains.compose.resources.DrawableResource
import org.koin.compose.viewmodel.koinViewModel

import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    settingViewModel: SettingViewModel = koinViewModel(),
    taskViewModel: TaskViewModel = koinViewModel()
) {
    val selectedLanguage by settingViewModel.selectedLanguage.collectAsState()
    val selectedDuration by settingViewModel.snoozeDuration.collectAsState()
    val isDarkMode by settingViewModel.isDarkMode.collectAsState()
    val swipeGestureEnable by settingViewModel.swipeGestureEnable.collectAsState()
    val reminderEnable by settingViewModel.reminderEnable.collectAsState()
    val completionToneEnable by settingViewModel.completionToneEnable.collectAsState()
    val vibrateEnable by settingViewModel.vibrateEnable.collectAsState()
    
    val selectedDurationInMinutes = selectedDuration / 60000
    val duration = if (selectedDurationInMinutes >= 60) {
        val selectedDurationInHours = selectedDurationInMinutes / 60
        "$selectedDurationInHours Hour"
    } else {
        "$selectedDurationInMinutes Minutes"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.settings),
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
                colors = TopAppBarDefaults.topAppBarColors(taskViewModel.themeColor.value)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSection(title = stringResource(Res.string.general), taskViewModel) {
                    SettingItem(title = stringResource(Res.string.dark), description = stringResource(Res.string.enable), icon = Res.drawable.baseline_dark_24, isChecked = isDarkMode, onCheckedChange = { settingViewModel.onDarkModeChange(it) })
                    SettingItem(title = stringResource(Res.string.swipe_gesture), description = stringResource(Res.string.enable), icon = Res.drawable.baseline_dark_24, isChecked = swipeGestureEnable, onCheckedChange = { settingViewModel.onSwipeGestureChange(it) })
                    SettingItem(title = stringResource(Res.string.reminder_state), description = stringResource(Res.string.enable), icon = Res.drawable.ic_reminder, isChecked = reminderEnable, onCheckedChange = { settingViewModel.onReminderStateChange(it) })
                    SettingItem(title = stringResource(Res.string.language), description = selectedLanguage, icon = Res.drawable.ic_reminder)
                }
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSection(title = stringResource(Res.string.sounds), taskViewModel) {
                    SettingItem(title = stringResource(Res.string.vibrate), description = stringResource(Res.string.enable), icon = Res.drawable.ic_reminder, isChecked = vibrateEnable, onCheckedChange = { settingViewModel.onVibrateChange(it) })
                    SettingItem(title = stringResource(Res.string.snooze), description = duration, icon = Res.drawable.ic_reminder)
                }
                Spacer(modifier = Modifier.height(10.dp))
                SettingsSection(title = stringResource(Res.string.about), taskViewModel) {
                    SettingItem(title = stringResource(Res.string.privacy_policy), description = "Access policy", icon = Res.drawable.ic_reminder) {
                        navController.navigate(NavDestinations.PrivacyPolicy.route)
                    }
                    SettingItem(title = stringResource(Res.string.version), version = "v1.0.0", icon = Res.drawable.ic_reminder)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    taskViewModel: TaskViewModel,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Bold,
            color = taskViewModel.themeColor.value,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp)
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp), content = content)
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String? = null,
    version: String? = null,
    icon: DrawableResource,
    isChecked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onPrimary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                Column (modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins
                    )
                    if (description != null) {
                        Text(
                            text = description,
                            fontSize = 12.sp,
                            fontFamily = Poppins,
                            color = Color.Gray,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (isChecked != null && onCheckedChange != null) {
                    Switch(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange,
                    )
                } else if (version != null) {
                    Text(
                        text = version,
                        fontSize = 14.sp,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
