package com.rohitneel.todomaster.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rohitneel.todomaster.R

object NotificationUtil {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun createNotification(
        context: Context,
        title: String,
        contentText: String,
        notificationID: Int
    ){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                context,
                AppConstants.NOTIFICATION_CHANNEL_ID,
                AppConstants.NOTIFICATION_CHANNEL_NAME,
                AppConstants.NOTIFICATION_CHANNEL_DESCRIPTION
            )
        }
        val notification = NotificationCompat.Builder(context, AppConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Using the foreground icon we created earlier
            .setContentTitle(contentText)
            .setContentText(title)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setLights(Color.WHITE, 200, 200)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        if (isPermissionGranted(context)) {
            NotificationManagerCompat.from(context).notify(notificationID, notification)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    @RequiresApi(Build.VERSION_CODES.O)
    fun showPomodoroNotification(
        context: Context,
        title: String,
        message: String,
        notificationID: Int
    ) {
        createNotificationChannel(
            context,
            AppConstants.PomodoroConstants.POMODORO_NOTIFICATION_CHANNEL_ID,
            AppConstants.PomodoroConstants.POMODORO_NOTIFICATION_CHANNEL_NAME,
            AppConstants.PomodoroConstants.POMODORO_NOTIFICATION_CHANNEL_DESCRIPTION
        )
        val notification = NotificationCompat.Builder(context, AppConstants.PomodoroConstants.POMODORO_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        if (isPermissionGranted(context)) {
            NotificationManagerCompat.from(context).notify(notificationID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        id: String,
        name: String,
        desc: String,
    ) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = desc
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    private fun isPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
