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
@Database(entities = [Bill::class], version = 1)
@TypeConverters(Converters::class)
abstract class BillRoomDatabase : RoomDatabase() {

    abstract fun billDao(): BillDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: BillRoomDatabase? = null

        fun getDatabase(
            context: Context//,
            //scope: CoroutineScope
        ): BillRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillRoomDatabase::class.java,
                    "bill_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab. (Add back two following lines and
                    // the one with scope, then add callback. GT)
                    //.fallbackToDestructiveMigration() // Removed
                    //.addCallback(BillDatabaseCallback(scope)) // Removed
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}