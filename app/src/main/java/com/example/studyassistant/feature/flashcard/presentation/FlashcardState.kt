package com.example.studyassistant.feature.flashcard.presentation

import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

data class FlashcardState(
    val question: String = "",
    val answer: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val flashcard: Flashcard? = null
)
