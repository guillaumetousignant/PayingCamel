package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

class NewPathActivity : AppCompatActivity(R.layout.activity_new_path) {

    private lateinit var editNameView: EditText
    private lateinit var editDistanceView: EditText
    private lateinit var editFromView: EditText
    private lateinit var editToView: EditText
    private lateinit var editNoteView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editNameView = findViewById(R.id.path_edit_name)
        editDistanceView = findViewById(R.id.path_edit_distance)
        editFromView = findViewById(R.id.path_edit_from)
        editToView = findViewById(R.id.path_edit_to)
        editNoteView = findViewById(R.id.path_edit_note)

        setSupportActionBar(findViewById(R.id.new_path_toolbar))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.new_word_menu, menu)
        return super.onCreateOptionsMenu(menu)
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
                if (TextUtils.isEmpty(editDistanceView.text)) {
                    val view = findViewById<View>(android.R.id.content)

                    Snackbar.make(view, R.string.empty_distance, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                else {
                    val replyIntent = Intent()
                    val name = if (TextUtils.isEmpty(editNameView.text)) {
                        null
                    } else {
                        editNameView.text.toString()
                    }
                    val from = if (TextUtils.isEmpty(editFromView.text)) {
                        null
                    } else {
                        editFromView.text.toString()
                    }
                    val to = if (TextUtils.isEmpty(editToView.text)) {
                        null
                    } else {
                        editToView.text.toString()
                    }
                    val note = if (TextUtils.isEmpty(editNoteView.text)) {
                        null
                    } else {
                        editNoteView.text.toString()
                    }
                    replyIntent.putExtra(EXTRA_NAME, name)
                    replyIntent.putExtra(EXTRA_DISTANCE, editDistanceView.text.toString().toDouble())
                    replyIntent.putExtra(EXTRA_FROM, from)
                    replyIntent.putExtra(EXTRA_TO, to)
                    replyIntent.putExtra(EXTRA_NOTE, note)

                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_DISTANCE = "com.example.android.wordlistsql.REPLY_DISTANCE"
        const val EXTRA_FROM = "com.example.android.wordlistsql.REPLY_FROM"
        const val EXTRA_TO = "com.example.android.wordlistsql.REPLY_TO"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
    }
}
