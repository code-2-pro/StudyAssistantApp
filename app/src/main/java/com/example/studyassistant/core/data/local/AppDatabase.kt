package com.example.studyassistant.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.studyassistant.feature.flashcard.data.FlashcardDao
import com.example.studyassistant.feature.flashcard.data.FlashcardEntity
import com.example.studyassistant.feature.studytracker.data.local.dao.SessionDao
import com.example.studyassistant.feature.studytracker.data.local.dao.SubjectDao
import com.example.studyassistant.feature.studytracker.data.local.dao.TaskDao
import com.example.studyassistant.feature.studytracker.data.local.entity.SessionEntity
import com.example.studyassistant.feature.studytracker.data.local.entity.SubjectEntity
import com.example.studyassistant.feature.studytracker.data.local.entity.TaskEntity
import com.example.studyassistant.feature.studytracker.data.util.ColorListConverter

@Database(
    entities = [SubjectEntity::class, SessionEntity::class, TaskEntity::class,
        FlashcardEntity::class],
    version = 3
)

@TypeConverters(ColorListConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao
    abstract fun flashcardDao(): FlashcardDao

    companion object {
        val migration1To2 = object : Migration(1, 2){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS FlashcardEntity (" +
                            "question TEXT NOT NULL, " +
                            "answer TEXT NOT NULL, " +
                            "flashcardId INTEGER PRIMARY KEY AUTOINCREMENT)"
                )
            }
        }

        val migration2To3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create new `TaskEntity` table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS TaskEntity_new (
                taskId TEXT NOT NULL PRIMARY KEY,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                dueDate INTEGER NOT NULL,
                priority INTEGER NOT NULL,
                relatedToSubject TEXT NOT NULL,
                isComplete INTEGER NOT NULL,
                taskSubjectId TEXT NOT NULL
            )
            """
                )

                // 2. Create new `SubjectEntity` table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS SubjectEntity_new (
                subjectId TEXT NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                goalHours REAL NOT NULL,
                colors TEXT NOT NULL
            )
            """
                )

                // 3. Create new `SessionEntity` table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS SessionEntity_new (
                sessionId TEXT NOT NULL PRIMARY KEY,
                sessionSubjectId TEXT NOT NULL,
                relatedToSubject TEXT NOT NULL,
                date INTEGER NOT NULL,
                duration INTEGER NOT NULL
            )
            """
                )

                // 4. Create new `FlashcardEntity` table
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS FlashcardEntity_new (
                flashcardId TEXT NOT NULL PRIMARY KEY,
                question TEXT NOT NULL,
                answer TEXT NOT NULL
            )
            """
                )

                // 5. Copy data from old `TaskEntity` to `TaskEntity_new`
                database.execSQL(
                    """
            INSERT INTO TaskEntity_new (taskId, title, description, dueDate, priority, relatedToSubject, isComplete, taskSubjectId)
            SELECT CAST(taskId AS TEXT), title, description, dueDate, priority, relatedToSubject, isComplete, taskSubjectId 
            FROM TaskEntity
            """
                )

                // 6. Copy data from old `SubjectEntity` to `SubjectEntity_new`
                database.execSQL(
                    """
            INSERT INTO SubjectEntity_new (subjectId, name, goalHours, colors)
            SELECT CAST(subjectId AS TEXT), name, goalHours, colors 
            FROM SubjectEntity
            """
                )

                // 7. Copy data from old `SessionEntity` to `SessionEntity_new`
                database.execSQL(
                    """
            INSERT INTO SessionEntity_new (sessionId, sessionSubjectId, relatedToSubject, date, duration)
            SELECT CAST(sessionId AS TEXT), sessionSubjectId, relatedToSubject, date, duration 
            FROM SessionEntity
            """
                )

                // 8. Copy data from old `FlashcardEntity` to `FlashcardEntity_new`
                database.execSQL(
                    """
            INSERT INTO FlashcardEntity_new (flashcardId, question, answer)
            SELECT CAST(flashcardId AS TEXT), question, answer 
            FROM FlashcardEntity
            """
                )

                // 9. Drop old tables
                database.execSQL("DROP TABLE IF EXISTS TaskEntity")
                database.execSQL("DROP TABLE IF EXISTS SubjectEntity")
                database.execSQL("DROP TABLE IF EXISTS SessionEntity")
                database.execSQL("DROP TABLE IF EXISTS FlashcardEntity")

                // 10. Rename new tables to original names
                database.execSQL("ALTER TABLE TaskEntity_new RENAME TO TaskEntity")
                database.execSQL("ALTER TABLE SubjectEntity_new RENAME TO SubjectEntity")
                database.execSQL("ALTER TABLE SessionEntity_new RENAME TO SessionEntity")
                database.execSQL("ALTER TABLE FlashcardEntity_new RENAME TO FlashcardEntity")
            }
        }

    }




}