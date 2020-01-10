package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.guillaumetousignant.payingcamel.MainActivity
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.backup.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
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
    fun backup(outPath: Uri, context: Context) = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val inStream = context.getDatabasePath("coach_database").inputStream()
        val outStream = context.contentResolver.openOutputStream(outPath)

        inStream.use { input ->
            outStream.use { output ->
                output?.let { outputStream ->
                    input.copyTo(outputStream)
                }
            }
        }
    }

    fun restore(inPath: Uri, context: Context) = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val outStream = context.getDatabasePath("coach_database").outputStream()
        val inStream = context.contentResolver.openInputStream(inPath)

        inStream.use { input ->
            outStream.use { output ->
                input?.copyTo(output)
            }
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }

        Runtime.getRuntime().exit(0)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}