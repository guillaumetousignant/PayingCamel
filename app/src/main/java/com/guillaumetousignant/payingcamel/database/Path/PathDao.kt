package com.guillaumetousignant.payingcamel.database.Path

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PathDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.
    @Query("SELECT * from path_table ORDER BY name ASC")
    fun getAscPaths(): LiveData<List<Path>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(path: Path)


    @Query("DELETE FROM path_table")
    fun deleteAll()
}