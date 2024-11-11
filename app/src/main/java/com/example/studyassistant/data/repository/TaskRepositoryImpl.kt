package com.example.studyassistant.data.repository

import com.example.studyassistant.data.local.dao.TaskDao
import com.example.studyassistant.data.mapper.toTask
import com.example.studyassistant.data.mapper.toTaskEntity
import com.example.studyassistant.domain.model.Task
import com.example.studyassistant.domain.repository.TaskRepository
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

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)?.toTask()
    }

    override fun getUpcomingTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getCompletedTasksForSubject(subjectId: Int): Flow<List<Task>> {
        TODO("Not yet implemented")
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { tasks ->
            tasks.filter { it.isComplete.not() }
                .map { it -> it.toTask() } }
                .map { tasks -> sortTasks(tasks)
                }
        }
    }

    private fun sortTasks(tasks: List<Task>): List<Task>{
        return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })
    }