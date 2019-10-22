package com.guillaumetousignant.payingcamel.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat // Added
import com.guillaumetousignant.payingcamel.R

class SettingsFragment : PreferenceFragmentCompat() { // Changed

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        setPreferencesFromResource(R.xml.preference_screen, rootKey) // Added
    } // Added
}