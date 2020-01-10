package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.guillaumetousignant.payingcamel.MainActivity
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.backup.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    fun backup(outFolderPath: Uri, context: Context) = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val pickedDir = DocumentFile.fromTreeUri(context, outFolderPath)
        pickedDir?.let {directory ->
            val inStream = context.getDatabasePath("coach_database").inputStream()
            val inStream1 = context.getDatabasePath("coach_database-shm").inputStream()
            val inStream2 = context.getDatabasePath("coach_database-wal").inputStream()

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.BASIC_ISO_DATE
            val formatted = current.format(formatter)

            var increment = 0
            var outPath = "PayingCamelDatabase_$formatted.pcbackup"
            var outPath1 = "PayingCamelDatabase_$formatted-shm.pcbackup"
            var outPath2 = "PayingCamelDatabase_$formatted-wal.pcbackup"

            while ((directory.findFile(outPath) != null) || (directory.findFile(outPath1) != null) || (directory.findFile(
                    outPath2
                ) != null)
            ) {
                ++increment
                outPath = "PayingCamelDatabase_${formatted}_${increment}.pcbackup"
                outPath1 = "PayingCamelDatabase_${formatted}_${increment}-shm.pcbackup"
                outPath2 = "PayingCamelDatabase_${formatted}_${increment}-wal.pcbackup"
            }

            val newFile = directory.createFile("application/x-sqlite3", outPath)
            newFile?.let {
                val outStream = context.contentResolver.openOutputStream(it.uri)
                inStream.use { input ->
                    outStream?.use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val newFile1 = directory.createFile("application/x-sqlite3", outPath1)
            newFile1?.let {
                val outStream = context.contentResolver.openOutputStream(it.uri)
                inStream1.use { input ->
                    outStream?.use { output ->
                        input.copyTo(output)
                    }
                }
            }

            val newFile2 = directory.createFile("application/x-sqlite3", outPath2)
            newFile2?.let {
                val outStream = context.contentResolver.openOutputStream(it.uri)
                inStream2.use { input ->
                    outStream?.use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }

    fun restore(inPath: Uri, context: Context) = scope.launch(Dispatchers.IO) {
    repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

    val outStream = context.getDatabasePath("coach_database").outputStream()
    val outStream1 = context.getDatabasePath("coach_database-shm").outputStream()
    val outStream2 = context.getDatabasePath("coach_database-wal").outputStream()
    val inStream = context.contentResolver.openInputStream(inPath)
    //val inStream1 = context.contentResolver.openInputStream(Uri.withAppendedPath(inPath, "-shm.pcbackup"))
    //val inStream2 = context.contentResolver.openInputStream(Uri.withAppendedPath(inPath, "-wal.pcbackup"))

    inStream.use { input ->
        outStream.use { output ->
            input?.copyTo(output)
        }
    }

    /*inStream1.use { input ->
        outStream1.use { output ->
            input?.copyTo(output)
        }
    }

    inStream2.use { input ->
        outStream2.use { output ->
            input?.copyTo(output)
        }
    }*/

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