package com.guillaumetousignant.payingcamel.database.expense

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.room.*
import com.guillaumetousignant.payingcamel.database.trip.Trip

@Dao
interface ExpenseDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from expense_table ORDER BY start_time DESC")
    fun getDescExpenses(): LiveData<List<Expense>>

    @Query("SELECT * from expense_table WHERE start_time >= :startCalendar AND start_time <= :endCalendar ORDER BY start_time ASC")
    fun getDatedExpenses(startCalendar: Calendar, endCalendar: Calendar): List<Expense>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(expense: Expense)

    @Query("DELETE FROM expense_table")
    fun deleteAll()

    @Delete
    fun delete(expenses: List<Expense>)
}