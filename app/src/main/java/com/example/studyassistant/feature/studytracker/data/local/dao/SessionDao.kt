package com.example.studyassistant.feature.studytracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.studyassistant.feature.studytracker.data.local.entity.SessionEntity
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
    fun getRecentSessionForSubject(subjectId: String): Flow<List<SessionEntity>>

    @Query("SELECT SUM(duration) FROM sessionentity")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("SELECT SUM(duration) FROM sessionentity WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubject(subjectId: String): Flow<Long>

    @Query("DELETE FROM sessionentity WHERE sessionSubjectId = :subjectId")
    suspend fun deleteSessionBySubjectId(subjectId: String)


    @Transaction
    suspend fun replaceAllSessions(newSessions: List<SessionEntity> ) {
        deleteAllSessions();
        upsertAllSessions(newSessions);
    }

    @Query("DELETE FROM sessionentity")
    fun deleteAllSessions()

    @Upsert
    fun upsertAllSessions(newSessions: List<SessionEntity>)

}