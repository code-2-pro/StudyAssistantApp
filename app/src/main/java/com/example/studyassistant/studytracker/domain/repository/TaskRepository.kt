package com.example.studyassistant.studytracker.domain.repository

import com.example.studyassistant.studytracker.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    fun getTaskById(taskId: Int): Flow<Task?>

    fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>>

    fun getAllUpcomingTasks(): Flow<List<Task>>
}