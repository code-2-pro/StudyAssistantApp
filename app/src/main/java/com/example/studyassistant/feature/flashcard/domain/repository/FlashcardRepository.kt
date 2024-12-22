package com.example.studyassistant.feature.flashcard.domain.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {

    suspend fun upsertFlashcard(flashcard: Flashcard)

    suspend fun upsertFlashcardOnRemote(flashcard: Flashcard, userId: String): Result<Unit, RemoteDbError>

    suspend fun deleteFlashcard(flashcardId: String)

    suspend fun deleteFlashcardOnRemote(flashcardId: String, userId: String): Result<Unit, RemoteDbError>

    fun getFlashcardsForCategory(categoryId: String): Flow<List<Flashcard>>

    fun getFlashcardById(flashcardId: String): Flow<Flashcard?>

    fun getTotalFlashcardCountForCategory(categoryId: String): Flow<Int>
}