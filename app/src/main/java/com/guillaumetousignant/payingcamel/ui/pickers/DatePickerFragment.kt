package com.guillaumetousignant.payingcamel.ui.pickers

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData

class DatePickerFragment(val calendar: MutableLiveData<Calendar>) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = calendar.value?.get(Calendar.YEAR)
        val month = calendar.value?.get(Calendar.MONTH)
        val day = calendar.value?.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year?:1995, month?:4, day?:20)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        calendar.value?.set(Calendar.YEAR, year)
        calendar.value?.set(Calendar.MONTH, month)
        calendar.value?.set(Calendar.DAY_OF_MONTH, day)
        calendar.postValue(calendar.value) // CHECK weird way of doing this
    }
}