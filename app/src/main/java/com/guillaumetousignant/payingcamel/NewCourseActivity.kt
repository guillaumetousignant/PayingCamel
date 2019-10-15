package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

import java.util.UUID
import android.icu.util.Calendar
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.ui.new_course.NewCourseViewModel
import android.widget.Toast
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MenuItem


/**
 * Activity for entering a word.
 */
class NewCourseActivity : AppCompatActivity() {

    private lateinit var editCourseView: EditText
    private lateinit var overviewViewModel: NewCourseViewModel // Added

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)
        editCourseView = findViewById(R.id.edit_course)

        overviewViewModel =
            ViewModelProviders.of(this).get(NewCourseViewModel::class.java) // Added

        val button = findViewById<Button>(R.id.button_save_course)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editCourseView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val name = editCourseView.text.toString()
                replyIntent.putExtra(EXTRA_NAME, name)
                val skater = UUID.randomUUID()
                replyIntent.putExtra(EXTRA_SKATER, skater)
                val startTime = Calendar.getInstance()
                replyIntent.putExtra(EXTRA_START, startTime)
                val endTime = Calendar.getInstance()
                replyIntent.putExtra(EXTRA_END, endTime)
                val rate = 1000
                replyIntent.putExtra(EXTRA_RATE, rate)
                val amount = 1000
                replyIntent.putExtra(EXTRA_AMOUNT, amount)
                val note : String? = null
                replyIntent.putExtra(EXTRA_NOTE, note)
                val paid = false
                replyIntent.putExtra(EXTRA_PAID, paid)

                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        setSupportActionBar(findViewById(R.id.new_course_toolbar))

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val replyIntent = Intent()

        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED, replyIntent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
        const val EXTRA_START = "com.example.android.wordlistsql.REPLY_START"
        const val EXTRA_END = "com.example.android.wordlistsql.REPLY_END"
        const val EXTRA_RATE = "com.example.android.wordlistsql.REPLY_RATE"
        const val EXTRA_AMOUNT = "com.example.android.wordlistsql.REPLY_AMOUNT"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_PAID = "com.example.android.wordlistsql.REPLY_PAID"
    }
}
