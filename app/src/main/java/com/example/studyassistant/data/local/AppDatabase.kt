package com.example.studyassistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studyassistant.data.local.dao.SessionDao
import com.example.studyassistant.data.local.dao.SubjectDao
import com.example.studyassistant.data.local.dao.TaskDao
import com.example.studyassistant.data.local.entity.SessionEntity
import com.example.studyassistant.data.local.entity.SubjectEntity
import com.example.studyassistant.data.local.entity.TaskEntity

@Database(
    entities = [SubjectEntity::class, SessionEntity::class, TaskEntity::class],
    version = 1
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
}