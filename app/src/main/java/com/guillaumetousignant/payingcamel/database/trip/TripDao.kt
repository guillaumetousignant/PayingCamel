package com.guillaumetousignant.payingcamel.database.trip

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.room.*
import com.guillaumetousignant.payingcamel.database.course.Course
import java.util.*

@Dao
interface TripDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from trip_table ORDER BY start_time DESC")
    fun getDescTrips(): LiveData<List<Trip>>

    @Query("SELECT * from trip_table WHERE start_time >= :startCalendar AND start_time <= :endCalendar ORDER BY start_time ASC")
    fun getDatedTrips(startCalendar: Calendar, endCalendar: Calendar): List<Trip>

    @Query("SELECT * from trip_table WHERE start_time >= :startCalendar AND start_time <= :endCalendar AND skater IN (:skaters) ORDER BY start_time ASC")
    fun getDatedSkatersTrips(startCalendar: Calendar, endCalendar: Calendar, skaters: List<UUID>): List<Trip>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(trip: Trip)

    @Query("DELETE FROM trip_table")
    fun deleteAll()

    @Delete
    fun delete(trips: List<Trip>)
}