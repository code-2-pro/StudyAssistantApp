package com.example.studyassistant.feature.flashcard.data.mapper

import com.example.studyassistant.feature.flashcard.data.FlashcardEntity
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

fun Flashcard.toFlashcardEntity(): FlashcardEntity {
    return FlashcardEntity(
        question = question,
        answer = answer,
        flashcardId = flashcardId
    )
}

fun FlashcardEntity.toFlashcard(): Flashcard {
    return Flashcard(
        question = question,
        answer = answer,
        flashcardId = flashcardId
    )
}