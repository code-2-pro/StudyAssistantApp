package com.example.studyassistant.feature.authentication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteFlashcard(
    val userId: String = "",
    val flashcardCategoryId: String = "",
    val question: String = "",
    val answer: String = "",
    val createdAt: Long = 0,
    val flashcardId: String = ""
)
