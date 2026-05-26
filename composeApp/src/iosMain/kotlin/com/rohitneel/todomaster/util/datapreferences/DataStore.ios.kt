package com.rohitneel.todomaster.util.datapreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.rohitneel.todomaster.util.AppConstants
import platform.Foundation.*
import okio.Path.Companion.toPath

fun createDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    producePath = {
        val directory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        val path = directory?.path ?: NSHomeDirectory()
        (path + "/${AppConstants.SETTINGS}.preferences_pb").toPath()
    }
)
