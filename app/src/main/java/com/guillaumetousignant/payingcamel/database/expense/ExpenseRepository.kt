package com.guillaumetousignant.payingcamel.database.expense

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allExpenses: LiveData<List<Expense>> = expenseDao.getDescEexpenses()

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }
}