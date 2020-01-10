package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat // Added
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.R

class SettingsFragment : PreferenceFragmentCompat() { // Changed

    private lateinit var settingsViewModel: SettingsViewModel
    private val backupActivityRequestCode = 8
    private val restoreActivityRequestCode = 9

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        setPreferencesFromResource(R.xml.preference_screen, rootKey) // Added
    } // Added

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == backupActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let {data ->

                val inPath = context?.getDatabasePath("coach_database")
                val outPath = data.data

                inPath?.let { inputPath ->
                    outPath?.let {outputPath ->
                        context?.let{ theContext ->
                            settingsViewModel.backup(inputPath, outputPath, theContext.contentResolver)
                        }
                    }
                }
            }
        }
        else if (requestCode == backupActivityRequestCode) {
            view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }
        }
        else if (requestCode == restoreActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let {data ->

                val outPath = context?.getDatabasePath("coach_database")
                val inPath = data.data

                inPath?.let { inputPath ->
                    outPath?.let {outputPath ->
                        context?.let{ theContext ->
                            settingsViewModel.restore(inputPath, outputPath, theContext.contentResolver)
                        }
                    }
                }
            }
        }
        else if (requestCode == restoreActivityRequestCode) {
            view?.let{
                Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
}