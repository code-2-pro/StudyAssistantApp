package com.example.studyassistant.feature.flashcard.domain.model

data class CategoryWithFlashcardCount(
    val categoryId: String,
    val name: String,
    val totalCards: Int,
    val isMeaningful: Boolean,
    val colors: List<Int>
)
