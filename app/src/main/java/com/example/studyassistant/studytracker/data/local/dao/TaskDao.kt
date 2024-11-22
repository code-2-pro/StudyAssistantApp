package com.example.studyassistant.studytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studyassistant.studytracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {


    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Query("DELETE FROM taskentity WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("DELETE FROM taskentity WHERE taskSubjectId = :subjectId")
    suspend fun deleteTasksBySubjectId(subjectId: Int)

    @Query("SELECT * FROM taskentity WHERE taskId = :taskId")
    fun getTaskById(taskId: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM taskentity WHERE taskSubjectId = :subjectId")
    fun getTasksForSubject(subjectId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM taskentity")
    fun getAllTasks(): Flow<List<TaskEntity>>
}