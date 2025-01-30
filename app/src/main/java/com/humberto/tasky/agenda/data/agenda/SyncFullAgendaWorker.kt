package com.humberto.tasky.agenda.data.agenda

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.ZonedDateTime

@HiltWorker
class SyncFullAgendaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val agendaRepository: AgendaRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount > 3) {
            return Result.failure()
        }

        agendaRepository.syncAndUpdateCache(
            time = ZonedDateTime.now(),
            updateTimeOnly = false
        ).onSuccess {
            return Result.success()
        }.onError {
            if (it == DataError.Network.UNAUTHORIZED) {
                return Result.failure()
            }
            return Result.retry()
        }
        return Result.failure()
    }
}