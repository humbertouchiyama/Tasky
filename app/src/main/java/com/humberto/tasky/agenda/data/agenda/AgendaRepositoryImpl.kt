package com.humberto.tasky.agenda.data.agenda

import android.database.sqlite.SQLiteFullException
import androidx.room.withTransaction
import com.humberto.tasky.agenda.data.event.toEvent
import com.humberto.tasky.agenda.data.event.toEventEntity
import com.humberto.tasky.agenda.data.event.toPhoto
import com.humberto.tasky.agenda.data.reminder.toReminder
import com.humberto.tasky.agenda.data.reminder.toReminderEntity
import com.humberto.tasky.agenda.data.task.toTask
import com.humberto.tasky.agenda.data.task.toTaskEntity
import com.humberto.tasky.agenda.domain.AgendaItem
import com.humberto.tasky.agenda.domain.AgendaRepository
import com.humberto.tasky.core.data.networking.safeCall
import com.humberto.tasky.core.database.AgendaDatabase
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.domain.repository.SessionManager
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.EmptyResult
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.asEmptyDataResult
import com.humberto.tasky.core.domain.util.onSuccess
import com.humberto.tasky.core.domain.util.toEndOfDayUtc
import com.humberto.tasky.core.domain.util.toStartOfDayUtc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class AgendaRepositoryImpl @Inject constructor(
    private val agendaApiService: AgendaApiService,
    private val agendaDatabase: AgendaDatabase,
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val reminderDao: ReminderDao,
    private val sessionManager: SessionManager
): AgendaRepository {

    private val localUserId: String?
        get() = sessionManager.getUserId()

    override suspend fun logout(): EmptyResult<DataError.Network> {
        return safeCall {
            agendaApiService.logout()
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
            eventEntities.map { eventEntity ->
                val photos = eventDao
                    .getPhotosByKeys(eventEntity.photoKeys)
                    .map { it.toPhoto() }
                eventEntity.toEvent(
                    photos = photos
                )
            }
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

    override suspend fun getFullAgenda(): EmptyResult<DataError.Network> {
        val result = safeCall {
            agendaApiService.getFullAgenda()
        }.onSuccess { getFullAgendaResponse ->
            upsertFullAgenda(
                tasks = getFullAgendaResponse.tasks.map { it.toTask() },
                events = getFullAgendaResponse.events.map { it.toEvent() },
                reminders = getFullAgendaResponse.reminders.map { it.toReminder() }
            )
            // schedule future alarms
        }
        return result.asEmptyDataResult()
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

    override suspend fun syncDeletedAgendaItems() {
        val deletedTaskIds = taskDao.getDeletedTaskSync(
            userId = localUserId!!
        )
        val deletedReminderIds = reminderDao.getDeletedReminderSync(
            userId = localUserId!!
        )
        if(
            deletedTaskIds.isEmpty() &&
            deletedReminderIds.isEmpty()
        ) return
        safeCall {
            agendaApiService.syncAgenda(
                SyncAgendaRequest(
                    deletedEventIds = listOf(), // TODO will be implemented
                    deletedTaskIds = deletedTaskIds,
                    deletedReminderIds = deletedReminderIds,
                )
            )
        }.onSuccess {
            taskDao.deleteDeletedTasksSync(deletedTaskIds)
            reminderDao.deleteDeletedRemindersSync(deletedReminderIds)
        }
    }
}