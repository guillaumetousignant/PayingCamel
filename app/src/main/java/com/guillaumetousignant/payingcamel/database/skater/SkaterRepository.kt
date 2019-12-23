package com.guillaumetousignant.payingcamel.database.skater

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SkaterRepository(private val skaterDao: SkaterDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allSkaters: LiveData<List<Skater>> = skaterDao.getAscLastNameSkater()

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(skater: Skater) {
        skaterDao.insert(skater)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(skaters: List<Skater>) {
        skaterDao.delete(skaters)
    }
}