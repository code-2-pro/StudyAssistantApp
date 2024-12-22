package com.example.studyassistant.feature.flashcard.presentation.card

data class FlashcardState(
    val question: String = "",
    val answer: String = "",
    val relatedToCategory: String = "",
    val flashcardCategoryId: String = "",
    val currentFlashcardId: String = ""
)
