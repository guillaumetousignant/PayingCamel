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
                    //.fallbackToDestructiveMigration() // Removed
                    //.addCallback(CourseDatabaseCallback(scope)) // Removed
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}