package com.rohitneel.todomaster

import android.app.Application
import com.rohitneel.todomaster.di.initKoin
import com.rohitneel.todomaster.util.androidContext
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory

class TodoMasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        androidContext = this
        initKoin {
            androidContext(this@TodoMasterApplication)
            workManagerFactory()
        }
    }
}
