package com.example.studyassistant.feature.studytracker.data.mapper

import com.example.studyassistant.feature.authentication.data.dto.RemoteSession
import com.example.studyassistant.feature.authentication.domain.model.User
import com.example.studyassistant.feature.studytracker.data.local.entity.SessionEntity
import com.example.studyassistant.feature.studytracker.domain.model.Session

fun SessionEntity.toSession(): Session{
    return Session(
        sessionSubjectId = sessionSubjectId,
        relatedToSubject = relatedToSubject,
        date = date,
        duration = duration,
        sessionId = sessionId
    )
}

fun Session.toSessionEntity(): SessionEntity{
    return SessionEntity(
        sessionSubjectId = sessionSubjectId,
        relatedToSubject = relatedToSubject,
        date = date,
        duration = duration,
        sessionId = sessionId
    )
}
fun Session.toRemoteSession(user: User): RemoteSession{
    return RemoteSession(
        userId = user.userId?: "",
        sessionSubjectId = sessionSubjectId,
        relatedToSubject = relatedToSubject,
        date = date,
        duration = duration,
        sessionId = sessionId
    )
}

fun RemoteSession.toSession(): Session{
    return Session(
        sessionSubjectId = sessionSubjectId,
        relatedToSubject = relatedToSubject,
        date = date,
        duration = duration,
        sessionId = sessionId
    )
}