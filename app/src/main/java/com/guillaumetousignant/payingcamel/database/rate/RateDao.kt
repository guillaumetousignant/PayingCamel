package com.guillaumetousignant.payingcamel.database.rate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RateDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from rate_table ORDER BY name ASC")
    fun getAscRates(): LiveData<List<Rate>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(rate: Rate)


    @Query("DELETE FROM rate_table")
    fun deleteAll()

    @Delete
    fun delete(rates: List<Rate>)
}