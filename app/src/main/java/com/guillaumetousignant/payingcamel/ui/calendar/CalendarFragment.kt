package com.guillaumetousignant.payingcamel.ui.calendar

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class CalendarFragment : Fragment() {

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
            val msg = getString(R.string.calendar_toast, year, (month+1), dayOfMonth)
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
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
            SpeedDialActionItem.Builder(R.id.fab_calendar_trip, R.drawable.ic_test_trips_white_24dp)
                .create())

        return root
    }
}