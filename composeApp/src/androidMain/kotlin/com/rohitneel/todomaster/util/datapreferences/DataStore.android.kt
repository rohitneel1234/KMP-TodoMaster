package com.rohitneel.todomaster.util.datapreferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rohitneel.todomaster.util.AppConstants

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.SETTINGS)
