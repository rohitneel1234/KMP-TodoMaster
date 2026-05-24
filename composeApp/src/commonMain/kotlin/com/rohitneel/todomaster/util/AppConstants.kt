package com.rohitneel.todomaster.util

object AppConstants {

    const val SETTINGS = "settings_prefs"
    const val POMODORO_PREFERENCES = "pomodoro_prefs"
    const val TIME_FORMAT = "hh:mm a"
    const val TIME_FORMAT_24H = "HH:mm"
    const val DATE_FORMAT = "dd MM, yy"
    const val REMINDER_DATE_TIME_FORMAT = "dd-MM-yy hh:mm a"
    const val SEPARATOR = " - "
    const val MARKET_PLACE_HOLDER = "market://details?id="
    const val SCREEN_CONTENT_WIDTH_FRACTION = 0.92f
    const val SPLASH_DELAY = 500L
    const val PROGRESS_DELAY = 600L
    const val PAST_MONTH_RANGE = 60L
    const val FUTURE_MONTH_RANGE = 120L
    const val MAX_DAYS = 7
    const val TRANSLATE_X = 600f
    const val MAX_ANIM_DURATION = 1000
    const val MAX_Y_AXIS_LABEL_COUNT = 9
    const val INITIAL_MAX_TASK_COUNT = 8
    const val LARGE_SCREEN_DENSITY_THRESHOLD = 400
    const val LARGE_SCREEN_WIDTH_THRESHOLD = 360
    const val RESET_TIME = "reset_time"
    const val UPDATE_REQUEST_CODE = 110
    const val MAX_TASK_COUNT = 2
    const val ACTION_STOP_ALARM = "STOP_ALARM"
    const val ACTION_SNOOZE_ALARM = "SNOOZE_ALARM"
    const val TRASH_EXPIRY_DAYS = 10L
    const val NOTIFICATION_DEEP_LINK_ADD_TASK = "todomaster://com.rohitneel.todomaster/add_task"

    //Category
    const val ALL = "All"
    const val HOME = "Home"
    const val PERSONAL = "Personal"
    const val WORK = "Work"
    const val SCHOOL = "School"
    const val OTHER = "Other"

    //Reminder Repeat Interval
    const val NONE = "Never"
    const val DAILY = "Daily"
    const val WEEKLY = "Weekly"
    const val MONTHLY = "Monthly"
    const val CUSTOM = "Custom"
    const val DAY = "Days"
    const val WEEK = "Weeks"
    const val MONTH = "Months"
    //FontFamily Name
    const val DEFAULT = "Default"
    const val SOUTHERN_BOLD = "SouthernBold"
    const val SERPENTINE = "Serpentine"
    const val MEDIUM = "Medium"
    const val MONTSERRAT = "Montserrat"
    const val ABRILFAT = "Abrilfat"

    //FontSize
    const val DEFAULT_FONT_SIZE = 16

    //Notification
    const val NOTIFICATION_CHANNEL_ID = "com.rohitneel.todomaster.notificationID"
    const val NOTIFICATION_CHANNEL_NAME = "Reminder"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Reminder Notifications"
    const val REMINDER_NOTIFICATION_ID = 100
    const val OVERDUE_NOTIFICATION_ID = 101
    const val POMODORO_NOTIFICATION_ID = 102

    //Contact Feedback
    const val EMAIL_ID = "rohitneel.app.feedback@gmail.com"
    const val SUBJECT = "TodoMaster Feedback"

    //Work Manager
    const val MESSAGE_KEY = "message"
    const val TITLE_KEY = "title_key"
    const val SNOOZE_DURATION_KEY = "snooze_duration_key"
    const val TASK_ID = "task_id"
    const val EXPIRY_DATE = "expiry_date"
    const val REMINDER_WORK_MANAGER_TAG = "reminder_work_manager_tag"
    const val OVERDUE_WORK_MANAGER_TAG = "overdue_work_manager_tag"

    const val BACKUP = "backup"
    const val RESTORE = "restore"
    const val MIME_TYPE = "*/*"

    object DataStorePreference {
        const val DARK_MODE = "dark_mode"
        const val SWIPE_GESTURE = "swipe_gesture"
        const val LANGUAGE = "language"
        const val SORT_OPTION = "sort_option"
        const val THEME_COLOR = "theme_color"
        const val THEME_IMAGE = "theme_image"
        const val REMINDER = "reminder"
        const val ONBOARDING = "on_boarding_completed"
        const val COMPLETION_TONE = "completion_tone"
        const val VIBRATE = "vibrate"
        const val SELECTED_DURATION = "selected_duration"
        const val CATEGORIES = "categories"
        const val CATEGORY_COLORS = "category_colors"
        const val SNOOZE_DURATION = "snooze_duration"
    }

    object TaskBackUpRepo {
        const val ID = "ID"
        const val TITLE = "Title"
        const val DESCRIPTION = "Description"
        const val IS_CHECK = "IsCheck"
        const val COMPLETED = "Completed"
        const val PINNED = "Pin"
        const val FAVORITES = "Favorites"
        const val REMINDER = "Reminder"
        const val COLOR = "Color"
        const val GRADIENT_COLOR = "Gradient_Color"
        const val CREATION_DATE = "CreationDate"
        const val DUE_DATE = "DueDate"
        const val CATEGORY = "Category"
        const val CATEGORY_COLOR = "Category_Color"
        const val FONT_FAMILY = "FontFamily"
        const val FONT_SIZE = "FontSize"
        const val FONT_STYLE_MODEL = "FontStyleModel"
        const val CHECKLIST_ID = "Checklist_ID"
        const val CHECKLIST_VALUE = "Checklist_Value"
        const val CHECKLIST_CHECKED = "Checklist_Checked"
        const val CHECKLIST_UID = "Checklist_UID"
        const val TASK_DIR = "tasks_dir"
        const val CSV_TASK_FILE_NAME = "tasks.csv"
    }

    object TimeDurations {
        const val ONE_HOUR = 1
        const val FORTY_FIVE_MINUTES = 45
        const val THIRTY_MINUTES = 30
        const val TWENTY_FIVE_MINUTES = 25
        const val TWENTY_MINUTES = 20
        const val FIFTEEN_MINUTES = 15
        const val TEN_MINUTES = 10
        const val FIVE_MINUTES = 5
    }

    object TimeUnits {
        const val HOUR = "hour"
        const val MINUTES = "minutes"
    }

    object PomodoroConstants {
        const val ACTION_SERVICE_START = "ACTION_SERVICE_START"
        const val ACTION_SERVICE_STOP = "ACTION_SERVICE_STOP"
        const val ACTION_SERVICE_CANCEL = "ACTION_SERVICE_CANCEL"

        const val POMODORO_NOTIFICATION_CHANNEL_ID = "pomodoro_message_channel"
        const val POMODORO_NOTIFICATION_CHANNEL_NAME = "Pomodoro Message"
        const val POMODORO_NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications for Pomodoro Timer"
        const val NOTIFICATION_CHANNEL_ID = "pomodoro_timer_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Pomodoro Timer"
        const val NOTIFICATION_ID = 10
        const val CLICK_REQUEST_CODE = 100
        const val NOTIFICATION_DEEP_LINK = "todomaster://com.rohitneel.todomaster/pomodoro"
        const val POMODORO_NOTIFICATION_TITLE = "Pomodoro Finished !"
        const val POMODORO_NOTIFICATION_MESSAGE = "Congratulations! 🎉 You've successfully completed task!"
    }

    const val DEFAULT_ESCAPE_CHARACTER = '"'
    const val DEFAULT_SEPARATOR = ';'
    const val DEFAULT_QUOTE_CHARACTER = '"'
    const val NO_QUOTE_CHARACTER = '\u0000'
    const val NO_ESCAPE_CHARACTER = '\u0000'
    const val DEFAULT_LINE_END = "\n"
}
