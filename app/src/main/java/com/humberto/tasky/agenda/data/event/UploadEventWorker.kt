package com.humberto.tasky.agenda.data.event

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.humberto.tasky.agenda.data.agenda.AgendaApiService
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.event.EventRepository.Companion.EVENT_ID
import com.humberto.tasky.agenda.domain.event.EventUploader
import com.humberto.tasky.agenda.domain.photo.PhotoCompressor
import com.humberto.tasky.agenda.domain.photo.PhotoExtensionParser
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.domain.util.onError
import com.humberto.tasky.core.domain.util.onSuccess
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

@HiltWorker
class UploadEventWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val eventDao: EventDao,
    private val agendaApiService: AgendaApiService,
    private val photoExtensionParser: PhotoExtensionParser,
    private val photoCompressor: PhotoCompressor
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 3) {
            return Result.failure(
                Data.Builder()
                    .putString("result", "error")
                    .build()
            )
        }
        val eventId = inputData.getString(EVENT_ID) ?: return Result.failure()
        val type = EventUploader.Type.valueOf(
            inputData.getString("type")?.replaceFirstChar { it.uppercase() }
                ?: return Result.failure()
        )
        val requestJson =
            inputData.getString("requestJson") ?: return Result.failure()
        val photoUris =
            inputData.getStringArray("photoUris") ?: return Result.failure()

        val outputBuilder = Data.Builder()
        var photoSizeTooLargeCount = 0

        safeCall {
            val photos = supervisorScope {
                photoUris.mapIndexed { index, uri ->
                    async {
                        val uri = Uri.parse(uri)

                        val compressedBytes = photoCompressor.compress(
                            contentUri = uri,
                            compressionThreshold = 200 * 1024L
                        ) ?: return@async null

                        if(compressedBytes.size > AgendaItem.Event.MAX_PHOTO_SIZE) {
                            outputBuilder.putInt("photo_size_too_large_count", ++photoSizeTooLargeCount)
                            return@async null
                        }
                        val extension = photoExtensionParser.getExtensionForUri(uri)
                        MultipartBody.Part
                            .createFormData(
                                name = "photo$index",
                                filename = UUID.randomUUID().toString() + "." + extension,
                                body = compressedBytes.toRequestBody()
                            )
                    }
                }
                    .mapNotNull {
                        it.await()
                    }
            }

            when (type) {
                EventUploader.Type.Create -> {
                    agendaApiService.createEvent(
                        createEventRequest = MultipartBody.Part
                            .createFormData("create_event_request", requestJson),
                        photos = photos
                    )
                }
                EventUploader.Type.Update -> {
                    agendaApiService.updateEvent(
                        updateEventRequest = MultipartBody.Part
                            .createFormData("update_event_request", requestJson),
                        photos = photos
                    )
                }
            }
        }.onSuccess {
            eventDao.upsertEvent(it.toEvent().toEventEntity())
            return Result.success(
                outputBuilder
                    .putString("result", "success")
                    .build()
            )
        }.onError {
            // work on retrying
//            if (it.isRetryable()) {
//                eventDao.insertEventPendingSync(
//                    EventPendingSyncEntity(
//                        userId = ,
//                        event = it.toEventEntity(),
//                        type = when (type) {
//                            EventUploader.Type.Create -> ModificationType.Created
//                            EventUploader.Type.Update -> ModificationType.Updated
//                        }
//                    )
//                )
//            }
            return Result.failure(
                Data.Builder()
                    .putString("result", "error")
                    .build()
            )
        }
        return Result.failure()
    }
}