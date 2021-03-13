package com.guillaumetousignant.payingcamel.database.fill

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.room.*
import com.guillaumetousignant.payingcamel.database.expense.Expense

@Dao
interface FillDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from fill_table ORDER BY start_time DESC")
    fun getDescFills(): LiveData<List<Fill>>

    @Query("SELECT * from fill_table WHERE start_time >= :startCalendar AND start_time <= :endCalendar ORDER BY start_time ASC")
    fun getDatedFills(startCalendar: Calendar, endCalendar: Calendar): List<Fill>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(fill: Fill)

    @Query("DELETE FROM fill_table")
    fun deleteAll()

    @Delete
    fun delete(fills: List<Fill>)
}