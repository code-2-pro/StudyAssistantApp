package com.example.studyassistant.feature.flashcard.data.mapper

import com.example.studyassistant.feature.authentication.data.dto.RemoteFlashcard
import com.example.studyassistant.feature.flashcard.data.local.entity.FlashcardEntity
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard

fun Flashcard.toFlashcardEntity(): FlashcardEntity {
    return FlashcardEntity(
        flashcardCategoryId = flashcardCategoryId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        flashcardId = flashcardId
    )
}

fun FlashcardEntity.toFlashcard(): Flashcard {
    return Flashcard(
        flashcardCategoryId = flashcardCategoryId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        flashcardId = flashcardId
    )
}

fun Flashcard.toRemoteFlashcard(userId: String): RemoteFlashcard {
    return RemoteFlashcard(
        userId = userId,
        flashcardCategoryId = flashcardCategoryId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        flashcardId = flashcardId
    )
}

fun RemoteFlashcard.toFlashcard(): Flashcard {
    return Flashcard(
        flashcardCategoryId = flashcardCategoryId,
        question = question,
        answer = answer,
        createdAt = createdAt,
        flashcardId = flashcardId
    )
}