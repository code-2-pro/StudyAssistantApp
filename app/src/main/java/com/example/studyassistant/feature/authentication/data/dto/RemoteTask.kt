package com.example.studyassistant.feature.authentication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteTask(
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: Long = 0,
    val priority: Int = 0,
    val relatedToSubject: String = "",
    val isComplete: Boolean = false,
    val taskSubjectId: String = "",
    val taskId: String = ""
)
