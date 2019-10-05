package com.guillaumetousignant.payingcamel.ui.settings

import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat // Added
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

class SettingsFragment : PreferenceFragmentCompat() { // Changed

    //private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        setPreferencesFromResource(R.xml.preference_screen, rootKey) // Added
    } // Added

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_settings)
        settingsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }*/
}