package com.example.studyassistant.di

import com.example.studyassistant.feature.authentication.data.repository.AuthRepositoryImpl
import com.example.studyassistant.feature.authentication.domain.repository.AuthRepository
import com.example.studyassistant.feature.flashcard.data.repository.FlashcardCategoryRepositoryImpl
import com.example.studyassistant.feature.flashcard.data.repository.FlashcardRepositoryImpl
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardCategoryRepository
import com.example.studyassistant.feature.flashcard.domain.repository.FlashcardRepository
import com.example.studyassistant.feature.studytracker.data.repository.SessionRepositoryImpl
import com.example.studyassistant.feature.studytracker.data.repository.SubjectRepositoryImpl
import com.example.studyassistant.feature.studytracker.data.repository.TaskRepositoryImpl
import com.example.studyassistant.feature.studytracker.domain.repository.SessionRepository
import com.example.studyassistant.feature.studytracker.domain.repository.SubjectRepository
import com.example.studyassistant.feature.studytracker.domain.repository.TaskRepository
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

    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Singleton
    @Binds
    abstract fun bindFlashcardRepository(
        flashcardRepositoryImpl: FlashcardRepositoryImpl
    ): FlashcardRepository

    @Singleton
    @Binds
    abstract fun bindFlashcardCategoryRepository(
        categoryRepositoryImpl: FlashcardCategoryRepositoryImpl
    ): FlashcardCategoryRepository

}