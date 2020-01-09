package com.guillaumetousignant.payingcamel.ui.settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceFragmentCompat // Added
import com.guillaumetousignant.payingcamel.R

class SettingsFragment : PreferenceFragmentCompat() { // Changed

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        setPreferencesFromResource(R.xml.preference_screen, rootKey) // Added
    } // Added
}