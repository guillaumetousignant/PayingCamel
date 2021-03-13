package com.guillaumetousignant.payingcamel.database.fill

import android.icu.util.Calendar
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.guillaumetousignant.payingcamel.database.expense.Expense

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FillRepository(private val fillDao: FillDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allFills: LiveData<List<Fill>> = fillDao.getDescFills()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDatedFills(startCalendar: Calendar, endCalendar: Calendar): List<Fill> {
        return fillDao.getDatedFills(startCalendar, endCalendar)
    }

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(fill: Fill) {
        fillDao.insert(fill)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(fills: List<Fill>) {
        fillDao.delete(fills)
    }
}