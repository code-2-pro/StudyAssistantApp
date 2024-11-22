package com.example.studyassistant.studytracker.domain.repository

import com.example.studyassistant.studytracker.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun deleteSubject(subjectId: Int)

    fun getSubjectById(subjectId: Int): Flow<Subject?>

    fun getAllSubjects(): Flow<List<Subject>>
}