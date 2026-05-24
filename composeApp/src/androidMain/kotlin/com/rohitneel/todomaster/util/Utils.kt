package com.rohitneel.todomaster.util

import android.app.DatePickerDialog
import android.app.LocaleManager
import android.app.PendingIntent
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.LocaleList
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.os.LocaleListCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.rohitneel.todomaster.BuildConfig
import com.rohitneel.todomaster.MainActivity
import com.rohitneel.todomaster.R
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.events.ChartEvent
import com.rohitneel.todomaster.presentation.service.PomodoroService
import com.rohitneel.todomaster.presentation.viewmodel.TaskViewModel
import com.rohitneel.todomaster.util.AppConstants.CUSTOM
import com.rohitneel.todomaster.util.AppConstants.DAILY
import com.rohitneel.todomaster.util.AppConstants.DAY
import com.rohitneel.todomaster.util.AppConstants.MARKET_PLACE_HOLDER
import com.rohitneel.todomaster.util.AppConstants.MONTH
import com.rohitneel.todomaster.util.AppConstants.MONTHLY
import com.rohitneel.todomaster.util.AppConstants.NONE
import com.rohitneel.todomaster.util.AppConstants.PomodoroConstants.CLICK_REQUEST_CODE
import com.rohitneel.todomaster.util.AppConstants.RESET_TIME
import com.rohitneel.todomaster.util.AppConstants.WEEK
import com.rohitneel.todomaster.util.AppConstants.WEEKLY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

// This is a temporary way to get context in androidMain for Utils
// In a real app, you should inject a platform-specific service
lateinit var androidContext: Context

actual object Utils {
    actual fun generateRandomColor(): Color {
        val random = Random(Clock.System.now().toEpochMilliseconds())
        return Color(
            red = random.nextInt(256),
            green = random.nextInt(256),
            blue = random.nextInt(256),
            alpha = 255
        )
    }

    actual fun shareText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share text")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        androidContext.startActivity(chooserIntent)
    }

    actual fun shareApp() {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Share App")
            val shareMsg = "Check out this app: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            intent.putExtra(Intent.EXTRA_TEXT, shareMsg)
            val chooserIntent = Intent.createChooser(intent, "Share by")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            androidContext.startActivity(chooserIntent)
        } catch (e: Exception) {
            Toast.makeText(androidContext, "something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMoreApp(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.market_string))))
        } catch (e: ActivityNotFoundException) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.market_developer_string))))
            } catch (e: Throwable) {
                Toast.makeText(context, "something went wrong!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Throwable) {
            Toast.makeText(context, "something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun openPlayStoreRating(context: Context) {
        val uri = Uri.parse(MARKET_PLACE_HOLDER + context.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "something went wrong", Toast.LENGTH_SHORT).show()
        } catch (e: Throwable) {
            Log.e("Utils", e.toString())
        }
    }

    fun openWebsite(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        } catch (e: Throwable) {
            Toast.makeText(context, "something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGmail(context: Context, to: String, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Send email using"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun vibrateDevice(
        context: Context,
        duration: Long = 500,
        vibrationEffect: Int = VibrationEffect.DEFAULT_AMPLITUDE
    ) {
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator?
        if (vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, vibrationEffect))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, PomodoroService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }

    fun clickPendingIntent(context: Context): PendingIntent {
        val deepLinkIntent = Intent(context, MainActivity::class.java).apply {
            action = AppConstants.PomodoroConstants.NOTIFICATION_DEEP_LINK
        }
        return PendingIntent.getActivity(context, CLICK_REQUEST_CODE, deepLinkIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    actual fun formatText(text: String, isUpperCase: Boolean, toggleUpperCase: Boolean): String {
        return when {
            isUpperCase -> text.uppercase()
            toggleUpperCase -> text.lowercase()
            else -> text
        }
    }

    actual fun getVisibleCategories(tasks: List<TaskModel>): Map<String, Int> {
        return tasks
            .filter { !it.isCompleted }
            .groupBy { it.category }
            .mapValues { (_, tasks) -> tasks.size }
    }

    actual fun determineChartEvent(tasks: List<TaskModel>): ChartEvent {
        val aggregatedData = tasks.filter { !it.isCompleted }
            .groupBy { it.category }
            .mapValues { (_, tasks) -> tasks.size.toLong() }
        return when {
            tasks.isEmpty() -> ChartEvent.Empty
            aggregatedData.isEmpty() -> ChartEvent.Completed
            else -> ChartEvent.ShowingData(aggregatedData)
        }
    }

    @Composable
    actual fun voiceRecognizerLauncher(onResult: (String) -> Unit): () -> Unit {
        val voiceLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val strArr = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (!strArr.isNullOrEmpty()) {
                        onResult(strArr[0])
                    }
                }
            }
        )

        return {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now")
            }
            voiceLauncher.launch(intent)
        }
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        return when {
            DocumentsContract.isDocumentUri(context, uri) -> {
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val type = split[0]
                        if ("primary".equals(type, ignoreCase = true)) {
                            "${Environment.getExternalStorageDirectory()}/${split[1]}"
                        } else {
                            null
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        var cursor: Cursor? = null
                        try {
                            cursor = context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)
                            cursor!!.moveToNext()
                            val fileName = cursor.getString(0)
                            val path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName
                            if (!TextUtils.isEmpty(path)) {
                                return fileName
                            }
                        } finally {
                            cursor?.close()
                        }
                        val id = DocumentsContract.getDocumentId(uri)
                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:".toRegex(), "")
                        }
                        val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads"), java.lang.Long.valueOf(id))
                        getDataColumn(context, contentUri)
                    }
                    else -> null
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> {
                getDataColumn(context, uri)
            }
            "file".equals(uri.scheme, ignoreCase = true) -> {
                uri.path
            }
            else -> {
                Log.e("FilePathResolver", "Unknown URI scheme: ${uri.scheme}")
                null
            }
        }
    }

    private fun getDataColumn(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        try {
            cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
    private fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority

    actual fun handleDateTimeClick(
        onEditTask: Boolean,
        isDueDate: Boolean,
        task: TaskModel?,
        snoozeDuration: Long,
        taskViewModel: TaskViewModel,
        snackBarHostState: SnackbarHostState,
        coroutineScope: CoroutineScope,
        workerTag: String
    ) {
        val context = androidContext
        setDateTime(context, onEditTask, isDueDate, task) { calendar, time, repeatInterval, isReminderSet ->
            if (isDueDate) {
                if (time == RESET_TIME) {
                    taskViewModel.dueDate = null
                    WorkScheduler.cancelOverdueWorkRequest(context = context, tag = workerTag)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Due date has been reset successfully")
                    }
                } else {
                    taskViewModel.dueDate = calendar.timeInMillis
                    WorkScheduler.setOverdueWorkRequest(
                        context = context,
                        task = task,
                        title = taskViewModel.title,
                        endDate = calendar,
                        tag = workerTag
                    )
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Due date set to $time")
                    }
                }
            } else {
                val dateTime = Pair(calendar.timeInMillis, Pair(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
                if (isReminderSet) {
                    taskViewModel.reminder = dateTime.first
                    WorkScheduler.setReminderWorkRequest(
                        context = context,
                        title = taskViewModel.title,
                        calendar = calendar,
                        repeatInterval = repeatInterval,
                        snoozeDuration = snoozeDuration,
                        tag = workerTag
                    )
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Reminder set successfully!")
                    }
                } else {
                    taskViewModel.reminder = null
                    WorkScheduler.cancelReminderWorkRequest(context = context, tag = workerTag)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Reminder removed")
                    }
                }
            }
        }
    }

    private fun setDateTime(
        context: Context,
        isEditTask: Boolean = false,
        isDueDate: Boolean = false,
        taskModel: TaskModel? = null,
        doWork: (calendar: Calendar, time: String, repeatInterval: String, isReminderSet: Boolean) -> Unit,
    ) {
        val mCalendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(AppConstants.TIME_FORMAT, Locale.getDefault())
        var hour = formatter.format(mCalendar.time).substring(0, 2).trim().toInt()
        val min = formatter.format(mCalendar.time).substring(3, 5).trim().toInt()

        val isAm = formatter.format(mCalendar.time).substring(6).trim().lowercase()

        if (isAm == context.getString(R.string.pm_format))
            hour += 12

        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(context.getString(R.string.set_time))
            .setHour(hour)
            .setMinute(min)
            .build()
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                materialTimePicker.show(
                    (context as AppCompatActivity).supportFragmentManager,
                    context.getString(R.string.set_time)
                )
                // dialog update the TextView accordingly
                materialTimePicker.addOnPositiveButtonClickListener {
                    val pickedHour: Int = materialTimePicker.hour
                    val pickedMinute: Int = materialTimePicker.minute

                    mCalendar.set(Calendar.HOUR_OF_DAY, pickedHour)
                    mCalendar.set(Calendar.MINUTE, pickedMinute)
                    mCalendar.set(Calendar.SECOND, 0)
                    if (isDueDate) {
                        val time = DateFormat.getDateTimeInstance(
                            DateFormat.MEDIUM,
                            DateFormat.SHORT
                        ).format(mCalendar.time)
                        doWork(mCalendar, time, "", false)
                    } else {
                        showRepeatOptionsDialog(context) { repeatInterval ->
                            val time = DateFormat.getDateTimeInstance(
                                DateFormat.MEDIUM,
                                DateFormat.SHORT
                            ).format(mCalendar.time)
                            doWork(mCalendar, time, repeatInterval, true)
                        }
                    }
                }
            }
        val datePickerDialog = DatePickerDialog(
            context,
            dateListener,
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )
        val roundedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(context.getColor(android.R.color.white))
        }
        taskModel?.takeIf { isEditTask }?.let { task ->
            val dateToClear = if (isDueDate) task.dueDate else task.reminder
            dateToClear?.let {
                datePickerDialog.setButton(
                    DatePickerDialog.BUTTON_NEUTRAL,
                    context.getString(R.string.clear)
                ) { _, _ ->
                    if (isDueDate) {
                        task.dueDate = null
                    } else {
                        task.reminder = null
                    }
                    mCalendar.apply {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                    }
                    doWork(mCalendar, RESET_TIME, "", false)
                }
            }
        }
        datePickerDialog.window?.setBackgroundDrawable(roundedDrawable)
        datePickerDialog.show()
    }

    private fun showRepeatOptionsDialog(context: Context, onRepeatSelected: (String) -> Unit) {
        var selectedIndex = 0
        val repeatOptions = arrayOf(NONE, DAILY, WEEKLY, MONTHLY, CUSTOM)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.repeat))
            .setSingleChoiceItems(repeatOptions, selectedIndex) { dialogInterface, which ->
                selectedIndex = which
                if (repeatOptions[selectedIndex] == CUSTOM) {
                    dialogInterface.dismiss()
                    showCustomRepeatDialog(context) { customRepeat ->
                        onRepeatSelected(customRepeat)
                    }
                }
            }
            .setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                if (repeatOptions[selectedIndex] != CUSTOM) {
                    onRepeatSelected(repeatOptions[selectedIndex])
                }
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showCustomRepeatDialog(context: Context, onCustomRepeatSelected: (String) -> Unit) {
        val numbers = (1..30).map { it.toString() }.toTypedArray() // Range from 1 to 30
        val units = arrayOf(DAY, WEEK, MONTH) // Units for repeat interval

        var selectedNumber = 0
        var selectedUnit = 0

        val linearLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 48, 16, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Create the first spinner (numbers 1-30)
        val numberSpinner = Spinner(context).apply {
            val numberAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, numbers)
            numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = numberAdapter

            val marginLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            marginLayoutParams.setMargins(0, 16, 16, 16)  // Add space between the spinners
            layoutParams = marginLayoutParams
        }

        val unitSpinner = Spinner(context).apply {
            val unitAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, units)
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            adapter = unitAdapter

            val marginLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            marginLayoutParams.setMargins(0, 16, 0, 0)  // Add space between spinners
            layoutParams = marginLayoutParams
        }

        linearLayout.addView(numberSpinner)
        linearLayout.addView(unitSpinner)

        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.custom_repeat_interval))
            .setView(linearLayout)
            .setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                val customRepeat = "Every ${numbers[selectedNumber]} ${units[selectedUnit]}"
                onCustomRepeatSelected(customRepeat)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        numberSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedNumber = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        unitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUnit = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    object LanguageUtil {
        fun Context.changeLocale(languageCode: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.getSystemService(LocaleManager::class.java)
                    .applicationLocales = LocaleList(
                    Locale.forLanguageTag(languageCode)
                )
            } else {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(languageCode)
                )
            }
        }
    }
}
