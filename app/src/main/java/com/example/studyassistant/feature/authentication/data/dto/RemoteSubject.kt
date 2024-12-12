package com.example.studyassistant.feature.authentication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteSubject(
    val userId: String = "",
    val name: String = "",
    val goalHours: Float = 0f,
    val colors: List<Int> = emptyList(),
    val subjectId: String = ""
)


