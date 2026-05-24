package com.rohitneel.todomaster.util.datapreferences

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.rohitneel.todomaster.data.model.TaskModel
import com.rohitneel.todomaster.presentation.theme.AppColors
import com.rohitneel.todomaster.presentation.theme.categoryColorMapping
import com.rohitneel.todomaster.util.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class DataStorePreferenceManager(private val dataStore: DataStore<Preferences>) {

    private object DataStorePreferenceKeys {
        val DARK_MODE_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.DARK_MODE)
        val SWIPE_GESTURE_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.SWIPE_GESTURE)
        val LANGUAGE_KEY = stringPreferencesKey(AppConstants.DataStorePreference.LANGUAGE)
        val SELECTED_SORT_OPTION_KEY = stringPreferencesKey(AppConstants.DataStorePreference.SORT_OPTION)
        val THEME_COLOR_KEY = intPreferencesKey(AppConstants.DataStorePreference.THEME_COLOR)
        val THEME_IMAGE_KEY = intPreferencesKey(AppConstants.DataStorePreference.THEME_IMAGE)
        val REMINDER_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.REMINDER)
        val ONBOARDING_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.ONBOARDING)
        val COMPLETION_TONE_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.COMPLETION_TONE)
        val VIBRATE_KEY = booleanPreferencesKey(AppConstants.DataStorePreference.VIBRATE)
        val CATEGORIES_KEY = stringSetPreferencesKey(AppConstants.DataStorePreference.CATEGORIES)
        val CATEGORY_COLORS_KEY = stringPreferencesKey(AppConstants.DataStorePreference.CATEGORY_COLORS)
        val SNOOZE_DURATION_KEY = longPreferencesKey(AppConstants.DataStorePreference.SNOOZE_DURATION)
    }

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.DARK_MODE_KEY] ?: false
        }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.DARK_MODE_KEY] = isDarkMode
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.LANGUAGE_KEY] = language
        }
    }

    suspend fun setSortOption(sortOption: String) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.SELECTED_SORT_OPTION_KEY] = sortOption
        }
    }

    suspend fun setThemeColor(color: Color, imageRes: Int) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.THEME_COLOR_KEY] = color.toArgb()
            preferences[DataStorePreferenceKeys.THEME_IMAGE_KEY] = imageRes
        }
    }

    suspend fun setReminder(reminder: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.REMINDER_KEY] = reminder
        }
    }

    suspend fun setCompletionTone(isToneEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.COMPLETION_TONE_KEY] = isToneEnabled
        }
    }

    suspend fun setVibrate(isVibrateEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.VIBRATE_KEY] = isVibrateEnabled
        }
    }

    suspend fun saveOnBoardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.ONBOARDING_KEY] = completed
        }
    }

    suspend fun setSwipeIconVisibility(isVisible: Boolean) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.SWIPE_GESTURE_KEY] = isVisible
        }
    }

    suspend fun saveCategories(categories: List<String>, categoryColors: Map<String, Color>) {
        val categorySet = categories.toSet()
        val colorMapString = categoryColors.entries.joinToString(separator = ",") {
            "${it.key}:${it.value.toArgb()}"
        }
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.CATEGORIES_KEY] = categorySet
            preferences[DataStorePreferenceKeys.CATEGORY_COLORS_KEY] = colorMapString
        }
    }

    suspend fun setSnoozeDuration(durationMillis: Long) {
        dataStore.edit { preferences ->
            preferences[DataStorePreferenceKeys.SNOOZE_DURATION_KEY] = durationMillis
        }
    }

    fun readOnBoardingState(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val onBoardingState = preferences[DataStorePreferenceKeys.ONBOARDING_KEY] ?: false
                onBoardingState
            }
    }

    val themeColorFlow: Flow<Pair<Color, Int>> = dataStore.data
        .map { preferences ->
            val colorInt = preferences[DataStorePreferenceKeys.THEME_COLOR_KEY] ?: AppColors.VibrantBlue.toArgb()
            val imageRes = preferences[DataStorePreferenceKeys.THEME_IMAGE_KEY] ?: 0 // Placeholder
            Pair(Color(colorInt), imageRes)
        }

    val selectedLanguage: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.LANGUAGE_KEY] ?: "English"
        }

    val selectedSortOption: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.SELECTED_SORT_OPTION_KEY] ?: "Created time (Newest first)"
        }

    val swipeGestureEnable: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.SWIPE_GESTURE_KEY] ?: true
        }

    val reminderEnable: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.REMINDER_KEY] ?: true
        }

    val completionToneEnable: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.COMPLETION_TONE_KEY] ?: true
        }

    val vibrateEnable: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.VIBRATE_KEY] ?: true
        }

    val categoriesFlow: Flow<Pair<List<String>, Map<String, Color>>> = dataStore.data
        .map { preferences ->
            val defaultCategories = TaskModel.categoryType.toSet()
            val categorySet = preferences[DataStorePreferenceKeys.CATEGORIES_KEY] ?: defaultCategories
            val colorMappingString = preferences[DataStorePreferenceKeys.CATEGORY_COLORS_KEY] ?: ""
            val storedCategoryColorMap = if (colorMappingString.isEmpty()) emptyMap() else colorMappingString.split(",").mapNotNull {
                val parts = it.split(":")
                if (parts.size == 2) parts[0] to Color(parts[1].toLong()) else null
            }.toMap()
            val mergedColorMapping = categoryColorMapping.toMutableMap().apply {
                putAll(storedCategoryColorMap)
            }
            categorySet.toList() to mergedColorMapping
        }

    val snoozeDuration: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[DataStorePreferenceKeys.SNOOZE_DURATION_KEY] ?: (5 * 60 * 1000L)
        }
}
