package com.guillaumetousignant.payingcamel.ui.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.guillaumetousignant.payingcamel.R

import android.widget.CalendarView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

class CalendarFragment : Fragment() {

    private val newCourseActivityRequestCode = 1
    private val newTripActivityRequestCode = 3
    private val newExpenseActivityRequestCode = 4
    private val newFillActivityRequestCode = 5
    private lateinit var calendarViewModel: CalendarViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        calendarViewModel =
            ViewModelProviders.of(this).get(CalendarViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)

        val calendarView = root.findViewById<CalendarView>(R.id.calendar_view)
        calendarView?.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            //val msg = "Selected date is %d-%d-%d" + dayOfMonth + "/" + (month + 1) + "/" + year
            /*val msg = getString(R.string.calendar_toast, year, (month+1), dayOfMonth)
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()*/
            calendarViewModel.year = year
            calendarViewModel.month = month
            calendarViewModel.day = dayOfMonth
        }

        /*val textView: TextView = root.findViewById(R.id.text_calendar)
        calendarViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        /*val fabCalendar: FloatingActionButton = root.findViewById(R.id.fab_calendar)
        fabCalendar.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val speedDialView = root.findViewById<SpeedDialView>(R.id.speedDial)
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

        speedDialView.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                val intent = Intent(activity, NewCourseActivity::class.java)
                val intentCalendar = Calendar.getInstance()
                intentCalendar.set(Calendar.DAY_OF_MONTH, calendarViewModel.day)
                intentCalendar.set(Calendar.MONTH, calendarViewModel.month)
                intentCalendar.set(Calendar.YEAR, calendarViewModel.year)
                intent.putExtra(NewCourseActivity.EXTRA_CALENDAR, intentCalendar)
                startActivityForResult(intent, newCourseActivityRequestCode)

                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
            }
        })

        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_calendar_trip -> {
                    val intent = Intent(activity, NewTripActivity::class.java)
                    val intentCalendar = Calendar.getInstance()
                    intentCalendar.set(Calendar.DAY_OF_MONTH, calendarViewModel.day)
                    intentCalendar.set(Calendar.MONTH, calendarViewModel.month)
                    intentCalendar.set(Calendar.YEAR, calendarViewModel.year)
                    intent.putExtra(NewTripActivity.EXTRA_CALENDAR, intentCalendar)
                    startActivityForResult(intent, newTripActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_calendar_expense -> {
                    val intent = Intent(activity, NewExpenseActivity::class.java)
                    val intentCalendar = Calendar.getInstance()
                    intentCalendar.set(Calendar.DAY_OF_MONTH, calendarViewModel.day)
                    intentCalendar.set(Calendar.MONTH, calendarViewModel.month)
                    intentCalendar.set(Calendar.YEAR, calendarViewModel.year)
                    intent.putExtra(NewExpenseActivity.EXTRA_CALENDAR, intentCalendar)
                    startActivityForResult(intent, newExpenseActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.fab_calendar_fill -> {
                    val intent = Intent(activity, NewFillActivity::class.java)
                    val intentCalendar = Calendar.getInstance()
                    intentCalendar.set(Calendar.DAY_OF_MONTH, calendarViewModel.day)
                    intentCalendar.set(Calendar.MONTH, calendarViewModel.month)
                    intentCalendar.set(Calendar.YEAR, calendarViewModel.year)
                    intent.putExtra(NewFillActivity.EXTRA_CALENDAR, intentCalendar)
                    startActivityForResult(intent, newFillActivityRequestCode)

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })

        return root
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
                    data.getBooleanExtra(NewCourseActivity.EXTRA_PAID, false)
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

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
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

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
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

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
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

            /*Toast.makeText(
                context,
                R.string.cancelled,
                Toast.LENGTH_LONG
            ).show()*/
        }
        else {
            view?.let{
                Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
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