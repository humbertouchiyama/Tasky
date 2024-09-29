package com.humberto.tasky.main

import android.app.Application
import com.humberto.tasky.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class TaskyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) // Plant a tree for logging in debug mode
        }
    }
}