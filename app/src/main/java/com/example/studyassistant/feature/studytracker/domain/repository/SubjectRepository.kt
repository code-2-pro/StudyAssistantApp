package com.example.studyassistant.feature.studytracker.domain.repository

import com.example.studyassistant.core.domain.util.RemoteDbError
import com.example.studyassistant.core.domain.util.Result
import com.example.studyassistant.feature.studytracker.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    suspend fun upsertSubjectOnRemote(subject: Subject, userId: String): Result<Unit, RemoteDbError>

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun deleteSubject(subjectId: String)

    suspend fun deleteSubjectOnRemote(subjectId: String, userId: String): Result<Unit, RemoteDbError>

    fun getSubjectById(subjectId: String): Flow<Subject?>

    fun getAllSubjects(): Flow<List<Subject>>

    suspend fun subscribeToRealtimeUpdates(): Result<Unit, RemoteDbError>
}