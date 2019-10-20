package com.guillaumetousignant.payingcamel

import android.app.Activity
import android.content.Intent
import android.icu.text.NumberFormat
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.guillaumetousignant.payingcamel.database.Course
import com.guillaumetousignant.payingcamel.database.Path
import com.guillaumetousignant.payingcamel.database.Skater
import com.guillaumetousignant.payingcamel.ui.pickers.*
import com.guillaumetousignant.payingcamel.ui.trips.NewTripViewModel
import com.guillaumetousignant.payingcamel.ui.trips.NewTripViewModelFactory
import java.text.DateFormat

class NewTripActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_new_trip)

        editNameView = findViewById(R.id.trip_edit_name)
        startTimeText = findViewById(R.id.trip_start_time)
        startDateText = findViewById(R.id.trip_start_date)
        pathNameText = findViewById(R.id.trip_path_name)
        skaterNameText = findViewById(R.id.trip_skater_name)
        courseNameText = findViewById(R.id.trip_course_name)
        editNoteView = findViewById(R.id.trip_edit_note)
        distanceView = findViewById(R.id.trip_edit_distance)

        val initCalendar = intent.getSerializableExtra(EXTRA_CALENDAR) as Calendar

        val factory = NewTripViewModelFactory(application, initCalendar)
        newTripViewModel =
            ViewModelProviders.of(this, factory).get(NewTripViewModel::class.java) // Added

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
                newTripViewModel.distance.postValue(path.distance)
            }
        }

        val distanceObserver = Observer<Double> { amount ->
            // Update the UI, in this case, a TextView.
            amount?.let{
                //distanceView.setText(NumberFormat.getCurrencyInstance().format(it/100))
                distanceView.setText(it.toString())
            }
        }

        newTripViewModel.startCalendar.observe(this, startObserver)
        newTripViewModel.skater.observe(this, skaterObserver)
        newTripViewModel.course.observe(this, courseObserver)
        newTripViewModel.path.observe(this, pathObserver)
        newTripViewModel.distance.observe(this, distanceObserver)

        distanceView.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != current){
                    current = s.toString()
                    newTripViewModel.distance.value = current.toDouble()
                }
            }
        })

        /*distanceView.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString() != current){
                    distanceView.removeTextChangedListener(this)

                    val replaceable =
                        String.format("[%s,.]", NumberFormat.getCurrencyInstance().currency.symbol)
                    val cleanString = s.toString().replace(replaceable.toRegex(), "").replace("\\s".toRegex(), "")
                    val parsed = cleanString.toDouble()
                    val formatted = NumberFormat.getCurrencyInstance().format((parsed/100))

                    current = formatted
                    distanceView.setText(formatted)
                    distanceView.setSelection(formatted.length)

                    distanceView.addTextChangedListener(this)

                    newTripViewModel.distance.postValue(parsed)
                }
            }
        })*/
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
                /*if (newTripViewModel.startCalendar.value?.compareTo(newTripViewModel.endCalendar.value)?:0 > 0) {
                    val view = findViewById<View>(android.R.id.content)

                    Snackbar.make(view, R.string.end_before_start, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                else {*/
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

                    replyIntent.putExtra(EXTRA_NAME, name)
                    replyIntent.putExtra(EXTRA_PATH, newTripViewModel.path.value?.name)
                    replyIntent.putExtra(EXTRA_FROM, newTripViewModel.path.value?.from)
                    replyIntent.putExtra(EXTRA_TO, newTripViewModel.path.value?.to)
                    replyIntent.putExtra(EXTRA_DISTANCE, newTripViewModel.distance.value)
                    replyIntent.putExtra(EXTRA_COURSE, newTripViewModel.course.value?.uuid)
                    replyIntent.putExtra(EXTRA_SKATER, newTripViewModel.skater.value?.uuid)
                    replyIntent.putExtra(EXTRA_START, newTripViewModel.startCalendar.value)
                    replyIntent.putExtra(EXTRA_NOTE, note)

                    setResult(Activity.RESULT_OK, replyIntent)
                    finish()
                //}

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
        TimePickerFragment(newTripViewModel.startCalendar).show(supportFragmentManager, "TripStartTimePicker")
    }

    fun showStartDatePickerDialog(v: View) {
        DatePickerFragment(newTripViewModel.startCalendar).show(supportFragmentManager, "TripStartDatePicker")
    }

    fun showSkaterPickerDialog(v: View) {
        SkaterPickerFragment(newTripViewModel.skater, newTripViewModel.allSkaters).show(supportFragmentManager, "TripSkaterPicker")
    }

    fun showCoursePickerDialog(v: View) {
        CoursePickerFragment(newTripViewModel.course, newTripViewModel.allCourses).show(supportFragmentManager, "TripCoursePicker")
    }

    fun showPathPickerDialog(v: View) {
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
