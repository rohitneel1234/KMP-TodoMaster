package com.rohitneel.todomaster.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rohitneel.todomaster.util.datapreferences.DataStorePreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val dataStorePreferenceManager: DataStorePreferenceManager,
) : ViewModel() {

    val isDarkMode = dataStorePreferenceManager.isDarkMode.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val swipeGestureEnable = dataStorePreferenceManager.swipeGestureEnable.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val reminderEnable = dataStorePreferenceManager.reminderEnable.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val completionToneEnable = dataStorePreferenceManager.completionToneEnable.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val vibrateEnable = dataStorePreferenceManager.vibrateEnable.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val snoozeDuration: StateFlow<Long> = dataStorePreferenceManager.snoozeDuration
        .stateIn(viewModelScope, SharingStarted.Lazily, 5 * 60 * 1000L)

    fun onDarkModeChange(isDarkMode: Boolean) = viewModelScope.launch {
        dataStorePreferenceManager.setDarkMode(isDarkMode)
    }

    val selectedLanguage: StateFlow<String> = dataStorePreferenceManager.selectedLanguage
        .stateIn(viewModelScope, SharingStarted.Lazily, "English")

    val selectedSortOption: StateFlow<String> = dataStorePreferenceManager.selectedSortOption
        .stateIn(viewModelScope, SharingStarted.Lazily, "Created time (Newest first)")

    fun onLanguageChange(language: String) = viewModelScope.launch {
        val languageName = language.split("  ").last()
        dataStorePreferenceManager.setLanguage(languageName)
    }

    fun onSortOptionChange(sortOption: String) = viewModelScope.launch {
        dataStorePreferenceManager.setSortOption(sortOption)
    }

    fun onSwipeGestureChange(isVisible: Boolean) = viewModelScope.launch {
        dataStorePreferenceManager.setSwipeIconVisibility(isVisible)
    }

    fun onReminderStateChange(reminder: Boolean) {
        viewModelScope.launch {
            dataStorePreferenceManager.setReminder(reminder)
        }
    }

    fun onCompletionToneChange(isToneEnabled: Boolean) {
        viewModelScope.launch {
            dataStorePreferenceManager.setCompletionTone(isToneEnabled)
        }
    }

    fun onVibrateChange(isVibrateEnabled: Boolean) {
        viewModelScope.launch {
            dataStorePreferenceManager.setVibrate(isVibrateEnabled)
        }
    }

    fun updateSnoozeDuration(durationMillis: Long) = viewModelScope.launch {
        dataStorePreferenceManager.setSnoozeDuration(durationMillis)
    }
}
