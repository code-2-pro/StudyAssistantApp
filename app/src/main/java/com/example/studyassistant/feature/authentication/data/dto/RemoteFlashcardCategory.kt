package com.example.studyassistant.feature.authentication.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteFlashcardCategory(
    val userId: String = "",
    val name: String = "",
    val isMeaningful: Boolean = false,
    val colors: List<Int> = emptyList(),
    val categoryId: String = ""
)
