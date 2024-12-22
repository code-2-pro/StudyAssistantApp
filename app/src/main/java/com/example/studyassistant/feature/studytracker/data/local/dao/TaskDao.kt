package com.example.studyassistant.feature.studytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.studyassistant.feature.studytracker.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(taskEntity: TaskEntity)

    @Query("DELETE FROM taskentity WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: String)

    @Query("DELETE FROM taskentity WHERE taskSubjectId = :subjectId")
    suspend fun deleteTasksBySubjectId(subjectId: String)

    @Query("SELECT * FROM taskentity WHERE taskId = :taskId")
    fun getTaskById(taskId: String): Flow<TaskEntity?>

    @Query("SELECT * FROM taskentity WHERE taskSubjectId = :subjectId")
    fun getTasksForSubject(subjectId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM taskentity")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Transaction
    suspend fun replaceAllTasks(newTasks: List<TaskEntity> ) {
        deleteAllTasks();
        upsertAllTasks(newTasks);
    }

    @Query("DELETE FROM taskentity")
    fun deleteAllTasks()

    @Upsert
    fun upsertAllTasks(newTasks: List<TaskEntity>)

}