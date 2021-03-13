package com.guillaumetousignant.payingcamel.database.expense

import android.icu.util.Calendar
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.guillaumetousignant.payingcamel.database.trip.Trip

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allExpenses: LiveData<List<Expense>> = expenseDao.getDescExpenses()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getDatedExpenses(startCalendar: Calendar, endCalendar: Calendar): List<Expense> {
        return expenseDao.getDatedExpenses(startCalendar, endCalendar)
    }

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(expenses: List<Expense>) {
        expenseDao.delete(expenses)
    }
}