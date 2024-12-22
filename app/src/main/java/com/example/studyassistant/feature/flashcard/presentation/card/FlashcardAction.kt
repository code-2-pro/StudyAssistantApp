package com.example.studyassistant.feature.flashcard.presentation.card

sealed interface FlashcardAction {
    data class OnQuestionChange(val question: String): FlashcardAction
    data class OnAnswerChange(val answer: String): FlashcardAction
    object SaveFlashcard: FlashcardAction
    object DeleteFlashcard: FlashcardAction
}