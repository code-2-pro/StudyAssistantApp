package com.example.studyassistant.feature.flashcard.presentation.card_display

import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

data class FlashcardDisplayState(
    val categoryName: String = "",
    val flashcards: List<Flashcard> = emptyList(),
)
