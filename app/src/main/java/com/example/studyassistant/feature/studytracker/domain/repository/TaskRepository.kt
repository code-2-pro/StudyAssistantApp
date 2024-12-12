package com.example.studyassistant.feature.studytracker.domain.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.studytracker.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task): Result<Unit, RemoteDbError>

    suspend fun deleteTask(taskId: String): Result<Unit, RemoteDbError>

    fun getTaskById(taskId: String): Flow<Task?>

    fun getUpcomingTasksForSubject(subjectId: String): Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectId: String): Flow<List<Task>>

    fun getAllUpcomingTasks(): Flow<List<Task>>

    suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError>
}