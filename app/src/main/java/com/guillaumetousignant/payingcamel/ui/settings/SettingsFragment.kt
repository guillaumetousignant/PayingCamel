package com.guillaumetousignant.payingcamel.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.guillaumetousignant.payingcamel.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class SettingsFragment : PreferenceFragmentCompat() { // Changed

    private val backupRequestAuthorize = 1234567
    private val restoreRequestAuthorize = 1234568

    private lateinit var settingsViewModel: SettingsViewModel

    private val coroutineScope = MainScope() // Probably needs to be CoroutineScope(), but this needs a CoroutineContext as an argument,

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) { // Added
        settingsViewModel =
            ViewModelProvider(this)[SettingsViewModel::class.java]
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
            "backup_drive" -> {
                // user clicked "backup_drive" button
                // take appropriate actions
                // return "true" to indicate you handled the click
                context?.let { theContext ->
                    if (ContextCompat.checkSelfPermission(theContext, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        backupDriveClick()
                    } else {
                        backupInternetResultLauncher.launch(Manifest.permission.INTERNET)
                    }
                }

                true
            }
            "restore_drive" -> {
                context?.let { theContext ->
                    if (ContextCompat.checkSelfPermission(theContext, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                        restoreDriveClick()
                    } else {
                        restoreInternetResultLauncher.launch(Manifest.permission.INTERNET)
                    }
                }

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

    private fun googleSignIn() {
        val nonce = "${getString(R.string.app_name)}-getGoogleSingInClient-${Calendar.getInstance()}"

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // true in doc
            .setServerClientId("304629624245-noig90cri5lom6jpp40ec9oag3ikkpj7.apps.googleusercontent.com") // WEB_CLIENT_ID originally
            .setAutoSelectEnabled(true)
            .setNonce(nonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        context?.let  { theContext ->
            val credentialManager = CredentialManager.create(theContext)

            coroutineScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = theContext,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    handleSignInFailure(e)
                }
            }
        }
    }

    private fun googleSignOut() {
        context?.let  { theContext ->
            val request = ClearCredentialStateRequest()

            val credentialManager = CredentialManager.create(theContext)
            coroutineScope.launch {
                try {
                    credentialManager.clearCredentialState(
                        request = request
                    )
                    view?.let {
                        Snackbar.make(it, getString(R.string.signed_out), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
                catch(e: ClearCredentialException) {
                    view?.let {
                        Snackbar.make(it, getString(R.string.failed_sign_out, e.localizedMessage), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        view?.let {
                            Snackbar.make(it, this.getString(R.string.signed_in, googleIdTokenCredential.id), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        view?.let {
                            Snackbar.make(it, this.getString(R.string.invalid_google_token_id_response), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show()
                        }
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    view?.let {
                        Snackbar.make(it, this.getString(R.string.unexpected_type_custom_credential), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                view?.let {
                    Snackbar.make(it, this.getString(R.string.unexpected_type_custom_credential), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private fun handleSignInFailure(error: GetCredentialException) {
        view?.let {
            Snackbar.make(it, this.getString(R.string.failed_sign_in, error.localizedMessage), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun backupDriveClick() {
        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))
        val authorizationRequest = AuthorizationRequest.Builder().setRequestedScopes(requestedScopes).build()
        context?.let {
            Identity.getAuthorizationClient(it)
                .authorize(authorizationRequest)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        // Access needs to be granted by the user
                        val pendingIntent = authorizationResult.pendingIntent
                        try {
                            pendingIntent?.let { thePendingIntent ->
                                startIntentSenderForResult(
                                    thePendingIntent.intentSender,
                                    backupRequestAuthorize, null, 0, 0, 0, null
                                );
                            }

                        } catch ( e: IntentSender.SendIntentException) {
                            view?.let { theView ->
                                Snackbar.make(theView, "Couldn't start Authorization UI: ${e.localizedMessage}", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }
                        }
                    } else {
                        // Access already granted, continue with user action
                        val drive = getDrive(authorizationResult)
                        googleDriveBackup(drive);
                    }
                }
                .addOnFailureListener { e ->
                    view?.let { theView ->
                        Snackbar.make(theView, "Failed to authorize, error: ${e.localizedMessage}", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
        }
    }

    private fun getDrive(authorizationResult: AuthorizationResult) : Drive {
        val credentials =
            GoogleCredentials.create(AccessToken(authorizationResult.accessToken, null))

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        )
            .setApplicationName(getString(R.string.app_name))
            .build()
    }

    private fun googleDriveBackup(drive: Drive) {
        context?.let { theContext ->
            settingsViewModel.backupDrive(drive, theContext)
        }
    }

    private fun restoreDriveClick() {
        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))
        val authorizationRequest = AuthorizationRequest.Builder().setRequestedScopes(requestedScopes).build()
        context?.let {
            Identity.getAuthorizationClient(it)
                .authorize(authorizationRequest)
                .addOnSuccessListener { authorizationResult ->
                    if (authorizationResult.hasResolution()) {
                        // Access needs to be granted by the user
                        val pendingIntent = authorizationResult.pendingIntent
                        try {
                            pendingIntent?.let { thePendingIntent ->
                                startIntentSenderForResult(
                                    thePendingIntent.intentSender,
                                    restoreRequestAuthorize, null, 0, 0, 0, null
                                );
                            }

                        } catch ( e: IntentSender.SendIntentException) {
                            view?.let { theView ->
                                Snackbar.make(theView, "Couldn't start Authorization UI: ${e.localizedMessage}", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show()
                            }
                        }
                    } else {
                        // Access already granted, continue with user action
                        val drive = getDrive(authorizationResult)
                        googleDriveRestore(drive);
                    }
                }
                .addOnFailureListener { e ->
                    view?.let { theView ->
                        Snackbar.make(theView, "Failed to authorize, error: ${e.localizedMessage}", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                }
        }
    }

    private fun googleDriveRestore(drive: Drive) {
        context?.let { theContext ->
            settingsViewModel.restoreDrive(drive, theContext)
            view?.let{ theView ->
                Snackbar.make(theView, R.string.database_restored, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private val backupInternetResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Handle Permission granted/rejected
        if (isGranted) {
            // Permission is granted
            backupDriveClick()
        } else {
            // Permission is denied
            view?.let {
                Snackbar.make(it, R.string.no_internet_permission, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            }
        }
    }

    private val restoreInternetResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // Handle Permission granted/rejected
        if (isGranted) {
            // Permission is granted
            restoreDriveClick()
        } else {
            // Permission is denied
            view?.let {
                Snackbar.make(it, R.string.no_internet_permission, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            }
        }
    }

    @Deprecated("This is deprecated, but there is no replacement for requestPermissions by Google yet.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            restoreRequestAuthorize -> {
                if(resultCode == Activity.RESULT_OK) {
                    context?.let {
                        val authorizationResult = Identity.getAuthorizationClient(it).getAuthorizationResultFromIntent(data)
                        val drive = getDrive(authorizationResult)
                        googleDriveRestore(drive)
                    }
                }
                else {
                    // Permission is denied
                    view?.let {
                        Snackbar.make(it, R.string.no_drive_permission, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show()
                    }
                }
            }
            backupRequestAuthorize -> {
                if(resultCode == Activity.RESULT_OK) {
                    context?.let {
                        val authorizationResult = Identity.getAuthorizationClient(it).getAuthorizationResultFromIntent(data)
                        val drive = getDrive(authorizationResult)
                        googleDriveBackup(drive)
                    }
                }
                else {
                    // Permission is denied
                    view?.let {
                        Snackbar.make(it, R.string.no_drive_permission, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show()
                    }
                }
            }
        }
    }
}
