package com.example.studyassistant.data.mapper

import com.example.studyassistant.data.local.entity.SessionEntity
import com.example.studyassistant.domain.model.Session

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