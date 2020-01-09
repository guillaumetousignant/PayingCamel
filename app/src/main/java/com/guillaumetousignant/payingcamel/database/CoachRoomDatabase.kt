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
import com.guillaumetousignant.payingcamel.database.backup.BackupDao
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.course.CourseDao
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.expense.ExpenseDao
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.fill.FillDao
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.path.PathDao
import com.guillaumetousignant.payingcamel.database.rate.Rate
import com.guillaumetousignant.payingcamel.database.rate.RateDao
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.skater.SkaterDao
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.guillaumetousignant.payingcamel.database.trip.TripDao

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Course::class, Expense::class, Fill::class, Path::class, Rate::class, Skater::class, Trip::class],
          version = 6)
@TypeConverters(Converters::class)
abstract class CoachRoomDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun fillDao(): FillDao
    abstract fun pathDao(): PathDao
    abstract fun rateDao(): RateDao
    abstract fun skaterDao(): SkaterDao
    abstract fun tripDao(): TripDao
    abstract fun backupDao(): BackupDao

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
                        populateDatabase(database.courseDao(), database.expenseDao(),
                                            database.fillDao(), database.pathDao(),
                                            database.rateDao(), database.skaterDao(),
                                            database.tripDao())
                    }
                }
            }

            /**
             * Populate the database in a new coroutine.
             * If you want to start with more words, just add them.
             */
            fun populateDatabase(courseDao: CourseDao,
                                 expenseDao: ExpenseDao,
                                 fillDao: FillDao,
                                 pathDao: PathDao,
                                 rateDao: RateDao,
                                 skaterDao: SkaterDao,
                                 tripDao: TripDao
            ) {
                // Start the app with a clean database every time.
                // Not needed if you only populate on creation.
                courseDao.deleteAll()
                expenseDao.deleteAll()
                fillDao.deleteAll()
                pathDao.deleteAll()
                rateDao.deleteAll()
                skaterDao.deleteAll()
                tripDao.deleteAll()

                var course = Course(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    2000,
                    1000,
                    "first course",
                    "First course note",
                    true,
                    10537
                )
                courseDao.insert(course)

                course = Course(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    2000,
                    2000,
                    "second course",
                    "Second course note",
                    false,
                    10538
                )
                courseDao.insert(course)

                course = Course(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    2000,
                    2000,
                    "third course",
                    "Third course note",
                    false,
                    10539
                )
                courseDao.insert(course)

                course = Course(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    2000,
                    2000,
                    "fourth course",
                    "Fourth course note",
                    false,
                    10540
                )
                courseDao.insert(course)

                course = Course(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    Calendar.getInstance(),
                    Calendar.getInstance(),
                    2000,
                    2000,
                    "fifth course",
                    "Fifth course note",
                    false,
                    10541
                )
                courseDao.insert(course)

                var expense = Expense(
                    UUID.randomUUID(), 1000, Calendar.getInstance(),
                    null, null, "Beignes", "Beignes pour manger miam miam",
                    10541
                )
                expenseDao.insert(expense)

                expense = Expense(
                    UUID.randomUUID(), 2000, Calendar.getInstance(),
                    null, null, "Beignes 2", "2e achat de beignes miam miam",
                    10541
                )
                expenseDao.insert(expense)

                var fill = Fill(
                    UUID.randomUUID(), 4300, Calendar.getInstance(), "Plein",
                    null,
                    10541
                )
                fillDao.insert(fill)

                fill = Fill(
                    UUID.randomUUID(), 1300, Calendar.getInstance(), "Plein",
                    "Plus d'essence",
                    10541
                )
                fillDao.insert(fill)

                var path = Path(
                    UUID.randomUUID(), 10.5, "maison", "aréna",
                    "Maison-Cholette", null,
                    10541
                )
                pathDao.insert(path)

                path = Path(
                    UUID.randomUUID(), 30.5, "maison", "Baribeauy",
                    "Maison-Baribeau", "Sans traffic",
                    10541
                )
                pathDao.insert(path)

                var rate = Rate(
                    UUID.randomUUID(), 2100, "Privé", "Augmentation 2019",
                    null,
                    10541
                )
                rateDao.insert(rate)

                rate = Rate(
                    UUID.randomUUID(), 2450, "Club", "Augmentation 2019",
                    null,
                    10541
                )
                rateDao.insert(rate)

                var skater = Skater(
                    UUID.randomUUID(), "Guillaume", "Tousignant",
                    "Niveau 0 lol", "moi_guillaume@hotmail.com", true, 10539
                )
                skaterDao.insert(skater)

                skater = Skater(
                    UUID.randomUUID(), "Ariane", "Laurier",
                    "Amour d'amour ❤❤❤", null, true, 10540
                )
                skaterDao.insert(skater)

                var trip = Trip(
                    UUID.randomUUID(), "Maison-Cholette", "maison",
                    "Cholette", 10.5, Calendar.getInstance(), null, null,
                    "Mardi soir", "J'ai vu un chevreuil",
                    10541
                )
                tripDao.insert(trip)

                trip = Trip(
                    UUID.randomUUID(), "Maison-Baribeau", "maison",
                    "Baribeau", 30.5, Calendar.getInstance(), null, null,
                    "Mardi pm", null,
                    10541
                )
                tripDao.insert(trip)

                trip = Trip(
                    UUID.randomUUID(), "Maison-Cholette", "maison",
                    "Cholette", 10.5, Calendar.getInstance(), null, null,
                    "Jeudi soir", null,
                    10541
                )
                tripDao.insert(trip)
            }
        }
    }
}