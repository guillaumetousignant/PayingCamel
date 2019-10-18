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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.TimePickerFragment
import java.text.DateFormat


/**
 * Activity for entering a word.
 */
class NewCourseActivity : AppCompatActivity() {

    private lateinit var newCourseViewModel: NewCourseViewModel // Added
    private lateinit var editCourseView: EditText
    private lateinit var startTimeText: TextView
    private lateinit var startDateText: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)
        editCourseView = findViewById(R.id.edit_course)
        startTimeText = findViewById(R.id.start_time)
        startDateText = findViewById(R.id.start_date)

        newCourseViewModel =
            ViewModelProviders.of(this).get(NewCourseViewModel::class.java) // Added

        setSupportActionBar(findViewById(R.id.new_course_toolbar))

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp) // set drawable icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        window.statusBarColor = getColor(R.color.colorPrimaryDark) // Why is this needed??

        val startObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT) // CHECK add locale
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            startTimeText.text = timeFormat.format(calendar.time)
            startDateText.text = dateFormat.format(calendar.time)
        }

        newCourseViewModel.calendar.observe(this, startObserver)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val replyIntent = Intent()

        return when (item.itemId) {
            android.R.id.home -> {
                setResult(Activity.RESULT_CANCELED, replyIntent)
                finish()
                true
            }
            R.id.new_word_save_button -> {
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

    fun showTimePickerDialog(v: View) {
        TimePickerFragment(newCourseViewModel.calendar).show(supportFragmentManager, "StartTimePicker")
    }

    fun showDatePickerDialog(v: View) {
        DatePickerFragment(newCourseViewModel.calendar).show(supportFragmentManager, "StartDatePicker")
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
