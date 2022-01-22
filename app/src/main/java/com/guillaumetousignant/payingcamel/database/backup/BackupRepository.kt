package com.guillaumetousignant.payingcamel.database.backup

import androidx.annotation.WorkerThread
import androidx.sqlite.db.SupportSQLiteQuery

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class BackupRepository(private val backupDao: BackupDao) {

    // Room executes all queries on a separate thread.

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery) {
        backupDao.checkpoint(supportSQLiteQuery)
    }
}