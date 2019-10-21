package com.guillaumetousignant.payingcamel.database.rate

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class RateRepository(private val rateDao: RateDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allRates: LiveData<List<Rate>> = rateDao.getAscRates()

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(rate: Rate) {
        rateDao.insert(rate)
    }
}