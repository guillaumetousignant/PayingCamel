package com.guillaumetousignant.payingcamel.database.path

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class PathRepository(private val pathDao: PathDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allPaths: LiveData<List<Path>> = pathDao.getAscPaths()

    // The suspend modifier tells the compiler that this must be called from a
    // coroutine or another suspend function.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(path: Path) {
        pathDao.insert(path)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(paths: List<Path>) {
        pathDao.delete(paths)
    }
}