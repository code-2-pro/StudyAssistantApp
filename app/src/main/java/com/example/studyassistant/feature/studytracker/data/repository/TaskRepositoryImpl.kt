package com.example.studyassistant.feature.studytracker.data.repository

import com.example.studyassistant.core.domain.ConnectivityObserver
import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.authentication.data.mapper.toUser
import com.example.studyassistant.feature.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.feature.studytracker.data.mapper.toRemoteTask
import com.example.studyassistant.feature.studytracker.data.mapper.toTask
import com.example.studyassistant.feature.studytracker.data.mapper.toTaskEntity
import com.example.studyassistant.feature.studytracker.data.util.sortTasks
import com.example.studyassistant.feature.studytracker.domain.model.Task
import com.example.studyassistant.feature.studytracker.domain.repository.TaskRepository
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

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val connectivityObserver: ConnectivityObserver,
    private val auth: FirebaseAuth,
    private val remoteDb: FirebaseFirestore
): TaskRepository {
    override suspend fun upsertTask(task: Task): Result<Unit, RemoteDbError> {
        taskDao.upsertTask(task.toTaskEntity())

        val isConnect = connectivityObserver.isConnected.first()
        if(isConnect){
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val taskCollectionRef = remoteDb.collection("Task")
                val remoteTask = task.toRemoteTask(currentUser)
                try{
                    val querySnapshot = taskCollectionRef
                        .whereEqualTo("userId", remoteTask.userId)
                        .whereEqualTo("taskId", remoteTask.taskId)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val taskRef = querySnapshot.documents[0].reference
                        taskRef.set(remoteTask, SetOptions.merge()).await()
                    } else {
                        // Create a new document
                        taskCollectionRef.add(remoteTask).await()
                    }
                }catch (e: Exception){
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }
        return Result.Success(Unit)
    }

    override suspend fun deleteTask(taskId: String): Result<Unit, RemoteDbError> {
        taskDao.deleteTask(taskId)
        val isConnect = connectivityObserver.isConnected.first()
        if(isConnect){
            val currentUser = auth.currentUser?.toUser()
            currentUser?.let {
                val taskCollectionRef = remoteDb.collection("Task")
                val taskQuery = taskCollectionRef
                    .whereEqualTo("userId", currentUser.userId)
                    .whereEqualTo("taskId", taskId)
                try{
                    val taskQuerySnapshot = taskQuery.get().await()
                    for (document in taskQuerySnapshot.documents) {
                        document.reference.delete().await()
                    }
                }catch (e: Exception){
                    return Result.Error(RemoteDbError(message = e.message ?: "Unknown error"))
                }
            }
        }
        return Result.Success(Unit)
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        return taskDao.getTaskById(taskId).map {
            it?.toTask()
        }
    }

    override fun getUpcomingTasksForSubject(subjectId: String): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { taskEntities ->
            taskEntities
                .filter { it.isComplete.not() }
                .map { taskEntity -> taskEntity.toTask() }
        }.map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectId: String): Flow<List<Task>> {
        return taskDao.getTasksForSubject(subjectId).map { taskEntities ->
            taskEntities
                .filter { it.isComplete }
                .map { taskEntity -> taskEntity.toTask() }
        }.map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { taskEntities ->
            taskEntities
                .filter { it.isComplete.not() }
                .map { taskEntity -> taskEntity.toTask() }
        }.map { tasks -> sortTasks(tasks) }
    }

    override suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError> = try {
        val taskCollectionRef = remoteDb.collection("Task")

        // Use callbackFlow to bridge Firestore's snapshot listener with coroutines
        callbackFlow {
            val registration = taskCollectionRef.addSnapshotListener { snapshots, e ->
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
                val task = documentChange.document.toObject(Task::class.java)
                when (documentChange.type) {
                    DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                        upsertTask(task) // Call the suspend function
                    }
                    DocumentChange.Type.REMOVED -> {
                        deleteTask(task.taskId) // Call the suspend function
                    }
                }
            }
        }

        Result.Success(Unit) // Return success if no issues
    } catch (e: Exception) {
        Result.Error(RemoteDbError(e.message ?: "Unknown error")) // Wrap and return the error
    }

}

