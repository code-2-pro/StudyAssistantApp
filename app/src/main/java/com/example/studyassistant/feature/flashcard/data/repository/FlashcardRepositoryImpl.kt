package com.example.studyassistant.feature.flashcard.data.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardDao
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcard
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardEntity
import com.example.studyassistant.feature.flashcard.data.mapper.toRemoteFlashcard
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val remoteDb: FirebaseFirestore
): FlashcardRepository {

    override suspend fun upsertFlashcard(flashcard: Flashcard) {
        flashcardDao.upsertFlashcard(flashcard.toFlashcardEntity())
    }

    override suspend fun upsertFlashcardOnRemote(
        flashcard: Flashcard,
        userId: String,
    ): Result<Unit, RemoteDbError> {
        val flashcardCollectionRef = remoteDb.collection("Flashcard")
        val remoteFlashcard = flashcard.toRemoteFlashcard(userId)
        try{
            val querySnapshot = flashcardCollectionRef
                .whereEqualTo("userId", remoteFlashcard.userId)
                .whereEqualTo("flashcardId", remoteFlashcard.flashcardId)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val flashcardRef = querySnapshot.documents[0].reference
                flashcardRef.set(remoteFlashcard, SetOptions.merge()).await()
            } else {
                // Create a new document
                flashcardCollectionRef.add(remoteFlashcard).await()
            }
        }catch (e: Exception){
            return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteFlashcard(flashcardId: String) {
        flashcardDao.deleteFlashcard(flashcardId)
    }

    override suspend fun deleteFlashcardOnRemote(
        flashcardId: String,
        userId: String,
    ): Result<Unit, RemoteDbError> {
        val flashcardCollectionRef = remoteDb.collection("Flashcard")
        val flashcardQuery = flashcardCollectionRef
            .whereEqualTo("userId", userId)
            .whereEqualTo("flashcardId", flashcardId)
        try{
            val flashcardQuerySnapshot = flashcardQuery.get().await()
            for (document in flashcardQuerySnapshot.documents) {
                document.reference.delete().await()
            }
        }catch (e: Exception){
            return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
        }
        return Result.Success(Unit)
    }

    override fun getFlashcardsForCategory(categoryId: String): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsForCategory(categoryId).map { flashcardEntities ->
            flashcardEntities
                .map { flashcardEntity -> flashcardEntity.toFlashcard() }
                .sortedBy { it.createdAt }
        }
    }

    override fun getFlashcardById(flashcardId: String): Flow<Flashcard?> {
        return flashcardDao.getFlashcardById(flashcardId).map {
            it?.toFlashcard()
        }
    }

    override fun getTotalFlashcardCountForCategory(categoryId: String): Flow<Int> {
        return flashcardDao.getTotalFlashcardCountForCategory(categoryId)
    }
}