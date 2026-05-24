package com.rohitneel.todomaster.presentation.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.rohitneel.todomaster.R
import com.rohitneel.todomaster.util.AppConstants
import com.rohitneel.todomaster.util.AppConstants.ACTION_SNOOZE_ALARM
import com.rohitneel.todomaster.util.AppConstants.ACTION_STOP_ALARM
import com.rohitneel.todomaster.util.AppConstants.REMINDER_NOTIFICATION_ID

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var title: String = "Alarm"
    private var snoozeDuration: Long = 5 * 60 * 1000L
    private val snoozeHandler = Handler(Looper.getMainLooper())
    private val snoozeRunnable = Runnable {
        playAlarmSound()
        startForegroundServiceWithNotification()
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            stopSelf()
            return START_NOT_STICKY
        }
        title = intent?.getStringExtra(AppConstants.TITLE_KEY) ?: "Alarm"
        snoozeDuration = intent?.getLongExtra(AppConstants.SNOOZE_DURATION_KEY, 5 * 60 * 1000L) ?: (5 * 60 * 1000L)

        startForegroundServiceWithNotification()

        when (intent?.action) {
            ACTION_STOP_ALARM -> {
                stopAlarm()
                stopSelf()
            }
            ACTION_SNOOZE_ALARM -> {
                snoozeAlarm()
            }
            else -> {}
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val snoozeIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE_ALARM
            putExtra(AppConstants.TITLE_KEY, title)
            putExtra(AppConstants.SNOOZE_DURATION_KEY, snoozeDuration)
        }
        val snoozePendingIntent = PendingIntent.getService(
            this,
            0,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, AppConstants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Task Reminder")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .addAction(R.drawable.baseline_snooze_24, "Snooze", snoozePendingIntent)
            .addAction(R.drawable.baseline_stop_24, "Stop", stopPendingIntent)
            .build()

        startForeground(REMINDER_NOTIFICATION_ID, notification)
        playAlarmSound()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            AppConstants.NOTIFICATION_CHANNEL_ID,
            AppConstants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = AppConstants.NOTIFICATION_CHANNEL_DESCRIPTION
        }
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun playAlarmSound() {
        if (mediaPlayer.isPlaying) return
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer.apply {
                reset()
                setDataSource(this@AlarmService, alarmUri)
                setOnPreparedListener { it.start() }
                setOnCompletionListener { stopSelf() }
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun stopAlarm() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        clearNotification()
    }

    private fun snoozeAlarm() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        snoozeHandler.removeCallbacks(snoozeRunnable)
        snoozeHandler.postDelayed(snoozeRunnable, snoozeDuration)
    }

    private fun clearNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(REMINDER_NOTIFICATION_ID)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        snoozeHandler.removeCallbacksAndMessages(null)
        clearNotification()
        mediaPlayer.release()
        super.onDestroy()
    }
}
