package com.guillaumetousignant.payingcamel.ui.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import android.widget.CalendarView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar

import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.NewExpenseActivity
import com.guillaumetousignant.payingcamel.NewFillActivity
import com.guillaumetousignant.payingcamel.NewTripActivity
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    private val newCourseActivityRequestCode = 1
    private val newTripActivityRequestCode = 3
    private val newExpenseActivityRequestCode = 4
    private val newFillActivityRequestCode = 5
    private lateinit var calendarViewModel: CalendarViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarViewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)

        val calendarView = view.findViewById<CalendarView>(R.id.calendar_view)
        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            //val msg = "Selected date is %d-%d-%d" + dayOfMonth + "/" + (month + 1) + "/" + year
            /*val msg = getString(R.string.calendar_toast, year, (month+1), dayOfMonth)
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()*/
            calendarViewModel.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendarViewModel.calendar.set(Calendar.MONTH, month)
            calendarViewModel.calendar.set(Calendar.YEAR, year)
        }

        // REMOVE  if removed, first thing added from the main view will fail if nothing from the database is used (no rate etc)
        calendarViewModel.allCourses.observe(this, Observer { /*courses ->*/
            // Update the cached copy of the words in the adapter.
            //courses?.let { adapter.setCourses(it) }
        })
        calendarViewModel.allTrips.observe(this, Observer { /*trips ->*/
            // Update the cached copy of the words in the adapter.
            //trips?.let { adapter.setTrips(it) }
        })
        calendarViewModel.allExpenses.observe(this, Observer { /*expenses ->*/
            // Update the cached copy of the words in the adapter.
            //expenses?.let { adapter.setExpenses(it) }
        })
        calendarViewModel.allFills.observe(this, Observer { /*fills ->*/
            // Update the cached copy of the words in the adapter.
            //fills?.let { adapter.setFills(it) }
        })

        val speedDialView = view.findViewById<SpeedDialView>(R.id.speedDial)
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_calendar_trip, R.drawable.ic_svg_car_24px)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBackground, activity?.theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorAccent, activity?.theme))
                .setLabel(getString(R.string.label_trip))
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_calendar_expense, R.drawable.ic_svg_shopping_cart_24px)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBackground, activity?.theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorAccent, activity?.theme))
                .setLabel(getString(R.string.label_expense))
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .create())
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.fab_calendar_fill, R.drawable.ic_svg_gas_station_24px)
                .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorBackground, activity?.theme))
                .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorAccent, activity?.theme))
                .setLabel(getString(R.string.label_fill))
                .setLabelBackgroundColor(Color.TRANSPARENT)
                .create())

        val courseText = view.findViewById<TextView>(R.id.calendar_course_text)
        courseText.setOnClickListener {
            val intent = Intent(activity, NewCourseActivity::class.java)
            intent.putExtra(NewCourseActivity.EXTRA_CALENDAR, calendarViewModel.calendar)
            startActivityForResult(intent, newCourseActivityRequestCode)
        }
        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                val intent = Intent(activity, NewCourseActivity::class.java)
                intent.putExtra(NewCourseActivity.EXTRA_CALENDAR, calendarViewModel.calendar)
                startActivityForResult(intent, newCourseActivityRequestCode)

                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
                if (isOpen){
                    courseText.visibility = View.VISIBLE
                }
                else {
                    courseText.visibility = View.GONE
                }
            }
        })

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_calendar_trip -> {
                    val intent = Intent(activity, NewTripActivity::class.java)
                    intent.putExtra(NewTripActivity.EXTRA_CALENDAR, calendarViewModel.calendar)
                    startActivityForResult(intent, newTripActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_calendar_expense -> {
                    val intent = Intent(activity, NewExpenseActivity::class.java)
                    intent.putExtra(NewExpenseActivity.EXTRA_CALENDAR, calendarViewModel.calendar)
                    startActivityForResult(intent, newExpenseActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_calendar_fill -> {
                    val intent = Intent(activity, NewFillActivity::class.java)
                    intent.putExtra(NewFillActivity.EXTRA_CALENDAR, calendarViewModel.calendar)
                    startActivityForResult(intent, newFillActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == newCourseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val course = Course(
                    UUID.randomUUID(),
                    data.getSerializableExtra(NewCourseActivity.EXTRA_SKATER) as UUID?,
                    data.getSerializableExtra(NewCourseActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewCourseActivity.EXTRA_END) as Calendar,
                    data.getIntExtra(NewCourseActivity.EXTRA_RATE, 0),
                    data.getIntExtra(NewCourseActivity.EXTRA_AMOUNT, 0),
                    data.getStringExtra(NewCourseActivity.EXTRA_NAME),
                    data.getStringExtra(NewCourseActivity.EXTRA_NOTE),
                    data.getBooleanExtra(NewCourseActivity.EXTRA_PAID, false),
                    getRandomMaterialColor(getString(R.string.icon_color_type))
                )
                calendarViewModel.insert(course)
                Unit
            }
        }
        else if (requestCode == newCourseActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/
        }
        else if (requestCode == newTripActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val trip = Trip(
                    UUID.randomUUID(),
                    data.getStringExtra(NewTripActivity.EXTRA_PATH),
                    data.getStringExtra(NewTripActivity.EXTRA_FROM),
                    data.getStringExtra(NewTripActivity.EXTRA_TO),
                    data.getDoubleExtra(NewTripActivity.EXTRA_DISTANCE, 0.0),
                    data.getSerializableExtra(NewTripActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewTripActivity.EXTRA_COURSE) as UUID?,
                    data.getSerializableExtra(NewTripActivity.EXTRA_SKATER) as UUID?,
                    data.getStringExtra(NewTripActivity.EXTRA_NAME),
                    data.getStringExtra(NewTripActivity.EXTRA_NOTE)
                )
                calendarViewModel.insert(trip)
                Unit
            }
        }
        else if (requestCode == newTripActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/
        }
        else if (requestCode == newExpenseActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val expense = Expense(
                    UUID.randomUUID(),
                    data.getIntExtra(NewExpenseActivity.EXTRA_AMOUNT, 0),
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_START) as Calendar,
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_COURSE) as UUID?,
                    data.getSerializableExtra(NewExpenseActivity.EXTRA_SKATER) as UUID?,
                    data.getStringExtra(NewExpenseActivity.EXTRA_NAME),
                    data.getStringExtra(NewExpenseActivity.EXTRA_NOTE)
                )
                calendarViewModel.insert(expense)
                Unit
            }
        }
        else if (requestCode == newExpenseActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/
        }
        else if (requestCode == newFillActivityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.let { data ->
                val fill = Fill(
                    UUID.randomUUID(),
                    data.getIntExtra(NewFillActivity.EXTRA_AMOUNT, 0),
                    data.getSerializableExtra(NewFillActivity.EXTRA_START) as Calendar,
                    data.getStringExtra(NewFillActivity.EXTRA_NAME),
                    data.getStringExtra(NewFillActivity.EXTRA_NOTE)
                )
                calendarViewModel.insert(fill)
                Unit
            }
        }
        else if (requestCode == newFillActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
            /* view?.let{
                 Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                     .setAction("Action", null).show()
             }*/
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = resources.getIdentifier("mdcolor_$typeColor", "array", activity?.packageName)

        if (arrayId != 0) {
            val colors = resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    /*override fun onBackPressed() {
        // Closes menu if its opened.
        if (speedDialView.isOpen) {
            speedDialView.close()
        } else {
            super.onBackPressed()
        }
    }*/
}