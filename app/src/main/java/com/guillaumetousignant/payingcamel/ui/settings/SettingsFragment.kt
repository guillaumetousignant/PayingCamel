package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.R


class SettingsFragment : PreferenceFragmentCompat() { // Changed

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        setPreferencesFromResource(R.xml.preference_screen, rootKey) // Added
    } // Added

    private val startBackupForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    data.data?.let { outputPath ->
                        context?.let { theContext ->
                            settingsViewModel.backup(outputPath, theContext)
                            view?.let{
                                Snackbar.make(it, R.string.database_backed_up, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                view?.let{
                    Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            else -> {
                view?.let{
                    Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private val startRestoreForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val clipData = data.clipData

                    if ((clipData != null) && (clipData.itemCount == 3)) {
                        context?.let{
                            settingsViewModel.restore(clipData, it)
                            view?.let{ theView ->
                                Snackbar.make(theView, R.string.database_restored, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }
                        }
                    }
                    else {
                        view?.let{
                            Snackbar.make(it, R.string.restore_too_few_files, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                view?.let{
                    Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            else -> {
                view?.let{
                    Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        super.onPreferenceTreeClick(preference)
        return when(preference.key) {
            "backup" -> {
                // user clicked "backup" button
                // take appropriate actions
                // return "true" to indicate you handled the click
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                startBackupForResult.launch(intent)

                true
            }
            "restore" -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "application/x-sqlite3" // this line is a must when using ACTION_CREATE_DOCUMENT
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startRestoreForResult.launch(intent)

                true
            }
            else -> false
        }
    }
}