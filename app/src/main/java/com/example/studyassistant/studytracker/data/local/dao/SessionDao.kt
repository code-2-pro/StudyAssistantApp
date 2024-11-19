package com.example.studyassistant.studytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studyassistant.studytracker.data.local.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun insertSession(sessionEntity: SessionEntity)

    @Delete
    suspend fun deleteSession(sessionEntity: SessionEntity)

    @Query("SELECT * FROM sessionentity")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessionentity WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionForSubject(subjectId: Int): Flow<List<SessionEntity>>

    @Query("SELECT SUM(duration) FROM sessionentity")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM sessionentity WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long>

    @Query("DELETE FROM sessionentity WHERE sessionSubjectId = :subjectId")
    suspend fun deleteSessionBySubjectId(subjectId: Int)
}