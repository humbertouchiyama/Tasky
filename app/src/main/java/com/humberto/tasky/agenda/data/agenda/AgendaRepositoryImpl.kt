package com.humberto.tasky.agenda.data.agenda

import android.database.sqlite.SQLiteFullException
import androidx.room.withTransaction
import com.humberto.tasky.agenda.data.event.toCreateEventRequest
import com.humberto.tasky.agenda.data.event.toEvent
import com.humberto.tasky.agenda.data.event.toEventEntity
import com.humberto.tasky.agenda.data.event.toUpdateEventRequest
import com.humberto.tasky.agenda.data.reminder.toReminder
import com.humberto.tasky.agenda.data.reminder.toReminderEntity
import com.humberto.tasky.agenda.data.reminder.toReminderRequest
import com.humberto.tasky.agenda.data.task.toTask
import com.humberto.tasky.agenda.data.task.toTaskEntity
import com.humberto.tasky.agenda.data.task.toTaskRequest
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.AgendaDatabase
import com.humberto.tasky.core.database.ModificationType
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.entity.EventPendingSyncEntity
import com.humberto.tasky.core.database.entity.ReminderPendingSyncEntity
import com.humberto.tasky.core.database.entity.TaskPendingSyncEntity
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.map
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.domain.util.toEndOfDayUtc
import com.humberto.tasky.core.domain.util.toStartOfDayUtc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val agendaApi: AgendaApiService,
    private val agendaDatabase: AgendaDatabase,
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val reminderDao: ReminderDao,
    private val sessionManager: SessionManager,
    private val applicationScope: CoroutineScope
): AgendaRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        return safeCall {
            agendaApi.logout()
        }
    }

    override fun getAgendaForDate(localDate: LocalDate): Flow<List<AgendaItem>> {
        val startOfDay = localDate.toStartOfDayUtc().toInstant().toEpochMilli()
        val endOfDay = localDate.toEndOfDayUtc().toInstant().toEpochMilli()

        val tasksFlow = taskDao.getTasksForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        ).map { taskEntities ->
            taskEntities.map { it.toTask() }
        }
        val eventsFlow = eventDao.getEventsForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        ).map { eventEntities ->
            eventEntities.map { it.toEvent() }
        }
        val remindersFlow = reminderDao.getRemindersForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        ).map { reminderEntities ->
            reminderEntities.map {
                it.toReminder()
            }
        }
        return combine(
            tasksFlow,
            eventsFlow,
            remindersFlow
        ) { tasks, events, reminders ->
            (tasks + events + reminders).sortedBy { it.from }
        }
    }

    override suspend fun syncAndUpdateCache(
        time: ZonedDateTime,
        updateTimeOnly: Boolean
    ): Result<List<AgendaItem>, DataError> {
        return syncAgendaItems(time, updateTimeOnly)
            .onSuccess { items ->
                val fetchedEvents: List<AgendaItem.Event> =
                    items.filterIsInstance<AgendaItem.Event>()
                val fetchedTasks: List<AgendaItem.Task> =
                    items.filterIsInstance<AgendaItem.Task>()
                val fetchedReminders: List<AgendaItem.Reminder> =
                    items.filterIsInstance<AgendaItem.Reminder>()

                upsertFullAgenda(
                    tasks = fetchedTasks,
                    events = fetchedEvents,
                    reminders = fetchedReminders
                )
            }
    }

    private suspend fun syncAgendaItems(
        time: ZonedDateTime,
        updateTimeOnly: Boolean
    ): Result<List<AgendaItem>, DataError.Network> = supervisorScope {
        val pendingEventEntitiesDeferred = async {
            eventDao.getPendingEventsSync(userId = localUserId ?: "")
        }
        val pendingTaskEntitiesDeferred = async {
            taskDao.getPendingTasksSync(userId = localUserId ?: "")
        }
        val pendingReminderEntitiesDeferred = async {
            reminderDao.getPendingRemindersSync(userId = localUserId ?: "")
        }

        val modifiedEvents = pendingEventEntitiesDeferred.await().groupBy { it.type }

        val jobs = mutableListOf<Job>()
        jobs += launch {
            modifiedEvents[ModificationType.Created]?.let { syncCreatedEvents(it) }
        }
        jobs += launch {
            modifiedEvents[ModificationType.Updated]?.let { syncUpdatedEvents(it) }
        }

        val modifiedTasks = pendingTaskEntitiesDeferred.await().groupBy { it.type }
        jobs += launch {
            modifiedTasks[ModificationType.Created]?.let { syncCreatedTasks(it) }
        }
        jobs += launch {
            modifiedTasks[ModificationType.Updated]?.let { syncUpdatedTasks(it) }
        }

        val modifiedReminders = pendingReminderEntitiesDeferred.await().groupBy { it.type }
        jobs += launch {
            modifiedReminders[ModificationType.Created]?.let { syncCreatedReminders(it) }
        }
        jobs += launch {
            modifiedReminders[ModificationType.Updated]?.let { syncUpdatedReminders(it) }
        }

        val deletedEventIdsDeferred = async {
            eventDao.getDeletedEventSync(userId = localUserId!!)
        }
        val deletedTaskIdsDeferred = async {
            taskDao.getDeletedTaskSync(userId = localUserId!!)
        }
        val deletedReminderIdsDeferred = async {
            reminderDao.getDeletedReminderSync(userId = localUserId!!)
        }

        val deletedEventIds = deletedEventIdsDeferred.await()
        val deletedTaskIds = deletedTaskIdsDeferred.await()
        val deletedReminderIds = deletedReminderIdsDeferred.await()

        safeCall {
            agendaApi.syncAgenda(
                SyncAgendaRequest(
                    deletedEventIds = deletedEventIds,
                    deletedTaskIds = deletedTaskIds,
                    deletedReminderIds = deletedReminderIds
                )
            )
        }.onSuccess {
            jobs += applicationScope.launch {
                eventDao.deleteDeletedEventsSync(deletedEventIds)
                taskDao.deleteDeletedTasksSync(deletedTaskIds)
                reminderDao.deleteDeletedRemindersSync(deletedReminderIds)
            }
        }

        safeCall {
            applicationScope.async {
                if (updateTimeOnly) {
                    agendaApi.getAgenda(
                        timeZone = ZonedDateTime.now().zone.toString(),
                        time = time.toEpochSecond() * 1000L
                    )
                } else {
                    agendaApi.getFullAgenda()
                }
            }.await()
        }.map { agendaDto ->
            agendaDto.toAgendaItems()
        }.also {
            jobs.joinAll()
        }
    }

    override suspend fun upsertFullAgenda(
        tasks: List<AgendaItem.Task>,
        events: List<AgendaItem.Event>,
        reminders: List<AgendaItem.Reminder>
    ): EmptyResult<DataError.Local> {
        return try {
            taskDao.upsertTasks(tasks.map { it.toTaskEntity() })
            eventDao.upsertEvents(events.map { it.toEventEntity() })
            reminderDao.upsertReminders(reminders.map { it.toReminderEntity() })
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteAllAgenda() {
        agendaDatabase.withTransaction {
            withContext(Dispatchers.IO) {
                listOf(
                    async { eventDao.deleteAllEvents() },
                    async { reminderDao.deleteAllReminders() },
                    async { taskDao.deleteAllTasks() }
                ).awaitAll()
            }
        }
    }

    private suspend fun syncUpdatedEvents(updatedEvents: List<EventPendingSyncEntity>) {
        updatedEvents.map { event ->
            applicationScope.launch {
                val localEvent = event.event.toEvent()
                localEvent.toUpdateEventRequest(
                    deletedPhotoKeys = emptyList(), // Updating photos isn't supported in offline mode
                    isGoing = localEvent.getAttendee(localUserId ?: "")?.isGoing == true
                ).let { request ->
                    safeCall {
                        agendaApi.updateEvent(
                            updateEventRequest = MultipartBody.Part.createFormData(
                                "update_event_request",
                                json.encodeToString(request)
                            ),
                            photos = emptyList() // Syncing locally added photos is not supported
                        )
                    }.onSuccess {
                        eventDao.deleteEventPendingSync(eventId = request.id)
                    }
                }
            }
        }.forEach { it.join() }
    }

    private suspend fun syncCreatedEvents(createdEvents: List<EventPendingSyncEntity>) {
        createdEvents.map { event ->
            applicationScope.launch {
                event.event.toEvent().toCreateEventRequest()
                    .let { request ->
                        safeCall {
                            agendaApi.createEvent(
                                createEventRequest = MultipartBody.Part.createFormData(
                                    "create_event_request",
                                    json.encodeToString(request)
                                ),
                                photos = emptyList() // Syncing locally added photos is not supported
                            )
                        }.onSuccess {
                            eventDao.deleteEventPendingSync(eventId = request.id)
                        }
                    }
            }
        }.forEach { it.join() }
    }

    private suspend fun syncUpdatedTasks(updatedTasks: List<TaskPendingSyncEntity>) {
        updatedTasks.map { task ->
            applicationScope.launch {
                task.task.toTask().toTaskRequest().let { request ->
                    safeCall {
                        agendaApi.updateTask(request)
                    }.onSuccess {
                        taskDao.deleteTaskPendingSync(taskId = request.id)
                    }
                }
            }
        }.forEach { it.join() }
    }

    private suspend fun syncCreatedTasks(createdTasks: List<TaskPendingSyncEntity>) {
        createdTasks.map { task ->
            applicationScope.launch {
                task.task.toTask().toTaskRequest().let { request ->
                    safeCall {
                        agendaApi.createTask(request)
                    }.onSuccess {
                        taskDao.deleteTaskPendingSync(taskId = request.id)
                    }
                }
            }
        }.forEach { it.join() }
    }

    private suspend fun syncUpdatedReminders(updatedReminders: List<ReminderPendingSyncEntity>) {
        updatedReminders.map { reminder ->
            applicationScope.launch {
                reminder.reminder.toReminder().toReminderRequest()
                    .let { request ->
                        safeCall {
                            agendaApi.updateReminder(request)
                        }.onSuccess {
                            reminderDao.deleteReminderPendingSync(reminderId = request.id)
                        }
                    }
            }
        }.forEach { it.join() }
    }

    private suspend fun syncCreatedReminders(createdReminders: List<ReminderPendingSyncEntity>) {
        createdReminders.map { reminder ->
            applicationScope.launch {
                reminder.reminder.toReminder().toReminderRequest()
                    .let { request ->
                        safeCall {
                            agendaApi.createReminder(request)
                        }.onSuccess {
                            reminderDao.deleteReminderPendingSync(reminderId = request.id)
                        }
                    }
            }
        }.forEach { it.join() }
    }
}