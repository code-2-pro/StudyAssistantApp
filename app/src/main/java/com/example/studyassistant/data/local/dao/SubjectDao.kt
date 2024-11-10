package com.example.studyassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studyassistant.data.local.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Upsert
    suspend fun upsertSubject(subjectEntity: SubjectEntity)

    @Query("SELECT COUNT(*) FROM subjectentity")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHours) FROM subjectentity")
    fun getTotalGoalHours(): Flow<Float>

    @Query("SELECT * FROM subjectentity WHERE subjectId = :subjectId")
    suspend fun getSubjectById(subjectId: Int): SubjectEntity?

    @Query("DELETE FROM subjectentity WHERE subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)

    @Query("SELECT * FROM subjectentity")
    fun getAllSubjects(): Flow<List<SubjectEntity>>
}