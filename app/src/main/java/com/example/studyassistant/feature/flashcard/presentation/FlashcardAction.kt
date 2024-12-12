package com.example.studyassistant.feature.flashcard.presentation

import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

sealed interface FlashcardAction {
    object SaveFlashcard: FlashcardAction
    data class DeleteFlashcard(val flashcard: Flashcard): FlashcardAction
}