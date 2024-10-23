package com.humberto.tasky.core.database.data_source

import android.database.sqlite.SQLiteFullException
import com.humberto.tasky.core.database.dao.AgendaDao
import com.humberto.tasky.core.database.dao.EventDao
import com.humberto.tasky.core.database.dao.ReminderDao
import com.humberto.tasky.core.database.dao.TaskDao
import com.humberto.tasky.core.database.mapper.toAttendee
import com.humberto.tasky.core.database.mapper.toEvent
import com.humberto.tasky.core.database.mapper.toEventEntity
import com.humberto.tasky.core.database.mapper.toPhoto
import com.humberto.tasky.core.database.mapper.toReminder
import com.humberto.tasky.core.database.mapper.toReminderEntity
import com.humberto.tasky.core.database.mapper.toTask
import com.humberto.tasky.core.database.mapper.toTaskEntity
import com.humberto.tasky.core.domain.agenda.Agenda
import com.humberto.tasky.core.domain.agenda.LocalAgendaDataSource
import com.humberto.tasky.core.domain.event.Event
import com.humberto.tasky.core.domain.reminder.Reminder
import com.humberto.tasky.core.domain.task.Task
import com.humberto.tasky.core.domain.util.DataError
import com.humberto.tasky.core.domain.util.Result
import com.humberto.tasky.core.domain.util.toEndOfDayUtc
import com.humberto.tasky.core.domain.util.toStartOfDayUtc
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RoomLocalAgendaDataSource(
    private val agendaDao: AgendaDao,
    private val taskDao: TaskDao,
    private val eventDao: EventDao,
    private val reminderDao: ReminderDao,
): LocalAgendaDataSource {
    private suspend fun upsertTasks(
        tasks: List<Task>
    ): Result<Unit, DataError.Local> {
        return try {
            val taskEntities = tasks.map { it.toTaskEntity() }
            val taskIds = taskDao.upsertTasks(taskEntities)
            Result.Success(taskIds)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    private suspend fun upsertEvents(
        events: List<Event>
    ): Result<Unit, DataError.Local> {
        return try {
            val eventEntities = events.map { it.toEventEntity() }
            val eventIds = eventDao.upsertEvents(eventEntities)
            Result.Success(eventIds)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    private suspend fun upsertReminders(
        reminders: List<Reminder>
    ): Result<Unit, DataError.Local> {
        return try {
            val reminderEntities = reminders.map { it.toReminderEntity() }
            val reminderIds = reminderDao.upsertReminders(reminderEntities)
            Result.Success(reminderIds)
        } catch (e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAgendaForDate(localDate: LocalDate): Flow<Agenda> {
        val startOfDay = localDate.toStartOfDayUtc().toInstant().toEpochMilli()
        val endOfDay = localDate.toEndOfDayUtc().toInstant().toEpochMilli()
        
        val tasksFlow = agendaDao.getTasksForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        ).map { taskEntities ->
            taskEntities.map { it.toTask() }
        }
        val eventsFlow = agendaDao.getEventsForDay(
            startOfDay = startOfDay,
            endOfDay = endOfDay
        ).map { eventEntities ->
            eventEntities.map { eventEntity ->
                val attendees = eventDao
                    .getAttendeesByIds(eventEntity.attendeeIds)
                    .map { it.toAttendee() }
                val photos = eventDao
                    .getPhotosByKeys(eventEntity.photoKeys)
                    .map { it.toPhoto() }
                eventEntity.toEvent(
                    attendees = attendees,
                    photos = photos
                )
            }
        }
        val remindersFlow = agendaDao.getRemindersForDay(
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
        ) {
          tasks,
          events,
          reminders ->
            Agenda(
                tasks = tasks,
                events = events,
                reminders = reminders
            )
        }
    }

    override suspend fun upsertFullAgenda(
        tasks: List<Task>,
        events: List<Event>,
        reminders: List<Reminder>
    ): Result<Unit, DataError.Local> {
        val eventResult = upsertEvents(events)
        val taskResult = upsertTasks(tasks)
        val reminderResult = upsertReminders(reminders)

        return when {
            eventResult is Result.Error -> Result.Error(eventResult.error)
            taskResult is Result.Error -> Result.Error(taskResult.error)
            reminderResult is Result.Error -> Result.Error(reminderResult.error)
            else -> Result.Success(Unit)
        }
    }

    override suspend fun deleteAllAgenda() {
        taskDao.deleteAllTasks()
        eventDao.deleteAllEvents()
        reminderDao.deleteAllReminders()
    }
}