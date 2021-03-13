package com.guillaumetousignant.payingcamel.database.trip

import android.icu.util.Calendar
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.skater.Skater

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TripRepository(private val tripDao: TripDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allTrips: LiveData<List<Trip>> = tripDao.getDescTrips()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDatedTrips(startCalendar: Calendar, endCalendar: Calendar): List<Trip> {
        return tripDao.getDatedTrips(startCalendar, endCalendar)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDatedSkatersTrips(startCalendar: Calendar, endCalendar: Calendar, skaters: List<Skater>): List<Trip> {
        return tripDao.getDatedSkatersTrips(startCalendar, endCalendar, skaters.map{it.uuid})
    }

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(trip: Trip) {
        tripDao.insert(trip)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(trips: List<Trip>) {
        tripDao.delete(trips)
    }
}