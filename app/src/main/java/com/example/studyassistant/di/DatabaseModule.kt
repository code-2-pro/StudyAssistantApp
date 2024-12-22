package com.example.studyassistant.di

import android.app.Application
import androidx.room.Room
import com.example.studyassistant.core.data.local.AppDatabase
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardCategoryDao
import com.example.studyassistant.feature.flashcard.data.local.dao.FlashcardDao
import com.example.studyassistant.feature.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.feature.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.feature.studytracker.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ): AppDatabase{
        return Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "studyassistant.db"
            )
            .addMigrations(AppDatabase.migration1To2)
            .addMigrations(AppDatabase.migration2To3)
            .addMigrations(AppDatabase.migration3To4)
            .build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao{
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao{
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao{
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideFlashcardDao(database: AppDatabase): FlashcardDao{
        return database.flashcardDao()
    }

    @Provides
    @Singleton
    fun provideFlashcardCategoryDao(database: AppDatabase): FlashcardCategoryDao{
        return database.flashcardCategoryDao()
    }
}