package com.example.studyassistant.feature.flashcard.domain.repository

import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {

    fun getAllFlashcards(): Flow<List<Flashcard>>

    suspend fun addFlashcard(flashcard: Flashcard)

    suspend fun removeFlashcard(flashcard: Flashcard)
}