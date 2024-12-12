package com.example.studyassistant.feature.studytracker.domain.model

data class Session(
    val sessionSubjectId: String,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    val sessionId: String
)
