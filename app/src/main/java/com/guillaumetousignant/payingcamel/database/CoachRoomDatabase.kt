package com.guillaumetousignant.payingcamel.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.util.UUID // REMOVE
import android.icu.util.Calendar // REMOVE

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Course::class, Expense::class, Fill::class, Path::class, Rate::class, Skater::class, Trip::class],
          version = 2)
@TypeConverters(Converters::class)
abstract class CoachRoomDatabase : RoomDatabase() {

    abstract fun courseDao():  CourseDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun fillDao():    FillDao
    abstract fun pathDao():    PathDao
    abstract fun rateDao():    RateDao
    abstract fun skaterDao():  SkaterDao
    abstract fun tripDao():    TripDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: CoachRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): CoachRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoachRoomDatabase::class.java,
                    "coach_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab. (Add back two following lines and
                    // the one with scope, then add callback. GT)
                    .fallbackToDestructiveMigration() // Removed
                    .addCallback(CoachDatabaseCallback(scope)) // Removed
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class CoachDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.courseDao())
                    }
                }
            }

            /**
             * Populate the database in a new coroutine.
             * If you want to start with more words, just add them.
             */
            fun populateDatabase(courseDao: CourseDao) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.
                courseDao.deleteAll()

                var course = Course(UUID.randomUUID(), UUID.randomUUID(), Calendar.getInstance(), Calendar.getInstance(),
                    2000, 1000, "first course", "First course note", true)
                courseDao.insert(course)
                course = Course(UUID.randomUUID(), UUID.randomUUID(), Calendar.getInstance(), Calendar.getInstance(),
                    2000, 2000, "second course", "Second course note", false)
                courseDao.insert(course)

                course = Course(UUID.randomUUID(), UUID.randomUUID(), Calendar.getInstance(), Calendar.getInstance(),
                    2000, 2000, "third course", "Third course note", false)
                courseDao.insert(course)

                course = Course(UUID.randomUUID(), UUID.randomUUID(), Calendar.getInstance(), Calendar.getInstance(),
                    2000, 2000, "fourth course", "Fourth course note", false)
                courseDao.insert(course)

                course = Course(UUID.randomUUID(), UUID.randomUUID(), Calendar.getInstance(), Calendar.getInstance(),
                    2000, 2000, "fifth course", "Fifth course note", false)
                courseDao.insert(course)
            }
        }
    }
}