package com.example.studyassistant.studytracker.data.repository

import com.example.studyassistant.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.studytracker.data.mapper.toSubject
import com.example.studyassistant.studytracker.data.mapper.toSubjectEntity
import com.example.studyassistant.studytracker.domain.model.Subject
import com.example.studyassistant.studytracker.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao: TaskDao,
    private val sessionDao: SessionDao
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
        taskDao.deleteTasksBySubjectId(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
    }

    override fun getSubjectById(subjectId: Int): Flow<Subject?> {
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
}