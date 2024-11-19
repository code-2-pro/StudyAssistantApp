package com.example.studyassistant.di

import com.example.studyassistant.studytracker.data.repository.SessionRepositoryImpl
import com.example.studyassistant.studytracker.data.repository.SubjectRepositoryImpl
import com.example.studyassistant.studytracker.data.repository.TaskRepositoryImpl
import com.example.studyassistant.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.studytracker.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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