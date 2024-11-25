package com.example.studyassistant.studytracker.data.repository

import com.example.studyassistant.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.studytracker.data.mapper.toSession
import com.example.studyassistant.studytracker.data.mapper.toSessionEntity
import com.example.studyassistant.studytracker.domain.model.Session
import com.example.studyassistant.studytracker.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor (
    private val sessionDao: SessionDao
): SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session.toSessionEntity())
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session.toSessionEntity())
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .map { it -> it.toSession() }
        }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .take(5)
                .map { it.toSession() }
        }
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
       return sessionDao.getRecentSessionForSubject(subjectId).map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .take(10)
                .map { it.toSession() }
        }
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubject(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubject(subjectId)
    }
}