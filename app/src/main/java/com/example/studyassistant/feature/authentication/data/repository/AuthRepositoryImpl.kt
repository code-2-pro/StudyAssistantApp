package com.example.studyassistant.feature.authentication.data.repository

import android.util.Log
import com.example.studyassistant.core.domain.util.AuthError
import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.authentication.data.dto.RemoteFlashcard
import com.example.studyassistant.feature.authentication.data.dto.RemoteFlashcardCategory
import com.example.studyassistant.feature.authentication.data.dto.RemoteSession
import com.example.studyassistant.feature.authentication.data.dto.RemoteSubject
import com.example.studyassistant.feature.authentication.data.dto.RemoteTask
import com.example.studyassistant.feature.authentication.data.mapper.toUser
import com.example.studyassistant.feature.authentication.data.util.mapAuthExceptionToError
import com.example.studyassistant.feature.authentication.domain.model.User
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardCategoryDao
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardDao
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcard
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardCategory
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardCategoryEntity
import com.example.studyassistant.feature.flashcard.data.mapper.toFlashcardEntity
import com.example.studyassistant.feature.flashcard.data.mapper.toRemoteFlashcard
import com.example.studyassistant.feature.flashcard.data.mapper.toRemoteFlashcardCategory
import com.example.studyassistant.feature.flashcard.domain.model.Flashcard
import com.example.studyassistant.feature.flashcard.domain.model.FlashcardCategory
import com.example.studyassistant.feature.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.feature.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.feature.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteSession
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteSubject
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteTask
import com.example.studyassistant.feature.studytracker.data.mapper.toSession
import com.example.studyassistant.feature.studytracker.data.mapper.toSessionEntity
import com.example.studyassistant.feature.studytracker.data.mapper.toSubject
import com.example.studyassistant.feature.studytracker.data.mapper.toSubjectEntity
import com.example.studyassistant.feature.studytracker.data.mapper.toTask
import com.example.studyassistant.feature.studytracker.data.mapper.toTaskEntity
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.domain.model.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val remoteDb: FirebaseFirestore,
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao,
    private val flashcardDao: FlashcardDao,
    private val flashcardCategoryDao: FlashcardCategoryDao,
): AuthRepository {

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser
        return firebaseUser?.toUser()
    }

    override suspend fun login(
        email: String,
        password: String,
    ): Result<User, AuthError> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                Result.Success(firebaseUser.toUser())
            } else {
                Result.Error(AuthError.UNKNOWN)
            }
        } catch (e: Exception) {
            Log.d("Auth Exception", "Exception class: ${e::class.java.name}")
            Log.d("Auth Exception", "Exception message: ${e.message}")
            mapAuthExceptionToError(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<User, AuthError> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            // After user is created, update their profile with the display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser?.updateProfile(profileUpdates)?.await()

            if (firebaseUser != null) {
                Result.Success(firebaseUser.toUser())
            } else {
                Result.Error(AuthError.UNKNOWN)
            }
        } catch (e: Exception) {
            Log.d("Auth Exception", "Exception class: ${e::class.java.name}")
            Log.d("Auth Exception", "Exception message: ${e.message}")
            mapAuthExceptionToError(e)
        }
    }

    override suspend fun updateUserInfo(
        currentPassword: String,
        newDisplayName: String,
        newEmail: String,
        newPassword: String
    ): Result<Unit, AuthError> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // ReAuthenticate the user
                val credential = EmailAuthProvider.getCredential(currentUser.email.toString(), currentPassword)
                currentUser.reauthenticate(credential).await()

                // Update display name if provided
                if (newDisplayName != currentUser.displayName) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newDisplayName)
                        .build()
                    currentUser.updateProfile(profileUpdates).await()
                }

                // Update email if provided
                if (newEmail.isNotBlank() && newEmail != currentUser.email) {
                    currentUser.updateEmail(newEmail).await()
                }

                // Update password if provided
                if (newPassword.isNotBlank()) {
                    currentUser.updatePassword(newPassword).await()
                }
                Result.Success(Unit)
            }else{
                Result.Error(AuthError.USER_NOT_FOUND)
            }
        } catch (e: Exception) {
            Log.d("Auth Exception", "Exception class: ${e::class.java.name}")
            Log.d("Auth Exception", "Exception message: ${e.message}")
            mapAuthExceptionToError(e)
        }
    }


    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun catchRealtimeUpdates() {
//        subjectRepository.subscribeToRealtimeUpdates()
//        taskRepository.subscribeToRealtimeUpdates()
//        sessionRepository.subscribeToRealtimeUpdates()
    }

    override suspend fun getRemoteDataForLocal(): Result<Unit, RemoteDbError> {
        val currentUser = auth.currentUser?.toUser()
        currentUser?.let {
            try{
                withContext(Dispatchers.IO){
                    // Fetch data concurrently
                    val remoteSubjectsDeferred = async {
                        remoteDb.collection("Subject")
                            .whereEqualTo("userId", currentUser.userId).get().await()
                            .mapNotNull { document ->
                                try {
                                    document.toObject(RemoteSubject::class.java).toSubject()
                                } catch (e: Exception) {
                                    Log.e("SyncError", "Failed to map document in Subject: ${document.id}", e)
                                    null
                                }
                            }.map { it.toSubjectEntity() }
                    }
                    val remoteTasksDeferred = async {
                        remoteDb.collection("Task")
                            .whereEqualTo("userId", currentUser.userId).get().await()
                            .mapNotNull { document ->
                                try {
                                    document.toObject(RemoteTask::class.java).toTask()
                                } catch (e: Exception) {
                                    Log.e("SyncError", "Failed to map document in Task: ${document.id}", e)
                                    null
                                }
                            }.map { it.toTaskEntity() }
                    }
                    val remoteSessionsDeferred = async {
                        remoteDb.collection("Session")
                            .whereEqualTo("userId", currentUser.userId).get().await()
                            .mapNotNull { document ->
                                try {
                                    document.toObject(RemoteSession::class.java).toSession()
                                } catch (e: Exception) {
                                    Log.e("SyncError", "Failed to map document in Session: ${document.id}", e)
                                    null
                                }
                            }.map { it.toSessionEntity() }
                    }
                    val remoteFlashcardDeferred = async {
                        remoteDb.collection("Flashcard")
                            .whereEqualTo("userId", currentUser.userId).get().await()
                            .mapNotNull { document ->
                                try {
                                    document.toObject(RemoteFlashcard::class.java).toFlashcard()
                                } catch (e: Exception) {
                                    Log.e("SyncError", "Failed to map document in Flashcard: ${document.id}", e)
                                    null
                                }
                            }.map { it.toFlashcardEntity() }
                    }
                    val remoteFlashcardCategoryDeferred = async {
                        remoteDb.collection("FlashcardCategory")
                            .whereEqualTo("userId", currentUser.userId).get().await()
                            .mapNotNull { document ->
                                try {
                                    document.toObject(RemoteFlashcardCategory::class.java).toFlashcardCategory()
                                } catch (e: Exception) {
                                    Log.e("SyncError", "Failed to map document in Flashcard Category: ${document.id}", e)
                                    null
                                }
                            }.map { it.toFlashcardCategoryEntity() }
                    }

                    // Await the results and convert to lists
                    val remoteSubjects = remoteSubjectsDeferred.await()
                    val remoteTasks = remoteTasksDeferred.await()
                    val remoteSessions = remoteSessionsDeferred.await()
                    val remoteFlashcard = remoteFlashcardDeferred.await()
                    val remoteFlashcardCategory = remoteFlashcardCategoryDeferred.await()

                    // Update local database
                    subjectDao.replaceAllSubjects(remoteSubjects)
                    taskDao.replaceAllTasks(remoteTasks)
                    sessionDao.replaceAllSessions(remoteSessions)
                    flashcardDao.replaceAllFlashcards(remoteFlashcard)
                    flashcardCategoryDao.replaceAllCategories(remoteFlashcardCategory)
                }
            }catch (e: Exception){
                return Result.Error(RemoteDbError(message = e.message ?: "Unknown sync error"))
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun sendLocalDataToRemote(): Result<Unit, RemoteDbError> {
        val currentUser = auth.currentUser?.toUser()
        currentUser?.let {
            try {
                withContext(Dispatchers.IO) {
                    // Delete all data in Firestore collections
                    val subjectCollectionRef = remoteDb.collection("Subject")
                    val taskCollectionRef = remoteDb.collection("Task")
                    val sessionCollectionRef = remoteDb.collection("Session")
                    val flashcardCollectionRef = remoteDb.collection("Flashcard")
                    val flashcardCategoryCollectionRef = remoteDb.collection("FlashcardCategory")

                    // Remove data in Firestore
                    deleteCollection(subjectCollectionRef)
                    deleteCollection(taskCollectionRef)
                    deleteCollection(sessionCollectionRef)
                    deleteCollection(flashcardCollectionRef)
                    deleteCollection(flashcardCategoryCollectionRef)

                    // Collect data from the local database
                    val localSubjects = subjectDao.getAllSubjects().first().map { it.toSubject() }
                    val localTasks = taskDao.getAllTasks().first().map { it.toTask() }
                    val localSessions = sessionDao.getAllSessions().first().map { it.toSession() }
                    val localFlashcard = flashcardDao.getAllFlashcards().first().map { it.toFlashcard() }
                    val localFlashcardCategory = flashcardCategoryDao.getAllCategories().first().map { it.toFlashcardCategory() }

                    // Initialize batch
                    val batch = remoteDb.batch()

                    // Add subjects to Firestore
                    localSubjects.forEach { subject ->
                        val remoteSubject = subject.toRemoteSubject(currentUser.userId.toString())
                        val docRef = subjectCollectionRef.document()
                        batch.set(docRef, remoteSubject)
                    }

                    // Add tasks to Firestore
                    localTasks.forEach { task ->
                        val remoteTask = task.toRemoteTask(currentUser.userId.toString())
                        val docRef = taskCollectionRef.document()
                        batch.set(docRef, remoteTask)
                    }

                    // Add sessions to Firestore
                    localSessions.forEach { session ->
                        val remoteSession = session.toRemoteSession(currentUser.userId.toString())
                        val docRef = sessionCollectionRef.document()
                        batch.set(docRef, remoteSession)
                    }

                    // Add flashcards to Firestore
                    localFlashcard.forEach { flashcard ->
                        val remoteFlashcard = flashcard.toRemoteFlashcard(currentUser.userId.toString())
                        val docRef = flashcardCollectionRef.document()
                        batch.set(docRef, remoteFlashcard)
                    }

                    // Add categories to Firestore
                    localFlashcardCategory.forEach { flashcardCategory ->
                        val remoteFlashcardCategory =
                            flashcardCategory.toRemoteFlashcardCategory(currentUser.userId.toString())
                        val docRef = flashcardCategoryCollectionRef.document()
                        batch.set(docRef, remoteFlashcardCategory)
                    }
                    // Commit the batch
                    batch.commit().await()
                }
            } catch (e: Exception) {
                return Result.Error(RemoteDbError(message = e.message ?: "Unknown sync error"))
            }
        }
        return Result.Success(Unit)
    }

    // Helper function to delete all documents in a Firestore collection.
    private suspend fun deleteCollection(collectionRef: CollectionReference) {
        try {
            val documents = collectionRef.get().await().documents
            val batch = remoteDb.batch()

            documents.forEach { doc ->
                batch.delete(doc.reference)
            }

            // Commit the batch
            batch.commit().await()
        } catch (e: Exception) {
            println("Error deleting collection ${collectionRef.path}: ${e.message}")
        }
    }

    override suspend fun checkDataConsistency(): Result<Map<String, Int>, RemoteDbError>{
        Log.e("SyncProcess", "Start")
        val currentUser = auth.currentUser?.toUser()
        currentUser?.let {
            // Collecting the flows once
            val localSubjects: Set<Subject>
            val localTasks: Set<Task>
            val localSessions: Set<Session>
            val localFlashcard: Set<Flashcard>
            val localFlashcardCategory: Set<FlashcardCategory>

            try {
                localSubjects = subjectDao.getAllSubjects().first().map { it.toSubject() }.toSet()
                localTasks = taskDao.getAllTasks().first().map { it.toTask() }.toSet()
                localSessions = sessionDao.getAllSessions().first().map { it.toSession() }.toSet()
                localFlashcard = flashcardDao.getAllFlashcards().first().map { it.toFlashcard() }.toSet()
                localFlashcardCategory = flashcardCategoryDao.getAllCategories().first().map { it.toFlashcardCategory() }.toSet()
            } catch (e: Exception) {
                Log.e("SyncProcess", "Error collecting local data: ${e.message}")
                return Result.Error(RemoteDbError(message = e.message ?: "Unknown error."))
            }
            Log.e("SyncProcess", "Local data collected")


            val remoteSubjects: List<Subject>
            val remoteTasks: List<Task>
            val remoteSessions: List<Session>
            val remoteFlashcard: List<Flashcard>
            val remoteFlashcardCategory: List<FlashcardCategory>
            // Get User data only
            try {
                remoteSubjects = remoteDb.collection("Subject")
                    .whereEqualTo("userId", currentUser.userId).get().await()
                    .map { document -> document.toObject(RemoteSubject::class.java).toSubject() }
                remoteTasks = remoteDb.collection("Task")
                    .whereEqualTo("userId", currentUser.userId).get().await()
                    .map { document -> document.toObject(RemoteTask::class.java).toTask() }
                remoteSessions = remoteDb.collection("Session")
                    .whereEqualTo("userId", currentUser.userId).get().await()
                    .map { document -> document.toObject(RemoteSession::class.java).toSession() }
                remoteFlashcard = remoteDb.collection("Flashcard")
                    .whereEqualTo("userId", currentUser.userId).get().await()
                    .map { document -> document.toObject(RemoteFlashcard::class.java).toFlashcard() }
                remoteFlashcardCategory = remoteDb.collection("FlashcardCategory")
                    .whereEqualTo("userId", currentUser.userId).get().await()
                    .map { document -> document.toObject(RemoteFlashcardCategory::class.java).toFlashcardCategory() }
            } catch (e: Exception) {
                Log.d("Sync Exception", "Exception class: ${e::class.java.name}")
                Log.d("Sync Exception", "Exception message: ${e.message}")
                return Result.Error(RemoteDbError(message = e.message ?: "Unknown error."))
            }

            Log.e("SyncProcess", "Remote")

            val changesInRemoteSubject = remoteSubjects.filterNot { it in localSubjects }.size
            val changesInRemoteTask = remoteTasks.filterNot { it in localTasks }.size
            val changesInRemoteSession = remoteSessions.filterNot { it in localSessions }.size
            val changesInRemoteFlashcard = remoteFlashcard.filterNot { it in localFlashcard }.size
            val changesInRemoteFlashcardCategory = remoteFlashcardCategory.filterNot { it in localFlashcardCategory }.size
            val changeInRemote =
                changesInRemoteSubject + changesInRemoteTask + changesInRemoteSession +
                        changesInRemoteFlashcard + changesInRemoteFlashcardCategory

            val changesInLocalSubject = localSubjects.filterNot { it in remoteSubjects }.size
            val changesInLocalTask = localTasks.filterNot { it in remoteTasks }.size
            val changesInLocalSession = localSessions.filterNot { it in remoteSessions }.size
            val changesInLocalFlashcard = localFlashcard.filterNot { it in remoteFlashcard }.size
            val changesInLocalFlashcardCategory = localFlashcardCategory.filterNot { it in remoteFlashcardCategory }.size
            val changeInLocal = changesInLocalSubject + changesInLocalTask + changesInLocalSession +
                    changesInLocalFlashcard + changesInLocalFlashcardCategory

            Log.e("SyncProcess", "Calculate")

            return if (changeInRemote > 0 || changeInLocal > 0) {
                Log.e("SyncProcess", "End: Has Changed")
                Result.Success(
                    mapOf(
                        "Remote" to changeInRemote,
                        "Local" to changeInLocal
                    )
                )
            }else{
                Log.e("SyncProcess", "End: No changed")
                return Result.Success(emptyMap())
            }

        }
            Log.e("SyncProcess", "End: No user found")
        return Result.Error(RemoteDbError("No user found."))
    }

    override fun checkHasLocalData(): Flow<Boolean> {
        return flow {
            while (true) {
                val localSubject = subjectDao.getAllSubjects().first()
                val localTask = taskDao.getAllTasks().first()
                val localSession = sessionDao.getAllSessions().first()
                val localFlashcard = flashcardDao.getAllFlashcards().first()
                val localFlashcardCategory = flashcardCategoryDao.getAllCategories().first()
                // Emit true if any collection is not empty, otherwise false
                var hasLocalData = localSubject.isNotEmpty() || localTask.isNotEmpty() || localSession.isNotEmpty()
                        || localFlashcard.isNotEmpty() || localFlashcardCategory.isNotEmpty()
                emit(hasLocalData)
            }
        }
    }

    override suspend fun removeAllLocalData() {
        withContext(Dispatchers.IO){
            subjectDao.deleteAllSubjects()
            taskDao.deleteAllTasks()
            sessionDao.deleteAllSessions()
            flashcardDao.deleteAllFlashcards()
            flashcardCategoryDao.deleteAllFlashcardCategories()
        }
    }

}