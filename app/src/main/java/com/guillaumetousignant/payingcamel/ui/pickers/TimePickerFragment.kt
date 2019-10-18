package com.guillaumetousignant.payingcamel.ui.pickers

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData

class TimePickerFragment(val calendar: MutableLiveData<Calendar>) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val hour = calendar.value?.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.value?.get(Calendar.MINUTE)
        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour?:12, minute?:0, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.value?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.value?.set(Calendar.MINUTE, minute)
        calendar.postValue(calendar.value) // CHECK weird way of doing this
    }
}