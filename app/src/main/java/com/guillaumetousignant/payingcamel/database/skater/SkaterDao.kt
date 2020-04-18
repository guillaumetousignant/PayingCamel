package com.guillaumetousignant.payingcamel.database.skater

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SkaterDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from skater_table ORDER BY first_name, last_name ASC")
    fun getAscFirstNameSkater(): LiveData<List<Skater>>

    @Query("SELECT * from skater_table ORDER BY last_name, first_name ASC")
    fun getAscLastNameSkater(): LiveData<List<Skater>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(skater: Skater)


    @Query("DELETE FROM skater_table")
    fun deleteAll()

    @Delete
    fun delete(skaters: List<Skater>)
}