package com.humberto.tasky.agenda.data.agenda

import android.app.Application
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.humberto.tasky.agenda.domain.AgendaSynchronizer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AgendaSynchronizerImpl @Inject constructor(
    app: Application
): AgendaSynchronizer {

    private val workManager = WorkManager.getInstance(app)

    override fun scheduleSync() {
        workManager.enqueueUniquePeriodicWork(
            "sync_full_agenda_work",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            PeriodicWorkRequestBuilder<SyncFullAgendaWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(
                    Constraints(
                        requiredNetworkType = NetworkType.CONNECTED
                    )
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2000L, TimeUnit.MILLISECONDS)
                .build()
        )

    }

    override fun cancelSync() {
        workManager.cancelUniqueWork("sync_full_agenda_work")
    }
}