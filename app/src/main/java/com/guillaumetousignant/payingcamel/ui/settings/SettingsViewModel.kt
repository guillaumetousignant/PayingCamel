package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.AndroidViewModel
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.http.FileContent
import com.guillaumetousignant.payingcamel.MainActivity
import com.guillaumetousignant.payingcamel.R
import com.guillaumetousignant.payingcamel.database.CoachRoomDatabase
import com.guillaumetousignant.payingcamel.database.backup.BackupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
import com.google.api.services.drive.Drive


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

    fun restore(clipData: ClipData, context: Context) = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val inUri0 = clipData.getItemAt(0).uri
        val inUri1 = clipData.getItemAt(1).uri
        val inUri2 = clipData.getItemAt(2).uri

        val outStream = context.getDatabasePath("coach_database").outputStream()
        val outStream1 = context.getDatabasePath("coach_database-shm").outputStream()
        val outStream2 = context.getDatabasePath("coach_database-wal").outputStream()

        val inFileName0 = getFileName(inUri0, context)
        val inFileName1 = getFileName(inUri1, context)
        val inFileName2 = getFileName(inUri2, context)

        var uri0: Uri? = null
        var uri1: Uri? = null
        var uri2: Uri? = null

        when {
            inFileName0?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri0
            }
            inFileName0?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri0
            }
            inFileName0?.endsWith(".pcbackup") == true -> {
                uri0 = inUri0
            }
        }

        when {
            inFileName1?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri1
            }
            inFileName1?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri1
            }
            inFileName1?.endsWith(".pcbackup") == true -> {
                uri0 = inUri1
            }
        }

        when {
            inFileName2?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri2
            }
            inFileName2?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri2
            }
            inFileName2?.endsWith(".pcbackup") == true -> {
                uri0 = inUri2
            }
        }

        if ((uri0 != null) && (uri1 != null) && (uri2 != null)){
            val inStream = context.contentResolver.openInputStream(uri0)
            val inStream1 = context.contentResolver.openInputStream(uri1)
            val inStream2 = context.contentResolver.openInputStream(uri2)

            inStream.use { input ->
                outStream.use { output ->
                    input?.copyTo(output)
                }
            }

            inStream1.use { input ->
                outStream1.use { output ->
                    input?.copyTo(output)
                }
            }

            inStream2.use { input ->
                outStream2.use { output ->
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
        else {
            (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                Snackbar.make(it, R.string.restore_wrong_files, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun backupDrive(drive: Drive, context: Context) = scope.launch(Dispatchers.IO) {
        repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val inFile = context.getDatabasePath("coach_database")
        val inFile1 = context.getDatabasePath("coach_database-shm")
        val inFile2 = context.getDatabasePath("coach_database-wal")

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val formatted = current.format(formatter)

        val prefix = "PayingCamelDatabase_$formatted"
        val outPath = "$prefix.pcbackup"
        val outPath1 = "$prefix-shm.pcbackup"
        val outPath2 = "$prefix-wal.pcbackup"

        try {

            val gFile = com.google.api.services.drive.model.File()
            gFile.name = outPath
            gFile.parents = listOf("appDataFolder")
            val gFile1 = com.google.api.services.drive.model.File()
            gFile1.name = outPath1
            gFile1.parents = listOf("appDataFolder")
            val gFile2 = com.google.api.services.drive.model.File()
            gFile2.name = outPath2
            gFile2.parents = listOf("appDataFolder")

            val fileContent = FileContent("application/x-sqlite3", inFile)
            val fileContent1 = FileContent("application/x-sqlite3", inFile1)
            val fileContent2 = FileContent("application/x-sqlite3", inFile2)

            drive.Files().create(gFile, fileContent).setFields("id").execute()
            drive.Files().create(gFile1, fileContent1).setFields("id").execute()
            drive.Files().create(gFile2, fileContent2).setFields("id").execute()

            (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                Snackbar.make(it, R.string.database_backed_up, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        catch (e: Exception){
            (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                Snackbar.make(it, context.getString(R.string.google_drive_backup_failed, e.localizedMessage), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    fun restoreDrive(drive: Drive, context: Context) = scope.launch(Dispatchers.IO) {
        /*repository.checkpoint(SimpleSQLiteQuery("pragma wal_checkpoint(full)"))

        val inUri0 = clipData.getItemAt(0).uri
        val inUri1 = clipData.getItemAt(1).uri
        val inUri2 = clipData.getItemAt(2).uri

        val outStream = context.getDatabasePath("coach_database").outputStream()
        val outStream1 = context.getDatabasePath("coach_database-shm").outputStream()
        val outStream2 = context.getDatabasePath("coach_database-wal").outputStream()

        val inFileName0 = getFileName(inUri0, context)
        val inFileName1 = getFileName(inUri1, context)
        val inFileName2 = getFileName(inUri2, context)

        var uri0: Uri? = null
        var uri1: Uri? = null
        var uri2: Uri? = null

        when {
            inFileName0?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri0
            }
            inFileName0?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri0
            }
            inFileName0?.endsWith(".pcbackup") == true -> {
                uri0 = inUri0
            }
        }

        when {
            inFileName1?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri1
            }
            inFileName1?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri1
            }
            inFileName1?.endsWith(".pcbackup") == true -> {
                uri0 = inUri1
            }
        }

        when {
            inFileName2?.endsWith("-wal.pcbackup") == true -> {
                uri2 = inUri2
            }
            inFileName2?.endsWith("-shm.pcbackup") == true -> {
                uri1 = inUri2
            }
            inFileName2?.endsWith(".pcbackup") == true -> {
                uri0 = inUri2
            }
        }

        if ((uri0 != null) && (uri1 != null) && (uri2 != null)){
            val inStream = context.contentResolver.openInputStream(uri0)
            val inStream1 = context.contentResolver.openInputStream(uri1)
            val inStream2 = context.contentResolver.openInputStream(uri2)

            inStream.use { input ->
                outStream.use { output ->
                    input?.copyTo(output)
                }
            }

            inStream1.use { input ->
                outStream1.use { output ->
                    input?.copyTo(output)
                }
            }

            inStream2.use { input ->
                outStream2.use { output ->
                    input?.copyTo(output)
                }
            }

            try {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
                if (context is Activity) {
                    context.finish()
                }

                Runtime.getRuntime().exit(0)

                (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                    Snackbar.make(it, R.string.database_restored, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            catch (e: Exception) {
                (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                    Snackbar.make(it, context.getString(R.string.google_drive_restore_failed, e.localizedMessage), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
        else {
            (context as Activity).findViewById<View>(android.R.id.content).rootView?.let{
                Snackbar.make(it, R.string.restore_wrong_files, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }*/
    }

    override fun onCleared() {
    super.onCleared()
    parentJob.cancel()
    }
}

fun getFileName(uri: Uri, context: Context): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use{
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex >= 0) {
                    result = cursor.getString(columnIndex)
                }
            }
        }
    }
    if (result == null) {
        uri.path?.let { path ->
            result = path
            val cut = result?.lastIndexOf('/')
            cut?.let {
                if (cut != -1) {
                    result = result?.substring(it + 1)
                }
            }
        }
    }
    return result
}