package com.guillaumetousignant.payingcamel.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.guillaumetousignant.payingcamel.R

import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.android.material.snackbar.Snackbar
import android.app.Activity
import android.graphics.Color
import android.icu.text.NumberFormat
import android.icu.util.Calendar
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.expense.Expense
import com.guillaumetousignant.payingcamel.database.fill.Fill
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.database.trip.Trip
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.SkatersPickerFragment
import java.text.DateFormat
import java.util.UUID

class OverviewFragment : Fragment(R.layout.fragment_overview) {
    private lateinit var overviewViewModel: OverviewViewModel

    private lateinit var startDateText: TextView
    private lateinit var endDateText: TextView
    private lateinit var skaterListText: TextView
    private lateinit var courseAmountText: TextView
    private lateinit var expenseAmountText: TextView
    private lateinit var fillAmountText: TextView
    private lateinit var distanceAmountText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overviewViewModel =
            ViewModelProvider(this)[OverviewViewModel::class.java]

        startDateText = view.findViewById(R.id.start_date)
        endDateText = view.findViewById(R.id.end_date)
        skaterListText = view.findViewById(R.id.skater_list_text)
        courseAmountText = view.findViewById(R.id.course_amount)
        expenseAmountText = view.findViewById(R.id.expense_amount)
        fillAmountText = view.findViewById(R.id.fill_amount)
        distanceAmountText = view.findViewById(R.id.trip_amount)

        val skatersObserver = Observer<List<Skater>> { skaters ->
            // Update the UI, in this case, a TextView.
            if (skaters.isEmpty()) {
                skaterListText.text = getString(R.string.hint_selected_skaters)
            } else {
                val builder = StringBuilder()
                for (skater in skaters) {
                    builder.append("%s %s, ".format(skater.first_name, skater.last_name))
                }
                skaterListText.text = builder.toString().dropLast(2)
            }
            overviewViewModel.fetchCourses()
            overviewViewModel.fetchTrips()
        }

        val startObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            startDateText.text = dateFormat.format(calendar.time)
            overviewViewModel.fetchCourses()
            overviewViewModel.fetchTrips()
            overviewViewModel.fetchExpenses()
            overviewViewModel.fetchFills()
        }

        val endObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            endDateText.text = dateFormat.format(calendar.time)
            overviewViewModel.fetchCourses()
            overviewViewModel.fetchTrips()
            overviewViewModel.fetchExpenses()
            overviewViewModel.fetchFills()
        }

        val courseObserver = Observer<List<Course>> { courseList ->
            var amountTemp = 0
            courseList.let {
                for (course in it) {
                    amountTemp += course.amount
                }
            }
            overviewViewModel.amount.postValue(amountTemp)
        }

        val tripObserver = Observer<List<Trip>> { tripList ->
            var tripAmountTemp = 0.0
            tripList.let {
                for (trip in it) {
                    tripAmountTemp += trip.distance
                }
            }
            overviewViewModel.tripsAmount.postValue(tripAmountTemp)
        }

        val expenseObserver = Observer<List<Expense>> { expenseList ->
            var expensesAmountTemp = 0
            expenseList.let {
                for (expense in it) {
                    expensesAmountTemp += expense.amount
                }
            }
            overviewViewModel.expensesAmount.postValue(expensesAmountTemp)
        }

        val fillObserver = Observer<List<Fill>> { fillList ->
            var fillsAmountTemp = 0
            fillList.let {
                for (fill in it) {
                    fillsAmountTemp += fill.amount
                }
            }
            overviewViewModel.fillsAmount.postValue(fillsAmountTemp)
        }

        val amountObserver = Observer<Int> { amount ->
            courseAmountText.text = NumberFormat.getCurrencyInstance().format((amount.toDouble()/100))
        }

        val tripAmountObserver = Observer<Double> { tripsAmount ->
            distanceAmountText.text = getString(R.string.distance_format, NumberFormat.getNumberInstance().format(tripsAmount), getString(R.string.distance_unit))
        }

        val expenseAmountObserver = Observer<Int> { expenseAmount ->
            expenseAmountText.text = NumberFormat.getCurrencyInstance().format((expenseAmount.toDouble()/100))
        }

        val fillAmountObserver = Observer<Int> { fillAmount ->
            fillAmountText.text = NumberFormat.getCurrencyInstance().format((fillAmount.toDouble()/100))
        }

        overviewViewModel.skaters.observe(viewLifecycleOwner, skatersObserver)
        overviewViewModel.startCalendar.observe(viewLifecycleOwner, startObserver)
        overviewViewModel.endCalendar.observe(viewLifecycleOwner, endObserver)
        overviewViewModel.amount.observe(viewLifecycleOwner, amountObserver)
        overviewViewModel.courses.observe(viewLifecycleOwner, courseObserver)
        overviewViewModel.tripsAmount.observe(viewLifecycleOwner, tripAmountObserver)
        overviewViewModel.trips.observe(viewLifecycleOwner, tripObserver)
        overviewViewModel.expensesAmount.observe(viewLifecycleOwner, expenseAmountObserver)
        overviewViewModel.expenses.observe(viewLifecycleOwner, expenseObserver)
        overviewViewModel.fillsAmount.observe(viewLifecycleOwner, fillAmountObserver)
        overviewViewModel.fills.observe(viewLifecycleOwner, fillObserver)

        startDateText.setOnClickListener {
            DatePickerFragment(overviewViewModel.startCalendar).show(childFragmentManager, "StartDatePicker")
        }

        endDateText.setOnClickListener {
            DatePickerFragment(overviewViewModel.endCalendar).show(childFragmentManager, "EndDatePicker")
        }

        skaterListText.setOnClickListener {
            SkatersPickerFragment(overviewViewModel.skaters, overviewViewModel.allSkaters).show(childFragmentManager, "SkatersPicker")
        }

        val fabOverview: FloatingActionButton = view.findViewById(R.id.fab_overview)
        fabOverview.setOnClickListener {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private val startPrintForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val course = Course(
                        UUID.randomUUID(),
                        data.getSerializableExtra(NewCourseActivity.EXTRA_SKATER) as UUID?,
                        data.getSerializableExtra(NewCourseActivity.EXTRA_START) as Calendar? ?:Calendar.getInstance(),
                        data.getSerializableExtra(NewCourseActivity.EXTRA_END) as Calendar? ?:Calendar.getInstance(),
                        data.getIntExtra(NewCourseActivity.EXTRA_RATE, 0),
                        data.getIntExtra(NewCourseActivity.EXTRA_AMOUNT, 0),
                        data.getStringExtra(NewCourseActivity.EXTRA_NAME),
                        data.getStringExtra(NewCourseActivity.EXTRA_NOTE),
                        data.getBooleanExtra(NewCourseActivity.EXTRA_PAID, false),
                        getRandomMaterialColor()
                    )
                    overviewViewModel.insert(course)
                }
            }
            Activity.RESULT_CANCELED -> {
                /* view?.let{
                Snackbar.make(it, R.string.cancelled, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }*/
            }
            else -> {
                view?.let{
                    Snackbar.make(it, R.string.unknown_result_code, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }
    }

    private fun getRandomMaterialColor(): Int {
        val colors = resources.obtainTypedArray(R.array.mdcolor_400)
        val index = (Math.random() * colors.length()).toInt()
        val returnColor = colors.getColor(index, Color.GRAY)
        colors.recycle()

        return returnColor
    }
}