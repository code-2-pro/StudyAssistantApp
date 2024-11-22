package com.example.studyassistant.studytracker.data.repository

import com.example.studyassistant.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.studytracker.data.mapper.toTask
import com.example.studyassistant.studytracker.data.mapper.toTaskEntity
import com.example.studyassistant.studytracker.data.util.sortTasks
import com.example.studyassistant.studytracker.domain.model.Task
import com.example.studyassistant.studytracker.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
): TaskRepository {
    override suspend fun upsertTask(task: Task) {
        taskDao.upsertTask(task.toTaskEntity())
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override fun getTaskById(taskId: Int): Flow<Task?> {
        return taskDao.getTaskById(taskId). map {
            it?.toTask()
        }
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { taskEntities ->
            taskEntities.filter { it.isComplete.not() }
                .map { taskEntity -> taskEntity.toTask() } }
            .map { tasks -> sortTasks(tasks)
            }
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { taskEntities ->
            taskEntities.filter { it.isComplete }
                .map { taskEntity -> taskEntity.toTask() } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { taskEntities ->
            taskEntities.filter { it.isComplete.not() }
                .map { taskEntity -> taskEntity.toTask() } }
            .map { tasks -> sortTasks(tasks) }
        }
    }

