package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.path.Path
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.ui.pickers.*
import com.guillaumetousignant.payingcamel.ui.trips.NewTripViewModel
import com.guillaumetousignant.payingcamel.ui.trips.NewTripViewModelFactory
import java.text.DateFormat

class NewTripActivity : AppCompatActivity(R.layout.activity_new_trip) {

    private lateinit var newTripViewModel: NewTripViewModel // Added
    private lateinit var editNameView: EditText
    private lateinit var startTimeText: TextView
    private lateinit var startDateText: TextView
    private lateinit var pathNameText: TextView
    private lateinit var skaterNameText: TextView
    private lateinit var courseNameText: TextView
    private lateinit var editNoteView : EditText
    private lateinit var distanceView : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        editNameView = findViewById(R.id.trip_edit_name)
        startTimeText = findViewById(R.id.trip_start_time)
        startDateText = findViewById(R.id.trip_start_date)
        pathNameText = findViewById(R.id.trip_path_name)
        skaterNameText = findViewById(R.id.trip_skater_name)
        courseNameText = findViewById(R.id.trip_course_name)
        editNoteView = findViewById(R.id.trip_edit_note)
        distanceView = findViewById(R.id.trip_edit_distance)

        val initCalendar = intent.getSerializableExtra(EXTRA_CALENDAR) as Calendar
        initCalendar.set(Calendar.MILLISECOND, 0)
        initCalendar.set(Calendar.SECOND, 0)
        initCalendar.set(Calendar.MINUTE, 0)
        val factory = NewTripViewModelFactory(application, initCalendar)
        newTripViewModel =
            ViewModelProvider(this, factory).get(NewTripViewModel::class.java) // Added

        setSupportActionBar(findViewById(R.id.new_trip_toolbar))
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

        val skaterObserver = Observer<Skater?> { skater ->
            // Update the UI, in this case, a TextView.
            skater?.let{
                skaterNameText.text = "%s %s".format(it.first_name, it.last_name)
            }
        }

        val courseObserver = Observer<Course?> { course ->
            // Update the UI, in this case, a TextView.
            course?.let{
                courseNameText.text = it.name?:"(...)"
            }
        }

        val pathObserver = Observer<Path?> { path ->
            // Update the UI, in this case, a TextView.
            path?.let{
                pathNameText.text = it.name?:"(...)"
                distanceView.setText(path.distance.toString())
            }
        }

        newTripViewModel.startCalendar.observe(this, startObserver)
        newTripViewModel.skater.observe(this, skaterObserver)
        newTripViewModel.course.observe(this, courseObserver)
        newTripViewModel.path.observe(this, pathObserver)
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
                if (TextUtils.isEmpty(distanceView.text)) {
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
                    val note = if (TextUtils.isEmpty(editNoteView.text)) {
                        null
                    } else {
                        editNoteView.text.toString()
                    }
                    val distanceValue = distanceView.text.toString().toDouble()

                    replyIntent.putExtra(EXTRA_NAME, name)
                    replyIntent.putExtra(EXTRA_PATH, newTripViewModel.path.value?.name)
                    replyIntent.putExtra(EXTRA_FROM, newTripViewModel.path.value?.from)
                    replyIntent.putExtra(EXTRA_TO, newTripViewModel.path.value?.to)
                    replyIntent.putExtra(EXTRA_DISTANCE, distanceValue)
                    replyIntent.putExtra(EXTRA_COURSE, newTripViewModel.course.value?.uuid)
                    replyIntent.putExtra(EXTRA_SKATER, newTripViewModel.skater.value?.uuid)
                    replyIntent.putExtra(EXTRA_START, newTripViewModel.startCalendar.value)
                    replyIntent.putExtra(EXTRA_NOTE, note)

                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
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

    fun showStartTimePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        TimePickerFragment(newTripViewModel.startCalendar).show(supportFragmentManager, "TripStartTimePicker")
    }

    fun showStartDatePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        DatePickerFragment(newTripViewModel.startCalendar).show(supportFragmentManager, "TripStartDatePicker")
    }

    fun showSkaterPickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        SkaterPickerFragment(newTripViewModel.skater, newTripViewModel.allSkaters).show(supportFragmentManager, "TripSkaterPicker")
    }

    fun showCoursePickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        CoursePickerFragment(newTripViewModel.course, newTripViewModel.allCourses).show(supportFragmentManager, "TripCoursePicker")
    }

    fun showPathPickerDialog(@Suppress("UNUSED_PARAMETER") v: View) {
        PathPickerFragment(newTripViewModel.path, newTripViewModel.allPaths).show(supportFragmentManager, "TripPathPicker")
    }

    companion object {
        const val EXTRA_PATH = "com.example.android.wordlistsql.REPLY_PATH"
        const val EXTRA_FROM = "com.example.android.wordlistsql.REPLY_FROM"
        const val EXTRA_TO = "com.example.android.wordlistsql.REPLY_TO"
        const val EXTRA_DISTANCE = "com.example.android.wordlistsql.REPLY_DISTANCE"
        const val EXTRA_START = "com.example.android.wordlistsql.REPLY_START"
        const val EXTRA_COURSE = "com.example.android.wordlistsql.REPLY_COURSE"
        const val EXTRA_SKATER = "com.example.android.wordlistsql.REPLY_SKATER"
        const val EXTRA_NAME = "com.example.android.wordlistsql.REPLY_NAME"
        const val EXTRA_NOTE = "com.example.android.wordlistsql.REPLY_NOTE"
        const val EXTRA_CALENDAR = "com.example.android.wordlistsql.INPUT_CALENDAR"
    }
}
