package com.rohitneel.todomaster.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestPermission(context: Context) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // In KMP, we should ideally use a cross-platform way to show messages,
            // but for now, we use Toast on Android.
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
    val currentPermissionStatus = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    LaunchedEffect(Unit) {
        if (currentPermissionStatus != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
