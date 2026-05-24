package com.rohitneel.todomaster.presentation.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.rohitneel.todomaster.R
import com.rohitneel.todomaster.MainActivity
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.ACTION_SERVICE_CANCEL
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.ACTION_SERVICE_START
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.ACTION_SERVICE_STOP
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.CLICK_REQUEST_CODE
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.NOTIFICATION_CHANNEL_ID
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.NOTIFICATION_CHANNEL_NAME
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.NOTIFICATION_ID
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.NOTIFICATION_DEEP_LINK
import com.rohitneel.todomaster.util.datapreferences.PomodoroPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PomodoroService : Service() {

	private val binder = PomodoroBinder()

	private lateinit var timer: Timer

	var duration: Duration = 5.minutes
		private set

	private val _hours = MutableStateFlow(0)
	val hours: StateFlow<Int> get() = _hours

	private val _minutes = MutableStateFlow(5)
	val minutes: StateFlow<Int> get() = _minutes

	private val _seconds = MutableStateFlow(0)
	val seconds: StateFlow<Int> get() = _seconds

	var currentTimerState = mutableStateOf(TimerState.IDLE)
		private set

	override fun onBind(p0: Intent?) = binder

	override fun onCreate() {
		super.onCreate()
		val pomodoroPreferencesHelper = PomodoroPreferencesHelper(this)
		val savedDuration = pomodoroPreferencesHelper.getSelectedDuration()
		setTimerDuration(savedDuration)
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		intent?.action.let {
			when (it) {
				ACTION_SERVICE_START -> {
					startForegroundService()
					startTimer()
				}

				ACTION_SERVICE_STOP -> {
					stopTimer()
				}

				ACTION_SERVICE_CANCEL -> {
					stopTimer()
					cancelTimer()
					stopForegroundService()
				}
			}
		}
		return super.onStartCommand(intent, flags, startId)
	}

	@SuppressLint("ForegroundServiceType")
	private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification = createNotificationBuilder()
			.setContentTitle("Pomodoro Timer")
			.setContentText("Timer is running")
			.build()
		startForeground(NOTIFICATION_ID, notification)
	}

	private fun stopForegroundService() {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_ID)
		stopForeground(STOP_FOREGROUND_REMOVE)
		stopSelf()
	}

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
		val channel = NotificationChannel(
			NOTIFICATION_CHANNEL_ID,
			NOTIFICATION_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_HIGH
		)
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)
	}

	private fun createNotificationBuilder(): NotificationCompat.Builder {
		val deepLinkIntent = Intent(this, MainActivity::class.java).apply {
			action = NOTIFICATION_DEEP_LINK
		}
		val pendingIntent = PendingIntent.getActivity(
			this,
			CLICK_REQUEST_CODE,
			deepLinkIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
		return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_launcher_foreground)
			.setContentIntent(pendingIntent)
			.setOngoing(true)
	}

	@SuppressLint("NotificationPermission")
	private fun updateNotification(hours: String, minutes: String, seconds: String) {
		val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.notify(
			NOTIFICATION_ID,
			createNotificationBuilder()
				.setContentTitle("Pomodoro Timer")
				.setContentText("$hours:$minutes:$seconds")
				.build()
		)
	}

	fun setTimerDuration(durationInSeconds: Long) {
		duration = durationInSeconds.seconds
		updateTimeUnits()
	}

	private fun updateTimeUnits() {
		_hours.value = (duration.inWholeHours.toInt())
		_minutes.value = ((duration.inWholeMinutes % 60).toInt())
		_seconds.value = ((duration.inWholeSeconds % 60).toInt())
	}


	private fun startTimer() {
		currentTimerState.value = TimerState.STARTED
		timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
			if (duration.inWholeSeconds > 0) {
				duration = duration.minus(1.seconds)
				updateTimeUnits()
				updateNotification(
					_hours.value.toString().padStart(2, '0'),
					_minutes.value.toString().padStart(2, '0'),
					_seconds.value.toString().padStart(2, '0')
				)
			} else {
				cancel()
			}
		}
	}

	private fun stopTimer() {
		if (this::timer.isInitialized) {
			timer.cancel()
		}
		currentTimerState.value = TimerState.STOPPED
	}

	private fun cancelTimer() {
		currentTimerState.value = TimerState.IDLE
	}

	inner class PomodoroBinder : Binder() {
		fun getService(): PomodoroService = this@PomodoroService
	}
}

enum class TimerState {
	IDLE,
	STARTED,
	STOPPED
}
