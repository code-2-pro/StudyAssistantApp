package com.example.studyassistant.feature.flashcard.domain.model

data class Flashcard(
    val flashcardCategoryId: String,
    val question: String,
    val answer: String,
    val createdAt: Long,
    val flashcardId: String
)

