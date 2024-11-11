package com.example.studyassistant.di

import com.example.studyassistant.data.repository.SessionRepositoryImpl
import com.example.studyassistant.data.repository.SubjectRepositoryImpl
import com.example.studyassistant.data.repository.TaskRepositoryImpl
import com.example.studyassistant.domain.repository.SessionRepository
import com.example.studyassistant.domain.repository.SubjectRepository
import com.example.studyassistant.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton

@Module
@InstallIn(Singleton::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        subjectRepositoryImpl: SubjectRepositoryImpl
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository


}