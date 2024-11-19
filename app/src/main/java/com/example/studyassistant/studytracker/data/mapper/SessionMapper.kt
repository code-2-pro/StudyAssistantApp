package com.example.studyassistant.studytracker.data.mapper

import com.example.studyassistant.studytracker.data.local.entity.SessionEntity
import com.example.studyassistant.studytracker.domain.model.Session

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