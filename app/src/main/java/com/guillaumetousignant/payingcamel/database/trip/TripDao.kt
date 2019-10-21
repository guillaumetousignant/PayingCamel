package com.guillaumetousignant.payingcamel.database.trip

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from trip_table ORDER BY start_time DESC")
    fun getDescTrips(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(trip: Trip)


    @Query("DELETE FROM trip_table")
    fun deleteAll()
}