package com.example.studyassistant.feature.studytracker.data.repository

import android.util.Log
import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.authentication.data.mapper.toUser
import com.example.studyassistant.feature.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.feature.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.feature.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteSubject
import com.example.studyassistant.feature.studytracker.data.mapper.toSubject
import com.example.studyassistant.feature.studytracker.data.mapper.toSubjectEntity
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao,
    private val connectivityObserver: ConnectivityObserver,
    private val auth: FirebaseAuth,
    private val remoteDb: FirebaseFirestore
): SubjectRepository {

    override suspend fun upsertSubject(subject: Subject): Result<Unit, RemoteDbError> {
        subjectDao.upsertSubject(subject.toSubjectEntity())
        val isConnected = connectivityObserver.isConnected.first()
        if(isConnected){
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val subjectCollectionRef = remoteDb.collection("Subject")
                val remoteSubject = subject.toRemoteSubject(currentUser)
                try{
                    val querySnapshot = subjectCollectionRef
                        .whereEqualTo("userId", remoteSubject.userId)
                        .whereEqualTo("subjectId", remoteSubject.subjectId)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val subjectRef = querySnapshot.documents[0].reference
                        subjectRef.set(remoteSubject, SetOptions.merge()).await()
                    } else {
                        // Create a new document
                        subjectCollectionRef.add(remoteSubject).await()
                    }
                }catch (e: Exception){
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }
        return Result.Success(Unit)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: String): Result<Unit, RemoteDbError> {
        subjectDao.deleteSubject(subjectId)
        taskDao.deleteTasksBySubjectId(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
        val isConnected = connectivityObserver.isConnected.first()
        if(isConnected){
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val subjectCollectionRef = remoteDb.collection("Subject")
                val taskCollectionRef = remoteDb.collection("Task")
                val sessionCollectionRef = remoteDb.collection("Session")
                val subjectQuery = subjectCollectionRef
                    .whereEqualTo("userId", currentUser.userId)
                    .whereEqualTo("subjectId", subjectId)
                val taskQuery = taskCollectionRef
                    .whereEqualTo("userId", currentUser.userId)
                    .whereEqualTo("taskSubjectId", subjectId)
                val sessionQuery = sessionCollectionRef
                    .whereEqualTo("userId", currentUser.userId)
                    .whereEqualTo("sessionSubjectId", subjectId)

                try{
                    val batch = remoteDb.batch()

                    // Add subject deletions to the batch
                    val subjectQuerySnapshot = subjectQuery.get().await()
                    for (document in subjectQuerySnapshot.documents) {
                        batch.delete(document.reference)
                    }

                    // Add task deletions to the batch
                    val taskQuerySnapshot = taskQuery.get().await()
                    for (document in taskQuerySnapshot.documents) {
                        batch.delete(document.reference)
                    }

                    // Add session deletions to the batch
                    val sessionQuerySnapshot = sessionQuery.get().await()
                    for (document in sessionQuerySnapshot.documents) {
                        batch.delete(document.reference)
                    }

                    // Commit the batch
                    batch.commit().await()

                }catch (e: Exception){
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }

        return Result.Success(Unit)
    }

    override fun getSubjectById(subjectId: String): Flow<Subject?> {
        return subjectDao.getSubjectById(subjectId).map {
            it?.toSubject()
        }
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { subjectEntities ->
            subjectEntities
                .map { it.toSubject() }
        }
    }

    override suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError> = try {
        val subjectCollectionRef = remoteDb.collection("Subject")
        Log.d("RealTimeUpdate:", "Start")
        // Use callbackFlow to bridge Firestore's snapshot listener with coroutines
        callbackFlow {
            val registration = subjectCollectionRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e) // Close the flow with the error
                    return@addSnapshotListener
                }
                snapshots?.let { trySend(it) } // Emit the snapshots to the flow
            }

            // Clean up the listener when the flow is cancelled
            awaitClose { registration.remove() }
        }.collect { snapshots ->
            Log.d("RealTimeUpdate:", "Start Collect")
            // Process the snapshot changes
            for (documentChange in snapshots.documentChanges) {
                val subject = documentChange.document.toObject(Subject::class.java)
                when (documentChange.type) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        upsertSubject(subject) // Call the suspend function
                    }
                    DocumentChange.Type.REMOVED -> {
                        deleteSubject(subject.subjectId!!) // Call the suspend function
                    }
                }
            }
        }
        Log.d("RealTimeUpdate:", "End")
        Result.Success(Unit) // Return success if no issues
    } catch (e: Exception) {
        Log.d("RealTimeUpdate:", "Error")
        Result.Error(RemoteDbError(e.message ?: "Unknown error")) // Wrap and return the error
    }


}