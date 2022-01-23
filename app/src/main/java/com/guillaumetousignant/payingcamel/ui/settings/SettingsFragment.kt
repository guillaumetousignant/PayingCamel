package com.guillaumetousignant.payingcamel.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
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
            "sign_in" -> {
                // user clicked "sign_in" button
                // take appropriate actions
                // return "true" to indicate you handled the click
                googleSignIn()

                true
            }
            "sign_out" -> {
                // user clicked "sign_out" button
                // take appropriate actions
                // return "true" to indicate you handled the click
                googleSignOut()

                true
            }
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

    private fun isUserSignedIn(): Boolean {
        context?.let {
            val account = GoogleSignIn.getLastSignedInAccount(it)
            return account != null
        }
        return false
    }

    private fun googleSignIn() {
        if (!isUserSignedIn()) {
            getGoogleSingInClient()?.let {
                startSignInForResult.launch(it.signInIntent)
            } ?:run {
                view?.let {
                    Snackbar.make(it, R.string.no_sign_in_client, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
                }
            }
        } else {
            view?.let {
                Snackbar.make(it, R.string.already_signed_in, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun googleSignOut() {
        if (isUserSignedIn()){
            getGoogleSingInClient()?.let { googleSignInClient ->
                googleSignInClient.signOut().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        view?.let {
                            Snackbar.make(it, R.string.signed_out, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show()
                        }
                    } else {
                        view?.let {
                            Snackbar.make(it, R.string.failed_sign_out, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show()
                        }
                    }
                }
            } ?:run {
                view?.let {
                    Snackbar.make(it, R.string.no_sign_in_client, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show()
                }
            }
        } else {
            view?.let {
                Snackbar.make(it, R.string.not_signed_in, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun getGoogleSingInClient() : GoogleSignInClient? {
        /**
         * Configure sign-in to request the user's ID, email address, and basic
         * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
         */
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(Scope(Scopes.DRIVE_FILE))
            .build()


        /**
         * Build a GoogleSignInClient with the options specified by gso.
         */
        activity?.let {
            return GoogleSignIn.getClient(it, gso)
        }
        return null
    }

    private val startSignInForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    handleSignData(data)
                }
            }
            Activity.RESULT_CANCELED -> {
                view?.let {
                    Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
            else -> {
                view?.let {
                    Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private fun handleSignData(data: Intent?) {
        // The Task returned from this call is always completed, no need to attach
        // a listener.
        GoogleSignIn.getSignedInAccountFromIntent(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    // user successfully logged-in
                    view?.let {
                        Snackbar.make(it, String.format(this.getString(R.string.signed_in), task.result?.displayName), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                } else {
                    // authentication failed
                    view?.let {
                        Snackbar.make(it, String.format(this.getString(R.string.failed_sign_in), task.exception), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
            }
    }
}