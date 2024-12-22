package com.example.studyassistant

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.studyassistant.core.data.local.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val DB_NAME = "test"
@RunWith(AndroidJUnit4::class)
class AppMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun testAllMigrations() {
        helper.createDatabase(DB_NAME, 1).apply { close() }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            DB_NAME
        )
            .addMigrations(AppDatabase.migration1To2)
            .addMigrations(AppDatabase.migration2To3)
            .addMigrations(AppDatabase.migration3To4)
            .build().apply {
            openHelper.writableDatabase.close()
        }
    }

}