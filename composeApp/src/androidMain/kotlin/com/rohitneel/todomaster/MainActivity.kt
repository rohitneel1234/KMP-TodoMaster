package com.rohitneel.todomaster

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon.createWithResource
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rohitneel.todomaster.presentation.service.PomodoroService
import com.rohitneel.todomaster.presentation.viewmodel.PomodoroViewModel
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.rememberWindowSize
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val taskViewModel: TaskViewModel by viewModel()
    private val pomodoroViewModel: PomodoroViewModel by viewModel()

    private var pomodoroService: PomodoroService? = null
    private var isBound by mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PomodoroService.PomodoroBinder
            pomodoroService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            pomodoroService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { taskViewModel.isLoading.value }
        enableEdgeToEdge()

        val intent = Intent(this, PomodoroService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        setContent {
            val pomodoroHours by pomodoroService?.hours?.collectAsState(0) ?: remember { mutableStateOf(0) }
            val pomodoroMinutes by pomodoroService?.minutes?.collectAsState(0) ?: remember { mutableStateOf(0) }
            val pomodoroSeconds by pomodoroService?.seconds?.collectAsState(0) ?: remember { mutableStateOf(0) }
            val pomodoroTimerState by pomodoroService?.currentTimerState ?: remember { mutableStateOf(com.rohitneel.todomaster.presentation.service.TimerState.IDLE) }

            // Sync ViewModel with platform service
            LaunchedEffect(pomodoroHours, pomodoroMinutes, pomodoroSeconds) {
                val totalSeconds = (pomodoroHours * 3600) + (pomodoroMinutes * 60) + pomodoroSeconds
                pomodoroViewModel.updateProgressFromPlatform(totalSeconds.toLong())
            }

            App(
                windowSize = rememberWindowSize(),
                onVibrate = { vibrateDevice() },
                onStartPomodoro = { triggerPomodoroService(AppConstants.PomodoroConstants.ACTION_SERVICE_START) },
                onStopPomodoro = { triggerPomodoroService(AppConstants.PomodoroConstants.ACTION_SERVICE_STOP) },
                onCancelPomodoro = { triggerPomodoroService(AppConstants.PomodoroConstants.ACTION_SERVICE_CANCEL) },
                pomodoroHours = pomodoroHours,
                pomodoroMinutes = pomodoroMinutes,
                pomodoroSeconds = pomodoroSeconds,
                pomodoroTimerState = pomodoroTimerState.name
            )

            val context = this
            LaunchedEffect(Unit) {
                quickAddTaskShortcut(context)
            }
        }
    }

    private fun triggerPomodoroService(action: String) {
        val intent = Intent(this, PomodoroService::class.java).apply {
            this.action = action
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun vibrateDevice() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun quickAddTaskShortcut(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            val shortcut = ShortcutInfo.Builder(context, "quick_add_task")
                .setShortLabel("Add Task")
                .setLongLabel("Quickly add a new task")
                .setIcon(createWithResource(context, R.drawable.ic_launcher_foreground))
                .setIntent(
                    Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(AppConstants.NOTIFICATION_DEEP_LINK_ADD_TASK)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                .build()
            shortcutManager?.dynamicShortcuts = listOf(shortcut)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}
