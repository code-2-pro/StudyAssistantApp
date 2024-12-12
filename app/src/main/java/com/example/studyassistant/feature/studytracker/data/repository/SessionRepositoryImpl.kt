package com.example.studyassistant.feature.studytracker.data.repository

import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.authentication.data.mapper.toUser
import com.example.studyassistant.feature.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteSession
import com.example.studyassistant.feature.studytracker.data.mapper.toSession
import com.example.studyassistant.feature.studytracker.data.mapper.toSessionEntity
import com.example.studyassistant.feature.studytracker.domain.model.Session
import com.example.studyassistant.feature.studytracker.domain.repository.SessionRepository
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

class SessionRepositoryImpl @Inject constructor (
    private val sessionDao: SessionDao,
    private val connectivityObserver: ConnectivityObserver,
    private val auth: FirebaseAuth,
    private val remoteDb: FirebaseFirestore
): SessionRepository {
    override suspend fun insertSession(session: Session): Result<Unit, RemoteDbError> {
        sessionDao.insertSession(session.toSessionEntity())
        val isConnected = connectivityObserver.isConnected.first()
        if(isConnected){
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val sessionCollectionRef = remoteDb.collection("Session")
                val remoteSession = session.toRemoteSession(currentUser)
                try{
                    val querySnapshot = sessionCollectionRef
                        .whereEqualTo("userId", remoteSession.userId)
                        .whereEqualTo("sessionId", remoteSession.sessionId)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val sessionRef = querySnapshot.documents[0].reference
                        sessionRef.set(remoteSession, SetOptions.merge()).await()
                    } else {
                        // Create a new document
                        sessionCollectionRef.add(remoteSession).await()
                    }
                }catch (e: Exception){
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteSession(session: Session): Result<Unit, RemoteDbError> {
        sessionDao.deleteSession(session.toSessionEntity())
        val isConnected = connectivityObserver.isConnected.first()
        if(isConnected) {
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val sessionCollectionRef = remoteDb.collection("Session")
                val sessionQuery = sessionCollectionRef
                    .whereEqualTo("userId", currentUser.userId)
                    .whereEqualTo("sessionId", session.sessionId)
                try {
                    val sessionQuerySnapshot = sessionQuery.get().await()
                    for (document in sessionQuerySnapshot.documents) {
                        document.reference.delete().await()
                    }
                } catch (e: Exception) {
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }
        return Result.Success(Unit)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .map { it -> it.toSession() }
        }
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions().map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .take(5)
                .map { it.toSession() }
        }
    }

    override fun getRecentTenSessionsForSubject(subjectId: String): Flow<List<Session>> {
       return sessionDao.getRecentSessionForSubject(subjectId).map { sessionEntities ->
            sessionEntities
                .sortedByDescending { it.date }
                .take(10)
                .map { it.toSession() }
        }
    }

    override fun getTotalSessionsDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubject(subjectId: String): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubject(subjectId)
    }


    override suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError> = try {
        val sessionCollectionRef = remoteDb.collection("Session")

        // Use callbackFlow to bridge Firestore's snapshot listener with coroutines
        callbackFlow {
            val registration = sessionCollectionRef.addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e) // Close the flow with the error
                    return@addSnapshotListener
                }
                snapshots?.let { trySend(it) } // Emit the snapshots to the flow
            }

            // Clean up the listener when the flow is cancelled
            awaitClose { registration.remove() }
        }.collect { snapshots ->
            // Process the snapshot changes
            for (documentChange in snapshots.documentChanges) {
                val session = documentChange.document.toObject(Session::class.java)
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> {
                        insertSession(session) // Handle added session
                    }
                    DocumentChange.Type.MODIFIED -> {
                        // You can optionally update the session if needed, or just ignore it
                    }
                    DocumentChange.Type.REMOVED -> {
                        deleteSession(session) // Handle removed session
                    }
                }
            }
        }
        Result.Success(Unit) // Return success if no issues
    } catch (e: Exception) {
        Result.Error(RemoteDbError(e.message ?: "Unknown error")) // Wrap and return the error
    }

}