package com.example.studyassistant.feature.flashcard.data

import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcard
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardEntity
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao
): FlashcardRepository {
    override fun getAllFlashcards(): Flow<List<Flashcard>> {
        return flashcardDao.getAllFlashcards().map { flashcardEntities ->
            flashcardEntities.map { it.toFlashcard() }
        }
    }

    override suspend fun addFlashcard(flashcard: Flashcard) {
        flashcardDao.insertFlashcard(flashcard.toFlashcardEntity())
    }

    override suspend fun removeFlashcard(flashcard: Flashcard) {
        flashcardDao.deleteFlashcard(flashcard.toFlashcardEntity())
    }
}