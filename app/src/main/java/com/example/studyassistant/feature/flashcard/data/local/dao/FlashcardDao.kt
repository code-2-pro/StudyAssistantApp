package com.example.studyassistant.feature.flashcard.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.studyassistant.feature.flashcard.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Upsert
    suspend fun upsertFlashcard(flashcardEntity: FlashcardEntity)

    @Query("DELETE FROM flashcardentity WHERE flashcardId = :flashcardId")
    suspend fun deleteFlashcard(flashcardId: String)

    @Query("DELETE FROM flashcardentity WHERE flashcardCategoryId = :categoryId")
    suspend fun deleteFlashcardsByCategoryId(categoryId: String)

    @Query("SELECT * FROM flashcardentity WHERE flashcardId = :flashcardId")
    fun getFlashcardById(flashcardId: String): Flow<FlashcardEntity?>

    @Query("SELECT * FROM flashcardentity WHERE flashcardCategoryId = :categoryId")
    fun getFlashcardsForCategory(categoryId: String): Flow<List<FlashcardEntity>>

    @Query("SELECT COUNT(*) FROM flashcardentity WHERE flashcardCategoryId = :categoryId")
    fun getTotalFlashcardCountForCategory(categoryId: String): Flow<Int>

    @Query("SELECT * FROM flashcardentity")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Transaction
    suspend fun replaceAllFlashcards(newFlashcards: List<FlashcardEntity> ) {
        deleteAllFlashcards();
        upsertAllFlashcards(newFlashcards);
    }

    @Query("DELETE FROM flashcardentity")
    fun deleteAllFlashcards()

    @Upsert
    fun upsertAllFlashcards(newFlashcards: List<FlashcardEntity>)
}
