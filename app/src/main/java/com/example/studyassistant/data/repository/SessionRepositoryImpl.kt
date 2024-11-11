package com.example.studyassistant.data.repository

import com.example.studyassistant.data.local.dao.SessionDao
import com.example.studyassistant.data.mapper.toSession
import com.example.studyassistant.data.mapper.toSessionEntity
import com.example.studyassistant.domain.model.Session
import com.example.studyassistant.domain.repository.SessionRepository
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
            sessionEntities.map { it ->
                it.toSession()
            }
        }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().take(count = 5).map { sessionEntities ->
            sessionEntities.map { it.toSession() }
        }
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubjectId(subjectId)
    }
}