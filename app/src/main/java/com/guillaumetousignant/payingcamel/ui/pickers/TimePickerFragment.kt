package com.guillaumetousignant.payingcamel.ui.pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.guillaumetousignant.payingcamel.ui.new_course.NewCourseViewModel

class TimePickerFragment(val newCourseViewModel: NewCourseViewModel) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val hour = newCourseViewModel.calendar.value?.get(Calendar.HOUR_OF_DAY)
        val minute = newCourseViewModel.calendar.value?.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour?:12, minute?:0, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        newCourseViewModel.calendar.value?.set(Calendar.HOUR, hourOfDay)
        newCourseViewModel.calendar.value?.set(Calendar.MINUTE, minute)
        newCourseViewModel.calendar.postValue(newCourseViewModel.calendar.value) // CHECK weird way of doing this
    }
}