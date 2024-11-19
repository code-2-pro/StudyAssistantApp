package com.example.studyassistant.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studyassistant.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.studytracker.data.local.entity.SessionEntity
import com.example.studyassistant.studytracker.data.local.entity.SubjectEntity
import com.example.studyassistant.studytracker.data.local.entity.TaskEntity
import com.example.studyassistant.studytracker.data.util.ColorListConverter

@Database(
    entities = [SubjectEntity::class, SessionEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
}