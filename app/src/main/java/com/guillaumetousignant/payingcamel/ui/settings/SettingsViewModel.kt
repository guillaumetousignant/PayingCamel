package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.backup.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    // By default all the coroutines launched in this scope should be using the Main dispatcher
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    private val repository: BackupRepository
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    init {
        val backupDao = CoachRoomDatabase.getDatabase(application, scope).backupDao()
        repository = BackupRepository(backupDao)
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun checkpoint() = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}