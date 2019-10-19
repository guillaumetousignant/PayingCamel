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
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.SkaterPickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.TimePickerFragment
import java.text.DateFormat
import androidx.fragment.app.FragmentManager
import com.guillaumetousignant.payingcamel.database.Skater


/**
 * Activity for entering a word.
 */
class NewCourseActivity : AppCompatActivity() {

    private lateinit var newCourseViewModel: NewCourseViewModel // Added
    private lateinit var editCourseView: EditText
    private lateinit var startTimeText: TextView
    private lateinit var startDateText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var endDateText: TextView
    private lateinit var skaterNameText: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_course)
        editCourseView = findViewById(R.id.edit_course)
        startTimeText = findViewById(R.id.start_time)
        startDateText = findViewById(R.id.start_date)
        endTimeText = findViewById(R.id.end_time)
        endDateText = findViewById(R.id.end_date)
        skaterNameText = findViewById(R.id.skater_name)

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

        val endObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT) // CHECK add locale
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            endTimeText.text = timeFormat.format(calendar.time)
            endDateText.text = dateFormat.format(calendar.time)
        }

        val skaterObserver = Observer<Skater?> { skater ->
            // Update the UI, in this case, a TextView.
            skater?.let{
                skaterNameText.text = "%s %s".format(it.last_name, it.last_name)
            }
        }

        newCourseViewModel.startCalendar.observe(this, startObserver)
        newCourseViewModel.endCalendar.observe(this, endObserver)
        newCourseViewModel.skater.observe(this, skaterObserver)
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
                    replyIntent.putExtra(EXTRA_SKATER, newCourseViewModel.skater.value?.uuid)
                    replyIntent.putExtra(EXTRA_START, newCourseViewModel.startCalendar.value)
                    replyIntent.putExtra(EXTRA_END, newCourseViewModel.endCalendar.value)
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

    fun showStartTimePickerDialog(v: View) {
        TimePickerFragment(newCourseViewModel.startCalendar).show(supportFragmentManager, "StartTimePicker")
    }

    fun showStartDatePickerDialog(v: View) {
        DatePickerFragment(newCourseViewModel.startCalendar).show(supportFragmentManager, "StartDatePicker")
    }

    fun showEndTimePickerDialog(v: View) {
        TimePickerFragment(newCourseViewModel.endCalendar).show(supportFragmentManager, "EndTimePicker")
    }

    fun showEndDatePickerDialog(v: View) {
        DatePickerFragment(newCourseViewModel.endCalendar).show(supportFragmentManager, "EndDatePicker")
    }

    fun showSkaterPickerDialog(v: View) {
        SkaterPickerFragment(newCourseViewModel.skater, newCourseViewModel.allSkaters).show(supportFragmentManager, "SkaterPicker")
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
