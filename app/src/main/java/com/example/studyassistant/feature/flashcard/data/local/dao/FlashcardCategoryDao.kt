package com.example.studyassistant.feature.flashcard.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.studyassistant.feature.flashcard.data.local.entity.FlashcardCategoryEntity
import com.example.studyassistant.feature.flashcard.domain.model.CategoryWithFlashcardCount
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardCategoryDao {

    @Upsert
    suspend fun upsertCategory(categoryEntity: FlashcardCategoryEntity)

    @Query("SELECT * FROM flashcardCategoryentity WHERE categoryId = :categoryId")
    fun getCategoryById(categoryId: String): Flow<FlashcardCategoryEntity?>

    @Query("DELETE FROM flashcardCategoryentity WHERE categoryId = :categoryId")
    suspend fun deleteCategory(categoryId: String)

    @Query("SELECT * FROM flashcardCategoryentity")
    fun getAllCategories(): Flow<List<FlashcardCategoryEntity>>

    @Query("""
        SELECT 
            c.categoryId,
            c.name,
            c.isMeaningful,
            c.colors,
            COUNT(f.flashcardId) AS totalCards
        FROM flashcardCategoryentity c
        LEFT JOIN flashcardentity f ON c.categoryId = f.flashcardCategoryId
        GROUP BY c.categoryId, c.name, c.isMeaningful, c.colors
    """)
    fun getCategoryWithFlashcardCount(): Flow<List<CategoryWithFlashcardCount>>

    @Transaction
    suspend fun replaceAllCategories(newCategories: List<FlashcardCategoryEntity> ) {
        deleteAllFlashcardCategories();
        upsertAllFlashcardCategories(newCategories);
    }

    @Query("DELETE FROM flashcardCategoryentity")
    fun deleteAllFlashcardCategories()

    @Upsert
    fun upsertAllFlashcardCategories(newCategories: List<FlashcardCategoryEntity>)
    
}