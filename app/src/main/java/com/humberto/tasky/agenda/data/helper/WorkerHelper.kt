package com.humberto.tasky.agenda.data.helper

import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerHelper @Inject constructor(
    val workManager: WorkManager
) {

    inline fun <reified T: ListenableWorker> enqueueSyncPendingWorker(
        idKey: String,
        id: String
    ) {
        val request = OneTimeWorkRequestBuilder<T>()
            .setInputData(
                workDataOf(idKey to id)
            )
            .setInitialDelay(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints(
                    requiredNetworkType = NetworkType.CONNECTED
                )
            )
            .build()

        workManager.enqueue(request)
    }
}