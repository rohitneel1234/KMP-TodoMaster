package com.rohitneel.todomaster.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohitneel.todomaster.presentation.navigation.NavDestinations
import com.rohitneel.todomaster.presentation.theme.Clarendon
import com.rohitneel.todomaster.presentation.viewmodel.PomodoroViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import org.jetbrains.compose.resources.stringResource
import todomaster.composeapp.generated.resources.Res
import todomaster.composeapp.generated.resources.*

@Composable
fun PomodoroComplete(
    taskViewModel: TaskViewModel,
    pomodoroViewModel: PomodoroViewModel,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        // Placeholder for background animation
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Placeholder for check animation
                    Box(modifier = Modifier.size(200.dp).background(Color.LightGray))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.pomodoro_completed),
                        fontSize = 24.sp,
                        color = Color(taskViewModel.themeColor.value.toArgb()),
                        fontFamily = Clarendon,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(Res.string.pomodoro_remarks),
                        fontSize = 22.sp,
                        color = Color(taskViewModel.themeColor.value.toArgb()),
                        fontFamily = Clarendon,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = {
                    pomodoroViewModel.resetProgress()
                    navController.navigate(NavDestinations.Pomodoro.route) {
                        popUpTo(NavDestinations.PomodoroComplete.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
                colors = ButtonDefaults.buttonColors(Color(taskViewModel.themeColor.value.toArgb()))
            ) {
                Text(text = stringResource(Res.string.finish), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
