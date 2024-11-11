package com.example.studyassistant.data.repository

import com.example.studyassistant.data.local.dao.SubjectDao
import com.example.studyassistant.data.mapper.toSubject
import com.example.studyassistant.data.mapper.toSubjectEntity
import com.example.studyassistant.domain.model.Subject
import com.example.studyassistant.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
): SubjectRepository{
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject.toSubjectEntity())
    }

    override fun getTotalSubjectCount(): Flow<Int> {
         return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectId: Int) {
        subjectDao.deleteSubject(subjectId)
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)?.toSubject()
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { subjectEntities ->
            subjectEntities.map { it ->
                it.toSubject()
            }
        }
    }
}