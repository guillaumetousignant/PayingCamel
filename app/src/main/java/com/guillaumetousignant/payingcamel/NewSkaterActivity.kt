package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

class NewSkaterActivity : AppCompatActivity(R.layout.activity_new_skater) {

    private lateinit var firstNameView: EditText
    private lateinit var lastNameView: EditText
    private lateinit var editEmailView: EditText
    private lateinit var activeCheckbox : CheckBox
    private lateinit var editNoteView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firstNameView = findViewById(R.id.edit_first_name)
        lastNameView = findViewById(R.id.edit_last_name)
        editEmailView = findViewById(R.id.edit_email)
        activeCheckbox = findViewById(R.id.active_checkbox)
        editNoteView = findViewById(R.id.edit_skater_note)

        setSupportActionBar(findViewById(R.id.new_skater_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                val replyIntent = Intent()
                setResult(Activity.RESULT_CANCELED, replyIntent)
                finish()
                true
            }
            R.id.new_word_save_button -> {
                when {
                    TextUtils.isEmpty(firstNameView.text) -> {
                        val view = findViewById<View>(android.R.id.content)

                        Snackbar.make(view, R.string.missing_first_name, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                    TextUtils.isEmpty(lastNameView.text) -> {
                        val view = findViewById<View>(android.R.id.content)

                        Snackbar.make(view, R.string.missing_last_name, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }
                    else -> {
                        val replyIntent = Intent()

                        val note = if (TextUtils.isEmpty(editNoteView.text)) {
                            null
                        } else {
                            editNoteView.text.toString()
                        }
                        val email = if (TextUtils.isEmpty(editEmailView.text)) {
                            null
                        } else {
                            editEmailView.text.toString()
                        }

                        replyIntent.putExtra(EXTRA_FIRST_NAME, firstNameView.text.toString())
                        replyIntent.putExtra(EXTRA_LAST_NAME, lastNameView.text.toString())
                        replyIntent.putExtra(EXTRA_NOTE, note)
                        replyIntent.putExtra(EXTRA_EMAIL, email)
                        replyIntent.putExtra(EXTRA_ACTIVE, activeCheckbox.isChecked)

                        setResult(Activity.RESULT_OK, replyIntent)
                        finish()
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_word_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val EXTRA_FIRST_NAME = "com.example.android.wordlistsql.REPLY_FIRST_NAME"
        const val EXTRA_LAST_NAME = "com.example.android.wordlistsql.REPLY_LAST_NAME"
        const val EXTRA_EMAIL = "com.example.android.wordlistsql.REPLY_EMAIL"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_ACTIVE = "com.example.android.wordlistsql.REPLY_ACTIVE"
    }
}
