package com.humberto.tasky.agenda.data.agenda

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.humberto.tasky.agenda.domain.AgendaRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncFullAgendaWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val agendaRepository: AgendaRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            agendaRepository.getFullAgenda()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}