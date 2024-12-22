package com.example.studyassistant.feature.flashcard.presentation.category_detail

sealed interface CategoryDetailAction {
    data class GoToFlashcardScreen(val flashcardId: String?): CategoryDetailAction
    data class GenerateFlashcard(val cardQuantity: Int): CategoryDetailAction
}