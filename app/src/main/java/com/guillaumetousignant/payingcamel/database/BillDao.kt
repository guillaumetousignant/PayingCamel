package com.guillaumetousignant.payingcamel.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BillDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from bill_table ORDER BY start_time DESC")
    fun getDescBills(): LiveData<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(bill: Bill)


    @Query("DELETE FROM bill_table")
    fun deleteAll()
}