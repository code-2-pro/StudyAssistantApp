package com.example.studyassistant.feature.studytracker.domain.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.studytracker.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)

    suspend fun insertSessionOnRemote(session: Session, userId: String): Result<Unit, RemoteDbError>

    suspend fun deleteSession(session: Session)

    suspend fun deleteSessionOnRemote(session: Session , userId: String): Result<Unit, RemoteDbError>

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentFiveSessions(): Flow<List<Session>>

    fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>>

    fun getTotalSessionsDuration(): Flow<Long>

    fun getTotalSessionDurationBySubject(subjectId: String): Flow<Long>

    suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError>
}