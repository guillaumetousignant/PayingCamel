package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
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
            intentData?.data?.let {outputPath ->
                context?.let{ theContext ->
                    settingsViewModel.backup(outputPath, theContext)
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
            intentData?.data?.let {inputPath ->
                context?.let{ theContext ->
                    settingsViewModel.restore(inputPath, theContext)
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

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        super.onPreferenceTreeClick(preference)
        return when(preference.key) {
            "backup" -> {
                // user clicked "backup" button
                // take appropriate actions
                // return "true" to indicate you handled the click
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.type = "*/*" // this line is a must when using ACTION_CREATE_DOCUMENT
                startActivityForResult(intent, backupActivityRequestCode)

                true
            }
            "restore" -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*" // this line is a must when using ACTION_CREATE_DOCUMENT
                startActivityForResult(intent, restoreActivityRequestCode)

                true
            }
            else -> false
        }
    }
}