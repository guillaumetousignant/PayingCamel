package com.guillaumetousignant.payingcamel.ui.overview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.guillaumetousignant.payingcamel.R

import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.app.Activity
import android.graphics.Color
import android.icu.util.Calendar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.guillaumetousignant.payingcamel.NewCourseActivity
import com.guillaumetousignant.payingcamel.database.course.Course
import com.guillaumetousignant.payingcamel.database.skater.Skater
import com.guillaumetousignant.payingcamel.ui.pickers.DatePickerFragment
import com.guillaumetousignant.payingcamel.ui.pickers.SkatersPickerFragment
import java.text.DateFormat
import java.util.UUID

class OverviewFragment : Fragment(R.layout.fragment_overview) {

    private val printActivityRequestCode = 10
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
            ViewModelProvider(this).get(OverviewViewModel::class.java)

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
        }

        val startObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            startDateText.text = dateFormat.format(calendar.time)
        }

        val endObserver = Observer<Calendar> { calendar ->
            // Update the UI, in this case, a TextView.
            val dateFormat = DateFormat.getDateInstance(DateFormat.LONG) // CHECK add locale
            //getTimeInstance

            endDateText.text = dateFormat.format(calendar.time)
        }

        overviewViewModel.skaters.observe(viewLifecycleOwner, skatersObserver)
        overviewViewModel.startCalendar.observe(viewLifecycleOwner, startObserver)
        overviewViewModel.endCalendar.observe(viewLifecycleOwner, endObserver)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == printActivityRequestCode && resultCode == Activity.RESULT_OK) {
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
                overviewViewModel.insert(course)
                Unit
            }
        }
        else if (requestCode == printActivityRequestCode && resultCode == Activity.RESULT_CANCELED) {
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
}