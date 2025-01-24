package com.humberto.tasky.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.humberto.tasky.BuildConfig
import com.humberto.tasky.R
import com.humberto.tasky.core.domain.alarm.AlarmNotificationManager
import com.humberto.tasky.core.domain.util.DispatcherProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TaskyApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject lateinit var dispatchers: DispatcherProvider

    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope = CoroutineScope(SupervisorJob() + dispatchers.main)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree()) // Plant a tree for logging in debug mode
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            AlarmNotificationManager.ALARM_CHANNEL_ID,
            getString(R.string.agenda_alarm_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}