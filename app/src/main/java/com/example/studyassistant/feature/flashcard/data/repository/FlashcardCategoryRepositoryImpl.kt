package com.example.studyassistant.feature.flashcard.data.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardCategoryDao
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardDao
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardCategory
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardCategoryEntity
import com.example.studyassistant.feature.flashcard.data.mapper.toRemoteFlashcardCategory
import com.example.studyassistant.feature.flashcard.domain.model.CategoryWithFlashcardCount
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardCategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.collections.map

class FlashcardCategoryRepositoryImpl @Inject constructor(
    private val categoryDao: FlashcardCategoryDao,
    private val flashcardDao: FlashcardDao,
    private val remoteDb: FirebaseFirestore
): FlashcardCategoryRepository {

    override fun getAllCategories(): Flow<List<FlashcardCategory>> {
        return categoryDao.getAllCategories().map { categoryEntities ->
            categoryEntities
                .map { it.toFlashcardCategory() }
        }
    }

    override fun getCategoryWithFlashcardCount(): Flow<List<CategoryWithFlashcardCount>> {
        return categoryDao.getCategoryWithFlashcardCount()
    }

    override fun getCategoryById(categoryId: String): Flow<FlashcardCategory?> {
        return categoryDao.getCategoryById(categoryId).map {
            it?.toFlashcardCategory()
        }
    }

    override suspend fun upsertCategory(category: FlashcardCategory) {
        categoryDao.upsertCategory(category.toFlashcardCategoryEntity())
    }

    override suspend fun upsertCategoryOnRemote(
        category: FlashcardCategory,
        userId: String,
    ): Result<Unit, RemoteDbError> {
        val categoryCollectionRef = remoteDb.collection("FlashcardCategory")
        val remoteCategory = category.toRemoteFlashcardCategory(userId)
        try{
            val querySnapshot = categoryCollectionRef
                .whereEqualTo("userId", remoteCategory.userId)
                .whereEqualTo("categoryId", remoteCategory.categoryId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val categoryRef = querySnapshot.documents[0].reference
                categoryRef.set(remoteCategory, SetOptions.merge()).await()
            } else {
                // Create a new document
                categoryCollectionRef.add(remoteCategory).await()
            }
        }catch (e: Exception){
            return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteCategory(categoryId)
        flashcardDao.deleteFlashcardsByCategoryId(categoryId)
    }

    override suspend fun deleteCategoryOnRemote(
        categoryId: String,
        userId: String,
    ): Result<Unit, RemoteDbError> {
        val categoryCollectionRef = remoteDb.collection("FlashcardCategory")
        val flashcardCollectionRef = remoteDb.collection("Flashcard")
        val categoryQuery = categoryCollectionRef
            .whereEqualTo("userId", userId)
            .whereEqualTo("categoryId", categoryId)
        val flashcardQuery = flashcardCollectionRef
            .whereEqualTo("userId", userId)
            .whereEqualTo("flashcardCategoryId", categoryId)

        try{
            val batch = remoteDb.batch()

            // Add category deletions to the batch
            val categoryQuerySnapshot = categoryQuery.get().await()
            for (document in categoryQuerySnapshot.documents) {
                batch.delete(document.reference)
            }

            // Add flashcard deletions to the batch
            val flashcardQuerySnapshot = flashcardQuery.get().await()
            for (document in flashcardQuerySnapshot.documents) {
                batch.delete(document.reference)
            }

            // Commit the batch
            batch.commit().await()

        }catch (e: Exception){
            return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
        }
        return Result.Success(Unit)
    }
    
}