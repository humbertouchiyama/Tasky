package com.humberto.tasky.agenda.data.event

import android.app.Application
import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.humberto.tasky.agenda.domain.event.EventRepository.Companion.EVENT_ID
import com.humberto.tasky.agenda.domain.event.EventUploader
import com.humberto.tasky.agenda.domain.event.PhotoSizeTooLargeCount
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventUploaderImpl @Inject constructor(
    private val app: Application
): EventUploader {

    override fun upload(
        id: String,
        type: EventUploader.Type,
        requestJson: String,
        photoUris: Array<String>
    ): Flow<Result<PhotoSizeTooLargeCount, DataError.Network>> {
        val workManager = WorkManager.getInstance(app)
        val request = OneTimeWorkRequestBuilder<UploadEventWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 2000L, TimeUnit.MILLISECONDS)
            .setInputData(
                Data.Builder()
                    .putString(EVENT_ID, id)
                    .putString("requestJson", requestJson)
                    .putString("type", type.name.lowercase())
                    .putStringArray("photoUris", photoUris)
                    .build()
            )
            .build()
        workManager.beginUniqueWork(
            id,
            ExistingWorkPolicy.REPLACE,
            request
        ).enqueue()

        return workManager.getWorkInfosForUniqueWorkLiveData(id)
            .asFlow()
            .mapNotNull { infos ->
                val outputData = infos.firstOrNull()?.outputData
                outputData?.getString("result")?.let { result ->
                    val photoSizeTooLargeCount = outputData.getInt("photo_size_too_large_count", 0)
                    when(result) {
                        "success" -> Result.Success(data = photoSizeTooLargeCount)
                        "error" -> Result.Error(DataError.Network.UNKNOWN)
                        "unauthorized" -> Result.Error(DataError.Network.UNAUTHORIZED)
                        else -> Result.Error(DataError.Network.UNKNOWN)
                    }
                }
            }
    }
}