package com.guillaumetousignant.payingcamel.database.backup

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface BackupDao {

    // LiveData is a data holder class that can be observed within a given lifecycle.
    // Always holds/caches latest version of data. Notifies its active observers when the
    // data has changed. Since we are getting all the contents of the database,
    // we are notified whenever any of the database contents have changed.

    @RawQuery
    fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int
}