package com.example.studyassistant.feature.authentication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteSession(
    val userId: String = "",
    val sessionSubjectId: String = "",
    val relatedToSubject: String = "",
    val date: Long = 0,
    val duration: Long = 0,
    val sessionId: String = ""
)
