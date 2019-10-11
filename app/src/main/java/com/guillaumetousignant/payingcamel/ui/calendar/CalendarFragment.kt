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
        return root
    }
}