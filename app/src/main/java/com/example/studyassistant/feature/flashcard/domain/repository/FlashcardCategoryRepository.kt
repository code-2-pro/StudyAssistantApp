package com.example.studyassistant.feature.flashcard.domain.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.flashcard.domain.model.CategoryWithFlashcardCount
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory
import kotlinx.coroutines.flow.Flow

interface FlashcardCategoryRepository {

    fun getAllCategories(): Flow<List<FlashcardCategory>>

    fun getCategoryWithFlashcardCount(): Flow<List<CategoryWithFlashcardCount>>

    fun getCategoryById(categoryId: String): Flow<FlashcardCategory?>

    suspend fun upsertCategory(category: FlashcardCategory)

    suspend fun upsertCategoryOnRemote(category: FlashcardCategory, userId: String): Result<Unit, RemoteDbError>

    suspend fun deleteCategory(categoryId: String)

    suspend fun deleteCategoryOnRemote(categoryId: String, userId: String): Result<Unit, RemoteDbError>
}